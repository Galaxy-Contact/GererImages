package main;

import com.drew.imaging.ImageProcessingException;
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

    private DefaultListModel<DataModel> data, dataFinal = new DefaultListModel<>();
    private ExcelHandler excel;
    private MainGUI parent;


    private HashMap<String, ArrayList<DataModel>> mapFileName;
    private HashMap<String, Boolean> done = new HashMap<>();

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
        this.mapFileName = mapFileName;
    }

    private void concatAllInformation() {
        int length = data.size();
        for (int i = 0; i < length; i++) {
            if (done.containsKey(LoadfileTask.getKeyFileName(data.get(i).getFileName())))
                continue;

            ArrayList<DataModel> objectsData = mapFileName.get(LoadfileTask.getKeyFileName(data.get(i).getFileName()));

            String currentDescription = "", currentTitle = "";

            for (DataModel d : objectsData) {
                HashMap<String, String> temp = d.getParsedData();
                currentDescription = concat(currentDescription, temp, champs[8]);
                currentTitle = (temp.get(champs[5]) != null) ? temp.get(champs[5]) : "";
            }
            for (DataModel d : objectsData) {
                d.getParsedData().put(champs[8], currentDescription);
                d.getParsedData().putIfAbsent(champs[5], currentTitle);
            }
            done.put(LoadfileTask.getKeyFileName(data.get(i).getFileName()), true);
        }

        for (String champ : new String[] {champs[8], champs[14]}) {
            if (champ.equals(""))
                continue;
            String champKey = champ.substring(champ.indexOf("_") + 1, champ.lastIndexOf("_"));
            if (champ.equals(champs[14]))
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

    private String concat(String currentString, HashMap<String, String> other, String champ) {
        other.putIfAbsent(champ, "");
        if (!currentString.contains(other.get(champ)))
            if (currentString.equals(""))
                currentString += other.get(champ).trim();
            else
                currentString += ". " + other.get(champ).trim();
        return currentString.replaceAll("\\.{2}", ".").replaceAll("\\.{2}", "...");
    }

    private void finalControl() {
        int length = data.size();
        for (int i = 0; i < length; i ++) {
            HashMap<String, String> dm;
            dm = data.get(i).getParsedData();
            String description = dm.get(champs[8]);
            String ref = LoadfileTask.getKeyFileName(dm.get(champs[4]));

            // Title
            String titre = LoadfileTask.getKeyFileName(dm.get(champs[5]));
            if ((titre != null) && (ref != null)) {
                titre = titre.toLowerCase();
                if (titre.contains(ref.toLowerCase())) {
                    dm.put(champs[5], titre.replaceAll(ref, ""));
                }
            }

            // Desc

            if ((description == null) || (ref == null) || description.equals("") || ref.equals(""))
                continue;
            dm.put(champs[8], description.replaceAll("&amp;", "&"));
            if (description.trim().equals("(no description)"))
                dm.put(champs[8], null);
            if (!description.contains("---"))
                continue;
            dm.put(champs[8], description.substring(description.indexOf("(")).replaceAll("&amp;", "&"));
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

            for (int i = 0; i < length; i ++) {
                data.get(i).parseData();
                try {
                    data.get(i).parseImage();
                } catch (ImageProcessingException | IOException e) {
                    JOptionPane.showMessageDialog(parent, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                parent.getProgressBar().setValue((int) (((float) i) / length * parent.getProgressBar().getMaximum() / 2));
                parent.getProgressBar().setString("Gathering infos " + (i + 1) + " of " + length);
            }


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
