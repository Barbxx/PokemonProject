package com.mypokemon.game.inventario;

// Clase abstracta que representa un ítem crafteado mediante recetas.
// Cada tipo específico de ítem crafteado extiende esta clase.

public abstract class ObjetoCrafteado extends Item {
    protected String descripcion;

    /**
     * Constructor para un objeto crafteado.
     * 
     * @param id          Identificador único.
     * @param nombre      Nombre del ítem.
     * @param descripcion Descripción del ítem.
     * @param cantidad    Cantidad inicial.
     */
    public ObjetoCrafteado(String id, String nombre, String descripcion, int cantidad) {
        super(id, nombre, cantidad);
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la descripción del ítem.
     * 
     * @return Texto descriptivo.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Obtiene las opciones disponibles para este objeto. Debe ser implementado por
     * subclases concretas.
     * 
     * @return Lista de acciones disponibles
     */
    @Override
    public abstract java.util.List<String> getOpciones();
}
