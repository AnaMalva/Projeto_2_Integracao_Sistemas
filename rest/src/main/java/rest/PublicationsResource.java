package rest;

import java.util.List;

import beans.PublicationBean;
import data.Publication;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/publications")
@Produces(MediaType.APPLICATION_JSON)
public class PublicationsResource {

    @EJB
    private PublicationBean publicationBean;

    // 1. GET /publications
    @GET
    public List<Publication> getAll() {
        return publicationBean.findAll();
    }

    // 2. GET /publications/title?title=...
    @GET
    @Path("/title")
    public Response getByTitle(@QueryParam("title") @DefaultValue("") String title) {
        Publication p = publicationBean.findByTitle(title);
        if (p == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(p).build();
    }

    // 3. GET /publications/researcher?name=...
    @GET
    @Path("/researcher")
    public List<Publication> getByResearcher(@QueryParam("name") @DefaultValue("") String name) {
        return publicationBean.findByResearcher(name);
    }

    // 4. GET /publications/venue?name=...
    @GET
    @Path("/venue")
    public List<Publication> getByVenue(@QueryParam("name") @DefaultValue("") String venue) {
        return publicationBean.findByVenue(venue);
    }

    // 5. GET /publications/venue-researcher?venue=...&researcher=...
    @GET
    @Path("/venue-researcher")
    public List<Publication> getByVenueAndResearcher(
            @QueryParam("venue") @DefaultValue("") String venue,
            @QueryParam("researcher") @DefaultValue("") String researcher) {
        return publicationBean.findByVenueAndResearcher(venue, researcher);
    }
}