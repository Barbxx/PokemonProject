package com.mypokemon.game.inventario.interfaces;

// Interfaz para pokebolas porque pueden ser lanzadas.

public interface ILanzable {

    /**
     * Verifica si el ítem puede ser lanzado en el contexto actual.
     * 
     * @return true si el ítem puede ser lanzado, false en caso contrario
     */
    boolean puedeLanzar();

    /**
     * Obtiene el tipo de pokeball para la lógica de captura.
     * 
     * @return El ID interno del tipo de pokeball
     */
    String getTipoLanzable();
}
