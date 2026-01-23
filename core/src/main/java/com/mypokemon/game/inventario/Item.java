package com.mypokemon.game.inventario;

import java.io.Serializable;
import java.util.List;

// Clase abstracta base para todos los ítems del inventario.
// Implementa Serializable para compatibilidad con guardado de juego.

public abstract class Item implements Serializable {
    protected String id;
    protected String nombre;
    protected int cantidad;

    /**
     * Constructor base para un ítem.
     * 
     * @param id       Identificador único del ítem.
     * @param nombre   Nombre legible del ítem.
     * @param cantidad Cantidad inicial en el inventario.
     */
    public Item(String id, String nombre, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el ID del ítem.
     * 
     * @return ID único.
     */
    public String getId() {
        return id;
    }

    /**
     * Obtiene el nombre del ítem.
     * 
     * @return Nombre legible.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene la cantidad actual del ítem.
     * 
     * @return Cantidad disponible.
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad del ítem (no puede ser negativa).
     * 
     * @param cantidad Nueva cantidad.
     */
    public void setCantidad(int cantidad) {
        this.cantidad = Math.max(0, cantidad);
    }

    /**
     * Agrega una cantidad al ítem.
     * 
     * @param cantidad Cantidad a añadir.
     */
    public void agregar(int cantidad) {
        this.cantidad += cantidad;
        if (this.cantidad < 0) {
            this.cantidad = 0;
        }
    }

    /**
     * Intenta consumir una cantidad del ítem.
     * 
     * @param cantidad Cantidad a consumir.
     * @return true si había suficiente cantidad y se consumió, false en caso
     *         contrario.
     */
    public boolean consumir(int cantidad) {
        if (this.cantidad >= cantidad) {
            this.cantidad -= cantidad;
            return true;
        }
        return false;
    }

    /**
     * Obtiene las opciones disponibles para este ítem.
     * Cada subclase define sus propias opciones.
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
     * Verifica si este ítem es lanzable (implementa ILanzable)
     * 
     * @return true si el ítem puede ser lanzado
     */
    public boolean esLanzable() {
        return this instanceof com.mypokemon.game.inventario.interfaces.ILanzable;
    }
}
