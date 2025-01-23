package api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.json.JSONObject;
import utils.SchemaValidator;


import javax.xml.validation.Schema;
import java.util.UUID;

@Path("/customers")
public class CustomerResource {
    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerWithUUID(@PathParam("id") UUID uuid){
        //validate Request
        //execute necessary logic
        //return Response
        JSONObject schemaJson = SchemaValidator.loadSchema("");


        return Response.status(Response.Status.OK).entity("dummy text").build();
    }
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomer(){
        //validate Request
        //execute necessary logic
        //return Response

        return Response.status(Response.Status.OK).entity("dummy text").build();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postCustomer(){
        //validate Request
        //execute necessary logic
        //return Response

        return Response.status(Response.Status.CREATED).entity("dummy text").build();
    }
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response putCustomer(){
        //validate Request
        //execute necessary logic
        //return Response

        return Response.status(Response.Status.CREATED).entity("dummy text").build();
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomerWithUUID(@PathParam("id") UUID uuid){
        //validate Request
        //execute necessary logic
        //return Response

        return Response.status(Response.Status.OK).entity("dummy text").build();
    }
}
