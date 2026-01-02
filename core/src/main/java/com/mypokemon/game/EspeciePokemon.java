package com.mypokemon.game;

import java.io.Serializable;

public class EspeciePokemon implements Serializable {
    private static final long serialVersionUID = 3L; // Para serialización consistente

    private String nombre;
    private int nivelInvestigacion;
    private boolean completa;

    public EspeciePokemon(String nombre) {
        this.nombre = nombre;
        this.nivelInvestigacion = 0;
        this.completa = false;
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

    // Método especial para el Hito Final (Dr. Brenner)
    public void setInvestigacionMaxica() {
        this.nivelInvestigacion = 10;
        this.completa = true;
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
        // En la lógica anterior 'capturado' era un boolean separado.
        // Ahora el progreso se mide en puntos. Dependiendo de cómo quiera usarse,
        // podemos asumir que si está completo o algo más.
        // O simplemente ignoramos este método legado si ya no se usa fuera.
        // Pero mantengamos getters limpios.
        return completa;
    }
}
