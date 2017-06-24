package map_generator.items;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Liam-Portatil
 */
public class Obstacle extends Item {

    /**
     * Global parameters
     */
    protected float width;
    protected float height;

//    private Space parent;
    /**
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param parent
     */
    public Obstacle(float x, float y, float width, float height) {
        super(x, y, width * height / 2, Color.LIGHT_GRAY);
        this.width = width;
        this.height = height;
    }
    
    public Obstacle(float x, float y, float width, float height, Color color) {
        super(x, y, width * height / 2, color);
        this.width = width;
        this.height = height;
    }

    public Obstacle() {
    }

    /**
     * Draw the ball in the graphics context g. Note: The drawing color in g is
     * changed to the color of the ball.
     *
     * @param g
     */
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect((int) posX, (int) posY, (int) width, (int) height);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public synchronized boolean intersects(Ball b) {
        //distancia entre ejes del centro de la bola y del obstaculo
        double distX = Math.abs(b.getX() - posX - width / 2);
        double distY = Math.abs(b.getY() - posY - height / 2);

        //Si la distancia es mayor no colisiona
        if (distX > (width / 2 + b.getRadius())) {
            return false;
        }
        if (distY > (height / 2 + b.getRadius())) {
            return false;
        }
        
        //Si es menor colisiona
        if (distX <= width / 2) {
            return true;
        }
        if (distY <= height / 2) {
            return true;
        }

        //Se calcula si choca con una esquina
        double dx = distX - width / 2;
        double dy = distY - height / 2;
        return dx * dx + dy * dy <= b.getRadius() * b.getRadius();
    }
}
