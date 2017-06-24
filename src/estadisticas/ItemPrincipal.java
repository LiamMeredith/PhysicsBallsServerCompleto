package estadisticas;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;

/**
 * Extiende de la clase item i posee todas las caracteristicas que lo diferencian del item secundario
 * @author Toni Cifre Vicens
 */
public class ItemPrincipal extends Item {

    private Branch branchPanel;

    /**
     * El contructor setea el texto a mostrar y instancia un branch que extiende de jPanel para introducir todas sus subramas y mostrarlas al ser seleccionado.
     * @param text Texto Principal del Item.
     */
    public ItemPrincipal(String text) {
        super(text);
        this.branchPanel = new Branch();
    }

    /**
     * Devuelve un mouse adapter el cual al pasar por encima de dicho item pasara el tipo de cursor a hand cursor.
     * @return Mous adapter
     */
    @Override
    public MouseAdapter getDefaultMouseActions() {
        return new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        };
    }

    /**
     * Sobreescribe el metodo de la clase de Item. Devuelve un icono que muestra si el item esta desplegado o no.
     * @return Icono no seleccionado.
     */
    @Override
    public ImageIcon getDefaultNormalIcon() {
        return new ImageIcon(this.getClass().getResource("resources/list_plus.png"));
    }
    
    /**
     * Sobreescribe el metodo de la clase de Item. Devuelve un icono que muestra si el item esta desplegado o no.
     * @return Icono seleccionado.
     */
    @Override
    public ImageIcon getDefaultSelectedIcon() {
        return new ImageIcon(this.getClass().getResource("resources/list_minus.png"));
    }

    /**
     * Modifica el background dependiendo de si el estado del item es seleccionado o no.
     * @return Background.
     */
    @Override
    public Paint getDefaultBackgroundPaint() {
        Color c1, c2;
        if (isSelected()) {
            c2 = getBackground();
            c1 = c2.darker();
        } else {
            c1 = getBackground();
            c2 = c1.darker();
        }
        return new GradientPaint(0, 0, c1, 0, getHeight(), c2);
    }

    /**
     * Devuelve el branch del item.
     * @return Branch.
     */
    public Branch getBranchPanel() {
        return branchPanel;
    }
}
