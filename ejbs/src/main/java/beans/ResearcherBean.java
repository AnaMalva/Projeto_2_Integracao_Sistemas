package beans;

import java.util.List;
import java.util.ArrayList;

import data.Researcher;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
public class ResearcherBean {

    @PersistenceContext(unitName = "project2PU")
    private EntityManager em;

    public List<Researcher> findAll() {
        System.out.println("ResearcherBean: findAll");
        TypedQuery<Researcher> q = em.createQuery(
                "SELECT r FROM Researcher r ORDER BY r.name", Researcher.class);
        return q.getResultList();
    }

    public Researcher findByName(String name) {
        System.out.println("ResearcherBean: findByName " + name);
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        TypedQuery<Researcher> q = em.createQuery(
                "SELECT r FROM Researcher r WHERE LOWER(r.name) = LOWER(:name)", Researcher.class);
        q.setParameter("name", name.trim());
        List<Researcher> result = q.getResultList();
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public List<Researcher> findByInterest(String interest) {
        System.out.println("ResearcherBean: findByInterest " + interest);
        if (interest == null || interest.trim().isEmpty()) {
            return new ArrayList<Researcher>();
        }
        TypedQuery<Researcher> q = em.createQuery(
                "SELECT DISTINCT r FROM Researcher r JOIN r.interests i "
              + "WHERE LOWER(i) = LOWER(:interest) ORDER BY r.name", Researcher.class);
        q.setParameter("interest", interest.trim());
        return q.getResultList();
    }
}
