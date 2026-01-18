package com.mypokemon.game.network;

import java.io.*;
import java.net.Socket;

public class NetworkClient {
    private static NetworkClient instance;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    private NetworkClient() {
    }

    public static synchronized NetworkClient getInstance() {
        if (instance == null) {
            instance = new NetworkClient();
        }
        return instance;
    }

    /**
     * Connects to the server and creates/verifies the save file.
     * 
     * @param saveName Name of the game/save file.
     * @return true if connection and verification successful.
     */
    public boolean connect(String saveName) {
        try {
            // Assume localhost for Solo mode, or configured IP for Shared
            // For now, hardcoded logic for local testing
            socket = new Socket("localhost", 54321);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            // Send handshake/save name
            out.writeUTF(saveName);

            // Wait for confirmation
            String response = in.readUTF();
            return "OK".equals(response);

        } catch (IOException e) {
            System.err.println("NetworkClient Connection Error: " + e.getMessage());
            return false;
        }
    }

    public void sendPokedexUpdate(String jsonData) {
        if (socket != null && out != null && !socket.isClosed()) {
            new Thread(() -> {
                try {
                    synchronized (out) {
                        out.writeUTF("POKEDEX_UPDATE");
                        out.writeUTF(jsonData);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    // Helper to start server locally if needed
    public static void startLocalServer() {
        new Thread(() -> {
            new GameServer().start();
        }).start();
    }
}
