package com.mypokemon.game.inventario.interfaces;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.ResultadoUso;

// Interfaz para objetos que pueden ser usados en Pokémon (objetos consumibles, pociones, bayas, etc.).
public interface IUsable {

    // Verifica si el objeto puede ser usado en el Pokémon objetivo.
    boolean puedeUsar(Pokemon pokemon);

    // Usa el objeto en el Pokémon objetivo y actualiza el inventario.
    ResultadoUso usar(Pokemon pokemon, Inventario inventario);
}
