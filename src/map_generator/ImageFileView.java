package map_generator;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 *
 * @author Miquel Ginés
 */
public class ImageFileView extends FileView {
    ImageIcon jpgIcon = Utils.createImageIcon("images/jpg.png");
    ImageIcon gifIcon = Utils.createImageIcon("images/gif.png");
    ImageIcon pngIcon = Utils.createImageIcon("images/png.png");

    public String getName(File f) {
        return null; 
    }

    public String getDescription(File f) {
        return null; 
    }

    public Boolean isTraversable(File f) {
        return null; 
    }

    public String getTypeDescription(File f) {
        String extension = Utils.getExtension(f);
        String type = null;

        if (extension != null) {
            if (extension.equals(Utils.jpeg) ||
                extension.equals(Utils.jpg)) {
                type = "Imagen JPEG";
            } else if (extension.equals(Utils.gif)){
                type = "Imagen GIF";
            } else if (extension.equals(Utils.png)){
                type = "Imagen PNG";
            }
        }
        return type;
    }

    public Icon getIcon(File f) {
        String extension = Utils.getExtension(f);
        Icon icon = null;

        if (extension != null) {
            if (extension.equals(Utils.jpeg) ||
                extension.equals(Utils.jpg)) {
                icon = jpgIcon;
            } else if (extension.equals(Utils.gif)) {
                icon = gifIcon;
            } else if (extension.equals(Utils.png)) {
                icon = pngIcon;
            }
        }
        return icon;
    }
}
