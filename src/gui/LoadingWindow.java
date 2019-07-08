package gui;

import javax.swing.*;
import java.awt.*;

public class LoadingWindow extends JFrame {
    public LoadingWindow(String message, JFrame parent) {
        JLabel lblMessage = new JLabel(message, SwingConstants.CENTER);
        add(lblMessage, BorderLayout.CENTER);
        setSize(200, 100);
        setUndecorated(true);
        setLocation(parent.getLocation().x + (parent.getWidth() - getWidth()) / 2,
                parent.getLocation().y + (parent.getHeight() - getHeight()) / 2);

    }
}
