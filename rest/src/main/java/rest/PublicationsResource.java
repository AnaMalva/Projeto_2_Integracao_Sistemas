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

    @GET
    public List<Publication> getAll() {
        return publicationBean.findAll();
    }

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

    @GET
    @Path("/search")
    public Response searchByTitle(@QueryParam("title") @DefaultValue("") String text) {
        if (isBlank(text)) {
            return badRequest("Parameter 'title' is required.");
        }
        return okList(publicationBean.findByTitleContaining(text));
    }

    @GET
    @Path("/researcher")
    public Response getByResearcher(@QueryParam("name") @DefaultValue("") String name) {
        if (isBlank(name)) {
            return badRequest("Parameter 'name' is required.");
        }
        return okList(publicationBean.findByResearcher(name));
    }

    @GET
    @Path("/venue")
    public Response getByVenue(@QueryParam("name") @DefaultValue("") String venue) {
        if (isBlank(venue)) {
            return badRequest("Parameter 'name' is required.");
        }
        return okList(publicationBean.findByVenue(venue));
    }

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


    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(message)
                .type(MediaType.TEXT_PLAIN)
                .build();
    }

    private Response okList(List<Publication> list) {
        GenericEntity<List<Publication>> entity = new GenericEntity<List<Publication>>(list) {};
        return Response.ok(entity).build();
    }
}