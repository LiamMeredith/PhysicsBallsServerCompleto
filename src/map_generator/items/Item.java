package map_generator.items;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;

/**
 *
 * @author Oriol
 */
public class Item {
    
    protected float posX;
    protected float posY;
    protected float mass;
    protected Color color;

    public Item(float posX, float posY, float mass, Color color) {
        this.posX = posX;
        this.posY = posY;
        this.mass = mass;
        this.color = color;
    }
    
    public Item(){}
    
    public float getX() {
        return posX;
    }

    public void setX(float posX) {
        this.posX = posX;
    }

    public float getY() {
        return posY;
    }

    public void setY(float posY) {
        this.posY = posY;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    
}
