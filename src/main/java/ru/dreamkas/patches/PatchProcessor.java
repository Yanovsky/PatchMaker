package ru.dreamkas.patches;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.dreamkas.patches.mapper.Mapper;

public class PatchProcessor {
    private static final Logger log = LoggerFactory.getLogger(PatchProcessor.class);
    public static final String UPDATE_INFO_JSON = "update_info.json";

    public static Path create(@NotNull String title, Path patchFileFolder, List<PatchData> patches) throws Exception {
        FileOutputStream fos = null;
        ZipOutputStream zipOut = null;
        try {
            String from = patches.stream().min(Comparator.comparing(PatchData::getVersionFrom)).map(p -> p.getVersionFrom().toString()).orElse("");
            String to = patches.stream().max(Comparator.comparing(PatchData::getVersionTo)).map(p -> p.getVersionTo().toString()).orElse("");
            Path patchFile = patchFileFolder.resolve(String.format("start_%s_%s.update", from, to));
            Spinner.show(title, String.format("Создание файла %s", patchFile.getFileName()), patches.size());

            Files.deleteIfExists(patchFile);
            Files.createFile(patchFile);
            UpdateInfo updateInfo = new UpdateInfo();
            log.info("Create {}", patchFile.toAbsolutePath().normalize());
            fos = new FileOutputStream(patchFile.toFile());
            zipOut = new ZipOutputStream(fos);
            int ready = 0;
            for (PatchData patch : patches) {
                log.info("Start processing {}", patch);
                ZipEntry zipEntry = new ZipEntry(patch.getFileName());
                zipOut.putNextEntry(zipEntry);
                byte[] content = readContent(patch.getUrl());
                Spinner.setValue(++ready);
                zipOut.write(content, 0, content.length);
                updateInfo.addPatch(patch);
                log.info("Processing {}_{} complete", patch.getVersionFrom(), patch.getVersionTo());
            }
            log.info("Start process {}", UPDATE_INFO_JSON);
            Spinner.show(title, String.format("Создание файла %s", UPDATE_INFO_JSON));
            ZipEntry zipEntry = new ZipEntry(UPDATE_INFO_JSON);
            zipOut.putNextEntry(zipEntry);
            byte[] content = Mapper.getInstance().createBytes(updateInfo);
            log.info("Processing {} complete", UPDATE_INFO_JSON);
            Spinner.show(title, String.format("Сохранение файла %s", patchFile.getFileName()), patches.size());
            zipOut.write(content, 0, content.length);
            return patchFile;
        } finally {
            Optional.ofNullable(zipOut).ifPresent(it -> {
                try {
                    it.close();
                } catch (IOException e) {
                    log.warn("Can't close {}", it, e);
                }
            });
            Optional.ofNullable(fos).ifPresent(it -> {
                try {
                    it.close();
                } catch (IOException e) {
                    log.warn("Can't close {}", it, e);
                }
            });
            Spinner.hide();
        }
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
