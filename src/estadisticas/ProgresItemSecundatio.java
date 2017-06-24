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
import javax.swing.BoundedRangeModel;

public class ProgresItemSecundatio extends ProgresItem {

    public ProgresItemSecundatio(int min, int max, String name) {
        super(min, max, name);
    }
    
    @Override
    public BoundedRangeModel getModel() {
        return model;
    }

    @Override
    public void setModel(BoundedRangeModel model) {
        this.model = model;
    }

    @Override
    public Paint getDefaultBackgroundPaint() {
        return null;
    }
}
