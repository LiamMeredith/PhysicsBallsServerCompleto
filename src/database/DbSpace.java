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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author pepe
 */
@Entity
@Table(name = "db_space")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DbSpace.findAll", query = "SELECT d FROM DbSpace d")
    , @NamedQuery(name = "DbSpace.findById", query = "SELECT d FROM DbSpace d WHERE d.id = :id")
    , @NamedQuery(name = "DbSpace.findByName", query = "SELECT d FROM DbSpace d WHERE d.name = :name")
    , @NamedQuery(name = "DbSpace.findByGravityX", query = "SELECT d FROM DbSpace d WHERE d.gravityX = :gravityX")
    , @NamedQuery(name = "DbSpace.findByGravityY", query = "SELECT d FROM DbSpace d WHERE d.gravityY = :gravityY")
    , @NamedQuery(name = "DbSpace.findByFriction", query = "SELECT d FROM DbSpace d WHERE d.friction = :friction")
    , @NamedQuery(name = "DbSpace.findByItemX", query = "SELECT d FROM DbSpace d WHERE d.itemX = :itemX")
    , @NamedQuery(name = "DbSpace.findByItemY", query = "SELECT d FROM DbSpace d WHERE d.itemY = :itemY")
    , @NamedQuery(name = "DbSpace.findByItemMass", query = "SELECT d FROM DbSpace d WHERE d.itemMass = :itemMass")})
public class DbSpace implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Lob
    @Column(name = "background")
    private byte[] background;
    @Lob
    @Column(name = "ball_n")
    private byte[] ballN;
    @Lob
    @Column(name = "ball_e")
    private byte[] ballE;
    @Lob
    @Column(name = "ball_b")
    private byte[] ballB;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "gravity_x")
    private Float gravityX;
    @Column(name = "gravity_y")
    private Float gravityY;
    @Column(name = "friction")
    private Float friction;
    @Column(name = "item_x")
    private Float itemX;
    @Column(name = "item_y")
    private Float itemY;
    @Column(name = "item_mass")
    private Float itemMass;

    public DbSpace() {
    }

    public DbSpace(Integer id) {
        this.id = id;
    }

    public DbSpace(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getBackground() {
        return background;
    }

    public void setBackground(byte[] background) {
        this.background = background;
    }

    public byte[] getBallN() {
        return ballN;
    }

    public void setBallN(byte[] ballN) {
        this.ballN = ballN;
    }

    public byte[] getBallE() {
        return ballE;
    }

    public void setBallE(byte[] ballE) {
        this.ballE = ballE;
    }

    public byte[] getBallB() {
        return ballB;
    }

    public void setBallB(byte[] ballB) {
        this.ballB = ballB;
    }

    public Float getGravityX() {
        return gravityX;
    }

    public void setGravityX(Float gravityX) {
        this.gravityX = gravityX;
    }

    public Float getGravityY() {
        return gravityY;
    }

    public void setGravityY(Float gravityY) {
        this.gravityY = gravityY;
    }

    public Float getFriction() {
        return friction;
    }

    public void setFriction(Float friction) {
        this.friction = friction;
    }

    public Float getItemX() {
        return itemX;
    }

    public void setItemX(Float itemX) {
        this.itemX = itemX;
    }

    public Float getItemY() {
        return itemY;
    }

    public void setItemY(Float itemY) {
        this.itemY = itemY;
    }

    public Float getItemMass() {
        return itemMass;
    }

    public void setItemMass(Float itemMass) {
        this.itemMass = itemMass;
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
        if (!(object instanceof DbSpace)) {
            return false;
        }
        DbSpace other = (DbSpace) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "database.DbSpace[ id=" + id + " ]";
    }
    
}
