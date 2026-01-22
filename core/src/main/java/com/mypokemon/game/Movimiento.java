package com.mypokemon.game;

import java.io.Serializable;
import java.util.Random;

/**
 * Representa un ataque o habilidad que un Pokémon puede ejecutar en combate.
 * Gestiona la lógica de daño, precisión e interacciones de tipos (inmunidades).
 */
public class Movimiento implements Serializable {
    /** Nombre identificador del movimiento. */
    private String nombre;
    /** Valor base de daño del movimiento. */
    private int poder;
    /** Tipo elemental del movimiento. */
    private String tipo;
    /** Porcentaje de probabilidad de acierto (0-100). */
    private int precision;

    public Movimiento(String nombre, int poder, String tipo, int precision) {
        this.nombre = nombre;
        this.poder = poder;
        this.tipo = tipo;
        this.precision = precision;
    }

    public int ejecutar(Pokemon atacante, Pokemon defensor) {
        // Lógica especial para Recuperación (cura al atacante)
        if (this.nombre.equals("Recuperación")) {
            atacante.curar(Math.abs(this.poder));
            // No hace daño al oponente
            return 0;
        }

        // Verificar si el ataque acierta según la precisión
        if (new Random().nextInt(100) < this.precision) {
            // Verificar inmunidades primero
            if (esInmune(this.tipo, defensor)) {
                return -1; // Código especial para inmunidad
            }

            // El daño es el poder del ataque + el modificador temporal del atacante
            // (Elixir)
            int daño = this.poder + (int) atacante.obtenerModificadorAtaqueTemporal();

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
        if (defensor.obtenerInmunidades() != null) {
            for (String inmune : defensor.obtenerInmunidades()) {
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
