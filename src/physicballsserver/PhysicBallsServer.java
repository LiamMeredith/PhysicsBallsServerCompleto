/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physicballsserver;

import database.DBHandler;
import estadisticas.Estadisticas;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import map_generator.MapDesigner;
import org.physicballs.items.*;

/**
 *
 * @author Liam-Portatil
 */
public class PhysicBallsServer {

    /**
     * Global parameters
     */
    private static final int PORT = 11111;
    private MapaVirtual mapa = null;
    private ObjectOutputStream out;
    private Thread listener;
    private boolean open = false;
    private static DatagramSocket bcListener;
    private int stadisticID = 0;
    private Estadisticas statistics = null;
    private DBHandler db = null;
    private MapDesigner md = null;
    private String scenario = "Prueba_03";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new PhysicBallsServer();
    }

    /**
     * Public constructor
     */
    public PhysicBallsServer() {

//        String[] options = db.getSpaceList1();
//        String delMapName = (String) JOptionPane.showInputDialog(
//                null,
//                "Seleccione el mapa que desea eliminar",
//                "Eliminar mapa",
//                JOptionPane.PLAIN_MESSAGE,
//                null,
//                options,
//                options[0]);
//        if ((delMapName != null) && (delMapName.length() > 0)) {
//            scenario = delMapName;
//        }
        try {
            /**
             * Precaution initialization
             */
            mapa = new MapaVirtual(0, 0);

            //Objeto de Toni
            statistics = new Estadisticas();
            statistics.setVisible(true);

            //Objeto de Tore
            db = new DBHandler();

            //Objecto de miki gines
            String message = "Would like to open the map generator";
            int reply = JOptionPane.showConfirmDialog(null, message, "Map generator", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                md = new MapDesigner();
            }

            /**
             * Local variables
             */
            ServerSocket serverSock = new ServerSocket(PORT);
            Socket clientSock;
            String cliAddr;
            initListener();
            /**
             * While server alive
             */
            while (true) {
                System.out.println("Waiting for a client...");
                /**
                 * Open event that waits for socket petitions
                 */
                clientSock = serverSock.accept();

                cliAddr = clientSock.getInetAddress().getHostAddress();
                System.out.println("Client in: " + cliAddr);
                /**
                 * Clients Streams
                 */
                ObjectInputStream in = new ObjectInputStream(clientSock.getInputStream());
                out = new ObjectOutputStream(clientSock.getOutputStream());

                /**
                 * First client petition for registering type of client
                 */
                Object o = in.readObject();
                if (o instanceof String) {
                    String type = (String) o;
                    /**
                     * Switch that determines type of client and how to treat it
                     */
                    if (open || type.contentEquals("server_controller")) {
                        switch (type.toLowerCase()) {
                            case "modulo_visual":
                                /**
                                 * Creates new Modulo visual client with a
                                 * thread and delivers a response
                                 */
                                stadisticID++;
                                ModuloVisualThread mvt = new ModuloVisualThread(clientSock, cliAddr, in, out, mapa, stadisticID, statistics);
                                ArrayList<Walls.wall> flag = mapa.push(mvt);
                                if (flag == null) {
                                    out.writeObject(new Status(510, "No capacity in map"));
                                } else {
                                    Peticion reg = new Peticion("registration");
                                    reg.pushData(new Status(1, "Ok"));
                                    reg.pushData(flag);
                                    reg.pushData(scenario);
                                    reg.pushData(db.selectSpace(scenario));
                                    out.writeObject(reg);
                                }

                                break;
                            case "server_controller":
                                /**
                                 * Delivers an ok Status indicating that it can
                                 * send a petition object
                                 */
                                out.writeObject(new Status(1, "Ok"));
                                new ServerControllerThread(clientSock, cliAddr, in, out, mapa, this, db);
                                break;
                            case "client_controller":
                                /**
                                 * Delivers an ok Status indicating that it can
                                 * send a petition object
                                 */
                                out.writeObject(new Status(1, "Ok"));
                                new ClientControllerThread(clientSock, cliAddr, in, out, mapa);
                                break;
                            default:
                                out.writeObject(new Status(501, "Wrong type of client"));
                        }
                    } else {
                        out.writeObject(new Status(550, "Closed server"));
                        clientSock.close();
                    }
                } else {
                    out.writeObject(new Status(501, "Wrong type of client - wrong value"));
                    clientSock.close();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PhysicBallsServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PhysicBallsServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Opens access for clients
     */
    public void openServer() {
        open = true;
    }

    /**
     * Closes access for clients
     */
    public void closeServer() {
        open = false;
    }

    /**
     * Status getter
     *
     * @return
     */
    public boolean status() {
        return this.open;
    }

    /**
     * Overwrittable map, currently not in use
     *
     * @param mapa
     */
    public void setMapa(MapaVirtual mapa) {
        this.mapa = mapa;
    }

    /**
     * Sets scenario
     *
     * @param s
     */
    public void setScenario(String s) {
        this.scenario = s;
    }

    /**
     * Gets scenario
     *
     * @return
     */
    public String getScenario() {
        return this.scenario;
    }

    /**
     * Service that listens if someone connects via DataGram packet
     */
    private final void initListener() {

        this.listener = new Thread(new Runnable() {
            private DatagramSocket bcListener;

            @Override
            public void run() {
                try {
                    this.bcListener = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
                    this.bcListener.setBroadcast(true);
                    while (true) {
                        byte[] recvBuf = new byte[15000];
                        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                        try {
                            this.bcListener.receive(packet);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        String message = new String(packet.getData()).trim();
                        if (message.equals("/ping")) {
                            byte[] sendData;
                            sendData = "/ping".getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                            try {
                                this.bcListener.send(sendPacket);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println(message);
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });
        this.listener.start();
    }
}
