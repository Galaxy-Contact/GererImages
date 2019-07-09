package main;

import gui.MainGUI;
import model.DataModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class BrowseController implements ActionListener {

    private DefaultListModel<DataModel> data;
    private MainGUI parent;

    public BrowseController(DefaultListModel<DataModel> data, MainGUI parent) {
        this.data = data;
        this.parent = parent;
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
            try {
                loadImages(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void loadImages(Path filePath) throws IOException {
        data.clear();

        File[] directory = new File(String.valueOf(filePath)).listFiles();

        int total = directory.length;
        int current = 0;

        for (File file : directory) {
            String fullName = file.getName();
            if (fullName.lastIndexOf(".") == -1)
                continue;
            String fileName = fullName.substring(0, fullName.lastIndexOf("."));
            String fileExtension = fullName.substring(fullName.lastIndexOf(".") + 1);
            if (!fileExtension.equals("txt")) {
                DataModel newElement = new DataModel(data.size(), file.getPath(), fileName, fileExtension);
                newElement.loadInfos(newElement.getDirectory());
                data.addElement(newElement);
            }
            current ++;
            parent.getProgressBar().setValue((int) (((float) current) / total * parent.getProgressBar().getMaximum()) + 1);
            parent.getProgressBar().setString(current + " of " + total);
        }

    }
}
