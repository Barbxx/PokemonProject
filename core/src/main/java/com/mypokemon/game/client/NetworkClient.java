package com.mypokemon.game.client;

import java.io.*;
import java.net.*;

// Cliente de red principal que maneja la comunicación TCP y UDP con el servidor.

public class NetworkClient {
    private static final int PUERTO_TCP = 54321;
    private static final int PUERTO_UDP = 54777;
    private static final String MSG_FARO = "POKEMON_SERVER_DISCOVERY";

    private Socket socketTcp;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private volatile boolean escuchando = true;

    // Interfaz para manejar mensajes recibidos desde la red.
    
    public interface EscuchaRed {
        /**
         * Se llama cuando se recibe un mensaje del servidor.
         * 
         * @param msg El mensaje recibido.
         */
        void alRecibirMensaje(String msg);
    }

    private EscuchaRed escucha;

    /**
     * Establece el escuchador de eventos de red.
     * 
     * @param escucha El objeto que implementa EscuchaRed.
     */
    public void establecerEscucha(EscuchaRed escucha) {
        this.escucha = escucha;
    }

    /**
     * Busca la dirección IP del servidor en la red local mediante broadcast UDP.
     * 
     * @return La IP del servidor encontrada o "127.0.0.1" si no se detecta.
     */
    public String descubrirIPServidor() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(null);

            try {
                socket.bind(new InetSocketAddress(PUERTO_UDP));
            } catch (SocketException bindEx) {
                System.out.println("[Cliente] Puerto 54777 ocupado. Asumiendo jugadora 2 en misma PC (Localhost).");
                return "127.0.0.1";
            }

            socket.setSoTimeout(1000);

            byte[] buffer = new byte[1024];
            int intentos = 0;
            int maxIntentos = 3;

            System.out.println("[Cliente] Buscando señal Faro...");
            while (escuchando && intentos < maxIntentos) {
                try {
                    DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                    socket.receive(paquete);

                    String msg = new String(paquete.getData(), 0, paquete.getLength());
                    if (MSG_FARO.equals(msg)) {
                        String ipServidor = paquete.getAddress().getHostAddress();
                        System.out.println("[Cliente] ¡Faro detectado! Servidor en: " + ipServidor);
                        return ipServidor;
                    }
                } catch (SocketTimeoutException e) {
                    intentos++;
                    System.out.println("[Cliente] Buscando... (" + intentos + "/" + maxIntentos + ")");
                }
            }
            System.out.println("[Cliente] Tiempo agotado buscando faro. Probando Localhost por defecto.");
            return "127.0.0.1";

        } catch (Exception e) {
            e.printStackTrace();
            return "127.0.0.1";
        } finally {
            if (socket != null && !socket.isClosed())
                socket.close();
        }
    }

    /**
     * Conecta al servidor TCP especificado.
     * 
     * @param ip            Dirección IP del servidor.
     * @param nombreJugador Nombre que se enviará al conectar.
     * @return true si la conexión fue exitosa, false en caso contrario.
     */
    public boolean conectar(String ip, String nombreJugador) {
        try {
            System.out.println("[Cliente] Conectando a TCP " + ip + ":" + PUERTO_TCP);
            socketTcp = new Socket(ip, PUERTO_TCP);
            entrada = new DataInputStream(socketTcp.getInputStream());
            salida = new DataOutputStream(socketTcp.getOutputStream());

            enviarMensaje("NAME:" + nombreJugador);

            new Thread(this::escucharTcp, "Cliente-TCP-Escucha").start();

            return true;
        } catch (IOException e) {
            System.err.println("[Cliente] Error conexión TCP: " + e.getMessage());
            return false;
        }
    }

    /**
     * Envía un mensaje al servidor a través del socket TCP.
     * 
     * @param msg El mensaje a enviar.
     */
    public void enviarMensaje(String msg) {
        try {
            if (salida != null) {
                salida.writeUTF(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mantiene un hilo escuchando mensajes entrantes a través del socket TCP.
     * Los mensajes recibidos se pasan al escucha registrado.
     */
    private void escucharTcp() {
        try {
            while (escuchando && !socketTcp.isClosed()) {
                String msg = entrada.readUTF();
                if (escucha != null) {
                    escucha.alRecibirMensaje(msg);
                }
            }
        } catch (IOException e) {
            System.out.println("[Cliente] Desconectado del servidor.");
        }
    }

    /**
     * Detiene la conexión de red y libera los recursos del socket TCP.
     */
    public void detener() {
        escuchando = false;
        try {
            if (socketTcp != null)
                socketTcp.close();
        } catch (IOException e) {
        }
    }
}
