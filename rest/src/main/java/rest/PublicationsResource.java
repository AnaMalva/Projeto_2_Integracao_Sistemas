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
import jakarta.ws.rs.core.GenericEntity;
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

    // 2. GET /publications/title?title=...   (exact title, ignoring case)
    @GET
    @Path("/title")
    public Response getByTitle(@QueryParam("title") @DefaultValue("") String title) {
        if (isBlank(title)) {
            return badRequest("Parameter 'title' is required.");
        }
        Publication p = publicationBean.findByTitle(title);
        if (p == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(p).build();
    }

    // 2b. GET /publications/search?title=...   (part of the title)
    @GET
    @Path("/search")
    public Response searchByTitle(@QueryParam("title") @DefaultValue("") String text) {
        if (isBlank(text)) {
            return badRequest("Parameter 'title' is required.");
        }
        return okList(publicationBean.findByTitleContaining(text));
    }

    // 3. GET /publications/researcher?name=...
    @GET
    @Path("/researcher")
    public Response getByResearcher(@QueryParam("name") @DefaultValue("") String name) {
        if (isBlank(name)) {
            return badRequest("Parameter 'name' is required.");
        }
        return okList(publicationBean.findByResearcher(name));
    }

    // 4. GET /publications/venue?name=...
    @GET
    @Path("/venue")
    public Response getByVenue(@QueryParam("name") @DefaultValue("") String venue) {
        if (isBlank(venue)) {
            return badRequest("Parameter 'name' is required.");
        }
        return okList(publicationBean.findByVenue(venue));
    }

    // 5. GET /publications/venue-researcher?venue=...&researcher=...
    @GET
    @Path("/venue-researcher")
    public Response getByVenueAndResearcher(
            @QueryParam("venue") @DefaultValue("") String venue,
            @QueryParam("researcher") @DefaultValue("") String researcher) {
        if (isBlank(venue) || isBlank(researcher)) {
            return badRequest("Parameters 'venue' and 'researcher' are both required.");
        }
        return okList(publicationBean.findByVenueAndResearcher(venue, researcher));
    }

    // ---------- helpers ----------

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    // 400 Bad Request with a short explanation in plain text
    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(message)
                .type(MediaType.TEXT_PLAIN)
                .build();
    }

    // 200 OK with a list of publications as JSON
    private Response okList(List<Publication> list) {
        GenericEntity<List<Publication>> entity = new GenericEntity<List<Publication>>(list) {};
        return Response.ok(entity).build();
    }
}