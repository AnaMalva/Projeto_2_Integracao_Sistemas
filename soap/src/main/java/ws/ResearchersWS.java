package ws;

import java.util.List;

import beans.ResearcherBean;
import data.Researcher;
import jakarta.ejb.EJB;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService
public class ResearchersWS {

    @EJB
    private ResearcherBean researcherBean;

    @WebMethod
    public List<Researcher> getAllResearchers() {
        return researcherBean.findAll();
    }

    @WebMethod
    public Researcher getResearcherByName(@WebParam(name = "name") String name) {
        return researcherBean.findByName(name);
    }

    @WebMethod
    public List<Researcher> getResearchersByInterest(@WebParam(name = "interest") String interest) {
        return researcherBean.findByInterest(interest);
    }
}
