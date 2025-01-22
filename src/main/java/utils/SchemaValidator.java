package utils;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.everit.json.schema.loader.SchemaLoader;


import java.io.InputStream;

public class SchemaValidator {
    public static JSONObject loadSchema(String file) {
        InputStream schemaStream = SchemaValidator.class.getResourceAsStream(file);
        JSONTokener tokener = new JSONTokener(schemaStream);
        return new JSONObject(tokener);
    }

    public static void validateSchema(JSONObject inputJson, JSONObject schemaJson) {

        Schema schema = SchemaLoader.load(schemaJson);
        schema.validate(inputJson);


    }
}