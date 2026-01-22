package com.mypokemon.game.inventario;

import java.util.List;
import com.mypokemon.game.inventario.exceptions.SpaceException;

// Clase abstracta que representa un objeto fabricado mediante recetas (Poké Ball, Poción, etc.).
public abstract class ItemCrafteado extends Item {
    protected String descripcion;

    public ItemCrafteado(String id, String nombre, String descripcion, int cantidad) {
        super(id, nombre, cantidad);
        this.descripcion = descripcion;
    }

    @Override
    public String obtenerDescripcion() {
        return descripcion;
    }

    @Override
    public abstract List<String> obtenerOpciones();

    @Override
    public void guardarEn(Inventario inventario) throws SpaceException {
        inventario.agregarItemCrafteado(this);
    }
}
