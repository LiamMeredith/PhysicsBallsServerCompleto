/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Liam-Portatil
 */
@Entity
@Table(name = "db_ball")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DbBall.findAll", query = "SELECT d FROM DbBall d"),
    @NamedQuery(name = "DbBall.findById", query = "SELECT d FROM DbBall d WHERE d.id = :id"),
    @NamedQuery(name = "DbBall.findByX", query = "SELECT d FROM DbBall d WHERE d.x = :x"),
    @NamedQuery(name = "DbBall.findByY", query = "SELECT d FROM DbBall d WHERE d.y = :y"),
    @NamedQuery(name = "DbBall.findBySpeed", query = "SELECT d FROM DbBall d WHERE d.speed = :speed"),
    @NamedQuery(name = "DbBall.findByAccel", query = "SELECT d FROM DbBall d WHERE d.accel = :accel"),
    @NamedQuery(name = "DbBall.findByRadius", query = "SELECT d FROM DbBall d WHERE d.radius = :radius"),
    @NamedQuery(name = "DbBall.findByAngle", query = "SELECT d FROM DbBall d WHERE d.angle = :angle"),
    @NamedQuery(name = "DbBall.findByType", query = "SELECT d FROM DbBall d WHERE d.type = :type"),
    @NamedQuery(name = "DbBall.findByIdSpace", query = "SELECT d FROM DbBall d WHERE d.idSpace = :idSpace")})
public class DbBall implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "X")
    private float x;
    @Basic(optional = false)
    @Column(name = "Y")
    private float y;
    @Basic(optional = false)
    @Column(name = "speed")
    private double speed;
    @Basic(optional = false)
    @Column(name = "accel")
    private float accel;
    @Basic(optional = false)
    @Column(name = "radius")
    private float radius;
    @Basic(optional = false)
    @Column(name = "angle")
    private float angle;
    @Basic(optional = false)
    @Column(name = "TYPE")
    private Character type;
    @Basic(optional = false)
    @Column(name = "id_space")
    private int idSpace;

    public DbBall() {
    }

    public DbBall(Integer id) {
        this.id = id;
    }

    public DbBall(Integer id, float x, float y, double speed, float accel, float radius, float angle, Character type, int idSpace) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.accel = accel;
        this.radius = radius;
        this.angle = angle;
        this.type = type;
        this.idSpace = idSpace;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public float getAccel() {
        return accel;
    }

    public void setAccel(float accel) {
        this.accel = accel;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public Character getType() {
        return type;
    }

    public void setType(Character type) {
        this.type = type;
    }

    public int getIdSpace() {
        return idSpace;
    }

    public void setIdSpace(int idSpace) {
        this.idSpace = idSpace;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DbBall)) {
            return false;
        }
        DbBall other = (DbBall) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "database.DbBall[ id=" + id + " ]";
    }
    
}
