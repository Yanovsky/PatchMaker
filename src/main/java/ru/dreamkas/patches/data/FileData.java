package ru.dreamkas.patches.data;

@SuppressWarnings({ "FieldCanBeLocal", "unused" })
public class FileData {
    private final String path;
    private final long size;
    private final String md5;

    public FileData(String path, long size, String md5) {
        this.path = path;
        this.size = size;
        this.md5 = md5;
    }
}
