// src/main/java/utils/JsonUtil.java

package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import mixins.CustomerMixin;
import mixins.ReadingMixin;
import model.Customer;
import model.Reading;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // Unterstützt Java 8+ Date/Time API
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .addMixIn(Customer.class, CustomerMixin.class) // Register CustomerMixin
            .addMixIn(Reading.class, ReadingMixin.class); // Register ReadingMixin

    public static String toJson(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return mapper.readValue(json, clazz);
    }
}
