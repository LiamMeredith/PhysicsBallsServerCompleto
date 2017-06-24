/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physicballsserver;

import database.DBHandler;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import org.physicballs.items.Peticion;
import org.physicballs.items.Status;

/**
 *
 * @author Liam-Portatil
 */
public class ServerControllerThread extends ClientThread {

    MapaVirtual mapa;
    private PhysicBallsServer server;
    private DBHandler db = null;

    public ServerControllerThread(Socket s, String cliAddr, ObjectInputStream in, ObjectOutputStream out, MapaVirtual mapa, PhysicBallsServer server, DBHandler db) {
        super(s, cliAddr);
        this.in = in;
        this.out = out;
        this.mapa = mapa;
        this.server = server;
        this.db = db;
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
                        case "open_map":
                            mapa = new MapaVirtual((int) peticion.getObject(0), (int) peticion.getObject(1));
                            server.setMapa(mapa);
                            if (peticion.getData().size() > 2) {
                                System.out.println((String) peticion.getObject(2));
                                server.setScenario((String) peticion.getObject(2));
                            }
                            out.writeObject(new Status(1, "Ok"));
                            break;
                        case "set_plantilla":
                            mapa.setPlantilla((int[][]) peticion.getObject(0));
                            out.writeObject(new Status(1, "Ok"));
                            break;
                        case "open_server":
                            server.openServer();
                            out.writeObject(new Status(1, "Ok"));
                            break;
                        case "close_server":
                            server.closeServer();
                            out.writeObject(new Status(1, "Ok"));
                            break;
                        case "get_settings":
                            Peticion p = new Peticion("get_settings");
                            String outString = mapa.getSettings();
                            outString += "Status: " + server.status();
                            outString += "\n";
                            outString += "Scenario: " + server.getScenario();
                            outString += "\n";
                            p.pushData(outString);
                            out.writeObject(p);
                            break;
                        case "move_window":
                            mapa.moveWindow((int[]) peticion.getObject(0), (int[]) peticion.getObject(1));
                            out.writeObject(new Status(1, "Ok"));
                            break;
                        case "remove_window":
                            mapa.remove((int[]) peticion.getObject(0));
                            out.writeObject(new Status(1, "Ok"));
                            break;
                        case "get_scenarios":
                            Peticion p1 = new Peticion("get_scenarios");
                            p1.pushData(db.getSpaceList1());
                            out.writeObject(p1);
                            break;
                        case "echo":
                            out.writeObject(new Status(2, (String) peticion.getObject(0)));
                            break;
                        default:
                            out.writeObject(new Status(505, "Petition - nonexistent option"));
                    }
                } else {
                    out.writeObject(new Status(505, "Petition - wrong type"));
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
            System.out.println("Bye " + this.cliAddr);
            this.clientSock.close();
        } catch (ClassNotFoundException ex) {
            out.writeObject(new Status(503, "Error with the petition"));
        }
    }
}
