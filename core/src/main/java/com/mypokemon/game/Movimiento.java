package com.mypokemon.game;

import java.io.Serializable;
import java.util.Random;

public class Movimiento implements Serializable {
    private String nombre;
    private int poder;
    private String tipo;
    private int precision;

    public Movimiento(String nombre, int poder, String tipo, int precision) {
        this.nombre = nombre;
        this.poder = poder;
        this.tipo = tipo;
        this.precision = precision;
    }

    public int ejecutar(Pokemon atacante, Pokemon defensor) {
        // Verificar si el ataque acierta según la precisión
        if (new Random().nextInt(100) < this.precision) {
            // Verificar inmunidades primero
            if (esInmune(this.tipo, defensor)) {
                return -1; // Código especial para inmunidad
            }

            // El daño es directamente el poder del ataque
            // No hay cálculos complejos, solo resta directa de HP
            int daño = this.poder;

            // Asegurar que el daño sea al menos 1 si el ataque conecta
            if (daño < 1) {
                daño = 1;
            }

            // Restar el daño directamente del HP del defensor
            defensor.recibirDaño(daño);
            return daño;
        }
        return 0; // El ataque falló por precisión
    }

    /**
     * Verifica si el defensor es inmune al tipo de ataque
     */
    private boolean esInmune(String tipoAtaque, Pokemon defensor) {
        if (defensor.getInmunidades() != null) {
            for (String inmune : defensor.getInmunidades()) {
                if (tipoAtaque.equalsIgnoreCase(inmune)) {
                    return true; // Inmune
                }
            }
        }
        return false;
    }

    /**
     * NOTA: La efectividad de tipos ha sido removida.
     * El daño ahora es directo: Poder del Ataque = Daño al HP
     * No hay multiplicadores ni cálculos adicionales.
     */

    // Getters
    public String getNombre() {
        return nombre;
    }

    public int getPoder() {
        return poder;
    }

    public String getTipo() {
        return tipo;
    }

    public int getPrecision() {
        return precision;
    }
}
