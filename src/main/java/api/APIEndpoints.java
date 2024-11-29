package api;

import com.sun.net.httpserver.HttpExchange;
import interfaces.Route;
import utils.Server;

import java.io.IOException;
import java.util.Objects;

public class APIEndpoints extends Server {

    @Route(path = "/customer", method = {"GET","POST", "PUT"})
    public String create(HttpExchange exchange) throws IOException {
        if (Objects.equals(getRequestMethod(exchange), "POST")){
            String requestBody = readRequestBody(exchange);
            return "{\"message\": \"Resource created with data: " + requestBody + "\"}";
        };

        return "{\"message\": \"Resource created with data: }";

    }

}
