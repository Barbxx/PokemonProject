package com.mypokemon.game.inventario;

import java.io.Serializable;
import java.util.List;

/**
 * Clase abstracta base para todos los ítems del inventario.
 * Implementa Serializable para compatibilidad con guardado de juego.
 */
public abstract class Objeto implements Serializable {
    protected String id;
    protected String nombre;
    protected int cantidad;

    public Objeto(String id, String nombre, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public abstract String getDescripcion();

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = Math.max(0, cantidad);
    }

    public void agregar(int cantidad) {
        this.cantidad += cantidad;
        if (this.cantidad < 0) {
            this.cantidad = 0;
        }
    }

    public boolean consumir(int cantidad) {
        if (this.cantidad >= cantidad) {
            this.cantidad -= cantidad;
            return true;
        }
        return false;
    }

    /**
     * Obtiene las opciones disponibles para este ítem.
     * Cada subclase define sus propias opciones (ej: "Curar", "Tirar", etc.)
     * 
     * @return Lista de opciones disponibles
     */
    public abstract List<String> getOpciones();

    /**
     * Verifica si este ítem es usable (implementa IUsable).
     * 
     * @return true si el ítem puede ser usado en Pokémon
     */
    public boolean esUsable() {
        return this instanceof com.mypokemon.game.inventario.interfaces.IUsable;
    }

    /**
     * Verifica si este ítem es lanzable (implementa ILanzable).
     * 
     * @return true si el ítem puede ser lanzado
     */
    public boolean esLanzable() {
        return this instanceof com.mypokemon.game.inventario.interfaces.ILanzable;
    }
}
