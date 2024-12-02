package api;

import com.sun.net.httpserver.HttpExchange;
import interfaces.Route;
import org.json.JSONObject;
import utils.Server;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class APIEndpoints extends Server {

    //Dummy examples for later implementation
    @Route(path = "/customer", method = {"GET", "POST", "PUT"})
    public Object create(HttpExchange exchange) throws IOException {
        if (Objects.equals(getRequestMethod(exchange), "POST")) {
            //convert request to json
            JSONObject requestBody = readJsonRequest(readRequestBody(exchange));

            //do something with request parameters
            String name = requestBody.getString("name");

            //create response
            JSONObject responseJson = createJsonResponse(true, "Resource created with data: " + requestBody);

            return new ResponseWrapper(responseJson.toString(), 201);
        }

        JSONObject defaultResponseJson = createJsonResponse(false, "GET, PUT is not yet implemented");
        return new ResponseWrapper(defaultResponseJson.toString(), 400);
    }


}
