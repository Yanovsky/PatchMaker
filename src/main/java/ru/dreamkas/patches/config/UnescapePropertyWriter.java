package ru.dreamkas.patches.config;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.configuration.PropertiesConfiguration;

public class UnescapePropertyWriter extends PropertiesConfiguration.PropertiesWriter {
    public UnescapePropertyWriter(Writer writer, char delimiter) {
        super(writer, delimiter);
    }

    @Override
    public void writeProperty(String key, Object value, boolean forceSingleLine) throws IOException {
        this.write(key);
        this.write(this.fetchSeparator(key, value));
        this.write(String.valueOf(value));
        this.writeln(null);
    }
}
