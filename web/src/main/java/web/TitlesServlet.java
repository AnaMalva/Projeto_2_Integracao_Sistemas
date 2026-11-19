package web;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import beans.PublicationBean;
import data.Publication;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/titles")
public class TitlesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private PublicationBean publicationBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Publication> all = publicationBean.findAll();

        // Publishers for the filter buttons (TreeSet = sorted, no repeats)
        Set<String> publishers = new TreeSet<>();
        for (Publication p : all) {
            publishers.add(p.getPublisher());
        }

        // Optional filter: /titles?publisher=IEEE
        String publisher = request.getParameter("publisher");
        List<Publication> publications = all;
        if (publisher != null && !publisher.trim().isEmpty()) {
            publications = publicationBean.findByVenue(publisher);
            request.setAttribute("publisher", publisher.trim());
        }

        request.setAttribute("publishers", publishers);
        request.setAttribute("totalCount", all.size());
        request.setAttribute("publications", publications);
        request.getRequestDispatcher("/WEB-INF/titles.jsp").forward(request, response);
    }
}