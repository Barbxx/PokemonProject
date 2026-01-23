package com.mypokemon.game.inventario.exceptions;

// Excepción lanzada cuando el inventario está lleno y no hay espacio disponible.
public class EspacioException extends Exception {

    /**
     * Crea una nueva excepción de espacio insuficiente.
     * 
     * @param m Mensaje de error.
     */
    public EspacioException(String m) {
        super(m);
    }
}
