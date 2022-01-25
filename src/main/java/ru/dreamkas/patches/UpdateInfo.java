package ru.dreamkas.patches;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.dreamkas.patches.data.FileData;
import ru.dreamkas.patches.data.Info;
import ru.dreamkas.patches.data.Patch;
import ru.dreamkas.patches.data.Versions;

@SuppressWarnings({ "MismatchedQueryAndUpdateOfCollection", "unused" })
public class UpdateInfo {
    public static final String NEW_IN_VERSION = "Новое в версии";
    public static final String FIXED_IN_VERSION = "Исправленные ошибки";
    private final Map<String, Info> description = new HashMap<>();
    private final List<Patch> patches = new ArrayList<>();
    private final String product = "start_update";

    public UpdateInfo() {
        description.put(NEW_IN_VERSION, new Info(NEW_IN_VERSION, new ArrayList<>()));
        description.put(FIXED_IN_VERSION, new Info(FIXED_IN_VERSION, new ArrayList<>()));
    }

    public Collection<Info> getDescription() {
        return description.values();
    }

    @JsonIgnore
    public void addPatch(PatchData patch) {
        description.get(NEW_IN_VERSION).getContent().addAll(patch.getNewFeatures());
        description.get(FIXED_IN_VERSION).getContent().addAll(patch.getFixedErrors());
        patches.add(
            new Patch(
                new FileData(patch.getFileName(), patch.getSize(), patch.getMd5()),
                new Versions(patch.getVersionFrom(), patch.getVersionTo())
            )
        );
    }
}
