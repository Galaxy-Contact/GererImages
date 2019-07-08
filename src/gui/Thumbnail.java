package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Thumbnail extends JPanel {
    private BufferedImage brImage;

    public void setImagePath(String imagePath) {
        try {
            brImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            brImage = null;
        }
        paint(this.getGraphics());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (brImage != null) {
            int locx = 0, locy = 0;
            Image newImage;
            if (this.getWidth() < this.getHeight()) {
                newImage = brImage.getScaledInstance(this.getWidth(), -1, BufferedImage.SCALE_DEFAULT);
                locy = (this.getWidth() - newImage.getWidth(this)) / 2;
            } else {
                newImage = brImage.getScaledInstance(-1, this.getHeight(), BufferedImage.SCALE_DEFAULT);
                locx = (this.getWidth() - newImage.getWidth(this)) / 2;
            }

            g.drawImage(newImage, locx, locy, this);
        } else {
            g.drawString("No image found", 10, 10);
        }
    }
}