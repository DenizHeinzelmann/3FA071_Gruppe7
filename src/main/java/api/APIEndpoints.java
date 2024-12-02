package api;

import com.sun.net.httpserver.HttpExchange;
import interfaces.Route;
import org.json.JSONObject;
import utils.Server;

import java.io.IOException;
import java.util.Objects;

public class APIEndpoints extends Server {

    //Dummy examples for later implementation
    @Route(path = "/customer", method = {"GET", "POST", "PUT"})
    public Object create(HttpExchange exchange) throws IOException {
        if (Objects.equals(getRequestMethod(exchange), "POST")) {
            String requestBody = readRequestBody(exchange);

            JSONObject responseJson = createJsonResponse("Resource created with data: " + requestBody);
            return new ResponseWrapper(responseJson.toString(), 201);
        }

        JSONObject defaultResponseJson = createJsonResponse("GET, PUT is not yet implemented");
        return new ResponseWrapper(defaultResponseJson.toString(), 500);
    }

    private JSONObject createJsonResponse(String message) {
        JSONObject responseJson = new JSONObject();
        responseJson.put("message", message);

        return responseJson;
    }
}
