package main;

import gui.MainGUI;
import model.DataModel;
import model.ExcelHandler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExportController implements ActionListener {

    private DefaultListModel<DataModel> data;
    private ExcelHandler excel;
    private MainGUI parent;


    private String[] champs = new String[]{"", "1_Index_INTERNE", "2_Mission_PUBLIC", "3_Référence_Nasa_avec_Titre_INTERNE",
            "4_Référence_Nasa_INTERNE", "5_Titre_PUBLIC", "6_Date_de_prise_de_vue_/_Traitement_de_l’image_PUBLIC",
            "7_Date_de_traitement_de_l’image_PUBLIC", "8_Description_PUBLIC",
            "9_Référence_Galaxy_PUBLIC", "10_Référence_Nasa_ou_FL/Galaxy_INTERNE", "11_Référence_FL_INTERNE", "12_Credit_PUBLIC",
            "13_Wikipedia_Infos_about_...._PUBLIC", "14_Mots_clés_PUBLIC", "15_Taille_MB_PUBLIC", "16_Width_PUBLIC", "17_Height_PUBLIC",
            "18_Depth_PUBLIC", "19_Dpi_PUBLIC", "20_Format_PUBLIC", "21_Orientation_PUBLIC", "22_Focal_Length_PUBLIC",
            "23_Aperture_PUBLIC", "24_Aperture maxi_PUBLIC", "25_Exposure_PUBLIC", "26_Sensitivity_PUBLIC",
            "27_Mode Flash_PUBLIC", "28_Manufacturer_PUBLIC", "29_Model_PUBLIC", "30_User_Comment_INTERNE", "31_Propriétaire_PUBLIC"};

    public ExportController(DefaultListModel<DataModel> data, ExcelHandler excel, HashMap<String, ArrayList<DataModel>> mapFileName, MainGUI parent) {
        this.data = data;
        this.excel = excel;
        this.parent = parent;
    }

    private void concatAllInformation() {
        int length = data.size();

        for (String champ : new String[] {champs[8], champs[17]}) {
            String champKey = champ.substring(champ.indexOf("_") + 1, champ.lastIndexOf("_"));
            if (champ.equals(champs[17]))
                champKey = "tags";
            for (int i = 0; i < length; i ++) {
                HashMap<String, String> dm = data.get(i).getParsedData();
                if ((dm.get(champ) == null) || dm.get(champ).equals(""))
                    dm.put(champ, "(no " + champKey.toLowerCase() + ")");
            }
        }

        for (int i = 0; i < data.size(); i ++) {
            HashMap<String, String> dm = data.get(i).getParsedData();
            String description = dm.get(champs[8]);
//            System.out.println(description);
            if ((description == null) || (description.equals("")) || (!description.contains("---")))
                continue;
            int firstPos = description.indexOf("(") + 1;
            int secondPos = description.indexOf(")", firstPos);
            if (firstPos == -1)
                continue;
            String dateInParentheses = description.substring(firstPos, secondPos);
//            System.out.println("=>" + dateInParentheses);
            dm.put(champs[6], dateInParentheses);
        }
    }

    private void finalControl() {
        int length = data.size();
        HashMap<String, String> dm;
        for (int i = 0; i < length; i ++) {

            dm = data.get(i).getParsedData();
            String ref = LoadFileTask.refFromFileName(dm.get(champs[4]));

            // Title
            String titre = dm.get(champs[5]);
            if ((titre != null) && (ref != null)) {
                titre = titre.replaceAll("_", "-").replaceAll(ref.toLowerCase(), "").replaceAll(ref.toUpperCase(), "");
                if (titre.equals("."))
                    titre = "";
                while (titre.startsWith(". "))
                    titre = titre.substring(2);
                dm.put(champs[5], titre);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        new Thread(() -> {
            JFileChooser exportChooser = new JFileChooser(".");
            exportChooser.setFileFilter(new FileNameExtensionFilter("EXCEL File", "xls"));
            exportChooser.setSelectedFile(new File("output.xls"));
            int clicked = exportChooser.showSaveDialog(parent);



            if (clicked != exportChooser.getApproveButtonMnemonic()) {
                return;
            }

            if (exportChooser.getSelectedFile().toString().endsWith("xls"))
                excel.setOutputFile(exportChooser.getSelectedFile());
            else
                excel.setOutputFile(new File(exportChooser.getSelectedFile().toString() + ".xls"));

            int length = data.size();


            parent.getProgressBar().setString("Finalizing data...");

            finalControl();
            parent.getProgressBar().setString("Concatenating data...");
            concatAllInformation();

            try {
                excel.writeToFileExcel(data);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        }).start();
    }
}
