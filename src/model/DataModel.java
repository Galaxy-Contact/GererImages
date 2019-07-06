package model;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class DataModel {

    private String fileName, imageExtension, infos, directory;
    private int id;
    private String[] listImageExtensions = new String[]{".png", ".jpg", ".jpeg", ".gif", ".bmp"};
    private String[] champs = new String[]{"", "1_Index_INTERNE", "2_Mission_PUBLIC", "3_Référence_Nasa_avec_Titre_INTERNE",
            "4_Référence_Nasa_INTERNE", "5_Date_de_prise_de_vue_/_Traitement_de_l’image_PUBLIC",
            "6_Date_de_traitement_de_l’image_PUBLIC", "7_Référence_Galaxy_PUBLIC", "8_Référence_Nasa_ou_FL/Galaxy_INTERNE",
            "9_Référence_FL_INTERNE", "10_Titre_PUBLIC", "11_Description_PUBLIC", "12_Wikipedia_Infos_about_...._PUBLIC",
            "13_Mots_clés_PUBLIC", "14_Taille_MB_PUBLIC", "15_Width_PUBLIC", "16_Height_PUBLIC", "17_Depth_PUBLIC",
            "18_Dpi_PUBLIC", "19_Format_PUBLIC", "20_Orientation_PUBLIC", "21_Focal_Length_PUBLIC", "22_Aperture_PUBLIC",
            "23_Exposure_PUBLIC", "24_Sensitivity_PUBLIC", "25_Manufacturer_PUBLIC", "26_Model_PUBLIC",
            "27_User_Comment_INTERNE", "28_Propriétaire_PUBLIC", "29_OPTION_1", "30_OPTION_2", "31_OPTION_3"};


    private HashMap<String, String> parsedData = new HashMap<>();

    public HashMap<String, String> getParsedData() {
        return parsedData;
    }

    public String getDirectory() {
        return directory;
    }
    public String getFileName() {
        return fileName;
    }

    public String getInfos() {
        return infos;
    }

    public DataModel(int id, String directory, String fileName, String infos) {
        this.fileName = fileName;
        this.infos = infos;
        this.directory = directory;
        this.imageExtension = guessImageExtension(directory);
        this.id = id;
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
        String[] splited = infos.split("\n");
        for (int i = 1; i < champs.length; i ++)
            parsedData.put(champs[i], "");
        parsedData.put(champs[1], (id + 1) + "");
        parsedData.put(champs[4], getBigText(splited, "TITLE"));
        parsedData.put(champs[5], getTakenDate(splited));
        parsedData.put(champs[9], getFL(splited));
        parsedData.put(champs[11], getBigText(splited, "DESCRIPTION"));
        parsedData.put(champs[13], getTags(splited));
    }

    private void debugParsed() {
        for (int i = 1; i < champs.length; i ++)
            if (!parsedData.get(champs[i]).equals(""))
                System.out.println(champs[i] + ": " + parsedData.get(champs[i]));
    }

    private String getBigText(String[] splited, String group) {
        for (int i = 0; i < splited.length; i ++) {
            if (splited[i].contains(group))
                return splited[i + 2];
        }
        return "";
    }

    private String getTakenDate(String[] splited) {
        for (String s : splited) {
            if (s.startsWith("Taken Date"))
                return s.substring(s.indexOf(":") + 2);
        }
        return "";
    }

    private String getFL(String[] splited) {
        for (String s : splited) {
            if (s.startsWith("Photo URL")) {
                String[] temp = s.split("/");
                return temp[temp.length - 1];
            }
        }
        return "";
    }

    private String getTags(String[] splited) {
        StringBuilder tags = new StringBuilder(getBigText(splited, "TAGS"));
        String[] tagsList = tags.toString().split("\"");
        tags = new StringBuilder();
        for (String t : tagsList) {
            t = t.trim();
            if (!t.equals(""))
                tags.append(t).append(", ");
        }
        return tags.substring(0, tags.length() - 3);
    }



    public void parseImage() {
//        File imageFile = new File(directory.replaceAll(".txt", imageExtension));

    }
}
