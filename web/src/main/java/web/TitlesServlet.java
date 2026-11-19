package web;

import java.io.IOException;
import java.util.ArrayList;
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
        // + the oldest and newest year, to help fill the year boxes
        Set<String> publishers = new TreeSet<>();
        int minYear = Integer.MAX_VALUE;
        int maxYear = Integer.MIN_VALUE;
        for (Publication p : all) {
            publishers.add(p.getPublisher());
            minYear = Math.min(minYear, p.getYear());
            maxYear = Math.max(maxYear, p.getYear());
        }

        // Filter 1 (optional): /titles?publisher=IEEE
        String publisher = request.getParameter("publisher");
        List<Publication> publications = all;
        if (publisher != null && !publisher.trim().isEmpty()) {
            publications = publicationBean.findByVenue(publisher);
            request.setAttribute("publisher", publisher.trim());
        }

        // Filter 2 (optional): /titles?from=2015&to=2021  (works together with filter 1)
        Integer from = readYear(request, "from");
        Integer to = readYear(request, "to");
        if (from != null && to != null && from > to) {
            request.setAttribute("yearError", "\"From\" must be before \"To\".");
        } else if (from != null || to != null) {
            List<Publication> inRange = new ArrayList<>();
            for (Publication p : publications) {
                boolean afterFrom = (from == null || p.getYear() >= from);
                boolean beforeTo = (to == null || p.getYear() <= to);
                if (afterFrom && beforeTo) {
                    inRange.add(p);
                }
            }
            publications = inRange;
        }

        request.setAttribute("from", from);
        request.setAttribute("to", to);
        if (!all.isEmpty()) {
            request.setAttribute("minYear", minYear);
            request.setAttribute("maxYear", maxYear);
        }
        request.setAttribute("publishers", publishers);
        request.setAttribute("totalCount", all.size());
        request.setAttribute("publications", publications);
        request.getRequestDispatcher("/WEB-INF/titles.jsp").forward(request, response);
    }

    // Reads a year from the address (?from=2015). Empty -> null. Not a number -> null + message.
    private Integer readYear(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            request.setAttribute("yearError", "Years must be numbers, for example 2015.");
            return null;
        }
    }
}