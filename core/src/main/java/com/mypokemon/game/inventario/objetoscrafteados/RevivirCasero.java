package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;

import com.mypokemon.game.inventario.ResultadoUso;
import java.util.Arrays;
import java.util.List;

/**
 * Revivir Casero - Revive Pokémon debilitados con 50% HP.
 */
public class RevivirCasero extends ItemConsumible {

    public RevivirCasero(int cantidad) {
        super("revivir", "Revivir Casero", "Restaura el 50% de los PS del Pokémon.", cantidad);
    }

    @Override
    public boolean puedeUsar(Pokemon pokemon) {
        // Se puede usar siempre que no esté FULL HP
        return pokemon.getHpActual() < pokemon.getHpMaximo();
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        if (pokemon.getHpActual() >= pokemon.getHpMaximo()) {
            return ResultadoUso.fallo("El Pokémon ya está al máximo de salud.");
        }

        pokemon.recuperarSalud(pokemon.getHpMaximo() * 0.5f);
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("¡" + pokemon.getNombre() + " recuperó energía!");
    }

    @Override
    public List<String> getOpciones() {
        return Arrays.asList("Curar", "Tirar");
    }
}
