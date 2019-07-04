package gui;

import main.BrowseController;
import model.DataModel;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {

    private JPanel pnlMain, pnlDirectory, pnlInfos, pnlButtons;
    private JButton btnBrowse = new JButton("Browse");
    private JButton btnExport = new JButton("Export");
    private DefaultListModel<DataModel> listImageData = new DefaultListModel<>();
    private JList listImage = new JList(listImageData);
    private JScrollPane listScroller;
    private JTextArea txtInfos = new JTextArea();
    private JLabel lblDirectory = new JLabel("List images");
    private JLabel lblInfos = new JLabel("Information");
    private BrowseController controller = new BrowseController(listImageData);

    private void test() {

    }

    public MainGUI() {

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Images management");
        setLayout(new BorderLayout());
        setSize(800,600);

        setVisible(true);

        initComponents();

        add(pnlMain, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);

//        test();

        initEventListeners();

    }

    private void initComponents() {
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

        listImage.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (renderer instanceof JLabel && value instanceof DataModel) {
                    ((JLabel) renderer).setText(((DataModel) value).getFileName());
                }
                return renderer;
            }
        });

    }

    private void initEventListeners() {

        btnBrowse.addActionListener(controller);
        btnExport.addActionListener(controller);

    }
}
