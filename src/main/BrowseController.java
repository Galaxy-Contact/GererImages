package main;

import gui.LoadingWindow;
import model.DataModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;

import static java.nio.file.Files.newDirectoryStream;

public class BrowseController implements ActionListener {

    private DefaultListModel<DataModel> data;
    private JFrame parent;

    public BrowseController(DefaultListModel<DataModel> data, JFrame parent) {
        this.data = data;
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        LoadingWindow loading = new LoadingWindow("Loading images...", parent);
        loading.setVisible(true);


        int clicked = fileChooser.showOpenDialog(null);

        if (fileChooser.getSelectedFile() == null) {
            loading.dispose();
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

        loading.dispose();

    }

    private void loadImages(Path filePath) throws IOException {

        data.clear();
        DirectoryStream<Path> stream = newDirectoryStream(filePath);
        for (Path file : stream) {
            String fullName = file.getFileName().toString();
            if (fullName.lastIndexOf(".") == -1)
                continue;
            String fileName = fullName.substring(0, fullName.lastIndexOf("."));
            String fileExtension = fullName.substring(fullName.lastIndexOf(".") + 1);
            if (!fileExtension.equals("txt")) {
                DataModel newElement = new DataModel(data.size(), file.toString(), fileName, fileExtension);
                newElement.loadInfos(newElement.getDirectory());
                data.addElement(newElement);
            }
        }

    }
}
