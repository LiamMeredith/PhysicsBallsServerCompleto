package estadisticas;


import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.border.BevelBorder;
import org.physicballs.items.StatisticsData;

/**
 * Clase principal, dedicada a la recoleccion y representacion de los datos enviados al servidor desde las simulaciones.
 * Se basa en recibir las estadisticas de una en una, leer la informacion de la pantalla de la qual proceden e ir pasando la informacion a la zona que le corresponda, a la misma vez que van introduciendo la informacion a las estadisticas generales.
 * 
 * @author Toni Cifre Vicens
 */
public final class Estadisticas extends javax.swing.JFrame {

    private final Menu menu, menuGeneral;
    private ArrayList<StatisticsData> listDataStatistics = new ArrayList();
    
    private float velocitat = 0;
    private int totalBolles = 0, ultimNBolles = 0;
    private final ArrayList<Integer> pantalles = new ArrayList<>();
    int pos=0;

    /**
     * El constructor inicializa los componentes del JFrame, setea el tamaño e instancia el menu geneneral con todas sus caracteristicas y el menu donde se iran mostrando todas las estadisticas por separado de cada pantalla que se conecte.
     * 
     */
    public Estadisticas() {
        initComponents();
        setSize(700, 600);
        menuGeneral = new Menu();
        crearEstructuraDelMenuGeneral();
        menuGeneral.setBackground(new Color(111, 111, 111));
        menuGeneral.setForeground(Color.white);
        menuGeneral.setFont(new Font("monospaced", Font.PLAIN, 17));
        menuGeneral.setItemPrincipalBorde(new BevelBorder(BevelBorder.RAISED));
        menuGeneral.setIcon("menuGeneral", "resources/statistics.png");
        panel.add(menuGeneral);

        menu = new Menu();
        panel.add(menu);
    }

    /**
     * Este metodo es llamado desde el modulo visual del servidor al recibir una estadistica.
     * Si la información procede de una pantalla nunca anten recibiida añade una nueva rama al menu i setea su informacíón, si ya conoce la pantalla de la cual procede imprime la información a la rama correspondiente del menu.
     * Posee una condicional que comprueba que los datos recibidos son secuenciales para que las estadisticas generales sean reales por si se envia dos vezes segisas la informacion de una misma simulasión.
     * 
     * @param data Clase que contene la información actualizada de cada simulacion recibida por el servidor.
     */
    public void setData(StatisticsData data) {
        try {
            ItemPrincipal isMenu = menu.getMenu("menu" + data.nPantalla);
            if (isMenu == null) {
                addMenu(data);
                menu.setBackground(new Color(111, 111, 111));
                menu.setForeground(Color.white);
                menu.setFont(new Font("monospaced", Font.PLAIN, 15));
                menu.setItemPrincipalBorde(new BevelBorder(BevelBorder.RAISED));
                menu.repaint();
                pantalles.add(data.nPantalla);
            }
            if(velocitat < data.velocitatM){
                velocitat = data.velocitatM;
                menu.setProgresMaxValue((int)velocitat*100);
            }
            float ec = (float) (0.5 * data.velocitat * data.velocitat * data.massa);
            menu.setText("velocitat" + data.nPantalla, "Velocitat: " + String.valueOf(data.velocitat) + " m/s");
            menu.setText("velocitatM" + data.nPantalla, "Velocitat mitjana: " + String.valueOf(data.velocitatM) + " m/s");
            menu.setProgres("bar" + data.nPantalla, (int) (data.velocitatM * 100));
            menu.setText("acceleracio" + data.nPantalla, "Acceleració: " + String.valueOf(data.acceleracio) + " m/s2");
            menu.setText("acceleracioM" + data.nPantalla, "Acceleració mitjana: " + String.valueOf(data.acceleracioM) + " m/s2");
            menu.setText("massa" + data.nPantalla, "Massa: " + String.valueOf(data.massa) + " Kg");
            menu.setText("massaM" + data.nPantalla, "Massa mitjana: " + String.valueOf(data.massaM) + " Kg");
            menu.setText("bolles" + data.nPantalla, "N Bolles: " + String.valueOf(data.nBolles));
            menu.setText("EC" + data.nPantalla, "Ec: " + String.valueOf(ec) + " J");
            if (data.nPantalla == pantalles.get(pos)) {
                listDataStatistics.add(data);
                pos++;
                if(pos >= pantalles.size()){
                    pos=0;
                    listDataStatistics = setGeneralStatistics(listDataStatistics);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error setData\n " + ex);
            ex.printStackTrace();
        }
    }

    /**
     * Crea una nueva rama en el menu de estadisticas con un nombre unico basada en el numero de pantalla y le introduce la informacion pasada a traves del objeto de estadisticas.
     * Cada subrama posee un identificador unico y un icono.
     * @param data objeto que posee la informacion de un momento concreto de la simulacion de una pantalla.
     */
    public void addMenu( StatisticsData data) {
        menu.añadirNuevoItemPrincipal("menu" + data.nPantalla, "estadistiques " + data.nPantalla);
        menu.añadirItemSecundario("menu" + data.nPantalla, "velocitat" + data.nPantalla, "Velocitat: " + data.velocitat);
        menu.añadirItemSecundario("menu" + data.nPantalla, "velocitatM" + data.nPantalla, "Velocitat: " + data.velocitatM);
        menu.añadirItemSecundarioProgre("menu" + data.nPantalla, 0, 0, (int)velocitat*100, "bar" + data.nPantalla);
        menu.añadirItemSecundario("menu" + data.nPantalla, "acceleracio" + data.nPantalla, "Acceleració: " + data.acceleracio);
        menu.añadirItemSecundario("menu" + data.nPantalla, "acceleracioM" + data.nPantalla, "Acceleració: " + data.acceleracioM);
        menu.añadirItemSecundario("menu" + data.nPantalla, "massa" + data.nPantalla, "Massa: " + data.massa);
        menu.añadirItemSecundario("menu" + data.nPantalla, "massaM" + data.nPantalla, "Massa: " + data.massaM);
        menu.añadirItemSecundario("menu" + data.nPantalla, "bolles" + data.nPantalla, "N Bolles: " + data.nBolles);
        menu.añadirItemSecundario("menu" + data.nPantalla, "EC" + data.nPantalla, "Ec: " + data.nBolles);

        menu.setIcons("menu" + data.nPantalla);
        menu.calcularEspacio();
    }
    
    /**
     *  Generación de la estructura de las subramas del menu general.
     */
    public void crearEstructuraDelMenuGeneral() {
        menuGeneral.añadirNuevoItemPrincipal("menuGeneral", "Estadistiques generals");
        menuGeneral.añadirItemSecundario("menuGeneral", "velocitat", "Velocitat maxima: 0 m/s");
        menuGeneral.añadirItemSecundario("menuGeneral", "velocitatM", "Velocitat: 0 m/s");
        menuGeneral.añadirItemSecundario("menuGeneral", "acceleracio", "Acceleració total: 0 m/s2");
        menuGeneral.añadirItemSecundario("menuGeneral", "acceleracioM", "Acceleració: 0 m/s2");
        menuGeneral.añadirItemSecundario("menuGeneral", "massa", "Massa: 0");
        menuGeneral.añadirItemSecundario("menuGeneral", "massaM", "Massa mitjana: 0");
        menuGeneral.añadirItemSecundario("menuGeneral", "bollesTotal", "N Bolles Totals: 0");
        menuGeneral.añadirItemSecundario("menuGeneral", "bolles", "N Bolles: 0");
        menuGeneral.añadirItemSecundario("menuGeneral", "EC", "Ec: 0");

        menuGeneral.setIcons("menuGeneral");
        menuGeneral.calcularEspacio();
    }

    boolean toni = false;
    /**
     * Calcula la media de cada valor de las estadisticas,la energia cinética general y lo setea en cada una de las subramas correspondientes.
     * Tambien se encarga de calcular las bolas totales que se han generado durante toda la simulación y la velocidad maxima alcanzada.
     * @param data Array list que posee un objeto StatisticsData de cada pantalla de la simulación.
     * @return Devuelve un array list bacio para volver a empezar la recolección de datos de las estadisticas generales.
     */
    private ArrayList<StatisticsData> setGeneralStatistics(ArrayList<StatisticsData> data) {
        try {
            StatisticsData generalData = new StatisticsData(0, 0, 0, 0, 0, 0, 0);
            float ec = 0;
            for (StatisticsData statisticsData : data) {
                generalData.velocitat += statisticsData.velocitat;
                generalData.velocitatM += statisticsData.velocitatM;
                generalData.acceleracio += statisticsData.acceleracio;
                generalData.acceleracioM += statisticsData.acceleracioM;
                generalData.massa += statisticsData.massa;
                generalData.massaM += statisticsData.massaM;
                generalData.nBolles += statisticsData.nBolles;
                ec += (float) (0.5 * statisticsData.velocitat * statisticsData.velocitat * statisticsData.massa);
            }
            menuGeneral.setText("velocitat", "Velocitat maxima: " + /*String.valueOf(generalData.velocitat / data.size())*/velocitat + " m/s");
            menuGeneral.setText("velocitatM", "Velocitat mitjana: " + String.format("%.2f", generalData.velocitatM / data.size()) + " m/s");
            menuGeneral.setText("acceleracio", "Acceleració: " + String.format("%.2f", generalData.acceleracio / data.size()) + " m/s2");
            menuGeneral.setText("acceleracioM", "Acceleració total: " + String.format("%.2f", generalData.acceleracioM / data.size()) + " m/s2");
            menuGeneral.setText("massa", "Massa total: " + String.format("%.2f",generalData.massa) + " Kg");
            menuGeneral.setText("massaM", "Massa mitjana: " + String.format("%.2f",generalData.massaM / data.size()) + " Kg");
            menuGeneral.setText("bolles", "N Bolles: " + String.valueOf(generalData.nBolles));
            menuGeneral.setText("EC", "Ec: " + String.valueOf(ec / data.size())+" J");
            if(ultimNBolles < generalData.nBolles && toni){
                totalBolles += generalData.nBolles - ultimNBolles;
                toni = false;
                ultimNBolles = generalData.nBolles;
            }else if(ultimNBolles < generalData.nBolles){
                toni = true;
            }
            
            
            menuGeneral.setText("bollesTotal", "N Bolles Totals: " + totalBolles);
        } catch (Exception e) {
            System.err.println("Error en el set General Statistics\n " + e);
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
    
    /**
     * Cambia el icono que la rama correspondiente a la pantalla que se desea desconectar y elimina su identificador del array de pantallas.
     * @param i numero de la pantalla que se desea desconectar.
     */
    public void Disconect(int i){
        try {
            menu.setIconDisconected("menu"+i);
            for (int x = 0; x<pantalles.size(); x++) {
                if(i == pantalles.get(x)){
                    pantalles.remove(x);
                }
            }
            pos=0;
        } catch (Exception e) {
            System.out.println("Erro en el metodo disconecte de la clase estadisticas \n" +e);
        }
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        panel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 204, 204));

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridLayout(1, 3, 30, 0));

        panel.setBackground(new java.awt.Color(153, 153, 153));
        panel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.LINE_AXIS));
        jPanel1.add(panel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Main encargado de ejecutar el runnable que del JFrame.
     * @param args
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Estadisticas().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
