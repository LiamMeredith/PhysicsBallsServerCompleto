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

import java.awt.Paint;
import java.awt.event.MouseAdapter;
import javax.swing.ImageIcon;

public class ItemSecundario extends Item {

    public ItemSecundario(String text) {
        super(text);
    }

    /**
     * Devuelve un nuevo mouse adapter para sobrescribir el mouse adapter por defecto para evitar que modifique el tipo de cursor.
     * @return  Mouse adapter.
     */
    @Override
    public MouseAdapter getDefaultMouseActions() {
        return new MouseAdapter() {
        };
    }

    /**
     * Devuelve el icono por defecto del item el cual se caractesiza por ser una ayuda visual a la hora de reconocer cada item.
     * En principio dicho icono se deberia sobreescribir.
     * @return ImageIcon
     */
    @Override
    public ImageIcon getDefaultNormalIcon() {
        return new ImageIcon(this.getClass().getResource("resources/disconected.png"));
    }

     /**
     * Devuelve el icono seleccionado por defecto del item el cual se caractesiza por ser una ayuda visual a la hora de reconocer cada item.
     * En principio dicho icono se deberia sobreescribir.
     * @return ImageIcon
     */
    @Override
    public ImageIcon getDefaultSelectedIcon() {
        return new ImageIcon(this.getClass().getResource("resources/disconected.png"));
    }

    /**
     * Devuelve el background del item y lo elimina.
     * En principio dicho icono se deberia sobreescribir.
     * @return ImageIcon
     */
    @Override
    public Paint getDefaultBackgroundPaint() {
        return null;
    }
}
