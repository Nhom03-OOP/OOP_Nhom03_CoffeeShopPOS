package vn.edu.ptit.oop.nhom03.util;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;

public class IconLoader {
    public static ImageIcon load(String path, int width, int height) {
        URL imgUrl = IconLoader.class.getResource(path);
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }
}