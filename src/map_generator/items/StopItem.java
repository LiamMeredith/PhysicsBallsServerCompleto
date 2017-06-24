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
public class StopItem extends Obstacle{

    /**
     * Global parameters
     */


    private boolean occupied;

    Ball b = null;

    /**
     * Main constructor
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param parent
     */
    public StopItem(float x, float y, float width, float height) {
        super(x, y, width, height, Color.RED);
        this.occupied = false;
    }
    
    public StopItem(){}



    public synchronized void notifyBalls(){
            this.b = null;
            occupied = false;
            notifyAll();
    }
    
    /**
     * Draw the ball in the graphics context g. Note: The drawing color in g is
     * changed to the color of the ball.
     *
     */
    @Override
    public void draw(Graphics g) {
        if (this.occupied) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.MAGENTA);
        }
        g.fillRect((int) posX, (int) posY, (int) width, (int) height);
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean ocuped) {
        this.occupied = ocuped;
    }

    public void setOccupied(boolean ocuped, Ball b) {
        this.occupied = ocuped;
        this.b = b;
    }

    public void setBall(Ball b) {
        this.b = b;
    }

    public Ball getBall() {
        return this.b;
    }

    public Ball getB() {
        return b;
    }

    public void setB(Ball b) {
        this.b = b;
    }
    
}
