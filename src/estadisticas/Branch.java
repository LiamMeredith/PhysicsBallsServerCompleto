package estadisticas;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;

/**
 * Esta clase extiende de un jPanel, se utiliza para mostrar la informacion de los ítems 
 * @author Toni Cifre Vicens
 */
public class Branch extends javax.swing.JPanel {

     // Numero de items en el branch
    private int count = 0;
    // Elemento falso para rellenar espacios
    private List<JLabel> ghostItem;

    /**
     * Constructor que se encarga de transformar el jpanel a transparente, instanciar el ghostItem y establece el grid layout.
     */
    public Branch() {
        initComponents();
        container.setOpaque(false);
        container.setLayout(new GridLayout(0, 1, 0,5));
        this.ghostItem = new ArrayList<JLabel>();
    }

    /**
     * Añade un itmem al JPanel.
     * @param item
     */
    public void addItem(Item item) {
        container.add(item);
        count++;
    }

    /**
     * Añade un progres item al JPanel.
     * @param item
     */
    public void addItem(ProgresItem item) {
        container.add(item);
        count++;
    }

    /**
     * Separa todos los items i los coloca uno balo otro generando items transparentes para darle el espacio adecuado.
     * @param max
     */
    public void ajustarItems(int max) {
        for (JLabel l : this.ghostItem) {
            container.remove(l);
        }
        this.ghostItem.clear();
        for (int i = max; i > count; i--) {
            JLabel lab = new JLabel("");
            lab.setBackground(Color.green);
            container.add(lab);
            this.ghostItem.add(lab);
        }
    }

   
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        container = new javax.swing.JPanel();

        container.setLayout(new java.awt.GridLayout(0, 1, 0, 5));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(container, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(container, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel container;
    // End of variables declaration//GEN-END:variables
}
