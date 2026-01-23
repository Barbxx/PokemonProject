package com.mypokemon.game;

import java.io.Serializable;

/**
 * Representa una especie de Pokemon en la Pokedex con su nivel de
 * investigacion.
 * Gestiona el progreso de investigacion y el estado de captura.
 */
public class EspeciePokemon implements Serializable {
    private static final long serialVersionUID = 3L;

    private String nombre;
    private int nivelInvestigacion;
    private boolean completa;
    private boolean capturado;

    /**
     * Constructor de una especie de Pokemon.
     * 
     * @param nombre Nombre de la especie
     */
    public EspeciePokemon(String nombre) {
        this.nombre = nombre;
        this.nivelInvestigacion = 0;
        this.completa = false;
        this.capturado = false;
    }

    /**
     * Agrega puntos de investigacion a la especie.
     * 
     * @param puntos Cantidad de puntos a agregar
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
     * Establece la investigacion al nivel maximo instantaneamente.
     */
    public void setInvestigacionMaxica() {
        this.nivelInvestigacion = 10;
        this.completa = true;
        this.capturado = true;
    }

    /**
     * Establece directamente el nivel de investigacion.
     * 
     * @param nivel Nivel de investigacion a establecer (0-10)
     */
    public void setNivel(int nivel) {
        this.nivelInvestigacion = Math.max(0, Math.min(10, nivel));
        if (this.nivelInvestigacion >= 10) {
            this.completa = true;
        }
    }

    /**
     * Sube el nivel de investigacion de la especie.
     * 
     * @param cantidad Cantidad de niveles a subir
     */
    public void subirNivel(int cantidad) {
        añadirPuntos(cantidad);
    }

    /**
     * Obtiene el nombre de la especie.
     * 
     * @return Nombre de la especie
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el nivel de investigacion actual.
     * 
     * @return Nivel de investigacion de 0 a 10
     */
    public int getNivelInvestigacion() {
        return nivelInvestigacion;
    }

    /**
     * Verifica si la investigacion esta completa.
     * 
     * @return true si esta completa, false en caso contrario
     */
    public boolean isCompleta() {
        return completa;
    }

    /**
     * Verifica si la especie ha sido capturada.
     * 
     * @return true si fue capturada, false en caso contrario
     */
    public boolean isCapturado() {
        return capturado;
    }

    /**
     * Establece el estado de captura de la especie.
     * 
     * @param capturado true si fue capturada, false en caso contrario
     */
    public void setCapturado(boolean capturado) {
        this.capturado = capturado;
    }
}
