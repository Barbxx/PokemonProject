package com.mypokemon.game.client;

import java.io.*;
import java.net.*;

public class ClienteRed {
    private static final int TCP_PORT = 54321;
    private static final int UDP_PORT = 54777;
    private static final String BEACON_MSG = "POKEMON_SERVER_DISCOVERY";

    private Socket tcpSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private volatile boolean listening = true;

    // Interface for callbacks
    public interface EscuchaRed {
        void onMessageReceived(String msg);
    }

    private EscuchaRed listener;

    public void setListener(EscuchaRed listener) {
        this.listener = listener;
    }

    /**
     * Listens for the Server's UDP Beacon to auto-discover IP.
     * Blocking call (should be run in a separate thread).
     */
    public String discoverServerIP() {
        DatagramSocket socket = null;
        try {
            // Bind to UDP port
            // IMPORTANT: Do NOT use setReuseAddress(true) here for local testing logic to
            // work reliably.
            // We WANT the second instance to fail binding or timeout so it falls back to
            // localhost.
            socket = new DatagramSocket(null);

            try {
                socket.bind(new InetSocketAddress(UDP_PORT));
            } catch (SocketException bindEx) {
                // Port is busy - likely another local instance (Player 1) is running.
                // Fallback to localhost for local testing.
                System.out.println("[Client] Puerto 54777 ocupado. Asumiendo jugadora 2 en misma PC (Localhost).");
                return "127.0.0.1";
            }

            socket.setSoTimeout(1000); // Check every 1s

            byte[] buffer = new byte[1024];
            int retries = 0;
            int maxRetries = 3; // 3 seconds max wait

            System.out.println("[Client] Buscando seƒÂ±al Faro...");
            while (listening && retries < maxRetries) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String msg = new String(packet.getData(), 0, packet.getLength());
                    if (BEACON_MSG.equals(msg)) {
                        String serverIP = packet.getAddress().getHostAddress();
                        System.out.println("[Client] ‚¡Faro detectado! Servidor en: " + serverIP);
                        return serverIP;
                    }
                } catch (SocketTimeoutException e) {
                    retries++;
                    System.out.println("[Client] Buscando... (" + retries + "/" + maxRetries + ")");
                }
            }
            // If we timed out or loop ended
            System.out.println("[Client] Timeout buscando faro. Probando Localhost por defecto.");
            return "127.0.0.1";

        } catch (Exception e) {
            e.printStackTrace();
            // Safe fallback
            return "127.0.0.1";
        } finally {
            if (socket != null && !socket.isClosed())
                socket.close();
        }
    }

    public boolean connect(String ip, String playerName) {
        try {
            System.out.println("[Client] Conectando a TCP " + ip + ":" + TCP_PORT);
            tcpSocket = new Socket(ip, TCP_PORT);
            in = new DataInputStream(tcpSocket.getInputStream());
            out = new DataOutputStream(tcpSocket.getOutputStream());

            // Initial Handshake
            sendMessage("NAME:" + playerName);

            // Start listening for TCP messages
            new Thread(this::listenTcp, "Client-TCP-Listener").start();

            return true;
        } catch (IOException e) {
            System.err.println("[Client] Error conexiƒÂ³n TCP: " + e.getMessage());
            return false;
        }
    }

    public void sendMessage(String msg) {
        try {
            if (out != null) {
                out.writeUTF(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenTcp() {
        try {
            while (listening && !tcpSocket.isClosed()) {
                String msg = in.readUTF();
                if (listener != null) {
                    listener.onMessageReceived(msg);
                }
            }
        } catch (IOException e) {
            System.out.println("[Client] Desconectado del servidor.");
        }
    }

    public void stop() {
        listening = false;
        try {
            if (tcpSocket != null)
                tcpSocket.close();
        } catch (IOException e) {
        }
    }
}



