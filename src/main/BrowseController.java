package main;

import model.DataModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;

import static java.nio.file.Files.newDirectoryStream;

public class BrowseController implements ActionListener {

    private Path filePath;
    private DefaultListModel<DataModel> data;

    public BrowseController(DefaultListModel<DataModel> data) {
        this.data = data;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals("Browse")) {
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int clicked = fileChooser.showOpenDialog(null);
            if (clicked == fileChooser.getApproveButtonMnemonic()) {
                filePath = fileChooser.getSelectedFile().toPath();
                try {
                    loadImages(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
            if (fileExtension.equals("txt")) {
                data.addElement(new DataModel(fileName, loadInfos(file)));
            }
        }

    }

    private String loadInfos(Path file) throws IOException {
        String res = "", line;
        File f = file.toFile();
        BufferedReader br = new BufferedReader(new FileReader(f));
        while ((line = br.readLine()) != null) {
            res += "\n" + line;
        }
        return res;
    }
}
