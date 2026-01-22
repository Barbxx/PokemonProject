package com.mypokemon.game;

import java.io.Serializable;

/**
 * Representa una entrada en la Pokédex.
 * Realiza el seguimiento del nivel de investigación para una especie específica
 * y si ha sido capturada.
 */
public class EspeciePokemon implements Serializable {
    private static final long serialVersionUID = 3L;

    private String nombre;
    private int nivelInvestigacion;
    private boolean completa;
    private boolean capturado;

    /**
     * Constructor de una nueva entrada de especie.
     * 
     * @param nombre Nombre de la especie Pokémon.
     */
    public EspeciePokemon(String nombre) {
        this.nombre = nombre;
        this.nivelInvestigacion = 0;
        this.completa = false;
        this.capturado = false;
    }

    /**
     * Añade puntos al nivel de investigación de esta especie.
     * Si alcanza 10 puntos, se marca como completa.
     * 
     * @param puntos Cantidad de puntos a añadir.
     */
    public void añadirPuntos(int puntos) {
        if (!completa) {
            this.nivelInvestigacion += puntos;
            if (this.nivelInvestigacion >= 10) {
                this.nivelInvestigacion = 10;
                this.completa = true;
                System.out.println("¡Investigación de " + nombre + " completada!");
            }
        }
    }

    /**
     * Completa instantáneamente la investigación (cheat/evento).
     */
    public void setInvestigacionMaxica() {
        this.nivelInvestigacion = 10;
        this.completa = true;
        this.capturado = true;
    }

    // Mantenemos compatibilidad con lógica previa si es necesario
    public void subirNivel(int cantidad) {
        añadirPuntos(cantidad);
    }

    // Getters y Setters necesarios para el guardado y la UI
    public String getNombre() {
        return nombre;
    }

    public int getNivelInvestigacion() {
        return nivelInvestigacion;
    }

    public boolean isCompleta() {
        return completa;
    }

    // Compatibilidad
    public boolean isCapturado() {
        return capturado;
    }

    public void setCapturado(boolean capturado) {
        this.capturado = capturado;
    }
}
