package model;

import java.io.File;

public class DataModel {

    private String fileName, imageExtension, infos, directory;
    private String[] listImageExtensions = new String[]{".png", ".jpg", ".jpeg", ".gif", ".bmp"};

    public String getDirectory() {
        return directory;
    }
    public String getFileName() {
        return fileName;
    }

    public String getInfos() {
        return infos;
    }

    public DataModel(String directory, String fileName, String infos) {
        this.fileName = fileName;
        this.infos = infos;
        this.directory = directory;
        this.imageExtension = guessImageExtension(directory);
    }

    private String guessImageExtension(String directory) {
        for (String ext : listImageExtensions) {
            if (new File(directory.replaceAll(".txt", ext)).isFile())
                return ext;
        }
        return "";
    }

    public String getImageExtension() {
        return imageExtension;
    }

    public void parseData() {

    }
}
