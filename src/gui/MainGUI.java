package gui;

import main.BrowseController;
import main.ExportController;
import main.ListListener;
import model.DataModel;
import model.ExcelHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MainGUI extends JFrame {

    private HashMap<String, ArrayList<DataModel>> mapFileName = new HashMap<>();

    private ExcelHandler excel = new ExcelHandler(this);

    private JPanel pnlMain;
    private JPanel pnlButtons;
    private JButton btnBrowse = new JButton("Browse");
    private JButton btnExport = new JButton("Export");
    private DefaultListModel<DataModel> listImageData = new DefaultListModel<>();
    private JList<DataModel> listImage = new JList<>(listImageData);
    private JTextArea txtInfos = new JTextArea();
    private JLabel lblDirectory = new JLabel("List images");
    private JLabel lblInfos = new JLabel("Information");
    private BrowseController browseController = new BrowseController(listImageData, mapFileName, this);
    private ExportController exportController = new ExportController(listImageData, excel, mapFileName, this);
    private ListListener listListener = new ListListener(listImageData, txtInfos);

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    private JProgressBar progressBar = new JProgressBar(0, 1000);


    private Font fontLabel = new Font("open sans", Font.BOLD, 14);
    private Font fontText = new Font("open sans", Font.PLAIN, 12);


    public MainGUI() {

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Images management");
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocation(200, 200);

        setVisible(true);

        initComponents();

        add(pnlMain, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);

//        test();

        initEventListeners();

    }

    private void initComponents() {
        pnlMain = new JPanel(new GridLayout(1, 2));
        JPanel pnlDirectory = new JPanel(new BorderLayout());
        JPanel pnlInfos = new JPanel(new BorderLayout());
        JPanel pnlInfoCenter = new JPanel(new BorderLayout());
        pnlButtons = new JPanel(new FlowLayout());

        pnlMain.add(pnlDirectory);
        pnlMain.add(pnlInfos);

        listImage.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JScrollPane listScroller = new JScrollPane(listImage);

        JScrollPane infoScroller = new JScrollPane(txtInfos);
        txtInfos.setLineWrap(true);
        txtInfos.setToolTipText("Information shown here");

        pnlDirectory.add(lblDirectory, BorderLayout.NORTH);
        pnlDirectory.add(listScroller, BorderLayout.CENTER);
        pnlInfos.add(lblInfos, BorderLayout.NORTH);
        pnlInfos.add(pnlInfoCenter, BorderLayout.CENTER);
        pnlInfoCenter.add(infoScroller);
        progressBar.setStringPainted(true);
        pnlInfoCenter.add(progressBar, BorderLayout.SOUTH);


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
                    ((JLabel) renderer).setText((((DataModel) value).getID() + 1) + ". " + ((DataModel) value).getFileName());
                }
                return renderer;
            }
        });

        lblDirectory.setFont(fontLabel);
        lblInfos.setFont(fontLabel);
        listImage.setFont(fontText);
        txtInfos.setFont(fontText);


    }

    private void initEventListeners() {

        btnBrowse.addActionListener(browseController);
        btnExport.addActionListener(exportController);
        listImage.addMouseListener(listListener);

    }
}
