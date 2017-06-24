package estadisticas;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.MouseAdapter;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Clase que extiende de JPanel y posee la principales caracteristicas que heredaran tanto los items principales como los secundarios.
 * Posee todos los metodos para tratar el estdo del item, sus iconos y su estilo.
 * 
 * @author Toni Cifre Vicens
 */
public abstract class Item extends JLabel implements Comparable {

    /**
     * Booleas que define si el item esta seleccionado o no.
     */
    protected boolean selected = false;

    /**
     * Icono al no estar seleccionado.
     */
    protected ImageIcon normalIcon;

    /**
     * Icono al estar seleccionado.
     */
    protected ImageIcon selectedIcon;

    /**
     * Su posicion.
     */
    protected int index;

    /**
     * Constructor que inicializa el texto del item, setea si esta seleccionado, y añade un mouse listener.
     * @param text
     */
    public Item(String text) {
        super(text);
        setOpaque(false);
        addMouseListener(getDefaultMouseActions());
        setNormalIcon(getDefaultNormalIcon());
        setSelectedIcon(getDefaultSelectedIcon());
        setSelected(false);
    }

    public abstract MouseAdapter getDefaultMouseActions();

    /**
     * Devuelve el icono del item al no estar seleccionado.
     * @return
     */
    public abstract ImageIcon getDefaultNormalIcon();

    /**
     * Devuelve el icono del item al estar seleccionado.
     * @return
     */
    public abstract ImageIcon getDefaultSelectedIcon();

    /**
     * Devuelve el backgroun del jPanel.
     * @return
     */
    public abstract Paint getDefaultBackgroundPaint();

    /**
     * Cambia el valor del estado seleccionado.
     */
    public final void cambiarEstado() {
        setSelected(!isSelected());
    }

    /**
     * Sevuelve el valor del estado.
     * @return Boolean Selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Establece el valor del estado por el pasado por parametros.
     * @param selected Boolean col el valor del estado.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            setIcon(selectedIcon);
            setFont(getFont().deriveFont(Font.BOLD));
        } else {
            setIcon(normalIcon);
            setFont(getFont().deriveFont(Font.PLAIN));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getDefaultBackgroundPaint() != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(getDefaultBackgroundPaint());
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        super.paintComponent(g);
    }

    /**
     * Devuelve el icono del estado no seleccionado.
     * @return Icono del estado seleccionado.
     */
    public ImageIcon getNormalIcon() {
        return normalIcon;
    }

    /**
     * Establece el icono del estado no seleccionado.
     * @param normalIcon Icono a setear.
     */
    public void setNormalIcon(ImageIcon normalIcon) {
        this.normalIcon = normalIcon;
        setSelected(selected);
    }

    /**
     * Devuelve el icono del estado seleccionado.
     * @return icono del estado seleccionado.
     */
    public ImageIcon getSelectedIcon() {
        return selectedIcon;
    }

    /**
     * Establece el icono del estado seleccionado.
     * @param selectedIcon icono a setear.
     */
    public void setSelectedIcon(ImageIcon selectedIcon) {
        this.selectedIcon = selectedIcon;
         setSelected(selected);
    }

    /**
     * Devuelve el index del item.
     * @return int con el valor del item.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Establece el valor del item.
     * @param index int con el valor del item.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int compareTo(Object o) {
        Item target = (Item) o;
        if (getIndex() == target.getIndex()) {
            return 0;
        } else if (getIndex() > target.getIndex()) {
            return 1;
        } else {
            return -1;
        }
    }
}
