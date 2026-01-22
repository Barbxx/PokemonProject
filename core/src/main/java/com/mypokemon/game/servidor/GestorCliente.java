package com.mypokemon.game.servidor;

import java.io.*;
import java.net.Socket;

public class GestorCliente extends Thread {
    private Socket socket;
    private ServidorJuego server;
    private GestorCliente peer;
    private DataInputStream in;
    private DataOutputStream out;
    private int playerId;
    private String playerName;
    private String gender = "CHICO";

    public GestorCliente(Socket socket, ServidorJuego server) {
        this.socket = socket;
        this.server = server;
        try {
            out = new DataOutputStream(socket.getOutputStream()); // Initialize OUT first
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPeer(GestorCliente peer) {
        this.peer = peer;
    }

    public GestorCliente getPeer() {
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

    public void sendMessage(String msg) {
        try {
            if (out != null) {
                synchronized (out) {
                    out.writeUTF(msg);
                }
            }
        } catch (IOException e) {
            // Ignorar errores de escritura si se desconectƒÆ’‚Â³
        }
    }

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
                    // Delegar al servidor para lƒÆ’‚Â³gica centralizada (ValidaciƒÆ’‚Â³n, Estado, Broadcast)
                    server.onInfoReceived(this, msg);
                }
            }
        } catch (EOFException e) {
            System.out.println("Jugador " + playerId + " desconectado.");
        } catch (IOException e) {
            System.err.println("Error en conexiƒÆ’‚Â³n Jugador " + playerId + ": " + e.getMessage());
        } finally {
            close();
        }
    }

    private void checkAndCreateSave() {
        // La creaciƒÆ’‚Â³n automƒÆ’‚¡tica de archivo ha sido eliminada.
        // El archivo solo se crearƒÆ’‚¡ cuando ambos jugadores guarden explƒÆ’‚Â­citamente en el
        // juego.
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




