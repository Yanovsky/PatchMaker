package ru.dreamkas.patches;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ru.dreamkas.patches.mapper.Mapper;

public class PatchCreator {

    public static final String UPDATE_INFO_JSON = "update_info.json";

    public static void create(Path patchFileFolder, List<PatchData> patches) throws Exception {
        String from = patches.stream().min(Comparator.comparing(PatchData::getVersionFrom)).map(p -> p.getVersionFrom().toString()).orElse("");
        String to = patches.stream().max(Comparator.comparing(PatchData::getVersionTo)).map(p -> p.getVersionTo().toString()).orElse("");
        Path patchFile = patchFileFolder.resolve(String.format("start_%s_%s.update", from, to));
        Files.deleteIfExists(patchFile);
        Files.createFile(patchFile);
        UpdateInfo updateInfo = new UpdateInfo();
        System.out.printf("Create %s%n", patchFile.toAbsolutePath().normalize());
        FileOutputStream fos = new FileOutputStream(patchFile.toFile());
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (PatchData patch : patches) {
            System.out.printf("Start processing %s%n", patch);
            ZipEntry zipEntry = new ZipEntry(patch.getFileName());
            zipOut.putNextEntry(zipEntry);
            byte[] content = readContent(patch.getUrl());
            zipOut.write(content, 0, content.length);

            updateInfo.addPatch(patch);
            System.out.printf("Processing complete%n");
        }

        System.out.printf("Start process %s%n", UPDATE_INFO_JSON);
        ZipEntry zipEntry = new ZipEntry(UPDATE_INFO_JSON);
        zipOut.putNextEntry(zipEntry);
        byte[] content = Mapper.getInstance().createBytes(updateInfo);
        zipOut.write(content, 0, content.length);
        System.out.printf("Processing %s complete%n", UPDATE_INFO_JSON);

        zipOut.close();
        fos.close();
        System.out.println("Complete");
    }

    private static byte[] readContent(URL url) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream is = url.openStream()) {
            byte[] byteChunk = new byte[4096];
            int n;
            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n);
            }
        }
        return baos.toByteArray();
    }
}
