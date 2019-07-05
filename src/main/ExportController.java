package main;

import model.DataModel;
import model.ExcelHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExportController implements ActionListener {

    private DefaultListModel<DataModel> data;
    private ExcelHandler excel;

    public ExportController(DefaultListModel<DataModel> data, ExcelHandler excel) {
        this.data = data;
        this.excel = excel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JFileChooser exportChooser = new JFileChooser();
        exportChooser.showSaveDialog(null);
        System.out.println(exportChooser.getSelectedFile());
        excel.setOutputFile(exportChooser.getSelectedFile());

        for (int i = 0; i < data.size(); i ++)
            data.get(i).parseData();

        excel.writeToFile(data);
        JOptionPane.showMessageDialog(null, "Done!");
    }
}
