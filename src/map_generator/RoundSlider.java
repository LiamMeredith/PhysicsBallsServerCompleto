package map_generator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;

/**
 *
 * @author Miquel Ginés
 */
public class RoundSlider extends JComponent implements MouseListener, MouseMotionListener {

    private static final int radius = 25;
    private static final int spotRadius = 5;

    private double theta;
    private Color knobColor;
    private Color spotColor;

    private boolean pressedOnSpot;

    public RoundSlider() {
        this(0);
    }

    public RoundSlider(double initTheta) {
        this(initTheta, Color.gray, Color.black);
    }

    public RoundSlider(double initTheta, Color initKnobColor, Color initSpotColor) {
        theta = initTheta;
        pressedOnSpot = false;
        knobColor = initKnobColor;
        spotColor = initSpotColor;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(knobColor);
        g2d.fillOval(0, 0, 2 * radius, 2 * radius);
        Point pt = getSpotCenter();
        int xc = (int) pt.getX();
        int yc = (int) pt.getY();
        g2d.setColor(spotColor);
        g2d.fillOval(xc - spotRadius, yc - spotRadius, 2 * spotRadius, 2 * spotRadius);
        g2d.drawLine(radius, radius, xc, yc);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(2 * radius, 2 * radius);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(2 * radius, 2 * radius);
    }

    public double getAngle() {
        double ret = 0;
        if (theta >= 0 && theta < 1.5708001) {
            ret = 1.5708001 - theta;
        } else if (theta < 0 && theta >= -1.5708001) {
            ret = 1.5708001 + (theta * -1);
        } else if (theta >= 1.5708001 && theta <= 3.14159001) {
            ret = 6.28319 - (theta - 1.5708001);
        } else if (theta < -1.5708001 && theta >= -3.14159001) {
            ret = 3.14159001 + ((theta * -1) - 1.5708001);
        }
        //return theta;
        return ret;
    }

    private Point getSpotCenter() {
        int r = radius - spotRadius;
        int xcp = (int) (r * Math.sin(theta));
        int ycp = (int) (r * Math.cos(theta));
        int xc = radius + xcp;
        int yc = radius - ycp;
        return new Point(xc, yc);
    }
    
    private boolean isOnSpot(Point pt) {
        return (pt.distance(getSpotCenter()) < spotRadius);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point mouseLoc = e.getPoint();
        pressedOnSpot = isOnSpot(mouseLoc);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressedOnSpot = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (pressedOnSpot) {

            int mx = e.getX();
            int my = e.getY();

            int mxp = mx - radius;
            int myp = radius - my;

            theta = Math.atan2(mxp, myp);

            repaint();
        }
    }
}
