package com.mypokemon.game.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    private static final int PORT = 54321;
    private boolean isRunning = false;
    private ServerSocket serverSocket;

    // Key: Password, Value: Host Handler
    public static Map<String, ClientHandler> waitingHosts = new ConcurrentHashMap<>();

    // Key: Password, Value: Game Session (Active)
    // For now we just need to pair them up.

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            isRunning = true;
            System.out.println("Server started on port " + PORT);

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Server Error: " + e.getMessage());
            e.printStackTrace(); // Handle port busy if multiple local hosts (use dynamic?) but user said
                                 // "Servidor" implying central or 1 per app?
            // "Al elegir el boton Compartido se presentaran las opciones".
            // If Local Server is started by Client, it's peer-to-peer hosting?
            // "Servidor reservará ese archivo...". Likely the App acts as server for P2P.
        }
    }

    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized boolean registerHost(String password, ClientHandler host) {
        if (waitingHosts.containsKey(password))
            return false; // Duplicate pass
        waitingHosts.put(password, host);
        return true;
    }

    public static synchronized ClientHandler joinGuest(String password, ClientHandler guest) {
        if (waitingHosts.containsKey(password)) {
            ClientHandler host = waitingHosts.remove(password); // Remove from waiting, they are now paired
            return host;
        }
        return null;
    }
}
