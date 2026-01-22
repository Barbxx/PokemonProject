package com.mypokemon.game.servidor;

import java.io.*;
import java.net.Socket;

/**
 * Gestiona la conexión individual de un cliente (jugador) con el servidor.
 * Hereda de Thread para manejar cada cliente en un hilo separado.
 */
public class GestorCliente extends Thread {
    /** Canal de comunicación con el cliente. */
    private Socket socket;
    /** Referencia al servidor de juego para delegar lógica global. */
    private ServidorJuego server;
    /** Referencia al otro jugador en la sesión (para modo multijugador). */
    private GestorCliente peer;
    /** Flujo de entrada para recibir datos del cliente. */
    private DataInputStream in;
    /** Flujo de salida para enviar datos al cliente. */
    private DataOutputStream out;
    /** Identificador numérico del jugador dentro del servidor. */
    private int playerId;
    /** Nombre del explorador registrado por el cliente. */
    private String playerName;
    /** Género del personaje (CHICO o CHICA). */
    private String gender = "CHICO";

    /**
     * Constructor del gestor de cliente.
     * 
     * @param socket Canal de comunicación con el cliente.
     * @param server Referencia al servidor principal.
     */
    public GestorCliente(Socket socket, ServidorJuego server) {
        this.socket = socket;
        this.server = server;
        try {
            // Inicializar OUT primero para evitar bloqueos en el cliente
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error al inicializar flujos de E/S: " + e.getMessage());
        }
    }

    /**
     * Establece el compañero (rival) de este cliente.
     */
    public void setPeer(GestorCliente peer) {
        this.peer = peer;
    }

    /**
     * Obtiene el compañero vinculado a este cliente.
     */
    public GestorCliente getPeer() {
        return peer;
    }

    /**
     * Asigna un ID único de jugador a este cliente.
     */
    public void setPlayerId(int id) {
        this.playerId = id;
    }

    /**
     * Obtiene el nombre del explorador de este cliente.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Establece el género del personaje del cliente.
     */
    public void setGender(String sub) {
        this.gender = sub;
    }

    /**
     * Obtiene el género del personaje del cliente.
     */
    public String getGender() {
        return gender;
    }

    /**
     * Envía un mensaje (comando) al cliente por el socket.
     * 
     * @param msg Cadena de texto que contiene el mensaje/comando.
     */
    public void sendMessage(String msg) {
        try {
            if (out != null) {
                synchronized (out) {
                    out.writeUTF(msg);
                }
            }
        } catch (IOException e) {
            // Ignorar errores de escritura si el cliente se desconectó
        }
    }

    /**
     * Hilo principal de escucha. Lee mensajes entrantes del cliente continuamente.
     */
    @Override
    public void run() {
        try {
            while (true) {
                // Leer mensaje entrante (formato UTF)
                String msg = in.readUTF();

                if (msg.startsWith("IDENTITY:")) {
                    // Actualización de identidad (Nombre y Género)
                    String[] parts = msg.split(":");
                    if (parts.length >= 2)
                        this.playerName = parts[1];
                    if (parts.length >= 3)
                        this.gender = parts[2];

                    System.out.println("Jugador " + playerId + " ID recibido: " + playerName + " - " + gender);
                    checkAndCreateSave();
                    // Sincronizar estado inicial con el servidor
                    server.syncClientState(this);
                } else if (msg.startsWith("CHECK_NAME:")) {
                    // Validar si un nombre de usuario ya está en uso
                    String nameToCheck = msg.substring(11);
                    boolean isTaken = server.isNameTaken(nameToCheck, this);
                    if (isTaken) {
                        sendMessage("NAME_TAKEN");
                    } else {
                        sendMessage("NAME_OK");
                    }
                } else if (msg.startsWith("NAME:")) {
                    // Cambio directo de nombre
                    this.playerName = msg.substring(5);
                    System.out.println("Jugador " + playerId + " nombre recibido: " + playerName);
                    checkAndCreateSave();
                    server.syncClientState(this);
                } else {
                    // El resto de lógica (Movimiento, Combate, etc.) se delega al servidor
                    server.onInfoReceived(this, msg);
                }
            }
        } catch (EOFException e) {
            System.out.println("Jugador " + playerId + " desconectado.");
        } catch (IOException e) {
            System.err.println("Error en conexión Jugador " + playerId + ": " + e.getMessage());
        } finally {
            close();
        }
    }

    /**
     * Reservado para lógica de guardado automático (actualmente desactivado por
     * diseño).
     */
    private void checkAndCreateSave() {
        // La creación automática de archivo ha sido eliminada.
        // El archivo solo se creará cuando ambos jugadores guarden explícitamente en el
        // juego.
    }

    /**
     * Cierra el socket y notifica al servidor la desconexión.
     */
    private void close() {
        server.removeClient(this);
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            // Error al cerrar el socket (ignorar)
        }
    }
}
