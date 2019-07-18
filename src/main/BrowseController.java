package main;

import gui.MainGUI;
import model.DataModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class BrowseController implements ActionListener {

    private DefaultListModel<DataModel> data;
    private MainGUI parent;
    private HashMap<String, ArrayList<DataModel>> mapFileName;

    public BrowseController(DefaultListModel<DataModel> data, HashMap<String, ArrayList<DataModel>> mapFileName, MainGUI parent) {
        this.data = data;
        this.parent = parent;
        this.mapFileName = mapFileName;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int clicked = fileChooser.showOpenDialog(null);

        if (fileChooser.getSelectedFile() == null) {
            return;
        }


        if (clicked == fileChooser.getApproveButtonMnemonic()) {
            Path filePath = fileChooser.getSelectedFile().toPath();
            LoadFileTask loadTask = new LoadFileTask(data, mapFileName, parent, filePath);
            loadTask.execute();
        }

    }

}