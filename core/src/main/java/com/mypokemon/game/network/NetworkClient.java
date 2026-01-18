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
    public static void startHostMode(String gameName, String password) {
        startLocalServer();
        try {
            Thread.sleep(200);
        } catch (Exception e) {
        } // Wait for server boot

        getInstance().connectAndSetup("CREATE", gameName, password);
    }

    public static boolean connectToHost(String password) {
        // Assume localhost for now, or add IP param later
        return getInstance().connectAndSetup("JOIN", "Unknown", password);
    }

    private boolean connectAndSetup(String action, String gameName, String password) {
        try {
            socket = new Socket("localhost", 54321);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            // Protocol: ACTION | DATA1 | DATA2
            out.writeUTF(action);
            if (action.equals("CREATE")) {
                out.writeUTF(gameName);
                out.writeUTF(password);
                String resp = in.readUTF(); // Expect SESSION_CREATED
                return resp.equals("SESSION_CREATED");
            } else if (action.equals("JOIN")) {
                out.writeUTF(password); // Just pass for join
                String resp = in.readUTF(); // Expect JOIN_SUCCESS
                return resp.equals("JOIN_SUCCESS");
            } else {
                // connect() legacy
                out.writeUTF(gameName);
                String resp = in.readUTF();
                return resp.equals("OK");
            }

        } catch (IOException e) {
            System.err.println("Connection Failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Legacy Connect (Solo)
     */
    public boolean connect(String saveName) {
        return connectAndSetup("SOLO", saveName, "");
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

    public void close() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startLocalServer() {
        new Thread(() -> {
            new GameServer().start();
        }).start();
    }
}
