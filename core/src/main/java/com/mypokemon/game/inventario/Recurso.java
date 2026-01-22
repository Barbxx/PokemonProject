package com.mypokemon.game.inventario;

import java.util.ArrayList;
import java.util.List;
import com.mypokemon.game.inventario.exceptions.SpaceException;

// Representa un recurso básico recolectable (Planta, Guijarro, etc.).
public class Recurso extends Item {

    public Recurso(String id, String nombre, int cantidad) {
        super(id, nombre, cantidad);
    }

    @Override
    public String obtenerDescripcion() {
        return "Recurso básico: " + nombre;
    }

    @Override
    public List<String> obtenerOpciones() {
        List<String> opciones = new ArrayList<>();
        // Por defecto, un recurso básico solo permite ser descartado.
        opciones.add("Tirar");
        return opciones;
    }

    @Override
    public void guardarEn(Inventario inventario) throws SpaceException {
        inventario.agregarRecurso(this);
    }
}
