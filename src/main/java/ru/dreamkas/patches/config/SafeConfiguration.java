package ru.dreamkas.patches.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class SafeConfiguration extends PropertiesConfiguration {
    public static SafeConfiguration create(Path path, boolean useReloadingStrategy) {
        return create(path, "UTF-8", useReloadingStrategy);
    }

    public static SafeConfiguration create(Path path, String encoding, boolean useReloadingStrategy) {
        return create(path.toFile(), encoding, useReloadingStrategy);
    }

    public static SafeConfiguration create(File file, String encoding, boolean useReloadingStrategy) {
        SafeConfiguration result = new SafeConfiguration();
        result.setIOFactory(new UnescapeIOFactory());
        result.setFile(file);
        result.setEncoding(encoding);
        result.setDelimiterParsingDisabled(true);
        result.setListDelimiter('|');
        if (useReloadingStrategy) {
            FileChangedReloadingStrategy reloadingStrategy = new FileChangedReloadingStrategy();
            reloadingStrategy.setRefreshDelay(100);
            result.setReloadingStrategy(reloadingStrategy);
        }
        if (file.exists()) {
            try {
                result.load(file);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    FileUtils.forceDelete(file);
                } catch (IOException ex) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public SafeConfiguration(File file) throws ConfigurationException {
        super(file);
    }

    public SafeConfiguration() {}

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        if (!containsKey(key)) {
            return StringUtils.trimToNull(defaultValue);
        }
        return StringUtils.trimToNull(super.getString(key, defaultValue));
    }

    @Override
    public List<Object> getList(String key, List<?> defaultValue) {
        String delimiter = (getListDelimiter() == '|' ? "\\" : "") + getListDelimiter();
        return containsKey(key)
            ? Arrays.stream(getString(key, "").split(delimiter)).collect(Collectors.toList())
            : new ArrayList<>(defaultValue);
    }

    public void setString(String key, String value) {
        setProperty(key, StringUtils.trimToNull(value));
    }

    public void removeProperty(String key) {
        setProperty(key, null);
    }

    public void setStringAndSave(String key, String value) {
        setPropertyAndSave(key, StringUtils.trimToNull(value));
    }

    public void setBoolAndSave(String key, Boolean value) {
        setPropertyAndSave(key, value);
    }

    public void setPropertyAndSave(String key, Object value) {
        setProperty(key, value);
        save();
    }

    @Override
    public void save() {
        try {
            super.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(File file) {
        try {
            super.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(Path path) {
        try {
            super.save(path.toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getLongSafe(String key, long defaultValue) {
        try {
            return getLong(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int getIntSafe(String key, int defaultValue) {
        try {
            return getInt(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean getBooleanSafe(String key, boolean defaultValue) {
        try {
            return getBoolean(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void setBoolean(String key, boolean value) {
        setProperty(key, value);
    }

    public static SafeConfiguration loadProperties(Path path) {
        return loadProperties(path, true);
    }

    public static SafeConfiguration loadProperties(Path path, boolean useReloadingStrategy) {
        try {
            return SafeConfiguration.create(path, useReloadingStrategy);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeProperties(Path path, SafeConfiguration settings) {
        settings.save(path);
    }
}
