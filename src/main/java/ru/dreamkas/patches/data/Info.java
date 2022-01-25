package ru.dreamkas.patches.data;

import java.util.List;

@SuppressWarnings({ "FieldCanBeLocal", "unused" })
public class Info {
    private final String header;
    private final List<String> content;

    public Info(String header, List<String> content) {
        this.header = header;
        this.content = content;
    }

    public List<String> getContent() {
        return content;
    }
}
