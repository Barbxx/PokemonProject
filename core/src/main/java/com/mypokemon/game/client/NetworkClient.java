package com.mypokemon.game.client;

import java.io.*;
import java.net.*;

/**
 * Alias para ClienteRed - mantiene compatibilidad con código existente.
 * 
 * @deprecated Use ClienteRed instead
 */
@Deprecated
public class NetworkClient {
    private static final int TCP_PORT = 54321;
    private static final int UDP_PORT = 54777;
    private static final String BEACON_MSG = "POKEMON_SERVER_DISCOVERY";

    private Socket tcpSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private volatile boolean listening = true;

    public interface EscuchaRed {
        void onMessageReceived(String msg);
    }

    private EscuchaRed listener;

    public void setListener(EscuchaRed listener) {
        this.listener = listener;
    }

    public String discoverServerIP() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(null);

            try {
                socket.bind(new InetSocketAddress(UDP_PORT));
            } catch (SocketException bindEx) {
                System.out.println("[Client] Puerto 54777 ocupado. Asumiendo jugadora 2 en misma PC (Localhost).");
                return "127.0.0.1";
            }

            socket.setSoTimeout(1000);

            byte[] buffer = new byte[1024];
            int retries = 0;
            int maxRetries = 3;

            System.out.println("[Client] Buscando señal Faro...");
            while (listening && retries < maxRetries) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String msg = new String(packet.getData(), 0, packet.getLength());
                    if (BEACON_MSG.equals(msg)) {
                        String serverIP = packet.getAddress().getHostAddress();
                        System.out.println("[Client] ¡Faro detectado! Servidor en: " + serverIP);
                        return serverIP;
                    }
                } catch (SocketTimeoutException e) {
                    retries++;
                    System.out.println("[Client] Buscando... (" + retries + "/" + maxRetries + ")");
                }
            }
            System.out.println("[Client] Timeout buscando faro. Probando Localhost por defecto.");
            return "127.0.0.1";

        } catch (Exception e) {
            e.printStackTrace();
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

            sendMessage("NAME:" + playerName);

            new Thread(this::listenTcp, "Client-TCP-Listener").start();

            return true;
        } catch (IOException e) {
            System.err.println("[Client] Error conexión TCP: " + e.getMessage());
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
