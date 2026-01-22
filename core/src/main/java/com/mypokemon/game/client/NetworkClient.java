package com.mypokemon.game.client;

import java.io.*;
import java.net.*;

/**
 * Cliente de red que maneja la comunicación TCP/UDP con el servidor del juego.
 * Implementa descubrimiento automático de servidor mediante UDP (Beacon) y
 * comunicación persistente mediante TCP.
 */
public class NetworkClient {
    private static final int TCP_PORT = 54321;
    private static final int UDP_PORT = 54777;
    private static final String BEACON_MSG = "POKEMON_SERVER_DISCOVERY";

    private Socket tcpSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private volatile boolean listening = true;

    // Interface for callbacks
    /**
     * Interfaz para recibir callbacks cuando llegan mensajes del servidor.
     */
    public interface NetworkListener {
        /**
         * Se invoca cuando llega un mensaje del servidor.
         * 
         * @param msg El mensaje recibido como cadena de texto.
         */
        void onMessageReceived(String msg);
    }

    private NetworkListener listener;

    public void setListener(NetworkListener listener) {
        this.listener = listener;
    }

    /**
     * Listens for the Server's UDP Beacon to auto-discover IP.
     * Blocking call (should be run in a separate thread).
     */
    /**
     * Escucha la señal del faro (Beacon) UDP del servidor para auto-descubrir su
     * IP.
     * Esta llamada es bloqueante y debería ejecutarse en un hilo separado.
     * 
     * @return La dirección IP del servidor descubierto o "127.0.0.1" si falla o hay
     *         timeout.
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

    /**
     * Intenta establecer una conexión TCP con el servidor.
     * Envía el mensaje inicial de handshake con el nombre del jugador.
     * 
     * @param ip         IP del servidor.
     * @param playerName Nombre del jugador local.
     * @return true si la conexión fue exitosa.
     */
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
            System.err.println("[Client] Error conexión TCP: " + e.getMessage());
            return false;
        }
    }

    /**
     * Envía un mensaje de texto al servidor conectado mediante TCP.
     * 
     * @param msg El mensaje a enviar.
     */
    public void sendMessage(String msg) {
        try {
            if (out != null) {
                out.writeUTF(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bucle de escucha TCP que se ejecuta en su propio hilo.
     * Recibe mensajes entrantes y notifica al listener registrado.
     */
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

    /**
     * Detiene el cliente de red, cerrando sockets y terminando hilos de escucha.
     */
    public void stop() {
        listening = false;
        try {
            if (tcpSocket != null)
                tcpSocket.close();
        } catch (IOException e) {
        }
    }
}
