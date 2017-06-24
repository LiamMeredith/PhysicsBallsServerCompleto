/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Toni
 */
package estadisticas;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import javax.swing.ImageIcon;
import javax.swing.JProgressBar;

public abstract class ProgresItem extends JProgressBar implements Comparable {

    protected boolean selected = false;
    protected ImageIcon normalIcon;
    protected ImageIcon selectedIcon;
    protected int index;

    public ProgresItem(int min, int max, String name) {
        super(min, max);
        super.setName(name);
        setOpaque(false);
    }

    public abstract Paint getDefaultBackgroundPaint();

    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setMaximum(int n) {
        super.setMaximum(n); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMinimum(int n) {
        super.setMinimum(n); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setValue(int n) {
        super.setValue(n); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getValue() {
        return super.getValue(); //To change body of generated methods, choose Tools | Templates.
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

    public ImageIcon getNormalIcon() {
        return normalIcon;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int compareTo(Object o) {
        ProgresItem target = (ProgresItem) o;
        if (getIndex() == target.getIndex()) {
            return 0;
        } else if (getIndex() > target.getIndex()) {
            return 1;
        } else {
            return -1;
        }
    }
}
