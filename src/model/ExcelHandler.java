package model;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class ExcelHandler {

    private File outputFile;
    private String[] champs = new String[]{"", "1_Index_INTERNE", "2_Mission_PUBLIC", "3_Référence_Nasa_avec_Titre_INTERNE",
            "4_Référence_Nasa_INTERNE", "5_Date_de_prise_de_vue_/_Traitement_de_l’image_PUBLIC",
            "6_Date_de_traitement_de_l’image_PUBLIC", "7_Référence_Galaxy_PUBLIC", "8_Référence_Nasa_ou_FL/Galaxy_INTERNE",
            "9_Référence_FL_INTERNE", "10_Titre_PUBLIC", "11_Description_PUBLIC", "12_Wikipedia_Infos_about_...._PUBLIC",
            "13_Mots_clés_PUBLIC", "14_Taille_MB_PUBLIC", "15_Width_PUBLIC", "16_Height_PUBLIC", "17_Depth_PUBLIC",
            "18_Dpi_PUBLIC", "19_Format_PUBLIC", "20_Orientation_PUBLIC", "21_Focal_Length_PUBLIC", "22_Aperture_PUBLIC",
            "23_Exposure_PUBLIC", "24_Sensitivity_PUBLIC", "25_Manufacturer_PUBLIC", "26_Model_PUBLIC",
            "27_User_Comment_INTERNE", "28_Propriétaire_PUBLIC", "29_OPTION_1", "30_OPTION_2", "31_OPTION_3"};


    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public void writeToFile(DefaultListModel<DataModel> data) throws IOException {
        FileWriter fw = new FileWriter(outputFile);
        for (int i = 1; i < champs.length; i ++) {
            fw.write(champs[i] + ",");
        }
        fw.write("\n");

        int length = data.size();

        HashMap<String, String> hash;

        for (int i = 0; i < length; i ++) {
            hash = data.get(i).getParsedData();
            for (String champName : champs) {
                if (!champName.equals("")) {
                    String content = hash.get(champName);
                    if (!content.equals(","))
                        content = "\"" + content + "\",";
                    else
                        content = content + ",";
                    fw.write(content);
                }
            }
            fw.write("\n");
        }

        fw.flush();
        fw.close();
    }
}
