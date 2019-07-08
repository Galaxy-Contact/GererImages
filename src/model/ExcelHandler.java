package model;

import gui.MainGUI;
import main.Main;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ExcelHandler {

    private File outputFile;
    private MainGUI parent;
    private String[] champs = new String[]{"", "1_Index_INTERNE", "2_Mission_PUBLIC", "3_Référence_Nasa_avec_Titre_INTERNE",
            "4_Référence_Nasa_INTERNE", "5_Date_de_prise_de_vue_/_Traitement_de_l’image_PUBLIC",
            "6_Date_de_traitement_de_l’image_PUBLIC", "7_Référence_Galaxy_PUBLIC", "8_Référence_Nasa_ou_FL/Galaxy_INTERNE",
            "9_Référence_FL_INTERNE", "10_Titre_PUBLIC", "11_Description_PUBLIC", "12_Wikipedia_Infos_about_...._PUBLIC",
            "13_Mots_clés_PUBLIC", "14_Taille_MB_PUBLIC", "15_Width_PUBLIC", "16_Height_PUBLIC", "17_Depth_PUBLIC",
            "18_Dpi_PUBLIC", "19_Format_PUBLIC", "20_Orientation_PUBLIC", "21_Focal_Length_PUBLIC", "22_Aperture_PUBLIC",
            "23_Exposure_PUBLIC", "24_Sensitivity_PUBLIC", "25_Manufacturer_PUBLIC", "26_Model_PUBLIC",
            "27_User_Comment_INTERNE", "28_Mode_Flash_PUBLIC", "29_Aperture_Maxi_PUBLIC", "30_Propriétaire_PUBLIC", "31_OPTION_3"};

    public ExcelHandler(MainGUI parent) {
        this.parent = parent;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public void writeToFile(DefaultListModel<DataModel> data) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        FileOutputStream fos = new FileOutputStream(outputFile);

        Sheet sheet = workbook.createSheet();

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
            Row row = sheet.createRow(i + 1);
            for (int j = 1; j < champs.length; j++) {
                String content = hash.get(champs[j]);
                Cell cell = row.createCell(j - 1);
                cell.setCellValue((content != null) ? content : "");
            }
            parent.getProgressBar().setValue((int) (((float) i) / length * parent.getProgressBar().getMaximum()));
            parent.getProgressBar().setString((i + 1) + " of " + length);
        }

        for (int i = 0; i < champs.length;  i++)
            sheet.autoSizeColumn(i);

        workbook.write(fos);
        workbook.close();
    }
}
