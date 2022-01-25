package ru.dreamkas.patches.data;

@SuppressWarnings({ "FieldCanBeLocal", "unused" })
public class Patch {
    private final FileData file;
    private final Versions version;

    public Patch(FileData file, Versions version) {
        this.file = file;
        this.version = version;
    }
}
