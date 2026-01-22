package com.mypokemon.game.inventario.exceptions;

//Excepción lanzada cuando el inventario está lleno y no hay espacio disponible para añadir nuevos objetos.

public class EspacioException extends Exception {

    /**
     * Crea una nueva excepción con un mensaje detallado.
     * 
     * @param m El mensaje que describe la causa del error de espacio.
     */
    public EspacioException(String m) {
        super(m);
    }
}
