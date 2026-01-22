package com.mypokemon.game.inventario;

/**
 * Clase abstracta que representa un ítem crafteado mediante recetas.
 * Cada tipo específico de ítem crafteado debe extender esta clase.
 * Ejemplos: Poké Ball, Poción Herbal, Elixir, etc.
 */
public abstract class ObjetoCrafteado extends Objeto {
    protected String descripcion;

    public ObjetoCrafteado(String id, String nombre, String descripcion, int cantidad) {
        super(id, nombre, cantidad);
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Cada subclase debe definir sus propias opciones.
     * Este método es abstracto y debe ser implementado por las clases concretas.
     */
    @Override
    public abstract java.util.List<String> getOpciones();
}
