package com.mypokemon.game.servidor;

import java.io.*;
import java.net.Socket;

/**
 * Manejador de cliente en el servidor. Cada instancia corre en su propio hilo
 * y gestiona la comunicación con un jugador específico.
 */
public class ClientHandler extends Thread {
    private Socket socket;
    private GameServer server;
    private ClientHandler peer;
    private DataInputStream in;
    private DataOutputStream out;
    private int playerId;
    private String playerName;
    private String gender = "CHICO";

    /**
     * Constructor del manejador de cliente.
     * 
     * @param socket Sockets de conexión TCP.
     * @param server Instancia del servidor principal.
     */
    public ClientHandler(Socket socket, GameServer server) {
        this.socket = socket;
        this.server = server;
        try {
            out = new DataOutputStream(socket.getOutputStream()); // Initialize OUT first
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Establece el compañero (jugador rival) para la sincronización directa.
     * 
     * @param peer Instancia del otro manejador de cliente.
     */
    public void setPeer(ClientHandler peer) {
        this.peer = peer;
    }

    /**
     * Obtiene el manejador del compañero conectado.
     * 
     * @return Instancia del compañero o null si no hay.
     */
    public ClientHandler getPeer() {
        return peer;
    }

    /**
     * Establece el identificador numérico del jugador (1 o 2).
     * 
     * @param id ID del jugador.
     */
    public void setPlayerId(int id) {
        this.playerId = id;
    }

    /**
     * Obtiene el nombre del jugador manejado por esta instancia.
     * 
     * @return Nombre del jugador.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Establece el género del avatar del jugador.
     * 
     * @param sub Género (ej: "CHICO", "CHICA").
     */
    public void setGender(String sub) {
        this.gender = sub;
    }

    /**
     * Obtiene el género del avatar del jugador.
     * 
     * @return Género actual.
     */
    public String getGender() {
        return gender;
    }

    /**
     * Envía un mensaje UTF al cliente remoto de forma sincronizada.
     * 
     * @param msg Mensaje a enviar.
     */
    public void sendMessage(String msg) {
        try {
            if (out != null) {
                synchronized (out) {
                    out.writeUTF(msg);
                }
            }
        } catch (IOException e) {
            // Ignorar errores de escritura si se desconectó
        }
    }

    /**
     * Bucle principal de escucha de mensajes entrantes del cliente.
     * Procesa comandos de identidad, nombres y delega lógica de juego al servidor.
     */
    @Override
    public void run() {
        try {
            while (true) {
                // Leer mensaje
                String msg = in.readUTF();

                if (msg.startsWith("IDENTITY:")) {
                    String[] parts = msg.split(":");
                    if (parts.length >= 2)
                        this.playerName = parts[1];
                    if (parts.length >= 3)
                        this.gender = parts[2];

                    System.out.println("Jugador " + playerId + " ID recibido: " + playerName + " - " + gender);
                    checkAndCreateSave();
                    // Sync request
                    server.syncClientState(this);
                } else if (msg.startsWith("CHECK_NAME:")) {
                    String nameToCheck = msg.substring(11);
                    boolean isTaken = server.isNameTaken(nameToCheck, this);
                    if (isTaken) {
                        sendMessage("NAME_TAKEN");
                    } else {
                        sendMessage("NAME_OK");
                    }
                } else if (msg.startsWith("NAME:")) {
                    this.playerName = msg.substring(5);
                    System.out.println("Jugador " + playerId + " nombre recibido: " + playerName);
                    checkAndCreateSave();
                    // Sync request
                    server.syncClientState(this);
                } else {
                    // Delegar al servidor para lógica centralizada (Validación, Estado, Broadcast)
                    server.onInfoReceived(this, msg);
                }
            }
        } catch (EOFException e) {
            System.out.println("Jugador " + playerId + " desconectado.");
        } catch (IOException e) {
            System.err.println("Error en conexión Jugador " + playerId + ": " + e.getMessage());
        } finally {
            close();
        }
    }

    private void checkAndCreateSave() {
    }

    private void close() {
        server.removeClient(this);
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
        }
    }
}
