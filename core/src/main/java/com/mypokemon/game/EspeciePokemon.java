package com.mypokemon.game;

import java.io.Serializable;

// Representa una entrada en la Pokédex para una especie específica.
public class EspeciePokemon implements Serializable {
    private static final long serialVersionUID = 3L;
    private String nombre;
    private int nivelInvestigacion;
    private boolean completa;
    private boolean capturado;

    public EspeciePokemon(String nombre) {
        this.nombre = nombre;
        this.nivelInvestigacion = 0;
        this.completa = false;
        this.capturado = false;
    }

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

    public void establecerInvestigacionMaxima() {
        this.nivelInvestigacion = 10;
        this.completa = true;
        this.capturado = true;
    }

    public void subirNivel(int cantidad) {
        añadirPuntos(cantidad);
    }

    // Getters y Setters
    public String obtenerNombre() {
        return nombre;
    }

    public int obtenerNivelInvestigacion() {
        return nivelInvestigacion;
    }

    public boolean estaCompleta() {
        return completa;
    }

    public boolean estaCapturado() {
        return capturado;
    }

    public void establecerCapturado(boolean c) {
        this.capturado = c;
    }
}
