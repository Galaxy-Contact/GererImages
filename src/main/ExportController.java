package main;

import com.drew.imaging.ImageProcessingException;
import gui.LoadingWindow;
import model.DataModel;
import model.ExcelHandler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class ExportController implements ActionListener {

    private DefaultListModel<DataModel> data;
    private ExcelHandler excel;
    private JFrame parent;

    public ExportController(DefaultListModel<DataModel> data, ExcelHandler excel, JFrame parent) {
        this.data = data;
        this.excel = excel;
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        LoadingWindow loading = new LoadingWindow("Saving to excel...", parent);
        loading.setVisible(true);

        JFileChooser exportChooser = new JFileChooser(".");
        exportChooser.setFileFilter(new FileNameExtensionFilter("Excel File", "xlsx"));
        exportChooser.setSelectedFile(new File("output.xlsx"));
        int clicked = exportChooser.showSaveDialog(loading);


        if (clicked != exportChooser.getApproveButtonMnemonic()) {
            loading.dispose();
            return;
        }

        if (exportChooser.getSelectedFile().toString().endsWith("xlsx"))
            excel.setOutputFile(exportChooser.getSelectedFile());
        else
            excel.setOutputFile(new File(exportChooser.getSelectedFile().toString() + ".xlsx"));
        for (int i = 0; i < data.size(); i ++) {
            data.get(i).parseData();
            try {
                data.get(i).parseImage();
            } catch (ImageProcessingException | IOException e) {
                e.printStackTrace();
            }
        }

        try {
            excel.writeToFile(data);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, "ERROR " + e.getMessage());
        }
        loading.dispose();
    }
}
