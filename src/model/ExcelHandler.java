package model;

import gui.MainGUI;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ExcelHandler {

    private File outputFile;
    private MainGUI parent;
    private String[] champs = new String[]{"", "1_Index_INTERNE", "2_Mission_PUBLIC", "3_Référence_Nasa_avec_Titre_INTERNE",
            "4_Référence_Nasa_INTERNE", "5_Titre_PUBLIC", "6_Date_de_prise_de_vue_/_Traitement_de_l’image_PUBLIC",
            "7_Date_de_traitement_de_l’image_PUBLIC", "8_Description_PUBLIC",
            "9_Référence_Galaxy_PUBLIC", "10_Référence_Nasa_ou_FL/Galaxy_INTERNE", "11_Référence_FL_INTERNE", "12_Credit_PUBLIC",
            "13_Wikipedia_Infos_about_...._PUBLIC", "14_Mots_clés_PUBLIC", "15_Taille_MB_PUBLIC", "16_Width_PUBLIC", "17_Height_PUBLIC",
            "18_Depth_PUBLIC", "19_Dpi_PUBLIC", "20_Format_PUBLIC", "21_Orientation_PUBLIC", "22_Focal_Length_PUBLIC",
            "23_Aperture_PUBLIC", "24_Aperture maxi_PUBLIC", "25_Exposure_PUBLIC", "26_Sensitivity_PUBLIC",
            "27_Mode Flash_PUBLIC", "28_Manufacturer_PUBLIC", "29_Model_PUBLIC", "30_User_Comment_INTERNE", "31_Propriétaire_PUBLIC"};

    public ExcelHandler(MainGUI parent) {
        this.parent = parent;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public void writeToFileExcel(DefaultListModel<DataModel> data) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(parent, "Can't open file\nTry to close the output file before exporting", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Sheet sheet = workbook.createSheet("Result sheet");

        Row header = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        for (int i = 1; i < champs.length; i++) {
            Cell headerCell = header.createCell(i - 1);
            headerCell.setCellValue(champs[i]);
            headerCell.setCellStyle(headerStyle);
        }

        int length = data.size();

        HashMap<String, String> hash;

        for (int i = 0; i < length; i++) {
            hash = data.get(i).getParsedData();
//            data.get(i).debug();
            Row row = sheet.createRow(i + 1);
            for (int j = 1; j < champs.length; j++) {
                String content = hash.get(champs[j]);
                if ((content == null) || (content.equals("null")))
                    continue;
                Cell cell = row.createCell(j - 1);
                cell.setCellValue(content);
            }
            parent.getProgressBar().setValue((int) (((float) i) / length * parent.getProgressBar().getMaximum() / 2) + parent.getProgressBar().getValue() + 1);
            parent.getProgressBar().setString("Parsed " + (i + 1) + " of " + length);
        }

        parent.getProgressBar().setString("Saving file...");

        workbook.write(fos);
        fos.flush();
        fos.close();
        parent.getProgressBar().setString("Finished");
    }
}