package com.mypokemon.game.inventario.interfaces;

// Interfaz para objetos que pueden ser lanzados. Implementada principalmente por Poké Balls.
public interface ILanzable {

    // Verifica si el objeto puede ser lanzado en el contexto actual.
    boolean puedeLanzar();

    // Obtiene el tipo de Poké Ball para la lógica de captura.
    String obtenerTipoLanzable();
}
