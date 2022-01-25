package ru.dreamkas.patches.mapper;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class LongOverflowJacksonModule {
    public static SimpleModule simpleModule() {
        JsonDeserializer<Long> deserializer = deserializer();
        JsonSerializer<Long> serializer = serializer();
        return new SimpleModule()
            .addDeserializer(Long.class, deserializer)
            .addSerializer(Long.class, serializer);
    }

    private static JsonDeserializer<Long> deserializer() {
        return new JsonDeserializer<Long>() {
            @Override
            public Long deserialize(JsonParser p, DeserializationContext ctxt) {
                try {
                    return Long.parseLong(p.getValueAsString());
                } catch (Exception e) {
                    return 0L;
                }
            }
        };
    }

    private static JsonSerializer<Long> serializer() {
        return new JsonSerializer<Long>() {
            @Override
            public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(Objects.toString(value, "0"));
            }
        };
    }

}
