package gui;

import javax.swing.*;

public class LoadingWindow extends JFrame {
    public LoadingWindow(String message, JFrame parent) {
        JLabel lblMessage = new JLabel(message);
        lblMessage.setHorizontalTextPosition(JLabel.CENTER);
        getContentPane().add(lblMessage);
        setLocation(parent.getLocation().x + (parent.getWidth() - getWidth()) / 2,
                parent.getLocation().y + (parent.getHeight() - getHeight()) / 2);
        pack();
    }
}
