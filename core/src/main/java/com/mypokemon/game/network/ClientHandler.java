package com.mypokemon.game.network;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private ClientHandler peer; // The other player
    private DataInputStream in;
    private DataOutputStream out;
    private boolean isHost = false;
    private Socket socket;
    private String saveName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void setPeer(ClientHandler peer) {
        this.peer = peer;
    }

    public ClientHandler getPeer() {
        return peer;
    }

    public synchronized void notifyPeerConnected() {
        notifyAll();
    }

    public void sendMessage(String msg) {
        try {
            synchronized (out) {
                out.writeUTF(msg);
            }
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            String action = in.readUTF();

            if ("CREATE".equals(action)) {
                String gameName = in.readUTF();
                String password = in.readUTF();
                saveName = gameName;
                isHost = true;

                if (GameServer.registerHost(password, this)) {
                    out.writeUTF("SESSION_CREATED");
                    System.out.println("Host registered: " + gameName);

                    // Wait for guest
                    synchronized (this) {
                        while (peer == null) {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                    System.out.println("Guest joined session " + gameName);
                    // Send START to Host? Client might be polling or waiting.
                    // For now, Host Logic in Client proceeds to Intro. "Server blocks map".
                    // We can implement block by not sending map data? Or client logic handles it.
                    // Client logic: "Servidor bloqueará el acceso al mapa... hasta completar cupo".
                    // So Client checks if (p2Connected).

                } else {
                    out.writeUTF("ERROR_DUPLICATE_BEYOND_REPAIR");
                    return;
                }

            } else if ("JOIN".equals(action)) {
                String password = in.readUTF();
                ClientHandler hostHandler = GameServer.joinGuest(password, this);

                if (hostHandler != null) {
                    this.peer = hostHandler;
                    hostHandler.setPeer(this);
                    saveName = hostHandler.saveName; // Share same game name

                    hostHandler.notifyPeerConnected();
                    out.writeUTF("JOIN_SUCCESS");
                    System.out.println("Guest joined host for pass " + password);
                } else {
                    out.writeUTF("JOIN_FAIL");
                    return;
                }
            } else if ("SOLO".equals(action)) {
                saveName = in.readUTF();
                out.writeUTF("OK");
                // Proceed to SOLO loop
            }

            // GAME LOOP - Relay Messages
            // Simple echo server between peers
            while (!socket.isClosed()) {
                try {
                    String msg = in.readUTF();
                    // Logic for Resources/Movement relay
                    // If peer exists, forward
                    if (peer != null) {
                        peer.sendMessage(msg);
                    }

                    if ("POKEDEX_UPDATE".equals(msg)) { // Legacy support
                        // handle save
                        in.readUTF(); // data
                    }
                } catch (EOFException e) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("ClientHandler Error: " + e.getMessage());
        } finally {
            try {
                if (socket != null && !socket.isClosed())
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // If host disconnects, maybe notify peer?
            if (peer != null) {
                peer.sendMessage("DISCONNECT");
                // Remove from waiting if was waiting
            }
        }
    }
}
