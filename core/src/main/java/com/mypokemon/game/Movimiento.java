package com.mypokemon.game;

import java.io.Serializable;
import java.util.Random;

/**
 * Representa un movimiento o ataque que puede usar un Pokemon en batalla.
 * Incluye poder, tipo, precision y logica de ejecucion con inmunidades.
 */
public class Movimiento implements Serializable {
    private String nombre;
    private int poder;
    private String tipo;
    private int precision;

    /**
     * Constructor de un movimiento de Pokemon.
     *
     * @param nombre    Nombre del movimiento
     * @param poder     Poder de ataque del movimiento
     * @param tipo      Tipo elemental del movimiento
     * @param precision Precision del movimiento de 0 a 100
     */
    public Movimiento(String nombre, int poder, String tipo, int precision) {
        this.nombre = nombre;
        this.poder = poder;
        this.tipo = tipo;
        this.precision = precision;
    }

    /**
     * Ejecuta el movimiento contra un Pokemon defensor.
     *
     * @param atacante Pokemon que ejecuta el movimiento
     * @param defensor Pokemon que recibe el ataque
     * @return Cantidad de dano infligido, 0 si falla, -1 si es inmune
     */
    public int ejecutar(Pokemon atacante, Pokemon defensor) {
        // Lógica especial para Recuperación (cura al atacante)
        if (this.nombre.equals("Recuperación")) {
            atacante.recuperarSalud(Math.abs(this.poder));
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
            int daño = this.poder + (int) atacante.getModificadorAtaqueTemporal();

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
     * Verifica si el defensor es inmune al tipo de ataque.
     *
     * @param tipoAtaque Tipo del ataque a verificar
     * @param defensor   Pokemon defensor
     * @return true si es inmune, false en caso contrario
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
     * Obtiene el nombre del movimiento.
     *
     * @return Nombre del movimiento
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el poder del movimiento.
     *
     * @return Poder de ataque
     */
    public int getPoder() {
        return poder;
    }

    /**
     * Obtiene el tipo elemental del movimiento.
     *
     * @return Tipo del movimiento
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Obtiene la precision del movimiento.
     *
     * @return Precision de 0 a 100
     */
    public int getPrecision() {
        return precision;
    }
}
