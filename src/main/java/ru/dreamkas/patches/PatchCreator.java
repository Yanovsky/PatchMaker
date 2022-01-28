package ru.dreamkas.patches;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.dreamkas.patches.config.SafeConfiguration;
import ru.dreamkas.semver.Version;

public class PatchCreator {
    private static final Logger log = LoggerFactory.getLogger(PatchCreator.class);

    public static void main(String[] args) throws Exception {
        log.info("*********** Start PatchCreator ***********");
        Spinner.show("Создание патча", "Подключение к базе данных");
        Path propertiesFile = Paths.get(".", "application.properties");
        if (Files.notExists(propertiesFile)) {
            Spinner.hide();
            JOptionPane.showMessageDialog(null, "Файл настроек application.properties не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
            throw new Exception("Not found application.properties");
        }
        SafeConfiguration config = SafeConfiguration.loadProperties(propertiesFile, false);
        if (config == null) {
            Spinner.hide();
            JOptionPane.showMessageDialog(null, "Ошибка чтения файла настроек", "Ошибка", JOptionPane.ERROR_MESSAGE);
            throw new Exception("Error while read application.properties");
        }
        Path sshKey = Paths.get(config.getString("sshKey", "C:/id_rsa.ppk"));
        if (Files.notExists(sshKey)) {
            Spinner.hide();
            JOptionPane.showMessageDialog(null, "Файл ключа не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
            throw new Exception("Not found " + sshKey.toAbsolutePath().normalize());
        }
        @NotNull Version beginVersion = Version.of(config.getString("beginVersion"));
        @NotNull Version endVersion = Version.of(config.getString("endVersion"));
        @NotNull String title = String.format("Создание патча с %s до %s", beginVersion, endVersion);
        List<PatchData> patches;
        try {
            Spinner.show(title, "Подключение к базе данных");
            Database database = new Database(sshKey, config.getString("sshUserName", ""), config.getString("dbUserName", "postgres"), config.getString("dbPassword"));
            String productName = config.getString("product", "start_update");
            patches = database.select(
                "SELECT vf.comparable as from, vt.comparable as to, p.id, p.md5, p.size, p.url, p.info\n" +
                    "FROM patches p\n" +
                    "JOIN products pr ON (p.product_id = pr.id)\n" +
                    "JOIN versions vf on (p.version_from_id = vf.id)\n" +
                    "JOIN versions vt on (p.version_id = vt.id)\n" +
                    "WHERE pr.name = '" + productName + "'"
            );
            database.disconnect();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, title, e.getMessage(), JOptionPane.ERROR_MESSAGE);
            throw e;
        }
        Spinner.show(title, "Вычисление подходящих патчей");
        List<PatchData> applicablePatches = patches.stream()
            .sorted(Comparator.comparing(PatchData::getVersionFrom))
            .filter(p -> p.getVersionFrom().compareTo(beginVersion) >= 0 && p.getVersionTo().compareTo(endVersion) <= 0)
            .collect(Collectors.toList());
        if (applicablePatches.isEmpty()) {
            Spinner.hide();
            JOptionPane.showMessageDialog(null, "Список патчей пуст", "Ошибка", JOptionPane.ERROR_MESSAGE);
            throw new Exception("Patches list is empty");
        }
        Path result;
        try {
            result = PatchProcessor.create(title, Paths.get(config.getString("outputFolder", Paths.get(".").toAbsolutePath().normalize().toString())), applicablePatches);
            log.info("Patch {} complete\n\n", result);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, title, e.getMessage(), JOptionPane.ERROR_MESSAGE);
            throw e;
        }
        JOptionPane.showMessageDialog(null, String.format("Патч %s готов", result), "Информация", JOptionPane.INFORMATION_MESSAGE);
    }
}
