package com.mypokemon.game.servidor;

import java.io.*;
import java.net.Socket;

/**
 * Maneja la conexión individual de un cliente en el lado del servidor.
 * Se ejecuta en su propio hilo y gestiona la comunicación bidireccional
 * con el jugador conectado.
 */
public class ClientHandler extends Thread {
    private Socket socket;
    private GameServer server;
    /** Referencia al otro cliente conectado (para partidas de 2 jugadores). */
    private ClientHandler peer;
    private DataInputStream in;
    private DataOutputStream out;
    private int playerId;
    private String playerName;
    private String gender = "CHICO";

    /**
     * Constructor del manejador de cliente.
     * 
     * @param socket Socket conectado al cliente.
     * @param server Referencia al servidor principal.
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
     * Asigna el compañero (peer) de este cliente.
     * 
     * @param peer El otro ClientHandler.
     */
    public void setPeer(ClientHandler peer) {
        this.peer = peer;
    }

    public ClientHandler getPeer() {
        return peer;
    }

    public void setPlayerId(int id) {
        this.playerId = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setGender(String sub) {
        this.gender = sub;
    }

    public String getGender() {
        return gender;
    }

    /**
     * Envía un mensaje al cliente de forma thread-safe.
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
     * Bucle principal de escucha de mensajes del cliente.
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

    /**
     * Método placeholder para lógica de guardado automático (heredado).
     */
    private void checkAndCreateSave() {
        // La creación automática de archivo ha sido eliminada.
        // El archivo solo se creará cuando ambos jugadores guarden explícitamente en el
        // juego.
    }

    /**
     * Cierra la conexión y libera recursos.
     */
    private void close() {
        server.removeClient(this);
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
        }
    }
}
