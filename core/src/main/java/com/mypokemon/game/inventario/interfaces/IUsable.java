package com.mypokemon.game.inventario.interfaces;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.ResultadoUso;

// Interfaz para ítems que pueden ser usados en Pokémon. Implementada por ítems consumibles como pociones, bayas, etc.
public interface IUsable {

    /**
     * Verifica si el ítem puede ser usado en el Pokémon especificado.
     * 
     * @param pokemon El Pokémon objetivo
     * @return true si el ítem puede ser usado, false en caso contrario
     */
    boolean puedeUsar(Pokemon pokemon);

    /**
     * Usa el ítem en el Pokémon especificado.
     * 
     * @param pokemon    El Pokémon objetivo
     * @param inventario El inventario del jugador (para consumir el ítem)
     * @return ResultadoUso con información sobre el resultado de usar el ítem
     */
    ResultadoUso usar(Pokemon pokemon, Inventario inventario);

    /**
     * Obtiene el tipo de uso del ítem.
     * 
     * @return El tipo de uso (CURAR, REVIVIR, MEJORA, UTILIDAD)
     */

}
