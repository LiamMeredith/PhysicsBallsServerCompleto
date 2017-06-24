package map_generator;

import database.DBHandler;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.text.NumberFormatter;
import map_generator.items.Ball;
import map_generator.items.Obstacle;
import map_generator.items.StopItem;

/**
 *
 * @author Miquel Ginés
 */
public class Designer extends JSplitPane {

    // Listas de objetos
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private ArrayList<StopItem> stopItems = new ArrayList<>();
    private ArrayList<Ball> balls = new ArrayList<>();
    private float gravityX = 0;
    private float gravityY = 0;
    private float friction = 0;
    private String mapName = "";

    private StopItem stopItem;
    private ArrayList<Point[]> speedVecs = new ArrayList<>();
    private Ball ball;
    private Obstacle obstacle;

    private String item = "BALL";

    private boolean saved = false;

    private Object selectedObj = null;
    private Rectangle selectedObjRect;

    private DBHandler db;

    // Valores máximos y mínimos permitidos
    private final int MAX_HEIGHT = 200;
    private final int MAX_WIDTH = 200;
    private final int MIN_SPEED = 0;
    private final int MAX_SPEED = 90;
    private final int MIN_RADIUS = 5;
    private final int MAX_RADIUS = 40;
    private final int MIN_MARGIN = 5;
    private final int MAPPANEL_WIDTH = 970;
    private final int MAPPANEL_HEIGHT = 546;

    private double screenProp;

    // Pila usada para el botón de deshacer
    private Stack<String> undoList = new Stack<>();

    private JFrame frame;

    // Los dos paneles en los que se separa la ventana, el de los controles y
    // el del mapa
    private JPanel controlPanel;
    private JPanel mapPanel;

    // Objetos de labels de información
    private JLabel mapNameLb;
    private JLabel ballLb;
    private JLabel obsLb;
    private JLabel stopItemLb;
    private JLabel mapOptLb;
    private JLabel selectedItemLb;
    private JLabel posXLb;
    private JLabel posYLb;
    private JLabel widthLb;
    private JLabel heightLb;
    private JLabel radiusLb;
    private JLabel radiusInfoLb;
    private JLabel angleLb;
    private JLabel angleInfoLb;
    private JLabel speedLb;
    private JLabel speedInfoLb;
    private JLabel ballTypeLb;

    private JTextField posXTf;
    private JTextField posYTf;
    private JTextField widthTf;
    private JTextField heightTf;

    private JSlider radiusSl;
    private JSlider speedSl;
    private RoundSlider angleSl;

    // Botones del panel de controles
    private JButton settingsBtn;
    private JButton saveBtn;
    private JButton undoBtn;
    private JButton ballBtn;
    private JButton obsBtn;
    private JButton eraseBtn;
    private JButton stopItemBtn;
    private JButton configBtn;

    // Barra de menú superior
    private JMenuBar menuBar;
    private JMenu optionsMenu;
    private JMenuItem deleteMapIm;
    private JMenuItem newMapIm;
    private JMenuItem loadMapIm;
    private JMenuItem changeActualSettingsIm;
    private JMenuItem saveIm;
    private JMenuItem undoIm;
    private JMenuItem clearIm;

    // PopUp menú de edición de objetos
    private JPopupMenu popUp;
    private JMenuItem popUpItemModify;
    private JMenuItem popUpItemDelete;
    private JMenuItem popUpItemClose;

    private JComboBox ballTypeCb;

    private JButton ballBImgBtn;
    private JButton ballNImgBtn;
    private JButton bgImgBtn;
    private JSlider frictionSl;
    private JSlider gravityXSl;
    private JSlider gravityYSl;
    private JTextField spaceNameTf;
    private BufferedImage bgImg = null, ballNImg = null, ballEImg = null, ballBImg = null;
    private BufferedImage bgImgTemp = null, ballNImgTemp = null, ballEImgTemp = null, ballBImgTemp = null;
    private ArrayList<BufferedImage> images = new ArrayList<>();

    /**
     * Constructor inicial, llama a los métodos de creación de los paneles, y
     * crea la barra de menú
     *
     * @param frame
     * @throws IOException
     */
    public Designer(JFrame frame) throws IOException {
        super(JSplitPane.VERTICAL_SPLIT);
        setDividerSize(0);

        this.frame = frame;
        screenProp = MAPPANEL_WIDTH / 1280;

        db = new DBHandler();

        createMenus();

        // Llama a los métodos de creación de los paneles
        createControlPanel();
        createMapPanel();

        setLeftComponent(controlPanel);
        setRightComponent(mapPanel);

// Solicita el nombre inicial del mapa
        getInitialSettings();
    }

    private void createMenus() {
        popUp = new JPopupMenu();
        popUpItemModify = new JMenuItem("Modificar objeto");
        popUpItemModify.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (selectedObj instanceof Ball) {
                modifyBall((Ball) selectedObj);
            } else if (selectedObj instanceof Obstacle) {
                modifyObstacle((Obstacle) selectedObj);
            } else if (selectedObj instanceof StopItem) {
                modifyStopItem((StopItem) selectedObj);
            } else {

            }
            selectedObj = null;
            selectedObjRect = null;
            repaint();
        });
        popUpItemDelete = new JMenuItem("Eliminar objeto");
        popUpItemDelete.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (selectedObj instanceof Ball) {
                int i = getBallPos((Ball) selectedObj);
                speedVecs.remove(i);
                balls.remove(selectedObj);
            } else if (selectedObj instanceof Obstacle) {
                obstacles.remove(selectedObj);
            } else if (selectedObj instanceof StopItem) {
                stopItems.remove(selectedObj);
            } else {

            }
            selectedObj = null;
            selectedObjRect = null;
            repaint();
        });
        popUpItemClose = new JMenuItem("Cerrar menú");
        popUpItemClose.addActionListener((java.awt.event.ActionEvent evt) -> {

        });

        popUp.add(popUpItemModify);
        popUp.add(popUpItemDelete);
        popUp.addSeparator();
        popUp.add(popUpItemClose);

        // Crea la barra de menú superior y la añade al frame
        menuBar = new JMenuBar();
        optionsMenu = new JMenu("Opciones");
        newMapIm = new JMenuItem("Crear mapa nuevo");
        newMapIm.addActionListener((java.awt.event.ActionEvent evt) -> {
            clearMap();
            mapName = "";
            bgImg = null;
            ballNImg = null;
            ballEImg = null;
            ballBImg = null;
            friction = 0;
            gravityX = 0;
            gravityY = 0;
            getInitialSettings();
        });
        loadMapIm = new JMenuItem("Cargar un mapa de la base de datos");
        loadMapIm.addActionListener((java.awt.event.ActionEvent evt) -> {
            loadDbMap();
        });
        deleteMapIm = new JMenuItem("Eliminar un mapa de la base de datos");
        deleteMapIm.addActionListener((java.awt.event.ActionEvent evt) -> {
            deleteMap();
        });
        changeActualSettingsIm = new JMenuItem("Modificar parámetros del mapa actual");
        changeActualSettingsIm.addActionListener((java.awt.event.ActionEvent evt) -> {
            changeActualSettings();
        });
        saveIm = new JMenuItem("Guardar mapa");
        saveIm.addActionListener((java.awt.event.ActionEvent evt) -> {
            saveMap();
        });
        undoIm = new JMenuItem("Deshacer último objeto");
        undoIm.addActionListener((java.awt.event.ActionEvent evt) -> {
            undoObj();
        });
        clearIm = new JMenuItem("Vaciar mapa");
        clearIm.addActionListener((java.awt.event.ActionEvent evt) -> {
            clearMap();
        });
        optionsMenu.add(newMapIm);
        optionsMenu.add(loadMapIm);
        optionsMenu.add(deleteMapIm);
        optionsMenu.addSeparator();
        optionsMenu.add(changeActualSettingsIm);
        optionsMenu.add(saveIm);
        optionsMenu.add(undoIm);
        optionsMenu.add(clearIm);

        menuBar.add(optionsMenu);

        frame.setJMenuBar(menuBar);
    }

    private int getBallPos(Ball b) {
        for (int i = 0; i < balls.size(); i++) {
            if (balls.get(i) == b) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Elimina un mapa de la base de datos
     */
    private void deleteMap() {
        String[] options = db.getSpaceList1();
        String delMapName = (String) JOptionPane.showInputDialog(
                frame,
                "Seleccione el mapa que desea eliminar",
                "Eliminar mapa",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        if ((delMapName != null) && (delMapName.length() > 0)) {
            Object[] confirm = {"Sí", "No"};
            int selected = JOptionPane.showOptionDialog(frame,
                    "¿Seguro que quiere eliminar el mapa '" + delMapName + "'?",
                    "Eliminar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    confirm,
                    confirm[0]);
            if (selected == 0) {
                db.deleteSpaceCascade(delMapName);
                JOptionPane.showMessageDialog(this, "Mapa eliminado correctamente", "Mapa eliminado", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Solicita los ajustes para la creación inicial del mapa
     */
    private void getInitialSettings() {
        String[] newMap = {"Crear mapa nuevo", "----------"};
        String[] loadMaps = db.getSpaceList1();
        String[] options = Stream.of(newMap, loadMaps).flatMap(Stream::of).toArray(String[]::new);
        String optSelected = (String) JOptionPane.showInputDialog(
                frame,
                "Seleccione si desea crear un mapa nuevo o cargar uno ya existente",
                "Inicio",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        if ((optSelected != null) && (optSelected.length() > 0)) {
            if (optSelected.equals("----------")) {
                getInitialSettings();
            } else if (optSelected.equals("Crear mapa nuevo")) {
                if (changeSettings() == JOptionPane.OK_OPTION) {
                    if (!db.checkSpaceName(spaceNameTf.getText())) {
                        JOptionPane.showMessageDialog(this, "Ya existe un mapa con ese nombre en la base de datos.", "Erro", JOptionPane.ERROR_MESSAGE);
                        getInitialSettings();
                    } else if (spaceNameTf.getText() == null || spaceNameTf.getText().equals("")
                            || bgImgTemp == null || ballNImgTemp == null || ballEImgTemp == null || ballBImgTemp == null) {
                        JOptionPane.showMessageDialog(this, "No se puede crear un mapa con parámetros (nombre, imágenes, etc.) vacíos.", "Error", JOptionPane.ERROR_MESSAGE);
                        getInitialSettings();
                    } else {
                        mapName = spaceNameTf.getText();
                        mapNameLb.setText(mapName);
                        gravityX = gravityXSl.getValue() / 10f;
                        gravityY = gravityYSl.getValue() / 10f;
                        friction = frictionSl.getValue() / 10f;
                        bgImg = bgImgTemp;
                        bgImgTemp = null;
                        ballNImg = ballNImgTemp;
                        ballNImgTemp = null;
                        ballEImg = ballEImgTemp;
                        ballEImgTemp = null;
                        ballBImg = ballBImgTemp;
                        ballBImgTemp = null;
                        images.add(ballNImg);
                        images.add(ballEImg);
                        images.add(ballBImg);
                    }
                } else {
                    System.exit(0);
                }
            } else {
                loadMap(optSelected);
            }
        }
    }

    private void loadDbMap() {
        Object[] opt = {"Sí", "No"};
        int selected = JOptionPane.showOptionDialog(frame,
                "Se perderán los datos del mapa actual si no han sido guardados, ¿Seguro que quiere cargar un mapa de la base de datos?",
                "Atención",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opt,
                opt[0]);
        if (selected == 0) {
            String[] options = db.getSpaceList1();
            String optSelected = (String) JOptionPane.showInputDialog(
                    frame,
                    "Seleccione el mapa que desea cargar",
                    "Cargar mapa",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);
            if ((optSelected != null) && (optSelected.length() > 0)) {
                selectedObjRect = null;
                obstacles.clear();
                stopItems.clear();
                balls.clear();
                speedVecs.clear();
                undoBtn.setEnabled(false);
                eraseBtn.setEnabled(false);
                saveBtn.setEnabled(false);
                saved = false;
                loadMap(optSelected);
                repaint();
            }
        }

    }

    private void loadMap(String map) {
        InputStream in = null;
        ArrayList<Object> spaceData = (ArrayList) db.selectMap(map);
        mapName = (String) spaceData.get(0);
        in = new ByteArrayInputStream((byte[]) spaceData.get(1));
        try {
            bgImg = ImageIO.read(in);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        in = new ByteArrayInputStream((byte[]) spaceData.get(2));
        try {
            ballNImg = ImageIO.read(in);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        in = new ByteArrayInputStream((byte[]) spaceData.get(3));
        try {
            ballEImg = ImageIO.read(in);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        in = new ByteArrayInputStream((byte[]) spaceData.get(4));
        try {
            ballBImg = ImageIO.read(in);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        gravityX = (float) spaceData.get(5);
        gravityY = (float) spaceData.get(6);
        friction = (float) spaceData.get(7);

        images.clear();
        images.add(ballNImg);
        images.add(ballEImg);
        images.add(ballBImg);

        for (org.physicballs.items.Ball b : db.selectBalls(map)) {
            balls.add(Utils.conversor(b));
            Point p1 = new Point(0, 0);
            Point p2 = new Point(0, 0);
            if (b.getSpeed() != 0) {
                p1 = new Point((int) b.getX(), (int) b.getY());
                p2 = new Point((int) (b.getX() + (b.getSpeedx() * 3)), (int) (b.getY() + (b.getSpeedy() * 3)));
            }
            Point[] p = {p1, p2};
            speedVecs.add(p);
        }
        for (org.physicballs.items.Obstacle obs : db.selectObstacles(map)) {
            obstacles.add(Utils.conversor(obs));
        }
        for (org.physicballs.items.StopItem si : db.selectStopItems(map)) {
            stopItems.add(Utils.conversor(si));
        }
    }

    /**
     * Modifica los ajustes del mapa actual
     */
    private void changeActualSettings() {
        if (changeSettings() == JOptionPane.OK_OPTION) {
            if (!db.checkSpaceName(spaceNameTf.getText())) {
                JOptionPane.showMessageDialog(this, "Ya existe un mapa con ese nombre en la base de datos.", "Erro", JOptionPane.ERROR_MESSAGE);
                changeActualSettings();
            } else if (spaceNameTf.getText() == null || spaceNameTf.getText().equals("")
                    || bgImgTemp == null || ballNImgTemp == null || ballEImgTemp == null || ballBImgTemp == null) {
                JOptionPane.showMessageDialog(this, "No se puede crear un mapa con parámetros (nombre, imágenes, etc.) vacíos.", "Error", JOptionPane.ERROR_MESSAGE);
                changeActualSettings();
            } else {
                mapName = spaceNameTf.getText();
                mapNameLb.setText(mapName);
                gravityX = gravityXSl.getValue() / 10f;
                gravityY = gravityYSl.getValue() / 10f;
                friction = frictionSl.getValue() / 10f;
                bgImg = bgImgTemp;
                bgImgTemp = null;
                ballNImg = ballNImgTemp;
                ballNImgTemp = null;
                ballEImg = ballEImgTemp;
                ballEImgTemp = null;
                ballBImg = ballBImgTemp;
                ballBImgTemp = null;
                images.clear();
                images.add(ballNImg);
                images.add(ballEImg);
                images.add(ballBImg);
            }
        }
    }

    /**
     * Solicita el nombre inicial del mapa
     */
    private int changeSettings() {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home") + "/Desktop");
        fileChooser.addChoosableFileFilter(new ImageFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileView(new ImageFileView());
        fileChooser.setAccessory(new ImagePreview(fileChooser));

        JLabel spaceNameLb = new JLabel("Nombre del mapa:    ", SwingConstants.RIGHT);
        spaceNameTf = new JTextField(mapName);

        JLabel gravityXLb = new JLabel("Gravedad horizontal: " + gravityX + "    ", SwingConstants.RIGHT);
        gravityXSl = new JSlider(JSlider.HORIZONTAL, -100, 100, (int) (gravityX * 10f));
        gravityXSl.addChangeListener((ChangeEvent e) -> {
            gravityXLb.setText("Gravedad horizontal: " + (gravityXSl.getValue() / 10f) + "    ");
        });
        JLabel gravityYLb = new JLabel("Gravedad vertical: " + gravityY + "    ", SwingConstants.RIGHT);
        gravityYSl = new JSlider(JSlider.HORIZONTAL, -100, 100, (int) (gravityY * 10f));
        gravityYSl.addChangeListener((ChangeEvent e) -> {
            gravityYLb.setText("Gravedad ertical: " + (gravityYSl.getValue() / 10f) + "    ");
        });
        JLabel frictionLb = new JLabel("Fricción: " + friction + "    ", SwingConstants.RIGHT);
        frictionSl = new JSlider(JSlider.HORIZONTAL, 0, 10, (int) (friction * 10f));
        frictionSl.addChangeListener((ChangeEvent e) -> {
            frictionLb.setText("Fricción: " + (frictionSl.getValue() / 10f) + "    ");
        });

        JLabel bgImgLb = new JLabel("Imagen sin cargar", null, JLabel.CENTER);
        if (bgImg != null) {
            bgImgLb.setText("");
            bgImgLb.setIcon(new ImageIcon(bgImg.getScaledInstance(60, 34, Image.SCALE_DEFAULT)));
            bgImgTemp = bgImg;
        }
        bgImgBtn = new JButton("Fondo del mapa");
        bgImgBtn.setBorder(new RoundedBorder(10));
        bgImgBtn.setForeground(Color.GRAY);
        bgImgBtn.setToolTipText("Cargar imagen para el fondo de pantalla");
        bgImgBtn.addActionListener((ActionEvent e) -> {
            int retValue = fileChooser.showDialog(this, "Cargar imagen");
            if (retValue == JFileChooser.APPROVE_OPTION) {
                try {
                    File imgFile = fileChooser.getSelectedFile();
                    bgImgTemp = ImageIO.read(imgFile);
                    bgImgLb.setText("");
                    bgImgLb.setIcon(new ImageIcon(new ImageIcon(imgFile.getAbsolutePath()).getImage().getScaledInstance(60, 34, Image.SCALE_DEFAULT)));
                } catch (IOException ex) {
                    Logger.getLogger(Designer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        JLabel ballNImgLb = new JLabel("Imagen sin cargar", null, JLabel.CENTER);
        if (ballNImg != null) {
            ballNImgLb.setText("");
            ballNImgLb.setIcon(new ImageIcon(ballNImg.getScaledInstance(35, 35, Image.SCALE_DEFAULT)));
            ballNImgTemp = ballNImg;
        }
        ballNImgBtn = new JButton("Bola normal");
        ballNImgBtn.setBorder(new RoundedBorder(10));
        ballNImgBtn.setForeground(Color.GRAY);
        ballNImgBtn.setToolTipText("Cargar imagen para el fondo de pantalla");
        ballNImgBtn.addActionListener((ActionEvent e) -> {
            int retValue = fileChooser.showDialog(this, "Cargar imagen");
            if (retValue == JFileChooser.APPROVE_OPTION) {
                File imgFile = fileChooser.getSelectedFile();
                try {
                    ballNImgTemp = ImageIO.read(imgFile);
                    ballNImgLb.setText("");
                    ballNImgLb.setIcon(new ImageIcon(new ImageIcon(imgFile.getAbsolutePath()).getImage().getScaledInstance(35, 35, Image.SCALE_DEFAULT)));
                } catch (IOException ex) {
                    Logger.getLogger(Designer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        JLabel ballEImgLb = new JLabel("Imagen sin cargar", null, JLabel.CENTER);
        if (ballEImg != null) {
            ballEImgLb.setText("");
            ballEImgLb.setIcon(new ImageIcon(ballEImg.getScaledInstance(35, 35, Image.SCALE_DEFAULT)));
            ballEImgTemp = ballEImg;
        }
        JButton ballEImgBtn = new JButton("Bola explosiva");
        ballEImgBtn.setBorder(new RoundedBorder(10));
        ballEImgBtn.setForeground(Color.GRAY);
        ballEImgBtn.setToolTipText("Cargar imagen para el fondo de pantalla");
        ballEImgBtn.addActionListener((ActionEvent e) -> {
            int retValue = fileChooser.showDialog(this, "Cargar imagen");
            if (retValue == JFileChooser.APPROVE_OPTION) {
                File imgFile = fileChooser.getSelectedFile();
                try {
                    ballEImgTemp = ImageIO.read(imgFile);
                    ballEImgLb.setText("");
                    ballEImgLb.setIcon(new ImageIcon(new ImageIcon(imgFile.getAbsolutePath()).getImage().getScaledInstance(35, 35, Image.SCALE_DEFAULT)));
                } catch (IOException ex) {
                    Logger.getLogger(Designer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        JLabel ballBImgLb = new JLabel("Imagen sin cargar", null, JLabel.CENTER);
        if (ballBImg != null) {
            ballBImgLb.setText("");
            ballBImgLb.setIcon(new ImageIcon(ballBImg.getScaledInstance(35, 35, Image.SCALE_DEFAULT)));
            ballBImgTemp = ballBImg;
        }
        ballBImgBtn = new JButton("Bola bullet");
        ballBImgBtn.setBorder(new RoundedBorder(10));
        ballBImgBtn.setForeground(Color.GRAY);
        ballBImgBtn.setToolTipText("Cargar imagen para el fondo de pantalla");
        ballBImgBtn.addActionListener((ActionEvent e) -> {
            int retValue = fileChooser.showDialog(this, "Cargar imagen");
            if (retValue == JFileChooser.APPROVE_OPTION) {
                File imgFile = fileChooser.getSelectedFile();
                try {
                    ballBImgTemp = ImageIO.read(imgFile);
                    ballBImgLb.setText("");
                    ballBImgLb.setIcon(new ImageIcon(new ImageIcon(imgFile.getAbsolutePath()).getImage().getScaledInstance(35, 35, Image.SCALE_DEFAULT)));
                } catch (IOException ex) {
                    Logger.getLogger(Designer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 2, 1, 2));
        panel.add(spaceNameLb);
        panel.add(spaceNameTf);
        panel.add(new JLabel("Parámetros del mapa"));
        panel.add(new JLabel(""));
        panel.add(gravityXLb);
        panel.add(gravityXSl);
        panel.add(gravityYLb);
        panel.add(gravityYSl);
        panel.add(frictionLb);
        panel.add(frictionSl);
        panel.add(new JLabel("Imágenes del mapa"));
        panel.add(new JLabel(""));
        panel.add(bgImgBtn);
        panel.add(bgImgLb);
        panel.add(ballNImgBtn);
        panel.add(ballNImgLb);
        panel.add(ballEImgBtn);
        panel.add(ballEImgLb);
        panel.add(ballBImgBtn);
        panel.add(ballBImgLb);

        return JOptionPane.showConfirmDialog(null, panel, "Ajustes generales del espacio", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    }

    /**
     * Crea el panel superior
     *
     * @throws IOException
     */
    private void createControlPanel() throws IOException {
        // Botón para cambiar de item
        controlPanel = new JPanel() {
            // Este método pinta las líneas que hacen de separadores de los paneles
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(Color.GRAY);
                g.fillRect(0, 140, 1150, 3);
                g.fillRect(220, 0, 3, 140);
                g.fillRect(364, 0, 3, 140);
            }
        };
        controlPanel.setPreferredSize(new Dimension(MAPPANEL_WIDTH, 143));
        controlPanel.setLayout(null);

        createMapControlForm();
        createObjectParamForm();
    }

    /**
     * Crea el panel de control del mapa y objetos
     */
    private void createMapControlForm() {

        ballLb = new JLabel("Bola", SwingConstants.CENTER);
        ballLb.setSize(40, 15);
        ballLb.setLocation(14, 5);
        controlPanel.add(ballLb);

        // Botón para seleccionar la bola
        ballBtn = new JButton(Utils.createImageIcon("images/ball.png"));
        ballBtn.setSize(40, 40);
        ballBtn.setLocation(15, 20);
        ballBtn.setEnabled(false);
        ballBtn.setBorder(new RoundedBorder(10));
        ballBtn.setForeground(Color.GRAY);
        ballBtn.setToolTipText("Colocar bolas");
        ballBtn.addActionListener((ActionEvent e) -> {
            if (!item.equals("BALL")) {
                item = "BALL";
                ballBtn.setEnabled(false);
                obsBtn.setEnabled(true);
                stopItemBtn.setEnabled(true);
                radiusLb.setVisible(true);
                radiusInfoLb.setVisible(true);
                radiusSl.setVisible(true);
                widthLb.setVisible(false);
                widthTf.setVisible(false);
                heightLb.setVisible(false);
                heightTf.setVisible(false);
                speedLb.setVisible(true);
                speedSl.setVisible(true);
                speedInfoLb.setVisible(true);
                angleSl.setVisible(true);
                angleLb.setVisible(true);
                angleInfoLb.setVisible(true);
                ballTypeLb.setVisible(true);
                ballTypeCb.setVisible(true);
                selectedItemLb.setText("Bola");
            }
        });
        controlPanel.add(ballBtn);

        obsLb = new JLabel("Obstáculo", SwingConstants.CENTER);
        obsLb.setSize(60, 15);
        obsLb.setLocation(79, 5);
        controlPanel.add(obsLb);

        // Botón para seleccionar el obstáculo
        obsBtn = new JButton(Utils.createImageIcon("images/obs.png"));
        obsBtn.setSize(40, 40);
        obsBtn.setLocation(88, 20);
        obsBtn.setBorder(new RoundedBorder(10));
        obsBtn.setForeground(Color.GRAY);
        obsBtn.setToolTipText("Colocar obstáculos");
        obsBtn.addActionListener((ActionEvent e) -> {
            if (!item.equals("OBSTACLE")) {
                item = "OBSTACLE";
                obsBtn.setEnabled(false);
                ballBtn.setEnabled(true);
                stopItemBtn.setEnabled(true);
                radiusLb.setVisible(false);
                radiusInfoLb.setVisible(false);
                radiusSl.setVisible(false);
                widthLb.setVisible(true);
                widthTf.setVisible(true);
                heightLb.setVisible(true);
                heightTf.setVisible(true);
                speedLb.setVisible(false);
                speedSl.setVisible(false);
                speedInfoLb.setVisible(false);
                angleSl.setVisible(false);
                angleLb.setVisible(false);
                angleInfoLb.setVisible(false);
                ballTypeLb.setVisible(false);
                ballTypeCb.setVisible(false);
                selectedItemLb.setText("Obstáculo");
            }
        });
        controlPanel.add(obsBtn);

        stopItemLb = new JLabel("Semáforo", SwingConstants.CENTER);
        stopItemLb.setSize(60, 15);
        stopItemLb.setLocation(150, 5);
        controlPanel.add(stopItemLb);

        // Botón para seleccionar el semáforo
        stopItemBtn = new JButton(Utils.createImageIcon("images/bottle.png"));
        stopItemBtn.setSize(40, 40);
        stopItemBtn.setLocation(161, 20);
        stopItemBtn.setBorder(new RoundedBorder(10));
        stopItemBtn.setForeground(Color.GRAY);
        stopItemBtn.setToolTipText("Colocar semáforos");
        stopItemBtn.addActionListener((ActionEvent e) -> {
            item = "BOTTLENECK";
            stopItemBtn.setEnabled(false);
            ballBtn.setEnabled(true);
            obsBtn.setEnabled(true);
            radiusLb.setVisible(false);
            radiusInfoLb.setVisible(false);
            radiusSl.setVisible(false);
            widthLb.setVisible(true);
            widthTf.setVisible(true);
            heightLb.setVisible(true);
            heightTf.setVisible(true);
            speedLb.setVisible(false);
            speedSl.setVisible(false);
            speedInfoLb.setVisible(false);
            angleSl.setVisible(false);
            angleLb.setVisible(false);
            angleInfoLb.setVisible(false);
            ballTypeLb.setVisible(false);
            ballTypeCb.setVisible(false);
            selectedItemLb.setText("Semáforo");
        });
        controlPanel.add(stopItemBtn);

        mapNameLb = new JLabel(mapName, SwingConstants.CENTER);
        mapNameLb.setFont(new Font(mapNameLb.getFont().getName(), Font.BOLD, 27));
        mapNameLb.setSize(190, 30);
        mapNameLb.setLocation(15, 75);
        controlPanel.add(mapNameLb);

        mapOptLb = new JLabel("Opc. del mapa", SwingConstants.CENTER);
        mapOptLb.setSize(102, 13);
        mapOptLb.setLocation(242, 5);
        controlPanel.add(mapOptLb);

        // Botón de arrancar la simulación
        settingsBtn = new JButton(Utils.createImageIcon("images/settings.png"));
        settingsBtn.setSize(40, 40);
        settingsBtn.setLocation(242, 20);
        settingsBtn.setBorder(new RoundedBorder(10));
        settingsBtn.setForeground(Color.GRAY);
        settingsBtn.setToolTipText("Arrancar la simulación");
        settingsBtn.addActionListener((ActionEvent e) -> {
            changeActualSettings();
        });
        controlPanel.add(settingsBtn);

        // Botón de guardar
        saveBtn = new JButton(Utils.createImageIcon("images/save.png"));
        saveBtn.setEnabled(false);
        saveBtn.setSize(40, 40);
        saveBtn.setLocation(304, 20);
        saveBtn.setBorder(new RoundedBorder(10));
        saveBtn.setForeground(Color.GRAY);
        saveBtn.setToolTipText("Guardar escenario");
        saveBtn.addActionListener((ActionEvent e) -> {
            saveMap();
        });
        controlPanel.add(saveBtn);

        // Botón de deshacer
        undoBtn = new JButton(Utils.createImageIcon("images/undo.png"));
        undoBtn.setEnabled(false);
        undoBtn.setSize(40, 40);
        undoBtn.setLocation(242, 80);
        undoBtn.setBorder(new RoundedBorder(10));
        undoBtn.setForeground(Color.GRAY);
        undoBtn.setToolTipText("Deshacer última acción");
        undoBtn.addActionListener((ActionEvent e) -> {
            undoObj();
        });
        controlPanel.add(undoBtn);

        // Botón de borrar al completo
        eraseBtn = new JButton(Utils.createImageIcon("images/erase.png"));
        eraseBtn.setEnabled(false);
        eraseBtn.setSize(40, 40);
        eraseBtn.setLocation(304, 80);
        eraseBtn.setBorder(new RoundedBorder(10));
        eraseBtn.setForeground(Color.GRAY);
        eraseBtn.setToolTipText("Borrar toda la información del escenario");
        eraseBtn.addActionListener((ActionEvent e) -> {
            clearMap();
        });
        controlPanel.add(eraseBtn);
    }

    /**
     * Guarda el mapa en la base de datos
     */
    private void saveMap() {
        Object[] options = {"Sí", "No"};
        if (balls.size() > 0 || obstacles.size() > 0 || stopItems.size() > 0) {
            int selected = JOptionPane.showOptionDialog(frame,
                    "¿Seguro que quiere guardar el mapa?",
                    "Guardar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (selected == 0) {
                boolean validSave = true;
                if (!db.checkSpaceName(mapName)) {
                    selected = JOptionPane.showOptionDialog(frame,
                            "Ya existe el mapa '" + mapName + "', ¿desea sobreescribirlo?",
                            "Guardar",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);
                    if (selected == 0) {
                        db.deleteSpaceCascade(mapName);
                    } else {
                        validSave = false;
                    }
                }
                if (validSave) {
                    db.insertSpace(mapName, bgImg, ballEImg, ballNImg, ballBImg, gravityX, gravityY, friction, 150, 150, 50);
                    ArrayList<org.physicballs.items.Ball> newBalls = new ArrayList<>();
                    for (Ball b : balls) {
                        org.physicballs.items.Ball ball = new org.physicballs.items.Ball(b.getX(), b.getY(), b.getSpeedx(), b.getSpeedy(), b.getRadius(), String.valueOf(b.getType().name().charAt(0)));
                        ball.setAccel(b.getAccel());
                        newBalls.add(ball);
                    }
                    ArrayList<org.physicballs.items.Obstacle> newObs = new ArrayList<>();
                    for (Obstacle o : obstacles) {
                        org.physicballs.items.Obstacle obs = new org.physicballs.items.Obstacle(o.getX(), o.getY(), o.getWidth(), o.getHeight());
                        newObs.add(obs);
                    }
                    ArrayList<org.physicballs.items.StopItem> newStop = new ArrayList<>();
                    for (StopItem si : stopItems) {
                        org.physicballs.items.StopItem stop = new org.physicballs.items.StopItem(si.getX(), si.getY(), si.getWidth(), si.getHeight());
                        newStop.add(stop);
                    }
                    db.insertBalls(newBalls);
                    db.insertObstacles(newObs);
                    db.insertStopItems(newStop);
                    saved = true;
                    repaint();
                    JOptionPane.showMessageDialog(this, "Mapa guardado correctamente.", "Mapa guardado", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se puede guardar un mapa vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Deshace el último objeto añadido al mapa
     */
    private void undoObj() {
        if (!undoList.isEmpty()) {
            String toUndo = undoList.pop();
            switch (toUndo) {
                case "BALL":
                    balls.remove(balls.size() - 1);
                    speedVecs.remove(speedVecs.size() - 1);
                    break;
                case "OBSTACLE":
                    obstacles.remove(obstacles.size() - 1);
                    break;
                case "BOTTLENECK":
                    stopItems.remove(stopItems.size() - 1);
                    break;
                default:
                    // nada
                    break;
            }
        }
        if (undoList.isEmpty()) {
            undoBtn.setEnabled(false);
            eraseBtn.setEnabled(false);
            saveBtn.setEnabled(false);
        }
        repaint();
    }

    /**
     * Vacía todos los objetos del mapa
     */
    private void clearMap() {
        Object[] options = {"Sí", "No"};
        int selected = JOptionPane.showOptionDialog(frame,
                "¿Quiere eliminar completamente todos los objetos añadidos al mapa?",
                "Eliminar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        if (selected == 0) {
            selectedObjRect = null;
            obstacles.clear();
            stopItems.clear();
            balls.clear();
            speedVecs.clear();
            undoBtn.setEnabled(false);
            eraseBtn.setEnabled(false);
            saveBtn.setEnabled(false);
            saved = false;
            repaint();
        }
    }

    /**
     * Crea el panel de control de los parámetros de los objetos
     */
    private void createObjectParamForm() {
        selectedItemLb = new JLabel("Bola", SwingConstants.LEFT);
        selectedItemLb.setFont(new Font(selectedItemLb.getFont().getName(), Font.BOLD, 28));
        selectedItemLb.setSize(500, 30);
        selectedItemLb.setLocation(400, 5);
        controlPanel.add(selectedItemLb);

        posXLb = new JLabel("Pos. X: ", SwingConstants.RIGHT);
        posXLb.setSize(75, 20);
        posXLb.setLocation(357, 50);
        controlPanel.add(posXLb);

        posXTf = new JTextField("");
        posXTf.setSize(75, 20);
        posXTf.setLocation(435, 50);
        posXTf.setEditable(false);
        controlPanel.add(posXTf);

        posYLb = new JLabel("Pos. Y: ", SwingConstants.RIGHT);
        posYLb.setSize(75, 20);
        posYLb.setLocation(357, 90);
        controlPanel.add(posYLb);

        posYTf = new JTextField("");
        posYTf.setSize(75, 20);
        posYTf.setLocation(435, 90);
        posYTf.setEditable(false);
        controlPanel.add(posYTf);

        widthLb = new JLabel("Ancho: ", SwingConstants.RIGHT);
        widthLb.setSize(75, 20);
        widthLb.setLocation(500, 50);
        widthLb.setVisible(false);
        controlPanel.add(widthLb);

        widthTf = new JTextField("");
        widthTf.setSize(75, 20);
        widthTf.setLocation(578, 50);
        widthTf.setEditable(false);
        widthTf.setVisible(false);
        controlPanel.add(widthTf);

        heightLb = new JLabel("Alto: ", SwingConstants.RIGHT);
        heightLb.setSize(75, 20);
        heightLb.setLocation(500, 90);
        heightLb.setVisible(false);
        controlPanel.add(heightLb);

        heightTf = new JTextField("");
        heightTf.setSize(75, 20);
        heightTf.setLocation(578, 90);
        heightTf.setEditable(false);
        heightTf.setVisible(false);
        controlPanel.add(heightTf);

        NumberFormat intFormat = NumberFormat.getIntegerInstance();
        NumberFormatter numberFormatter = new NumberFormatter(intFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);
        numberFormatter.setMinimum(0);

        radiusLb = new JLabel("Radio: ", SwingConstants.RIGHT);
        radiusLb.setSize(75, 20);
        radiusLb.setLocation(500, 50);
        controlPanel.add(radiusLb);

        radiusInfoLb = new JLabel("15", SwingConstants.RIGHT);
        radiusInfoLb.setSize(75, 20);
        radiusInfoLb.setLocation(615, 50);
        controlPanel.add(radiusInfoLb);

        radiusSl = new JSlider(JSlider.HORIZONTAL, MIN_RADIUS, MAX_RADIUS, 15);
        radiusSl.setSize(105, 20);
        radiusSl.setLocation(570, 52);
        radiusSl.addChangeListener((ChangeEvent e) -> {
            radiusInfoLb.setText("" + radiusSl.getValue());
        });
        controlPanel.add(radiusSl);

        angleLb = new JLabel("Ángulo: ", SwingConstants.RIGHT);
        angleLb.setSize(75, 50);
        angleLb.setLocation(700, 24);
        controlPanel.add(angleLb);

        angleInfoLb = new JLabel("0,00º", SwingConstants.RIGHT);
        angleInfoLb.setSize(75, 20);
        angleInfoLb.setLocation(757, 75);
        controlPanel.add(angleInfoLb);

        angleSl = new RoundSlider(Math.toRadians(90));
        angleSl.setSize(50, 50);
        angleSl.setLocation(735, 63);
        angleSl.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                DecimalFormat angleFormat = new DecimalFormat("0.0");
                angleInfoLb.setText(angleFormat.format(Math.toDegrees(angleSl.getAngle())) + "º");
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
        controlPanel.add(angleSl);

        speedLb = new JLabel("Vel.: ", SwingConstants.RIGHT);
        speedLb.setSize(75, 20);
        speedLb.setLocation(500, 90);
        controlPanel.add(speedLb);

        speedInfoLb = new JLabel("15", SwingConstants.RIGHT);
        speedInfoLb.setSize(75, 20);
        speedInfoLb.setLocation(615, 90);
        controlPanel.add(speedInfoLb);

        speedSl = new JSlider(JSlider.HORIZONTAL, MIN_SPEED, MAX_SPEED, 5);
        speedSl.setSize(105, 20);
        speedSl.setLocation(570, 92);
        speedSl.addChangeListener((ChangeEvent e) -> {
            speedInfoLb.setText("" + speedSl.getValue());
        });
        controlPanel.add(speedSl);

        ballTypeLb = new JLabel("Tipo de bola: ", SwingConstants.RIGHT);
        ballTypeLb.setSize(75, 20);
        ballTypeLb.setLocation(870, 50);
        controlPanel.add(ballTypeLb);

        ballTypeCb = new JComboBox();
        ballTypeCb.addItem("Normal");
        ballTypeCb.addItem("Explosive");
        ballTypeCb.addItem("Bullet");
        ballTypeCb.setSize(75, 20);
        ballTypeCb.setLocation(870, 70);
        controlPanel.add(ballTypeCb);
    }

    /**
     * Crea el panel del lienzo del mapa
     */
    private void createMapPanel() {
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintMap(g);
            }
        };
        mapPanel.setPreferredSize(new Dimension(MAPPANEL_WIDTH, MAPPANEL_HEIGHT));
        mapPanel.setBackground(Color.BLACK);

        MouseAdapter ma;
        ma = new MouseAdapter() {

            private Point clickPoint;

            @Override
            public void mousePressed(MouseEvent e) {
                clickPoint = e.getPoint();
                selectedObj = getClickedItem(clickPoint);
                repaint();
                if (selectedObj != null) {
                    showOptionsForObj(clickPoint);
                } else {
                    switch (item) {
                        case "BALL":
                            int ballRadius = radiusSl.getValue();
                            int speed = speedSl.getValue();
                            float angle = (float) Math.toDegrees(angleSl.getAngle());
                            String ballType = "N";
                            switch (ballTypeCb.getSelectedItem().toString()) {
                                case "Normal":
                                    ballType = "N";
                                    break;
                                case "Explosive":
                                    ballType = "E";
                                    break;
                                case "Bullet":
                                    ballType = "B";
                                    break;
                                default:
                                    break;
                            }
                            ball = new Ball(clickPoint.x, clickPoint.y, speed, 0, ballRadius, angle, ballType);
                            break;
                        case "OBSTACLE":
                            obstacle = null;
                            break;
                        case "BOTTLENECK":
                            stopItem = null;
                            break;
                        default:
                            // nada
                            break;
                    }
                    mapPanel.repaint();
                    posXTf.setText("" + clickPoint.x);
                    posYTf.setText("" + clickPoint.y);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedObj != null) {

                } else {
                    Point dragPoint = e.getPoint();
                    int x, y, width, height;
                    switch (item) {
                        case "BALL":
                            //ball = dragPoint;
                            ball.setX(dragPoint.x);
                            ball.setY(dragPoint.y);
                            posXTf.setText("" + dragPoint.x);
                            posYTf.setText("" + dragPoint.y);
                            break;
                        case "OBSTACLE":
                            x = Math.min(clickPoint.x, dragPoint.x);
                            y = Math.min(clickPoint.y, dragPoint.y);
                            width = Math.max(clickPoint.x, dragPoint.x) - x;
                            if (width > MAX_WIDTH) {
                                width = MAX_WIDTH;
                            }
                            height = Math.max(clickPoint.y, dragPoint.y) - y;
                            if (height > MAX_HEIGHT) {
                                height = MAX_HEIGHT;
                            }
                            if (width > 0 && height > 0) {
                                if (obstacle == null) {
                                    obstacle = new Obstacle((float) x, (float) y, (float) width, (float) height);
                                } else {
                                    obstacle.setX(x);
                                    obstacle.setY(y);
                                    obstacle.setWidth(width);
                                    obstacle.setHeight(height);
                                }
                                widthTf.setText("" + width);
                                heightTf.setText("" + height);
                            }
                            break;
                        case "BOTTLENECK":
                            x = Math.min(clickPoint.x, dragPoint.x);
                            y = Math.min(clickPoint.y, dragPoint.y);
                            width = Math.max(clickPoint.x, dragPoint.x) - x;
                            if (width > MAX_WIDTH) {
                                width = MAX_WIDTH;
                            }
                            height = Math.max(clickPoint.y, dragPoint.y) - y;
                            if (height > MAX_HEIGHT) {
                                height = MAX_HEIGHT;
                            }
                            if (width > 0 && height > 0) {
                                if (stopItem == null) {
                                    stopItem = new StopItem(x, y, width, height);
                                } else {
                                    stopItem.setX(x);
                                    stopItem.setY(y);
                                    stopItem.setWidth(width);
                                    stopItem.setHeight(height);
                                }
                                widthTf.setText("" + width);
                                heightTf.setText("" + height);
                            }
                            break;
                        default:
                            // nada
                            break;
                    }
                    mapPanel.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedObj != null) {

                } else {
                    undoList.push(item);
                    switch (item) {
                        case "BALL":
                            if (isValidPosBall(ball)) {
                                balls.add(ball);
                                Point p1 = new Point(0, 0);
                                Point p2 = new Point(0, 0);
                                if (ball.getSpeed() != 0) {
                                    p1 = new Point((int) ball.getX(), (int) ball.getY());
                                    p2 = new Point((int) (ball.getX() + (ball.getSpeedx() * 3)), (int) (ball.getY() + (ball.getSpeedy() * 3)));
                                }
                                Point[] p = {p1, p2};
                                speedVecs.add(p);
                            } else {
                                JOptionPane.showMessageDialog(null, "Los objetos no pueden ser superpuestos ni colocados\ndemasiado cerca de los bordes de la pantalla.", "Error en la creación del objeto", JOptionPane.ERROR_MESSAGE);
                            }
                            ball = null;
                            break;
                        case "OBSTACLE":
                            if (obstacle != null) {
                                if (isValidPosObstacle(obstacle)) {
                                    obstacles.add(obstacle);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Los objetos no pueden ser superpuestos ni colocados\ndemasiado cerca de los bordes de la pantalla.", "Error en la creación del objeto", JOptionPane.ERROR_MESSAGE);
                                }
                                obstacle = null;
                            }
                            break;
                        case "BOTTLENECK":
                            if (stopItem != null) {
                                if (isValidPosStopItem(stopItem)) {
                                    stopItems.add(stopItem);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Los objetos no pueden ser superpuestos ni colocados\ndemasiado cerca de los bordes de la pantalla.", "Error en la creación del objeto", JOptionPane.ERROR_MESSAGE);
                                }
                                stopItem = null;
                            }
                            break;
                        default:
                            // nada
                            break;
                    }
                    undoBtn.setEnabled(true);
                    eraseBtn.setEnabled(true);
                    saveBtn.setEnabled(true);
                    mapPanel.repaint();
                }
            }
        };

        // Añade los escuchadores de clicks
        mapPanel.addMouseListener(ma);
        mapPanel.addMouseMotionListener(ma);
    }

    private Object getClickedItem(Point clickedPoint) {
        for (Ball ba : balls) {
            if (Math.sqrt((clickedPoint.x - ba.getX()) * (clickedPoint.x - ba.getX()) + (clickedPoint.y - ba.getY()) * (clickedPoint.y - ba.getY())) < ba.getRadius()) {
                selectedObjRect = new Rectangle((int) (ba.getX() - ba.getRadius()), (int) (ba.getY() - ba.getRadius()), (int) (ba.getRadius() * 2), (int) (ba.getRadius() * 2));
                return ba;
            }
        }
        for (Obstacle obs : obstacles) {
            Rectangle obsRec = new Rectangle((int) obs.getX(), (int) obs.getY(), (int) obs.getWidth(), (int) obs.getHeight());
            if (obsRec.contains(clickedPoint)) {
                selectedObjRect = obsRec;
                return obs;
            }
        }
        for (StopItem stop : stopItems) {
            Rectangle stopRec = new Rectangle((int) stop.getX(), (int) stop.getY(), (int) stop.getWidth(), (int) stop.getHeight());
            if (stopRec.contains(clickedPoint)) {
                selectedObjRect = stopRec;
                return stop;
            }
        }
        selectedObjRect = null;
        return null;
    }

    private void showOptionsForObj(Point click) {
        popUp.show(mapPanel, click.x, click.y);
    }

    private void modifyBall(Ball b) {
        JLabel titleLb = new JLabel("Bola", SwingConstants.RIGHT);
        titleLb.setFont(new Font("TimesRoman", Font.PLAIN, 25));

        JLabel speedLb = new JLabel("Velocidad: " + (int) b.getSpeed() + "    ", SwingConstants.RIGHT);
        JSlider speedSl = new JSlider(JSlider.HORIZONTAL, MIN_SPEED, MAX_SPEED, (int) b.getSpeed());
        speedSl.addChangeListener((ChangeEvent e) -> {
            speedLb.setText("Velocidad: " + (speedSl.getValue()) + "    ");
        });

        JLabel radiusLb = new JLabel("Radio: " + (int) b.getRadius() + "    ", SwingConstants.RIGHT);
        controlPanel.add(radiusLb);

        JSlider radiusSl = new JSlider(JSlider.HORIZONTAL, MIN_RADIUS, MAX_RADIUS, (int) b.getRadius());
        radiusSl.addChangeListener((ChangeEvent e) -> {
            radiusLb.setText("Radio: " + radiusSl.getValue() + "    ");
        });

        JLabel angleLb = new JLabel("Ángulo: " + b.getAngle() + "º    ");

        RoundSlider angleSl = new RoundSlider(Math.toRadians(b.getAngle() + 90));
        angleSl.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                DecimalFormat angleFormat = new DecimalFormat("0.0");
                angleLb.setText("Ángulo: " + angleFormat.format(Math.toDegrees(angleSl.getAngle())) + "º    ");
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });

        JLabel ballTypeLb = new JLabel("Tipo de bola: ", SwingConstants.RIGHT);

        JComboBox ballTypeCb = new JComboBox();
        ballTypeCb.addItem("Normal");
        ballTypeCb.addItem("Explosive");
        ballTypeCb.addItem("Bullet");
        switch (b.getType()) {
            case NORMAL:
                ballTypeCb.setSelectedIndex(0);
                break;
            case EXPLOSIVE:
                ballTypeCb.setSelectedIndex(1);
                break;
            case BULLET:
                ballTypeCb.setSelectedIndex(2);
                break;
            default:
                // nada
                break;
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 0, 0));
        panel.add(titleLb);
        panel.add(new JLabel(""));
        panel.add(radiusLb);
        panel.add(radiusSl);
        panel.add(speedLb);
        panel.add(speedSl);
        panel.add(angleLb);
        panel.add(angleSl);
        panel.add(ballTypeLb);
        panel.add(ballTypeCb);

        int ret = JOptionPane.showConfirmDialog(null, panel, "Ajustes generales del espacio", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (ret == JOptionPane.OK_OPTION) {
            b.setSpeed(speedSl.getValue(), (float) Math.toDegrees(angleSl.getAngle()));
            int bPos = getBallPos(b);
            speedVecs.get(bPos)[1].setLocation((int) (b.getX() + (b.getSpeedx() * 3)), (int) (b.getY() + (b.getSpeedy() * 3)));
            b.setRadius(radiusSl.getValue());
            switch (ballTypeCb.getSelectedItem().toString()) {
                case "Normal":
                    b.setType("N");
                    break;
                case "Explosive":
                    b.setType("E");
                    break;
                case "Bullet":
                    b.setType("B");
                    break;
                default:
                    break;
            }
        }
    }

    private void modifyObstacle(Obstacle o) {

    }

    private void modifyStopItem(StopItem si) {

    }

    /**
     * Comprueba si la bola está siendo colocada en una posición válida
     *
     * @param b
     * @return
     */
    private boolean isValidPosBall(Ball b) {
        if (b.getX() - b.getRadius() <= 0 + MIN_MARGIN
                || b.getY() - b.getRadius() <= 0 + MIN_MARGIN
                || b.getX() + b.getRadius() >= MAPPANEL_WIDTH - MIN_MARGIN
                || b.getY() + b.getRadius() >= MAPPANEL_HEIGHT - MIN_MARGIN) {
            return false;
        }
        for (Ball ba : balls) {
            if (Math.sqrt((b.getX() - ba.getX()) * (b.getX() - ba.getX()) + (b.getY() - ba.getY()) * (b.getY() - ba.getY())) < (b.getRadius() + ba.getRadius())) {
                return false;
            }
        }
        for (Obstacle obs : obstacles) {
            if (obs.intersects(b)) {
                return false;
            }
        }
        for (StopItem stop : stopItems) {
            if (stop.intersects(b)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Comprueba si el obstáculo está siendo colocado en una posición válida
     *
     * @param o
     * @return
     */
    private boolean isValidPosObstacle(Obstacle o) {
        if (o.getX() <= 0 + MIN_MARGIN
                || o.getY() <= 0 + MIN_MARGIN
                || o.getX() + o.getWidth() >= MAPPANEL_WIDTH - MIN_MARGIN
                || o.getY() + o.getHeight() >= MAPPANEL_HEIGHT - MIN_MARGIN) {
            return false;
        }
        Rectangle oR = new Rectangle((int) o.getX(), (int) o.getY(), (int) o.getWidth(), (int) o.getHeight());
        for (Ball ba : balls) {
            if (o.intersects(ba)) {
                return false;
            }
        }
        for (Obstacle obs : obstacles) {
            Rectangle obsR = new Rectangle((int) obs.getX(), (int) obs.getY(), (int) obs.getWidth(), (int) obs.getHeight());
            if (oR.intersects(obsR)) {
                return false;
            }
        }
        for (StopItem stop : stopItems) {
            Rectangle stopR = new Rectangle((int) stop.getX(), (int) stop.getY(), (int) stop.getWidth(), (int) stop.getHeight());
            if (oR.intersects(stopR)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Comprueba si el semáforo está siendo colocado en una posición válida
     *
     * @param si
     * @return
     */
    private boolean isValidPosStopItem(StopItem si) {
        if (si.getX() <= 0 + MIN_MARGIN
                || si.getY() <= 0 + MIN_MARGIN
                || si.getX() + si.getWidth() >= MAPPANEL_WIDTH - MIN_MARGIN
                || si.getY() + si.getHeight() >= MAPPANEL_HEIGHT - MIN_MARGIN) {
            return false;
        }
        Rectangle siR = new Rectangle((int) si.getX(), (int) si.getY(), (int) si.getWidth(), (int) si.getHeight());
        for (Ball ba : balls) {
            if (si.intersects(ba)) {
                return false;
            }
        }
        for (Obstacle obs : obstacles) {
            Rectangle obsR = new Rectangle((int) obs.getX(), (int) obs.getY(), (int) obs.getWidth(), (int) obs.getHeight());
            if (siR.intersects(obsR)) {
                return false;
            }
        }
        for (StopItem stop : stopItems) {
            Rectangle stopR = new Rectangle((int) stop.getX(), (int) stop.getY(), (int) stop.getWidth(), (int) stop.getHeight());
            if (siR.intersects(stopR)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Dibuja los objetos colocados en el lienzo del mapa
     *
     * @param g
     */
    private void paintMap(Graphics g) {

        g.drawImage(bgImg.getScaledInstance(MAPPANEL_WIDTH + 10, MAPPANEL_HEIGHT + 10, Image.SCALE_SMOOTH), 0, 0, null);

        // Pintado de los objetos ya existentes
        for (Ball b : balls) {
            //b.draw(g);
            b.drawImg(g, images);
        }
        for (Obstacle o : obstacles) {
            o.draw(g);
        }
        for (StopItem si : stopItems) {
            si.draw(g);
        }

        for (Point[] p : speedVecs) {
            if (p[0] != p[1]) {
                g.setColor(Color.white);
                g.drawLine(p[0].x, p[0].y, p[1].x, p[1].y);
                drawVecArrow((Graphics2D) g, p[1], p[0], Color.WHITE);
            }
        }

        // Pintado del objeto seleccionado
        if (ball != null) {
            //ball.draw(g);
            ball.drawImg(g, images);
        }
        if (obstacle != null) {
            obstacle.draw(g);
        }
        if (stopItem != null) {
            stopItem.draw(g);
        }

        if (selectedObjRect != null) {
            g.setColor(Color.YELLOW);
            g.drawRect(selectedObjRect.x, selectedObjRect.y, selectedObjRect.width, selectedObjRect.height);
        }
    }

    /**
     * Dibuja la forma de punta de flecha en los vectores de velocidad de las
     * bolas
     *
     * @param g2
     * @param head
     * @param tail
     * @param color
     */
    private void drawVecArrow(Graphics2D g2, Point head, Point tail, Color color) {
        double headAngle = Math.toRadians(30);
        double headLength = 12;
        g2.setPaint(color);
        double dy = head.y - tail.y;
        double dx = head.x - tail.x;
        double theta = Math.atan2(dy, dx);
        double x, y, rho = theta + headAngle;
        for (int j = 0; j < 2; j++) {
            x = head.x - headLength * Math.cos(rho);
            y = head.y - headLength * Math.sin(rho);
            g2.draw(new Line2D.Double(head.x, head.y, x, y));
            rho = theta - headAngle;
        }
    }

}
