package gui;

import javax.swing.*;

public class LoadingWindow extends JWindow {
    public LoadingWindow(String message, JFrame parent) {
        JLabel lblMessage = new JLabel(message);
        lblMessage.setHorizontalTextPosition(JLabel.CENTER);
        getContentPane().add(lblMessage);
        setSize(200, 150);
        toFront();
        requestFocus();
        setLocation(parent.getLocation().x + (parent.getWidth() - getWidth()) / 2,
                parent.getLocation().y + (parent.getHeight() - getHeight()) / 2);
    }
}
