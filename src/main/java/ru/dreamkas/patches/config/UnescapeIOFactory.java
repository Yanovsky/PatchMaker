package ru.dreamkas.patches.config;

import java.io.Writer;

import org.apache.commons.configuration.PropertiesConfiguration;

public class UnescapeIOFactory extends PropertiesConfiguration.DefaultIOFactory {
    @Override
    public PropertiesConfiguration.PropertiesWriter createPropertiesWriter(Writer writer, char delimiter) {
        return new UnescapePropertyWriter(writer, delimiter);
    }
}
