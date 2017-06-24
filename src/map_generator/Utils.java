package map_generator;

import java.io.File;
import javax.swing.ImageIcon;

public class Utils {

    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String png = "png";

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Utils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    protected static map_generator.items.Ball conversor(org.physicballs.items.Ball b) {
        map_generator.items.Ball ball = new map_generator.items.Ball(b.getX(), b.getY(), b.getSpeedx(), b.getSpeedy(), b.getRadius(), String.valueOf(b.getType().name().charAt(0)));
        ball.setAccel(b.getAccel());
        return ball;
    }

    protected static map_generator.items.Obstacle conversor(org.physicballs.items.Obstacle b) {
        map_generator.items.Obstacle obstacle = new map_generator.items.Obstacle(b.getX(), b.getY(), b.getWidth(), b.getHeight());
        return obstacle;
    }

    protected static map_generator.items.StopItem conversor(org.physicballs.items.StopItem b) {
        map_generator.items.StopItem stopItem = new map_generator.items.StopItem(b.getX(), b.getY(), b.getWidth(), b.getHeight());
        return stopItem;
    }
}
