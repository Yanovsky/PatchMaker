package ru.dreamkas.patches.mapper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class PathJacksonModule {
    public static SimpleModule simpleModule() {
        JsonDeserializer<Path> semverDeserializer = pathDeserializer();
        JsonSerializer<Path> semverSerializer = pathSerializer();
        return new SimpleModule()
            .addDeserializer(Path.class, semverDeserializer)
            .addSerializer(Path.class, semverSerializer);
    }

    private static JsonDeserializer<Path> pathDeserializer() {
        return new JsonDeserializer<Path>() {
            @Override
            public Path deserialize(JsonParser p, DeserializationContext ctxt) {
                try {
                    return Paths.get(p.getValueAsString());
                } catch (Exception e) {
                    return Paths.get("");
                }
            }
        };
    }

    private static JsonSerializer<Path> pathSerializer() {
        return new JsonSerializer<Path>() {
            @Override
            public void serialize(Path value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(value.toString());
            }
        };
    }
}
