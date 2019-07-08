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

public class ExportController implements ActionListener {

    private DefaultListModel<DataModel> data;
    private ExcelHandler excel;
    private MainGUI parent;

    public ExportController(DefaultListModel<DataModel> data, ExcelHandler excel, MainGUI parent) {
        this.data = data;
        this.excel = excel;
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        new Thread(() -> {
            JFileChooser exportChooser = new JFileChooser(".");
            exportChooser.setFileFilter(new FileNameExtensionFilter("Excel File", "xlsx"));
            exportChooser.setSelectedFile(new File("output.xlsx"));
            int clicked = exportChooser.showSaveDialog(parent);



            if (clicked != exportChooser.getApproveButtonMnemonic()) {
                return;
            }

            if (exportChooser.getSelectedFile().toString().endsWith("xlsx"))
                excel.setOutputFile(exportChooser.getSelectedFile());
            else
                excel.setOutputFile(new File(exportChooser.getSelectedFile().toString() + ".xlsx"));

            int length = data.size();

            for (int i = 0; i < length; i ++) {
                data.get(i).parseData();
                try {
                    data.get(i).parseImage();
                } catch (ImageProcessingException | IOException e) {
                    e.printStackTrace();
                }
                parent.getProgressBar().setValue((int) (((float) i) / length * parent.getProgressBar().getMaximum() / 2));
                parent.getProgressBar().setString((i + 1) + " of " + length);
            }

            try {
                excel.writeToFile(data);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "ERROR " + e.getMessage());
            }
        }).start();
    }
}
