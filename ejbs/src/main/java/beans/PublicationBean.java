package beans;

import java.util.List;

import data.Publication;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class PublicationBean {

    @PersistenceContext(unitName = "project2PU")
    private EntityManager em;

    // 1. Todas as publicações
    public List<Publication> findAll() {
        System.out.println("PublicationBean: findAll");
        return em.createQuery(
                "SELECT p FROM Publication p ORDER BY p.year DESC, p.title", Publication.class)
                .getResultList();
    }

    // 2. Uma publicação, dado o título
    public Publication findByTitle(String title) {
        System.out.println("PublicationBean: findByTitle " + title);
        List<Publication> result = em.createQuery(
                "SELECT p FROM Publication p WHERE LOWER(p.title) = LOWER(:title)", Publication.class)
                .setParameter("title", title.trim())
                .getResultList();
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    // 3. Publicações de um investigador
    public List<Publication> findByResearcher(String name) {
        System.out.println("PublicationBean: findByResearcher " + name);
        return em.createQuery(
                "SELECT DISTINCT p FROM Publication p JOIN p.authors a "
              + "WHERE LOWER(a) = LOWER(:name) ORDER BY p.year DESC, p.title", Publication.class)
                .setParameter("name", name.trim())
                .getResultList();
    }

    // 4. Publicações de uma revista/conferência
    public List<Publication> findByVenue(String venue) {
        System.out.println("PublicationBean: findByVenue " + venue);
        return em.createQuery(
                "SELECT p FROM Publication p "
              + "WHERE LOWER(p.publisher) = LOWER(:venue) ORDER BY p.year DESC, p.title", Publication.class)
                .setParameter("venue", venue.trim())
                .getResultList();
    }

    // 5. Publicações de uma revista/conferência e de um investigador
    public List<Publication> findByVenueAndResearcher(String venue, String name) {
        System.out.println("PublicationBean: findByVenueAndResearcher " + venue + " / " + name);
        return em.createQuery(
                "SELECT DISTINCT p FROM Publication p JOIN p.authors a "
              + "WHERE LOWER(p.publisher) = LOWER(:venue) AND LOWER(a) = LOWER(:name) "
              + "ORDER BY p.year DESC, p.title", Publication.class)
                .setParameter("venue", venue.trim())
                .setParameter("name", name.trim())
                .getResultList();
    }
}