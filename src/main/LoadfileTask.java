package main;

import gui.MainGUI;
import model.DataModel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LoadfileTask extends SwingWorker<Void, Integer> {

    DefaultListModel<DataModel> tempData = new DefaultListModel<>(), data;
    private MainGUI parent;
    private String[] imageExtensions = new String[]{"jpg", "jpeg", "png", "bmp", "gif", "tif"};
    private HashMap<String, ArrayList<DataModel>> mapFileName;
    File[] dir;
    int total;

    public LoadfileTask(DefaultListModel<DataModel> data, HashMap<String, ArrayList<DataModel>> mapFileName, MainGUI parent, Path filePath) {
        this.mapFileName = mapFileName;
        this.parent = parent;
        dir = new File(String.valueOf(filePath)).listFiles();
        this.data = data;
    }

    public static String getKeyFileName(String fileName) {
        if (fileName == null)
            return null;
        if (!fileName.contains("-"))
            return fileName;
        while (fileName.endsWith("-"))
            fileName = fileName.substring(0, fileName.length() - 1);
        String[] fileNameSeparated = fileName.split("-");
        String keyFileName = fileNameSeparated[fileNameSeparated.length - 1];
        if (keyFileName.equals(""))
            fileName = fileName.substring(0, fileName.length() - 1);
        try {
            if (Integer.parseInt(keyFileName) < 10) {
                keyFileName = fileName.substring(0, fileName.lastIndexOf("-"));
            } else {
                keyFileName = fileName;
            }
        } catch (NumberFormatException e) {
            keyFileName = fileName;
        }
        return keyFileName.toLowerCase();
    }

    @Override
    protected Void doInBackground() {
        if (dir == null)
            return null;
        total = dir.length;
        tempData.clear();
        int current = 0;
        for (File file : dir) {
            String fullName = file.getName();
            if (fullName.lastIndexOf(".") == -1)
                continue;
            String fileName = fullName.substring(0, fullName.lastIndexOf("."));
            String fileExtension = fullName.substring(fullName.lastIndexOf(".") + 1);
            if (Arrays.asList(imageExtensions).contains(fileExtension)) {
                DataModel newElement = new DataModel(tempData.size(), file.getPath(), fileName, fileExtension);
                try {
                    newElement.loadInfos(newElement.getDirectory());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(parent, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                tempData.addElement(newElement);
                if (!mapFileName.containsKey(getKeyFileName(fileName)))
                    mapFileName.put(getKeyFileName(fileName), new ArrayList<>());
                mapFileName.get(getKeyFileName(fileName)).add(newElement);
            }
            current++;
            publish(current);
        }

        System.out.println(tempData.size());

        return null;
    }

    @Override
    protected void process(List<Integer> chunks) {
        int current = chunks.get(chunks.size() - 1);
        parent.getProgressBar().setValue((int) (((float) current) / total * parent.getProgressBar().getMaximum()) + 1);
        parent.getProgressBar().setString("Loading " + current + " of " + total);
    }

    @Override
    protected void done() {
        data.clear();
        for (int i = 0; i < tempData.size(); i++)
            data.add(i, tempData.get(i));
    }
}
