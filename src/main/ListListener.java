package main;

import gui.Thumbnail;
import model.DataModel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ListListener extends MouseAdapter {

    private Thumbnail thumbnail;
    private DefaultListModel<DataModel> data;
    private JTextArea txtInfos;

    public ListListener(DefaultListModel<DataModel> data, Thumbnail thumbnail, JTextArea txtInfos) {
        this.thumbnail = thumbnail;
        this.data = data;
        this.txtInfos = txtInfos;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int index = ((JList) e.getSource()).getSelectedIndex();
        if (index < 0)
            return;
        String imagePath = data.get(index).getDirectory().replaceAll(".txt", data.get(index).getImageExtension());
        System.out.println(imagePath);
        thumbnail.setImagePath(imagePath);
        txtInfos.setText(data.get(index).getInfos());
    }
}
