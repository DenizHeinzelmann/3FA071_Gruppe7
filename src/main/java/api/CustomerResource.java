package api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import repository.CustomerRepository;
import utils.SchemaValidator;


import javax.xml.validation.Schema;
import java.sql.SQLException;
import java.util.UUID;

@Path("/api/customers")
public class CustomerResource {
    private final CustomerRepository customerRepository = new CustomerRepository();

    public CustomerResource() throws SQLException {
    }

    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerWithUUID(@PathParam("id") UUID uuid){
       JSONObject customerJson = new JSONObject(customerRepository.getCustomer(uuid));

       return Response.status(Response.Status.OK).entity(customerJson.toString()).build();
    }
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomer() throws SQLException {
        //validate Request
        //execute necessary logic
        //return Response

        JSONArray customerList = new JSONArray(customerRepository.getAllCustomers());

        return Response.status(Response.Status.OK).entity(customerList.toString()).build();

        //return Response.status(Response.Status.OK).entity(new JSONObject("{'name':'john'}").toString()).build();
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
