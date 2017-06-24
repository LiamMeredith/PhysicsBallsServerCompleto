/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physicballsserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.physicballs.items.*;

/**
 *
 * Class that manages incoming Modulo Visual Threads
 *
 * @author Liam-Portatil
 */
public class MapaVirtual {

    /**
     * Global parameters
     *
     */
    private int width = 0;
    private int height = 0;
    private ModuloVisualThread[][] visuales;
    private int[][] plantilla;

    /**
     * Constructor
     *
     * @param width
     * @param height
     */
    public MapaVirtual(int width, int height) {
        this.width = width;
        this.height = height;
        visuales = new ModuloVisualThread[height][width];
        plantilla = new int[height][width];
        clean();
    }

    /**
     * Empty the bidimensional array
     */
    private void clean() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                visuales[i][j] = null;
                plantilla[i][j] = 1;
            }
        }
    }

    /**
     * Inserts a new Modulo visual Thread in the virtual map If necessary it
     * will update the rest of the clients
     *
     * @param mvt
     * @return
     */
    public ArrayList<Walls.wall> push(ModuloVisualThread mvt) {
        boolean found = false;
        ArrayList<Walls.wall> w = null;
        for (int i = 0; i < height && !found; i++) {
            for (int j = 0; j < width && !found; j++) {
                if (visuales[i][j] == null && plantilla[i][j] == 1) {
                    found = true;
                    visuales[i][j] = mvt;
                    w = getAvailableWalls(j, i);
                    update(j, i);
                }
            }
        }
        if (!found) {
            System.out.println("No capacity");
            return null;
        }
        return w;
    }

    /**
     * Will return the walls that have a conection of a certain position
     *
     * @param x
     * @param y
     * @return
     */
    private ArrayList<Walls.wall> getAvailableWalls(int x, int y) {
        ArrayList<Walls.wall> w = new ArrayList<Walls.wall>();
        //left
        if (x - 1 >= 0 && visuales[y][x - 1] != null) {
            w.add(Walls.wall.LEFT);
        }
        //right
        if (x + 1 < width && visuales[y][x + 1] != null) {
            w.add(Walls.wall.RIGHT);
        }
        //up
        if (y - 1 >= 0 && visuales[y - 1][x] != null) {
            w.add(Walls.wall.TOP);
        }
        //down
        if (y + 1 < height && visuales[y + 1][x] != null) {
            w.add(Walls.wall.BOTTOM);
        }
        return w;
    }

    /**
     * Add walls to the neighbours screens
     *
     * @param x
     * @param y
     */
    public void update(int x, int y) {
        Peticion p;
        try {
            //left
            if (x - 1 >= 0 && visuales[y][x - 1] != null) {
                p = new Peticion("update_addWall");
                p.pushData(Walls.wall.RIGHT);
                visuales[y][x - 1].out.writeObject(p);
            }
            //right
            if (x + 1 < width && visuales[y][x + 1] != null) {
                p = new Peticion("update_addWall");
                p.pushData(Walls.wall.LEFT);
                visuales[y][x + 1].out.writeObject(p);
            }
            //up
            if (y - 1 >= 0 && visuales[y - 1][x] != null) {
                p = new Peticion("update_addWall");
                p.pushData(Walls.wall.BOTTOM);
                visuales[y - 1][x].out.writeObject(p);
            }
            //down
            if (y + 1 < height && visuales[y + 1][x] != null) {
                p = new Peticion("update_addWall");
                p.pushData(Walls.wall.TOP);
                visuales[y + 1][x].out.writeObject(p);
            }
        } catch (IOException ex) {
            Logger.getLogger(MapaVirtual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Moves windows position, Changes one position for the other
     *
     * @param w1
     * @param w2
     */
    public void moveWindow(int[] w1, int[] w2) {
        try {
            ModuloVisualThread visual1 = visuales[w1[1]][w1[0]];
            ModuloVisualThread visual2 = visuales[w2[1]][w2[0]];
            remove(w1);
            remove(w2);
            visuales[w2[1]][w2[0]] = visual1;
            visuales[w1[1]][w1[0]] = visual2;
            if (visuales[w1[1]][w1[0]] != null) {
                update(w1[0], w1[1]);
                Peticion p1 = new Peticion("update_walls");
                p1.pushData(new Status(1, "Ok"));
                p1.pushData(getAvailableWalls(w1[0], w1[1]));
                visuales[w1[1]][w1[0]].out.writeObject(p1);
            }
            if (visuales[w2[1]][w2[0]] != null) {
                update(w2[0], w2[1]);
                Peticion p2 = new Peticion("update_walls");
                p2.pushData(new Status(1, "Ok"));
                p2.pushData(getAvailableWalls(w2[0], w2[1]));
                visuales[w2[1]][w2[0]].out.writeObject(p2);
            }

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Moves ball from one screen to another
     *
     * @param mvt
     * @param w
     * @param b
     */
    public synchronized void move(ModuloVisualThread mvt, Walls.wall w, Ball b) {
        boolean found = false;
        Peticion p;
        try {
            for (int i = 0; i < height && !found; i++) {
                for (int j = 0; j < width && !found; j++) {
                    if (visuales[i][j] == mvt) {
                        found = true;
                        if (w == Walls.wall.TOP) {
                            p = new Peticion("addBall");
                            p.pushData(new Status(1, "Ok"));
                            p.pushData(b);
                            visuales[i - 1][j].out.writeObject(p);
                            visuales[i - 1][j].out.flush();
                            writeBall(visuales[i - 1][j], p);
                        }
                        if (w == Walls.wall.BOTTOM) {
                            p = new Peticion("addBall");
                            p.pushData(new Status(1, "Ok"));
                            p.pushData(b);
                            writeBall(visuales[i + 1][j], p);
                        }
                        if (w == Walls.wall.RIGHT) {
                            p = new Peticion("addBall");
                            p.pushData(new Status(1, "Ok"));
                            p.pushData(b);
                            writeBall(visuales[i][j + 1], p);
                        }
                        if (w == Walls.wall.LEFT) {
                            p = new Peticion("addBall");
                            p.pushData(new Status(1, "Ok"));
                            p.pushData(b);
                            writeBall(visuales[i][j - 1], p);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MapaVirtual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Adds ball to Modulo visual This method is similar to move, but it sends
     * it currently to the position in the Map and not in a wall
     *
     * @param window
     * @param b
     * @throws IOException
     */
    public synchronized void addBall(int[] window, Ball b) throws IOException {
        Peticion p = new Peticion("addBall");
        p.pushData(new Status(1, "Ok"));
        p.pushData(b);
        writeBall(visuales[window[1]][window[0]], p);
    }
    
    public synchronized void writeBall(ModuloVisualThread mvt, Peticion p){
        try {
            mvt.out.reset();
            mvt.out.writeObject(p);
            
        } catch (IOException ex) {
            Logger.getLogger(MapaVirtual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Removes window from virtual map using the thread reference
     *
     * @param mvt
     */
    public void remove(ModuloVisualThread mvt) {
        boolean found = false;
        Peticion p;
        try {
            for (int i = 0; i < height && !found; i++) {
                for (int j = 0; j < width && !found; j++) {
                    if (visuales[i][j] == mvt) {
                        visuales[i][j] = null;
                        found = true;
                        //left
                        if (j - 1 >= 0 && visuales[i][j - 1] != null) {
                            p = new Peticion("update_removeWall");
                            p.pushData(Walls.wall.RIGHT);
                            visuales[i][j - 1].out.writeObject(p);
                        }
                        //right
                        if (j + 1 < width && visuales[i][j + 1] != null) {
                            p = new Peticion("update_removeWall");
                            p.pushData(Walls.wall.LEFT);
                            visuales[i][j + 1].out.writeObject(p);
                        }
                        //up
                        if (i - 1 >= 0 && visuales[i - 1][j] != null) {
                            p = new Peticion("update_removeWall");
                            p.pushData(Walls.wall.BOTTOM);
                            visuales[i - 1][j].out.writeObject(p);
                        }
                        //down
                        if (i + 1 < height && visuales[i + 1][j] != null) {
                            p = new Peticion("update_removeWall");
                            p.pushData(Walls.wall.TOP);
                            visuales[i + 1][j].out.writeObject(p);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MapaVirtual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Removes window from virtual map using the window position
     *
     * @param window
     */
    public void remove(int[] window) {
        Peticion p;
        try {
            if (visuales[window[1]][window[0]] != null) {
                visuales[window[1]][window[0]] = null;
                //left
                if (window[0] - 1 >= 0 && visuales[window[1]][window[0] - 1] != null) {
                    p = new Peticion("update_removeWall");
                    p.pushData(Walls.wall.RIGHT);
                    visuales[window[1]][window[0] - 1].out.writeObject(p);
                }
                //right
                if (window[0] + 1 < width && visuales[window[1]][window[0] + 1] != null) {
                    p = new Peticion("update_removeWall");
                    p.pushData(Walls.wall.LEFT);
                    visuales[window[1]][window[0] + 1].out.writeObject(p);
                }
                //up
                if (window[1] - 1 >= 0 && visuales[window[1] - 1][window[0]] != null) {
                    p = new Peticion("update_removeWall");
                    p.pushData(Walls.wall.BOTTOM);
                    visuales[window[1] - 1][window[0]].out.writeObject(p);
                }
                //down
                if (window[1] + 1 < height && visuales[window[1] + 1][window[0]] != null) {
                    p = new Peticion("update_removeWall");
                    p.pushData(Walls.wall.TOP);
                    visuales[window[1] + 1][window[0]].out.writeObject(p);
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(MapaVirtual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the screens and the positions
     *
     * @return
     */
    public int[][] getWindows() {
        int[][] output = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (visuales[i][j] == null) {
                    output[i][j] = 0;
                } else {
                    output[i][j] = 1;
                }
            }
        }
        return output;
    }

    /**
     * Stablishes a template to indicate a pattern for the new screens
     *
     * @param p
     */
    public void setPlantilla(int[][] p) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                plantilla[i][j] = 0;
            }
        }
        for (int i = 0; i < p.length; i++) {
            plantilla[p[i][1]][p[i][0]] = 1;
        }
    }

    /**
     * Method and information given by tha data base
     *
     * @param s
     */
    public void setScenario(String s) {

    }

    /**
     * Devuelve un string con toda la configuracion
     *
     * @return
     */
    public String getSettings() {
        String output = "";
        output += "********************\n";
        output += "* DIMENSION        *\n";
        output += "********************\n";
        output += "\n";
        output += "*Width: " + this.width + "\n";
        output += "*Height: " + this.height + "\n";
        output += "\n";
        output += "\n";

        output += "********************\n";
        output += "* PLANTILLA        *\n";
        output += "********************\n";
        output += "\n";
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                output += plantilla[i][j] + " ";
            }
            output += "\n";
        }
        output += "\n";
        output += "\n";

        output += "********************\n";
        output += "* PANTALLAS        *\n";
        output += "********************\n";
        output += "\n";
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (visuales[i][j] == null) {
                    output += "x ";
                } else {
                    output += "p ";
                }
            }
            output += "\n";
        }
        output += "\n";
        output += "\n";

        return output;
    }
}
