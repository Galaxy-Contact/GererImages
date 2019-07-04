package gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Starter extends JFrame {
    private static JPanel pnlMain, pnlDirectory, pnlInfos, pnlButtons;
    private static JButton btnBrowse = new JButton("Parcourir");
    private static JButton btnExport = new JButton("Exporter");
    private static JList<String> listImage = new JList<>();
    private static JScrollPane listScroller;
    private static JTextArea txtInfos = new JTextArea();
    private static JLabel lblDirectory = new JLabel("Liste des images");
    private static JLabel lblInfos = new JLabel("Informations de l'image");

    private static void initComponents() {
        pnlMain = new JPanel(new GridLayout(1, 2));
        pnlDirectory = new JPanel(new BorderLayout());
        pnlInfos = new JPanel(new BorderLayout());
        pnlButtons = new JPanel(new FlowLayout());

        pnlMain.add(pnlDirectory);
        pnlMain.add(pnlInfos);

        listImage.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listScroller = new JScrollPane(listImage);

        pnlDirectory.add(lblDirectory, BorderLayout.NORTH);
        pnlDirectory.add(listScroller, BorderLayout.CENTER);
        pnlInfos.add(lblInfos, BorderLayout.NORTH);
        pnlInfos.add(txtInfos, BorderLayout.CENTER);

        txtInfos.setToolTipText("Information shown here");

        btnBrowse.setPreferredSize(new Dimension(100, 30));
        btnBrowse.setHorizontalTextPosition(JButton.CENTER);
        btnExport.setPreferredSize(new Dimension(100, 30));
        btnExport.setHorizontalTextPosition(JButton.CENTER);
        pnlButtons.add(btnBrowse);
        pnlButtons.add(btnExport);

    }

    private static void test() {
        String[] data = new String[1000];
        for (int i = 1; i < 1000; i ++)
            data[i] = i + "";
        listImage.setListData(data);
    }

    public Starter() {

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Logiciel pour gerer des images");
        setLayout(new BorderLayout());
        setSize(800,600);

        setVisible(true);

        initComponents();

        add(pnlMain, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);

        test();
    }
}
