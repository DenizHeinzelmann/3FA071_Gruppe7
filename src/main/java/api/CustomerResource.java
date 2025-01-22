package api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.json.JSONObject;
import utils.SchemaValidator;


import javax.xml.validation.Schema;

@Path("/customer")
public class CustomerResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomer(){
        //validate Request
        //execute necessary logic
        //return Response
        JSONObject schemaJson = SchemaValidator.loadSchema("");


        return Response.status(Response.Status.CREATED).entity("dummy text").build();
    }
}
