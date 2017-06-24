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
@Table(name = "db_obstacle")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DbObstacle.findAll", query = "SELECT d FROM DbObstacle d"),
    @NamedQuery(name = "DbObstacle.findById", query = "SELECT d FROM DbObstacle d WHERE d.id = :id"),
    @NamedQuery(name = "DbObstacle.findByX", query = "SELECT d FROM DbObstacle d WHERE d.x = :x"),
    @NamedQuery(name = "DbObstacle.findByY", query = "SELECT d FROM DbObstacle d WHERE d.y = :y"),
    @NamedQuery(name = "DbObstacle.findByWidth", query = "SELECT d FROM DbObstacle d WHERE d.width = :width"),
    @NamedQuery(name = "DbObstacle.findByHeight", query = "SELECT d FROM DbObstacle d WHERE d.height = :height"),
    @NamedQuery(name = "DbObstacle.findByIdSpace", query = "SELECT d FROM DbObstacle d WHERE d.idSpace = :idSpace")})
public class DbObstacle implements Serializable {

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
    @Column(name = "width")
    private float width;
    @Basic(optional = false)
    @Column(name = "height")
    private float height;
    @Basic(optional = false)
    @Column(name = "id_space")
    private int idSpace;

    public DbObstacle() {
    }

    public DbObstacle(Integer id) {
        this.id = id;
    }

    public DbObstacle(Integer id, float x, float y, float width, float height, int idSpace) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
        if (!(object instanceof DbObstacle)) {
            return false;
        }
        DbObstacle other = (DbObstacle) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "database.DbObstacle[ id=" + id + " ]";
    }
    
}
