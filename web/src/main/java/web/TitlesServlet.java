package web;

import java.io.IOException;
import java.util.List;

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
        List<Publication> publications = publicationBean.findAll();
        request.setAttribute("publications", publications);
        request.getRequestDispatcher("/WEB-INF/titles.jsp").forward(request, response);
    }
}