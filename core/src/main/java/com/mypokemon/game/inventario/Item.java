package com.mypokemon.game.inventario;

import java.io.Serializable;
import java.util.List;

// Clase abstracta base para todos los objetos del inventario (recursos y objetos fabricados).
public abstract class Item implements Serializable {
    protected String id;
    protected String nombre;
    protected int cantidad;

    public Item(String id, String nombre, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public abstract String obtenerDescripcion();

    public String obtenerId() {
        return id;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public int obtenerCantidad() {
        return cantidad;
    }

    public void establecerCantidad(int c) {
        this.cantidad = Math.max(0, c);
    }

    public void agregar(int c) {
        this.cantidad += c;
        if (this.cantidad < 0)
            this.cantidad = 0;
    }

    public boolean consumir(int c) {
        if (this.cantidad >= c) {
            this.cantidad -= c;
            return true;
        }
        return false;
    }

    // Obtiene las opciones disponibles (ej: "Curar", "Lanzar", "Tirar") según el
    // tipo de objeto.
    public abstract List<String> obtenerOpciones();

    public boolean esUsable() {
        return this instanceof com.mypokemon.game.inventario.interfaces.IUsable;
    }

    public boolean esLanzable() {
        return this instanceof com.mypokemon.game.inventario.interfaces.ILanzable;
    }

    // Permite que el objeto se guarde a sí mismo usando Double Dispatch.
    public abstract void guardarEn(Inventario inventario)
            throws com.mypokemon.game.inventario.exceptions.SpaceException;
}
