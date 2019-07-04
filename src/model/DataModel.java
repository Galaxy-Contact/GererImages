package model;

public class DataModel {
    public String getFileName() {
        return fileName;
    }

    public String getInfos() {
        return infos;
    }

    private String fileName, infos;

    public DataModel(String fileName, String infos) {
        this.fileName = fileName;
        this.infos = infos;
    }
}
