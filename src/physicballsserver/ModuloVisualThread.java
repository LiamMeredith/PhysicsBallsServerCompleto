/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physicballsserver;

import estadisticas.Estadisticas;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import org.physicballs.items.*;

/**
 *
 * @author Liam-Portatil
 */
public class ModuloVisualThread extends ClientThread {

    /**
     * Global parameters
     */
    String type = "Modulo visual";
    MapaVirtual mapa;
    int nPantalla = -1;
    private Estadisticas statistics;

    /**
     * Constructor
     *
     * @param s
     * @param cliAddr
     * @param in
     * @param out
     */
    public ModuloVisualThread(Socket s, String cliAddr, ObjectInputStream in, ObjectOutputStream out, MapaVirtual mapa, int id, Estadisticas e) {
        super(s, cliAddr);
        this.in = in;
        this.out = out;
        this.mapa = mapa;
        this.nPantalla = id;
        this.statistics = e;
        this.start();
    }

    /**
     * Client thread cycle
     */
    @Override
    public void run() {
        try {
            processClient(in, out);
            clientSock.close();
        } catch (Exception e) {
            System.out.println("process" + e);
        }
    }

    /**
     * Out to process client streams
     *
     * @param in
     * @param out
     */
    private void processClient(ObjectInputStream in, ObjectOutputStream out) throws IOException {
        try {
            while (live) {
                /**
                 * Receive petition
                 */
                Object o = (Peticion) in.readObject();
                if (o instanceof Peticion) {
                    peticion = (Peticion) o;
                    switch (peticion.getAccion().toLowerCase()) {
                        case "enviar_pelota":
                            mapa.move(this, (Walls.wall) peticion.getObject(1), (Ball) peticion.getObject(0));
                            break;
                        case "get_windows":
                            Peticion p = new Peticion("get_windows");
                            p.pushData(new Status(1, "Ok"));
                            p.pushData(mapa.getWindows());
                            out.writeObject(p);
                            break;
                        case "enviar_estadisticas":
                            StatisticsData d = (StatisticsData) peticion.getObject(0);
                            d.setnPantalla(nPantalla);
                            statistics.setData(d);
                            //System.out.println(d.velocitat);
                            break;
                        default:
                            out.writeObject(new Status(504, "NonExistent action"));
                    }
                } else {
                    out.writeObject(new Status(505, "Petition - wrong value"));
                }
            }
        } catch (IOException ex) {
            System.out.println("Bye " + this.cliAddr);
            mapa.remove(this);
            statistics.Disconect(nPantalla);
            this.clientSock.close();
        } catch (ClassNotFoundException ex) {
            out.writeObject(new Status(503, "Error with the petition"));
        }
    }
}
