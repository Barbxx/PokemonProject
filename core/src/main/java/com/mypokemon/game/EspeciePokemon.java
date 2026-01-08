package com.mypokemon.game;

import java.io.Serializable;

public class EspeciePokemon implements Serializable {
    private static final long serialVersionUID = 3L; // Para serialización consistente

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
