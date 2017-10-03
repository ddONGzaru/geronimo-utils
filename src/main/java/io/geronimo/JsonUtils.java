package io.geronimo;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@UtilityClass
public class JsonUtils {

    private static ObjectMapper mapper;

    private static ObjectMapper getInstance() {

        mapper = new ObjectMapper();

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.AUTO_DETECT_GETTERS, true);
        mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        mapper.registerModule(new JavaTimeModule());

        return mapper;
    }

    private static ObjectMapper getObjectMapper() {
        return mapper == null ? getInstance() : mapper;
    }

    public static String toJson(Object object) {

        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            log.error("JsonUtils-toJson :: {}", e.getMessage());
            return null;
        }
    }

    public static <T> T fromJson(String jsonStr, Class<T> clazz) {

        try {
            return getObjectMapper().readValue(jsonStr, clazz);
        } catch (Exception e) {
            log.error("JsonUtils-fromJson :: {}", e.getMessage());
            return null;
        }
    }

    public static String toPrettyJson(Object object) {

        try {

            ObjectMapper objectMapper = getObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            return objectMapper.writeValueAsString(object);

        } catch (Exception e) {

            log.error("JsonUtils-toPrettyJson :: {}", e.getMessage());
            return null;
        }
    }

    public static String toPrettyJson(String json) {

        Object jsonObject = JsonUtils.fromJson(json, Object.class);

        try {
            return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            log.error("JsonUtils-toPrettyJson :: {}", e.getMessage());
            return null;
        }
    }

    public static String toListJson(Object object) {

        try {

            Map<String, Object> map = new HashMap<>();
            map.put("list", object);

            return getObjectMapper().writeValueAsString(map);

        } catch (Exception e) {
            log.error("JsonUtils-toListJson :: {}", e.getMessage());
            return null;
        }
    }

    public static <T> T fromJsonToMap(String jsonStr) {

        try {
            return getObjectMapper().readValue(jsonStr, new TypeReference<HashMap<String, Object>>() {});
        } catch (Exception e) {
            log.error("JsonUtils-fromJsonToMap :: {}", e.getMessage());
            return null;
        }
    }

    public static <T> T fromJson(String jsonStr, TypeReference<T> typeReference) {

        try {
            return getObjectMapper().readValue(jsonStr, typeReference);
        } catch (Exception e) {
            log.error("JsonUtils-fromJson :: {}", e.getMessage());
            return null;
        }
    }

    public static JsonNode fromJson(String json) {

        try {
            return getObjectMapper().readTree(json);
        } catch (IOException e) {
            log.error("JsonUtils-fromJson :: {}", e.getMessage());
            return null;
        }
    }

    public static JsonNode fromJson(Object object) {

        try {
            return getObjectMapper().convertValue(object, JsonNode.class);
        } catch (Exception e) {
            log.error("JsonUtils-fromJson :: {}", e.getMessage());
            return null;
        }
    }

    public static <T extends Collection> T fromJson(String jsonStr, CollectionType collectionType) {

        try {
            return getObjectMapper().readValue(jsonStr, collectionType);
        } catch (Exception e) {
            log.error("JsonUtils-fromJson :: {}", e.getMessage());
            return null;
        }
    }

    public static List<Map<String, Object>> fromJsonToList(String jsonStr) {

        try {
            return fromJson(jsonStr, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            log.error("JsonUtils-fromJsonToList :: {}", e.getMessage());
            return null;
        }
    }
}
