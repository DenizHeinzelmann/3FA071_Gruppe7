package api;

import enums.KindOfMeter;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import utils.SchemaValidator;

import java.util.UUID;

@Path("/readings")
public class ReadingResource {
    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadingWithUUID(@PathParam("id") UUID uuid){
        //validate Request
        //execute necessary logic
        //return Response
        JSONObject schemaJson = SchemaValidator.loadSchema("");


        return Response.status(Response.Status.OK).entity("dummy text").build();
    }
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReading(@QueryParam("customer") UUID id, @QueryParam("start") String start, @QueryParam("end") String end, @QueryParam("kindOfMeter")KindOfMeter kindOfMeter){
        //validate Request
        //execute necessary logic, including the query parameters and their possible absence
        //return Response

        return Response.status(Response.Status.OK).entity("dummy text").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postReading(){
        //validate Request
        //execute necessary logic
        //return Response

        return Response.status(Response.Status.CREATED).entity("dummy text").build();
    }
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response putReading(){
        //validate Request
        //execute necessary logic
        //return Response

        return Response.status(Response.Status.CREATED).entity("dummy text").build();
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteReadingWithUUID(@PathParam("id") UUID uuid){
        //validate Request
        //execute necessary logic
        //return Response

        return Response.status(Response.Status.OK).entity("dummy text").build();
    }
}
