package main;

import model.DataModel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ListListener extends MouseAdapter {

    private DefaultListModel<DataModel> data;
    private JTextArea txtInfos;

    public ListListener(DefaultListModel<DataModel> data, JTextArea txtInfos) {
        this.data = data;
        this.txtInfos = txtInfos;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int index = ((JList) e.getSource()).getSelectedIndex();
        if (index < 0)
            return;
        txtInfos.setText(data.get(index).getInfos());
    }
}
