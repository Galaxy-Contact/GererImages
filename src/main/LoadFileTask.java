package main;

import com.drew.imaging.ImageProcessingException;
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

public class LoadFileTask extends SwingWorker<Void, Integer> {

    private DefaultListModel<DataModel> tempData = new DefaultListModel<>(), data;
    private MainGUI parent;
    private String[] imageExtensions = new String[]{"jpg", "jpeg", "png", "bmp", "gif", "tif"};
    private HashMap<String, ArrayList<DataModel>> mapFileName;
    private HashMap<String, ArrayList<String>> mapImageText = new HashMap<>();
    private File[] dir;

    LoadFileTask(DefaultListModel<DataModel> data, HashMap<String, ArrayList<DataModel>> mapFileName, MainGUI parent, Path filePath) {
        this.mapFileName = mapFileName;
        this.parent = parent;
        dir = new File(String.valueOf(filePath)).listFiles();
        this.data = data;
    }

    public static String refFromFileName(String fileName) {
        if (fileName == null)
            return null;
        if (!fileName.contains("-"))
            return fileName;
        while (fileName.endsWith("-"))
            fileName = fileName.substring(0, fileName.length() - 1);
        String[] fileNameSeparated = fileName.split("-");
        String keyFileName = fileNameSeparated[fileNameSeparated.length - 1].trim();
        // if (keyFileName.equals(""))
        //     fileName = fileName.substring(0, fileName.length() - 1);
        try {
            if ((Integer.parseInt(keyFileName) > 0) && (Integer.parseInt(keyFileName) < 10)) {
                keyFileName = fileName.substring(0, fileName.lastIndexOf("-"));
            } else {
                keyFileName = fileName;
            }
        } catch (NumberFormatException e) {
            keyFileName = fileName;
        }
        return keyFileName.toLowerCase();
    }

    private void createFileMap() {
        int total = dir.length;
        tempData.clear();
        mapImageText.clear();
        mapFileName.clear();
        int current = 0;
        for (File file : dir) {
            String fullName = file.getName();
            if (fullName.lastIndexOf(".") == -1)
                continue;
            String fileName = fullName.substring(0, fullName.lastIndexOf("."));
            String fileExtension = fullName.substring(fullName.lastIndexOf(".") + 1);
            String refKey = refFromFileName(fileName);

            // Mapping between images and text files
            mapImageText.putIfAbsent(refKey, new ArrayList<>());
            if (!Arrays.asList(imageExtensions).contains(fileExtension)) {
//                System.out.println(fileName + " " + refKey);
                mapImageText.get(refKey).add(file.getPath());
            } else {
                // Add image to files map
                DataModel newElement = new DataModel(tempData.size(), file.getPath(), fileName, fileExtension);
                tempData.add(tempData.size(), newElement);
                mapFileName.putIfAbsent(refKey, new ArrayList<>());
                mapFileName.get(refKey).add(newElement);
            }
            current++;
            publish(current, total, 1);
        }

    }

    private void crawlFromTextFiles() {
        int length = tempData.size();
        int current = 0;
        for (int i = 0; i < length; i ++) {
            DataModel dm = tempData.get(i);
            String refKey = refFromFileName(dm.getFileName());
            dm.setTextFiles(mapImageText.get(refKey));
            try {
                dm.loadInfos();
                dm.parseImage();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.getMessage());
            } catch (ImageProcessingException e) {
                e.printStackTrace();
            }
            current++;
            publish(current, length, 2);
        }
    }

    @Override
    protected Void doInBackground() {
        if (dir == null)
            return null;

        createFileMap();
        crawlFromTextFiles();

        System.out.println(tempData.size());

        return null;
    }

    @Override
    protected void process(List<Integer> chunks) {
        int current = chunks.get(chunks.size() - 3);
        int total = chunks.get(chunks.size() - 2);
        int type = chunks.get(chunks.size() - 1);
        parent.getProgressBar().setValue((int) (((float) current) / total * parent.getProgressBar().getMaximum()));
        switch (type) {
            case 1:
                parent.getProgressBar().setString("Analyzing files map...");
                break;
            case 2:
                parent.getProgressBar().setString("Parsing text data " + current + " of " + total);
                break;
        }
    }

    @Override
    protected void done() {
        parent.getProgressBar().setString("Updating UI...");
        data.clear();
        for (int i = 0; i < tempData.size(); i++)
            data.add(i, tempData.get(i));
        parent.getProgressBar().setString("Done loading");
    }
}
