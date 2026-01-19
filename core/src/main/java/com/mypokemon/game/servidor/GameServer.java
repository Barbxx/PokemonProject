package com.mypokemon.game.servidor;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    private static final int TCP_PORT = 54321;
    private static final int UDP_PORT = 54777;
    private static final String BEACON_MESSAGE = "POKEMON_SERVER_DISCOVERY";

    private boolean isRunning = false;
    private ServerSocket serverSocket;
    private ClientHandler player1;
    private ClientHandler player2;

    public static void main(String[] args) {
        new GameServer().start();
    }

    public void start() {
        isRunning = true;
        System.out.println("Iniciando Servidor de Juego (GameServer)...");

        // 1. Iniciar Hilo de Discovery UDP (Faro)
        new Thread(this::runUdpBeacon, "UDP-Beacon-Thread").start();

        // 2. Iniciar Servidor TCP
        try {
            serverSocket = new ServerSocket(TCP_PORT);
            System.out.println("Servidor TCP escuchando en puerto " + TCP_PORT);

            while (isRunning) {
                if (player1 != null && player2 != null) {
                    // Sala llena
                    Thread.sleep(100);
                    continue;
                }

                System.out.println("Esperando jugadores...");
                Socket clientSocket = serverSocket.accept();
                handleConnection(clientSocket);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void runUdpBeacon() {
        DatagramSocket udpSocket = null;
        try {
            udpSocket = new DatagramSocket();
            udpSocket.setBroadcast(true);
            byte[] buffer = BEACON_MESSAGE.getBytes();

            System.out.println("Faro UDP activo. Emitiendo señal...");

            while (isRunning) {
                try {
                    // Broadcast a 255.255.255.255
                    InetAddress broadcastAddr = InetAddress.getByName("255.255.255.255");
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddr, UDP_PORT);
                    udpSocket.send(packet);

                    // Intervalo de emisión
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.err.println("Error en Beacon UDP: " + e.getMessage());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } finally {
            if (udpSocket != null && !udpSocket.isClosed()) {
                udpSocket.close();
            }
        }
    }

    private java.util.Set<String> collectedResources = java.util.concurrent.ConcurrentHashMap.newKeySet();

    private synchronized void handleConnection(Socket socket) {
        ClientHandler handler = new ClientHandler(socket, this);
        boolean assigned = false;

        if (player1 == null) {
            player1 = handler;
            player1.setPlayerId(1);
            assigned = true;
            System.out.println("Jugador 1 conectado: " + socket.getInetAddress());
        } else if (player2 == null) {
            player2 = handler;
            player2.setPlayerId(2);
            assigned = true;
            System.out.println("Jugador 2 conectado: " + socket.getInetAddress());
        }

        if (assigned) {
            handler.start();

            // Check if we can start match
            if (player1 != null && player2 != null) {
                // Ambos conectados -> Emparejar
                player1.setPeer(player2);
                player2.setPeer(player1);

                createSharedSave();

                // Send Start Signal
                // Note: If a player was already waiting, they receive START signal again.
                // The client should handle duplicate START signals (idempotent transition).
                player1.sendMessage("MATCH_START:1");
                player2.sendMessage("MATCH_START:2");
            }
        } else {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    public synchronized void removeClient(ClientHandler client) {
        if (player1 == client) {
            player1 = null;
            System.out.println("Jugador 1 desconectado. Slot liberado.");
            if (player2 != null)
                player2.setPeer(null); // Unlink
        } else if (player2 == client) {
            player2 = null;
            System.out.println("Jugador 2 desconectado. Slot liberado.");
            if (player1 != null)
                player1.setPeer(null); // Unlink
        }
    }

    // Called by ClientHandler
    public void onInfoReceived(ClientHandler sender, String msg) {
        if (msg.startsWith("MOVE:")) {
            // Forward movement exactly as is to peer
            if (sender.getPeer() != null) {
                sender.getPeer().sendMessage(msg);
            }
        } else if (msg.startsWith("COLLECT:")) {
            String resId = msg.substring(8);
            System.out.println("Recurso recolectado: " + resId);
            collectedResources.add(resId);

            // Broadcast removal to ALL (including sender, for confirmation/consistency)
            String cmd = "RESOURCE_REMOVED:" + resId;
            if (player1 != null)
                player1.sendMessage(cmd);
            if (player2 != null)
                player2.sendMessage(cmd);
        }
    }

    public synchronized void syncClientState(ClientHandler client) {
        // Send all collected resources to the new client
        if (!collectedResources.isEmpty()) {
            StringBuilder sb = new StringBuilder("SYNC_RESOURCES:");
            for (String id : collectedResources) {
                sb.append(id).append(",");
            }
            client.sendMessage(sb.toString());
        }

        // Sync Peer Identification (Name)
        if (client.getPeer() != null && client.getPeer().getPlayerName() != null) {
            String pName = client.getPeer().getPlayerName();
            String pGen = client.getPeer().getGender();
            if (pGen == null)
                pGen = "CHICO";

            // Tell this client who the peer is
            client.sendMessage("PEER_INFO:" + pName + ":" + pGen);

            // Tell the peer who this client is
            String myName = client.getPlayerName();
            String myGen = client.getGender();
            if (myGen == null)
                myGen = "CHICO";

            // (We do this here to ensure mutual visibility as soon as both are ready)
            client.getPeer().sendMessage("PEER_INFO:" + myName + ":" + myGen);
        }
    }

    private void createSharedSave() {
        System.out.println("Preparando sesion compartida...");
    }

    public synchronized void notifyPlayerName(int playerId, String name) {
    }
}
