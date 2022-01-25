package ru.dreamkas.patches.data;

import ru.dreamkas.semver.Version;

@SuppressWarnings({ "FieldCanBeLocal", "unused" })
public class Versions {
    private final Version from;
    private final Version to;

    public Versions(Version from, Version to) {
        this.from = from;
        this.to = to;
    }
}
