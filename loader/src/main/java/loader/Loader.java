package loader;

import java.io.File;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import data.Publication;
import data.Researcher;
import generated.Author;
import generated.Googlescholar;
import generated.Paper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

public class Loader {

    public static void main(String[] args) throws Exception {
        Loader app = new Loader();

        String path = args.length > 0 ? args[0] : "data/googlescholar.xml";
        File file = new File(path);
        if (!file.exists()) {
            System.err.println("File not found: " + file.getAbsolutePath());
            System.exit(1);
        }

        try {
            Validator validator = app.initValidator("/googlescholar.xsd");
            validator.validate(new StreamSource(file));
        } catch (SAXException e) {
            System.err.println("The input XML is not valid: " + e.getMessage());
            System.err.println("Nothing was loaded.");
            System.exit(1);
        }
        System.out.println("The file " + path + " is valid against googlescholar.xsd.");

        Googlescholar gs = app.unmarshalGooglescholar(file);
        int nResearchers = gs.getResearchers().getResearcher().size();
        int nPapers = gs.getPapers().getPaper().size();
        System.out.println("Read " + nResearchers + " researchers and " + nPapers + " papers from " + path);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("loaderPU");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            for (generated.Researcher rx : gs.getResearchers().getResearcher()) {
                Researcher r = new Researcher();
                r.setName(rx.getName().trim());
                r.getUniversities().addAll(rx.getUniversity());
                r.getInterests().addAll(rx.getInterest());
                r.setHomepage(rx.getHomepage());
                r.setEmail(rx.getEmail());
                em.persist(r);
            }

            for (Paper px : gs.getPapers().getPaper()) {
                Publication p = new Publication();
                p.setTitle(px.getTitle().trim());
                p.setYear(px.getYear().getYear());
                p.setPublisher(px.getPublisher());
                p.setCitations(px.getCitations().intValue());
                p.setDoi(px.getDoi());
                for (Author a : px.getAuthor()) {
                    generated.Researcher author = (generated.Researcher) a.getRef();
                    if (author != null) {
                        p.getAuthors().add(author.getName().trim());
                    }
                }
                em.persist(p);
            }

            tx.commit();
            System.out.println("Loaded " + nResearchers + " researchers and " + nPapers + " publications into the database.");
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.err.println("Loading failed, nothing was saved.");
            throw e;
        } finally {
            em.close();
            emf.close();
        }
    }

    private Validator initValidator(String xsdResource) throws SAXException {
        URL xsd = Loader.class.getResource(xsdResource);
        if (xsd == null) {
            throw new IllegalStateException("Schema " + xsdResource + " not found inside the loader jar");
        }
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile = new StreamSource(xsd.toString());
        Schema schema = factory.newSchema(schemaFile);
        return schema.newValidator();
    }

    private Googlescholar unmarshalGooglescholar(File file) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Googlescholar.class);
        return (Googlescholar) context.createUnmarshaller().unmarshal(file);
    }
}