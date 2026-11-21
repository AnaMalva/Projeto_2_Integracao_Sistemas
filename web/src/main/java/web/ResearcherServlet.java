package web;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.time.Year;

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

                // 3. Total citations and citations per year of publication
                int totalCitations = 0;
                Map<Integer, Integer> citationsPerYear = new TreeMap<Integer, Integer>();
                for (Publication p : publications) {
                    totalCitations = totalCitations + p.getCitations();

                    Integer current = citationsPerYear.get(p.getYear());
                    if (current == null) {
                        current = 0;
                    }
                    citationsPerYear.put(p.getYear(), current + p.getCitations());
                }

                // 4. Most cited year, adjusted for age.
                //    A paper from 2007 has had 20 years to collect citations, one from 2020 only 7,
                //    so each year's citations are divided by the number of years since then
                //    (that year included). The year with the highest rate wins.
                int currentYear = Year.now().getValue();
                int bestYear = 0;
                double bestRate = 0;
                int maxYearCitations = 0;   // largest total, only used for the bar sizes

                for (Map.Entry<Integer, Integer> e : citationsPerYear.entrySet()) {
                    int year = e.getKey();
                    int citations = e.getValue();

                    if (citations > maxYearCitations) {
                        maxYearCitations = citations;
                    }

                    int yearsSince = currentYear - year + 1;
                    if (yearsSince < 1) {
                        yearsSince = 1;     // protects against a year in the future
                    }
                    double rate = (double) citations / yearsSince;

                    if (rate > bestRate) {
                        bestRate = rate;
                        bestYear = year;
                    }
                }
                double bestYearRate = Math.round(bestRate * 10) / 10.0;   // one decimal place

                request.setAttribute("researcher", researcher);
                request.setAttribute("publications", publications);
                request.setAttribute("totalCitations", totalCitations);
                request.setAttribute("citationsPerYear", citationsPerYear);
                request.setAttribute("bestYear", bestYear);
                request.setAttribute("maxYearCitations", maxYearCitations);
                request.setAttribute("bestYearRate", bestYearRate);
            }
        }

        request.getRequestDispatcher("/WEB-INF/researcher.jsp").forward(request, response);
    }
}