package ru.dreamkas.patches;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ru.dreamkas.semver.Version;

public class PatchData {
    private final Version versionFrom;
    private final Version versionTo;
    private final long size;
    private final String md5;
    private final URL url;
    private final List<String> newFeatures;
    private final List<String> fixedErrors;

    public PatchData(String versionFrom, String versionTo, long size, String md5, String url, String info) throws Exception {
        this.versionFrom = Version.of(versionFrom);
        this.versionTo = Version.of(versionTo);
        this.size = size;
        this.md5 = md5;
        this.url = new URL(url);
        this.newFeatures = new ArrayList<>();
        this.fixedErrors = new ArrayList<>();
        readInfo(info);
    }

    private void readInfo(String info) {
        boolean toNew = false;
        boolean toFix = false;
        for (String s : info.split("\r\n")) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            if (StringUtils.equalsIgnoreCase(s, "Новое в версии")) {
                toNew = true;
                toFix = false;
                continue;
            }
            if (StringUtils.equalsIgnoreCase(s, "Исправленные ошибки")) {
                toNew = false;
                toFix = true;
                continue;
            }
            s = StringUtils.substringAfter(s, "- ");
            if (toNew) {
                this.newFeatures.add(s);
            }
            if (toFix) {
                this.fixedErrors.add(s);
            }
        }
    }

    public Version getVersionFrom() {
        return versionFrom;
    }

    public Version getVersionTo() {
        return versionTo;
    }

    public long getSize() {
        return size;
    }

    public String getMd5() {
        return md5;
    }

    public URL getUrl() {
        return url;
    }

    public List<String> getNewFeatures() {
        return newFeatures;
    }

    public List<String> getFixedErrors() {
        return fixedErrors;
    }

    @Override
    public String toString() {
        return "PatchData{" + '\n' +
            '\t' + "versionFrom=" + versionFrom + ",\n" +
            '\t' + "versionTo=" + versionTo + ",\n" +
            '\t' + "size=" + size + ",\n" +
            '\t' + "md5='" + md5 + '\'' + ",\n" +
            '\t' + "url='" + url + '\'' + ",\n" +
            '\t' + "newFeatures=" + newFeatures + ",\n" +
            '\t' + "fixedErrors=" + fixedErrors + ",\n" +
            '}';
    }

    public String getFileName() {
        return String.format("patches/%s_%s", versionFrom.toString(), versionTo.toString());
    }
}
