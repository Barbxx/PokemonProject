package com.mypokemon.game.inventario;

import java.io.Serializable;
import java.util.List;

/**
 * Alias para Objeto - mantiene compatibilidad con código existente.
 * 
 * @deprecated Use Objeto instead
 */
@Deprecated
public abstract class Item implements Serializable {
    protected String id;
    protected String nombre;
    protected int cantidad;

    public Item(String id, String nombre, int cantidad) {
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

    public abstract List<String> getOpciones();

    public boolean esUsable() {
        return this instanceof com.mypokemon.game.inventario.interfaces.IUsable;
    }

    public boolean esLanzable() {
        return this instanceof com.mypokemon.game.inventario.interfaces.ILanzable;
    }
}
