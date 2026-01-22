package com.mypokemon.game.servidor;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Servidor principal del juego multijugador.
 * Gestiona el ciclo de vida del servidor, descubrimiento UDP y conexiones TCP.
 * Coordina la comunicación entre los dos jugadores.
 */
public class ServidorJuego {
    private static final int TCP_PORT = 54321;
    private static final int UDP_PORT = 54777;
    private static final String BEACON_MESSAGE = "POKEMON_SERVER_DISCOVERY";

    private boolean isRunning = false;
    private ServerSocket serverSocket;
    private GestorCliente player1;
    private GestorCliente player2;

    public static void main(String[] args) {
        new ServidorJuego().start();
    }

    /**
     * Inicia el servidor, activando el discovery UDP y el listener TCP.
     */
    public void start() {
        isRunning = true;
        System.out.println("Iniciando Servidor de Juego (ServidorJuego)...");

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

    /**
     * Hilo dedicado a emitir un beacon UDP para que los clientes encuentren
     * el servidor automáticamente en la red local.
     */
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

    private java.util.Set<String> collectedResources = java.util.Collections.synchronizedSet(new java.util.TreeSet<>());

    /**
     * Maneja una nueva conexión entrante y asigna un slot de jugador (1 o 2).
     * 
     * @param socket Socket del cliente conectado.
     */
    private synchronized void handleConnection(Socket socket) {
        GestorCliente handler = new GestorCliente(socket, this);
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
            // Check if we can start match and LINK peers BEFORE starting threads
            if (player1 != null && player2 != null) {
                player1.setPeer(player2);
                player2.setPeer(player1);

                createSharedSave();

                // Send Start Signal
                player1.sendMessage("MATCH_START:1");
                player2.sendMessage("MATCH_START:2");
            }

            handler.start();
        } else {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Elimina un cliente desconectado y libera su slot.
     */
    public synchronized void removeClient(GestorCliente client) {
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

    /**
     * Recibe y procesa mensajes de información/acciones desde un cliente.
     * Reenvía movimientos y gestiona la recolección de recursos.
     */
    public void onInfoReceived(GestorCliente sender, String msg) {
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
        } else if (msg.equals("SAVE_GAME")) {
            System.out.println("Jugador " + sender.getPlayerName() + " guardó su progreso individualmente.");
            sender.sendMessage("SAVE_CONFIRMED");
        }
    }

    /**
     * Sincroniza el estado inicial de un cliente recién conectado o identificado.
     * Envía recursos ya recolectados e información del compañero si existe.
     */
    public synchronized void syncClientState(GestorCliente client) {
        // Send all collected resources to the new client
        if (!collectedResources.isEmpty()) {
            StringBuilder sb = new StringBuilder("SYNC_RESOURCES:");
            for (String id : collectedResources) {
                sb.append(id).append(",");
            }
            client.sendMessage(sb.toString());
        }

        // 1. Sync Peer Info TO the Client (Let the client know who the other person is)
        if (client.getPeer() != null && client.getPeer().getPlayerName() != null) {
            String pName = client.getPeer().getPlayerName();
            String pGen = client.getPeer().getGender();
            if (pGen == null)
                pGen = "CHICO";

            System.out
                    .println("[Server] Informando a " + client.getPlayerName() + " sobre " + pName + " (" + pGen + ")");
            client.sendMessage("PEER_INFO:" + pName + ":" + pGen);
        }

        // 2. Sync Client Info TO the Peer (Let the other person know this client is
        // ready)
        if (client.getPeer() != null && client.getPlayerName() != null) {
            String myName = client.getPlayerName();
            String myGen = client.getGender();
            if (myGen == null)
                myGen = "CHICO";

            System.out.println("[Server] Informando a " + client.getPeer().getPlayerName() + " sobre " + myName + " ("
                    + myGen + ")");
            client.getPeer().sendMessage("PEER_INFO:" + myName + ":" + myGen);
        }
    }

    private void createSharedSave() {
        System.out.println("Preparando sesion compartida...");
    }

    public synchronized void notifyPlayerName(int playerId, String name) {
    }

    /**
     * Verifica si un nombre de usuario ya está en uso por otro jugador conectado.
     */
    public synchronized boolean isNameTaken(String name, GestorCliente requester) {
        if (player1 != null && player1 != requester && name.equalsIgnoreCase(player1.getPlayerName())) {
            return true;
        }
        if (player2 != null && player2 != requester && name.equalsIgnoreCase(player2.getPlayerName())) {
            return true;
        }
        return false;
    }
}
