// Alex Oliva
// 6/5/2022

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Dot {
    private JLabel label;

    public Dot(int x, int y, int size) {
        label = new JLabel();
        label.setName("dot"+y);
        label.setBounds(x, y, size, size);
        label.setForeground(new Color(235,235,235));


        //reference: https://stackoverflow.com/questions/16343098/resize-a-picture-to-fit-a-jlabel
        BufferedImage img;
        try {
            img = ImageIO.read(new File("imgs\\BlackCircle.png"));
            Image dimg = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(dimg));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //
    }

    public JLabel getLabel() {
        return label;
    }
    public void setPosition(int x, int y) {
        label.setBounds(x, y, 60, 60);
    }
}
