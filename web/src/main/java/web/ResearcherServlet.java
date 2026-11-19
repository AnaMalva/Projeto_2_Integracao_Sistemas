package web;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import beans.PublicationBean;
import beans.ResearcherBean;
import data.Publication;
import data.Researcher;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/researcher")
public class ResearcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private ResearcherBean researcherBean;

    @EJB
    private PublicationBean publicationBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Always needed: the list of names for the search box
        request.setAttribute("allResearchers", researcherBean.findAll());

        String name = request.getParameter("name");
        String interest = request.getParameter("interest");

        if (interest != null && !interest.trim().isEmpty()) {
            // Researchers that share an interest (SOAP bean, part b)
            request.setAttribute("interest", interest.trim());
            request.setAttribute("byInterest", researcherBean.findByInterest(interest));

        } else if (name != null && !name.trim().isEmpty()) {
            request.setAttribute("searched", name.trim());

            // 1. Profile from the Researchers "database"
            Researcher researcher = researcherBean.findByName(name);

            if (researcher != null) {
                // 2. Papers from the Publications "database", joined by name
                List<Publication> publications = publicationBean.findByResearcher(researcher.getName());

                // 3. Total citations and citations per year
                int totalCitations = 0;
                Map<Integer, Integer> citationsPerYear = new TreeMap<>();
                for (Publication p : publications) {
                    totalCitations = totalCitations + p.getCitations();
                    citationsPerYear.merge(p.getYear(), p.getCitations(), Integer::sum);
                }

                // 4. Year with most citations (used for the bar sizes and the "Most cited year" box)
                int bestYear = 0;
                int maxYearCitations = 0;
                for (Map.Entry<Integer, Integer> e : citationsPerYear.entrySet()) {
                    if (e.getValue() > maxYearCitations) {
                        maxYearCitations = e.getValue();
                        bestYear = e.getKey();
                    }
                }

                request.setAttribute("researcher", researcher);
                request.setAttribute("publications", publications);
                request.setAttribute("totalCitations", totalCitations);
                request.setAttribute("citationsPerYear", citationsPerYear);
                request.setAttribute("bestYear", bestYear);
                request.setAttribute("maxYearCitations", maxYearCitations);
            }
        }

        request.getRequestDispatcher("/WEB-INF/researcher.jsp").forward(request, response);
    }
}