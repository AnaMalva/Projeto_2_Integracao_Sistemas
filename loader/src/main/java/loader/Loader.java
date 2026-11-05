package loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

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
import jakarta.xml.bind.Unmarshaller;

public class Loader {

    public static void main(String[] args) throws Exception {
        String path = args.length > 0 ? args[0] : "data/googlescholar_statistics.xml";
        File file = new File(path);
        if (!file.exists()) {
            System.err.println("File not found: " + file.getAbsolutePath());
            System.exit(1);
        }

        Googlescholar gs = readGooglescholar(file);
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
                    if (a.getRef() instanceof generated.Researcher author) {
                        p.getAuthors().add(author.getName().trim());
                    } else {
                        System.err.println("Warning: unknown author in \"" + px.getTitle() + "\"");
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

    private static Googlescholar readGooglescholar(File file) throws Exception {
        try (InputStream in = new FileInputStream(file)) {
            XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(in);
            while (reader.hasNext()) {
                if (reader.next() == XMLStreamConstants.START_ELEMENT
                        && reader.getLocalName().equals("googlescholar")) {
                    Unmarshaller u = JAXBContext.newInstance(Googlescholar.class).createUnmarshaller();
                    return u.unmarshal(reader, Googlescholar.class).getValue();
                }
            }
        }
        throw new IllegalStateException("No <googlescholar> element found in " + file);
    }
}