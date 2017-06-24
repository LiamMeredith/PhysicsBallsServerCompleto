package estadisticas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Esta clase posee una lista de ramas que permitira guardar dentro suya tantas ramas y subramas como queramos y poder tratar sus valores y sus caracteristicas tales como sus iconos, el texto a mostrar, el tipo de fuente, el background... 
 * Posee animaciones para poder contraer y expandir cada rama y un resize para que se adapten al tamaño de la pantalla.
 * @author Toni Cifre Vicens
 */
public final class Menu extends JPanel {

    // Heigth de cada menu
    private final int menusSize = 35;
    // Numero de menus 
    private int menuCount;
    // Espacio vertical libre para mostrar una rama de menu
    private int branchEspacioDisponible;
    // Cuenta cuántas filas estan libres en una rama de menú
    private int filasDisponibles;
    private ItemPrincipal lastSelectedMenu;
    // Es el tamaño vertical de la rama que se abre
    private int tamañoAbierto;
    // Es el tamaño vertical de la rama que se cierra
    private int tamañoCerrado;
    // Arbol de menús con el identificador de cada elemento de menú.
    private TreeMap<ItemPrincipal, List<ItemSecundario>> leafMap;
    private TreeMap<ItemPrincipal, List<ProgresItemSecundatio>> leafProgresMap;

    /**
     * El contructor añade el listener para actualizar el espazio al cambie el tamaño de la pantalla e instancia un mapa cor cada tipo de item.
     */
    public Menu() {
        this.addComponentListener(getDefaultComponentAdapter());
        this.setLayout(null);
        this.leafMap = new TreeMap<ItemPrincipal, List<ItemSecundario>>();
        this.leafProgresMap = new TreeMap<ItemPrincipal, List<ProgresItemSecundatio>>();
    }
    
    /**
     * Al pulsar una rama principal, cambia su valor seleccionado y llama una animacion que desplegara su jPanel si esta seleccionado o lo contraera si no lo esta.
     */
    private MouseAdapter getDefaultMenuMouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boolean lastSelected=false;
                ItemPrincipal item = (ItemPrincipal) e.getSource();
                if (item.isSelected()) {
                    lastSelectedMenu=item;
                    item.setSelected(false);
                }else{
                    for (ItemPrincipal menu : getItemPrincipals()) {
                        if (menu.isSelected()) {
                            lastSelectedMenu = menu;
                            menu.setSelected(false);
                            lastSelected=true;
                        }
                    }
                    if(!lastSelected){
                        lastSelectedMenu=null;
                    }
                    item.setSelected(true);
                }
                startAnimation();
            }
        };
    }

    /**
     * Animacion de extension o contraccion del jPanel enfocada a esconder o mostrar la información de una pantalla
     */
    private void startAnimation() {
        Thread thread = new Thread(new Runnable() {

            public void run() {
                tamañoAbierto = 0;
                tamañoCerrado = branchEspacioDisponible;
                int x = 30;
                while (tamañoCerrado > 0) {
                    tamañoAbierto += x;
                    tamañoCerrado -= x;
                    update();
                    repaint();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                tamañoAbierto = branchEspacioDisponible;
                tamañoCerrado = 0;
                    
                update();
                repaint();
            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    /**
     * Actualiza el jPanel interno de cada rama con la intencion contraerse o exterderse para poder ajecutar la animación.
     */
    public void update() {
        getItemPrincipals().forEach((menu) -> {
            menu.getBranchPanel().updateUI();
        });
    }

    /**
     * Va modificando pixel a pixel el height del branch para contraer o expandir una rama.
     * Si la rama se ha seleccionado, se extiende todo el espacio disponible y contrae la que estaba previamente abierta.
     * En el caso de que se desseleccione solo se contrae la pulsada dejando el resto del espacio libre.
     * @param g Graphics.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int y = 0;
        for (ItemPrincipal menu : getItemPrincipals()) {
            menu.setSize(this.getWidth(), this.menusSize);
            menu.setLocation(0, y);

            if (menu == lastSelectedMenu && !menu.isSelected()) {
                y += this.menusSize;
                menu.getBranchPanel().setSize(this.getWidth(), this.tamañoCerrado);
                menu.getBranchPanel().setLocation(0, y);
                y += this.tamañoCerrado;
            }
            if (menu.isSelected()) {
                y += this.menusSize;
                menu.getBranchPanel().ajustarItems(filasDisponibles);
                menu.getBranchPanel().setSize(this.getWidth(), this.tamañoAbierto);
                menu.getBranchPanel().setLocation(0, y);
                y += this.tamañoAbierto;
            } else if (!menu.isSelected() && menu != lastSelectedMenu) {
                menu.getBranchPanel().setSize(0, 0);
                y += this.menusSize;
            }
        }
        update();
    }

    /**
     * Devuelve un list que posee todos los items principales del menu.
     * @return list con todos los items principales del menu.
     */
    public List<ItemPrincipal> getItemPrincipals() {
        return new ArrayList<ItemPrincipal>(leafMap.keySet());
    }

    /**
     * devuelve un item principal en concreto del menu a traves de su identificador.
     * @param name identificador del item.
     * @return item principal
     */
    public ItemPrincipal getMenu(String name) {
        for (ItemPrincipal menu : leafMap.keySet()) {
            if (menu.getName().equals(name)) {
                return menu;
            }
        }
        return null;
    }

    /**
     * Devuelve un list que posee todos los items secundarios del menu.
     * @return List con todos los items secundarios del menu.
     */
    public List<ItemSecundario> getSecundarios() {
        List<ItemSecundario> leafs = new ArrayList<ItemSecundario>();
        for (ItemPrincipal menu : leafMap.keySet()) {
            leafs.addAll(leafMap.get(menu));
        }
        return leafs;
    }
    
    /**
     * Devuelve un list que posee todos los progres items secundarios del menu.
     * @return List con todos los progres items secundarios del menu.
     */
    public List<ProgresItemSecundatio> getProgresSecundarios() {
        List<ProgresItemSecundatio> leafs = new ArrayList<ProgresItemSecundatio>();
        for (ItemPrincipal menu : leafProgresMap.keySet()) {
            leafs.addAll(leafProgresMap.get(menu));
        }
        return leafs;
    }

    /**
     * Devuelve todos los items secundarios de un item principal en concreto definido por su clave.
     * @param name identificador del item principal.
     * @return List de items secundarios.
     */
    public List<ItemSecundario> getSecundariosDe(String name) {
        List<ItemSecundario> leafs = new ArrayList<ItemSecundario>();
        for (ItemPrincipal menu : leafMap.keySet()) {
            if (menu.getName().equals(name)) {
                leafs.addAll(leafMap.get(menu));
            }
        }
        return leafs;
    }

    /**
     * Devuelve un item secundario en concreto definido por su identificador.
     * @param name identificador del item.
     * @return Item secundario.
     */
    public ItemSecundario getSecundario(String name) {
        for (ItemSecundario leaf : getSecundarios()) {
            if (leaf.getName().equals(name)) {
                return leaf;
            }
        }
        return null;
    }
    
    /**
     * Devuelve un progres item secundario en concreto definido por su identificador.
     * @param name identificador del item.
     * @return Progres item secundatio.
     */
    public ProgresItemSecundatio getProgersSedundario(String name) {
        for (ProgresItemSecundatio leaf : getProgresSecundarios()) {
            if (leaf.getName().equals(name)) {
                return leaf;
            }
        }
        return null;
    }
    
    /**
     * Setea el valor maximo del todos los progres items.
     * @param value valor maximo en forma de integer.
     */
    public void setProgresMaxValue(int value) {
        for (ProgresItemSecundatio leaf : getProgresSecundarios()) {
                leaf.setMaximum(value);
        }
    }

    /**
     * Setea un listener para controlar que cuando el tamaño de la pantalla sea modificado los menus se adapten a nuevo tamaño.
     * @return ComponentAdapter.
     */
    public ComponentAdapter getDefaultComponentAdapter() {
        return new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                calcularEspacio();
            }
        };
    }

    /**
     * Calcula el espacio disponible para ajustar el espacio al abrir, cerrar o añadir una rama o al modificar el tamaño de la ventana.
     */
    public void calcularEspacio() {
        int height = getHeight();
        double scale = menusSize / 20;
        branchEspacioDisponible = height - (menuCount * menusSize);
        filasDisponibles = (int) (Math.ceil(height / (menusSize)) * scale) - menuCount + 3;
        tamañoAbierto = branchEspacioDisponible;
        tamañoCerrado = 0;
        update();
    }

    /**
     * Crea un nuevo item princial con un texto por defecto y un identificador unico y lo añade al menu.
     * @param title Texto por defecto.
     * @param name nombre unico del item.
     * @return Item principal.
     */
    private ItemPrincipal crearItemPrincipal(String title, String name) {
        ItemPrincipal menu = new ItemPrincipal(title);
        menu.setName(name);
        add(menu);
        return menu;
    }

    /**
     * Crea un nuevo item secundario con un texto por defecto y un identificador unico y lo añade al menu.
     * @param title Texto por defecto.
     * @param name Nombre unico del item.
     * @return Item secundario.
     */
    private ItemSecundario crearItemSecundario(String title, String name) {
        ItemSecundario leaf = new ItemSecundario(title);
        leaf.setName(name);
        return leaf;
    }
    
    /**
     * Crea un nuevo progres item princial con sus valor, su maximo y minimo y su identificador unico.
     * @param val valor del progreso.
     * @param min valor minimo del progres.
     * @param max Valor maximo del progres.
     * @param name nombre unico.
     * @return  Progres item secundatio
     */
    private ProgresItemSecundatio crearItemSecundarioPregres(int val, int min,int max, String name) {
        ProgresItemSecundatio leaf = new ProgresItemSecundatio(min, max, name);
        leaf.setValue(val);
        return leaf;
    }

    /**
     * Añade un nuevo item secundatio a un item principal en concreto y a su mapa correspondiente y le define su nombre y titulo.
     * @param menuName nombre del item principal.
     * @param leafName nombre unico del item secundario.
     * @param leafTitle titulo del item secundatio.
     */
    public void añadirItemSecundario(String menuName, String leafName, String leafTitle) {
        for (ItemPrincipal menu : getItemPrincipals()) {
            if (menu.getName().equals(menuName)) {
                ItemSecundario item = crearItemSecundario(leafTitle, leafName);
                this.leafMap.get(menu).add(item);
                menu.getBranchPanel().addItem(item);
                return;
            }
        }
    }

    /**
     *  Añade un nuevo progres item secundatio a un item principal en concreto y a su mapa correspondiente y le define su nombre y titulo.
     * @param menuName nombre del item principal.
     * @param val valor del progres bar.
     * @param min valor minimo del progres bar.
     * @param max valor maximo del progres bar.
     * @param name nombre unico del item.
     */
    public void añadirItemSecundarioProgre(String menuName, int val, int min,int max, String name) {
        for (ItemPrincipal menu : getItemPrincipals()) {
            if (menu.getName().equals(menuName)) {
                ProgresItemSecundatio item = crearItemSecundarioPregres(val, min, max, name);
                item.setStringPainted(true);
                item.setForeground(Color.GREEN);
                this.leafProgresMap.get(menu).add(item);
                menu.getBranchPanel().addItem(item);
                return;
            }
        }
    }

    /**
     *  Crea un nuevo item principal con su nombre unico y titulo y los añade al mapa junto con un arraylist de items secundarios para poder asociarle items secundarios.
     * @param menuName nombre unico del item principal.
     * @param menuTitle titulo del item principal.
     */
    public void añadirNuevoItemPrincipal(String menuName, String menuTitle) {
        List<ItemSecundario> leafs = new ArrayList<>();
        List<ProgresItemSecundatio> progresLeafs = new ArrayList<>();
        ItemPrincipal menu = crearItemPrincipal(menuTitle, menuName);
        if(!"menuGeneral".equals(menuName)){
            menu.addMouseListener(getDefaultMenuMouseAdapter());
        }
        menu.setIndex(menuCount);
        if (menuCount == 0) {
            menu.setSelected(true);
        }
        menuCount++;
        this.leafMap.put(menu, leafs);
        this.leafProgresMap.put(menu, progresLeafs);
        this.add(menu.getBranchPanel());
    }

    /**
     * Instancia el color del background al timen principal i a sus secundarios asociados al ser creados.
     * @param back color del background.
     */
    @Override
    public void setBackground(Color back) {
        if (this.leafMap == null) {
            return;
        }
        for (ItemPrincipal menu : leafMap.keySet()) {
            menu.setBackground(back);
            menu.getBranchPanel().setBackground(back);
            for (ItemSecundario leaf : leafMap.get(menu)) {
                leaf.setBackground(back);
            }
        }
    }

    /**
     *  Cambia el texto de un item secundario especificado a traves de su identificador.
     * @param name Nombre del item.
     * @param text Texto que se le desea poner.
     */
    public void setText(String name, String text) {
            getSecundario(name).setText(text);
    }
    
    /**
     *  Cambia el valor de un progres bar en concreto seleccionado a traves de su nombre unico y lo repinta.
     * @param name nombre unico del progres bar.
     * @param value valor.
     */
    public void setProgres(String name, int value) {
        ProgresItemSecundatio progres= getProgersSedundario(name);
        progres.setValue(value);
        progres.repaint();
    }
    
    /**
     * Establece el estilo de borde de todos los items principales.
     * @param border Tipo de borde.
     */
    public void setItemPrincipalBorde(Border border) {
        getItemPrincipals().forEach((menu) -> {
            menu.setBorder(border);
        });
    }

    /**
     * Setea el tipo de fuente de todos los items principales y secundarios.
     * @param font Tipo de fuente.
     */
    @Override
    public void setFont(Font font) {
        if (this.leafMap == null) {
            return;
        }
        getItemPrincipals().stream().map((menu) -> {
            menu.setFont(font);
            return menu;
        }).forEachOrdered((menu) -> {
            getSecundariosDe(menu.getName()).forEach((leaf) -> {
                leaf.setFont(font);
            });
        });
    }
    
    /**
     * Cambia el icono de un item principal establecido por su identificador para poder observar si su pantalla correspondiente aun sige connectada.
     * @param name nombre del item principal.
     */
    public void setIconDisconected(String name) {
        getItemPrincipals().stream().filter((menu) -> (menu.getName().equals(name))).forEachOrdered((menu) -> {
            ImageIcon i = new ImageIcon(this.getClass().getResource("resources/disconected.png"));
            menu.setNormalIcon(i);
            menu.setSelectedIcon(i);
        });
    }
    
    /**
     * Cambia el icono por el deseado de un item secundario a traves de su identificador.
     * @param name identificador del item.
     * @param url url del icono.
     */
    public void setIcon(String name, String url) {
        getItemPrincipals().stream().filter((menu) -> (menu.getName().equals(name))).forEachOrdered((menu) -> {
            ImageIcon i = new ImageIcon(this.getClass().getResource(url));
            menu.setNormalIcon(i);
            menu.setSelectedIcon(i);
        });
    }
    
    /**
     * Establece los iconos por defecto de todos los items secundarios de un item principal.
     * @param name
     */
    public void setIcons(String name) {
        ImageIcon i;
        for (ItemSecundario leaf : getSecundariosDe(name)) {
            if(leaf.getName().contains("velocitat")){
                i=new ImageIcon(this.getClass().getResource("resources/speed.png"));
                leaf.setIcon(i);
            }else if(leaf.getName().contains("massa")){
                i = new ImageIcon(this.getClass().getResource("resources/masa.png"));
                leaf.setIcon(i);
            }else if(leaf.getName().contains("accel")){
                i = new ImageIcon(this.getClass().getResource("resources/accel.png"));
                leaf.setIcon(i);
            }else if(leaf.getName().contains("boll")){
                i = new ImageIcon(this.getClass().getResource("resources/ball.png"));
                leaf.setIcon(i);
            }else if(leaf.getName().contains("EC")){
                i = new ImageIcon(this.getClass().getResource("resources/Ec.png"));
                leaf.setIcon(i);
            }
        }
    }

    @Override
    public void setForeground(Color fg) {
        if (this.leafMap == null) {
            return;
        }
        for (ItemPrincipal menu : getItemPrincipals()) {
            menu.setForeground(fg);
            for (ItemSecundario leaf : getSecundariosDe(menu.getName())) {
                leaf.setForeground(fg);
            }
        }
    }
}
