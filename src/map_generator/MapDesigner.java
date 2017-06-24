package map_generator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Miquel Ginés
 */
public class MapDesigner extends JFrame {
    
    public MapDesigner() throws IOException {
        super("PhysicsBalls - Map designer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new Designer(this));
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new MapDesigner();
        } catch (IOException ex) {
            Logger.getLogger(MapDesigner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
