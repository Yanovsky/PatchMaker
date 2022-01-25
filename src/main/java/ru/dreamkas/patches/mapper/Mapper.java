package ru.dreamkas.patches.mapper;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * ObjectMapper для POJO сервера обновлений и сервера хаба Позволяет мапить сущности в/из JSON Tree Model ({@link JsonNode})
 */
public class Mapper {
    private static Mapper defaultServerMapper;

    public static Mapper getInstance() {
        if (defaultServerMapper == null) {
            defaultServerMapper = new Mapper();
        }
        return defaultServerMapper;
    }

    private final ObjectMapper mapper = new ObjectMapper();

    public Mapper() {
        mapper.registerModules(
                new JavaTimeModule(),
                SemverJacksonModule.simpleModule(),
                PathJacksonModule.simpleModule(),
                LongOverflowJacksonModule.simpleModule()
            )
            .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setDateFormat(StdDateFormat.getInstance())
            .setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @SuppressWarnings("unused")
    public ObjectMapper getMapper() {
        return mapper;
    }

    @SuppressWarnings("unused")
    public <C> C createAnyData(JsonNode node, Class<C> clazz) throws JsonProcessingException {
        return mapper.treeToValue(node, clazz);
    }

    @SuppressWarnings("unused")
    public <C> C createAnyData(String data, Class<C> clazz) throws IOException {
        return mapper.readValue(data, clazz);
    }

    @SuppressWarnings("unused")
    public <T> T createAnyList(String data, TypeReference<T> typeReference) throws IOException {
        return mapper.readValue(data, typeReference);
    }

    public byte[] createBytes(Object updateInfo) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(updateInfo).getBytes();
        } catch (JsonProcessingException e) {
            return new byte[0];
        }
    }
}
