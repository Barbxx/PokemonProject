package com.mypokemon.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Mision3Red {
    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;
    private boolean conectado;

    public Mision3Red(String host, int puerto) {
        try {
            socket = new Socket(host, puerto);
            salida = new PrintWriter(socket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            conectado = true;

            // Iniciar escucha del rival en hilo separado
            escucharRival();
        } catch (IOException e) {
            System.err.println("No se pudo conectar al servidor de batalla: " + e.getMessage());
            conectado = false;
        }
    }

    public void enviarAtaque(int idMovimiento) {
        if (conectado && salida != null) {
            salida.println("ATAQUE:" + idMovimiento);
        }
    }

    public void escucharRival() {
        if (!conectado)
            return;

        Thread hiloEscucha = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String mensaje;
                    while ((mensaje = entrada.readLine()) != null) {
                        procesarMensajeRival(mensaje);
                    }
                } catch (IOException e) {
                    System.err.println("Error en la conexión con el rival: " + e.getMessage());
                }
            }
        });
        hiloEscucha.start();
    }

    private void procesarMensajeRival(String mensaje) {
        if (mensaje.startsWith("ATAQUE:")) {
            try {
                int idMovimientoRival = Integer.parseInt(mensaje.split(":")[1]);
                System.out.println("El rival usó el movimiento ID: " + idMovimientoRival);
                // Aquí se conectaría con la lógica de actualizar la vida de tu Pokémon
                // Por ejemplo, callback o event bus.
            } catch (NumberFormatException e) {
                System.err.println("Mensaje de ataque inválido: " + mensaje);
            }
        }
    }

    public void cerrarConexion() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
