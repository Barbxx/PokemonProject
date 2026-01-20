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
        super("revivir", "Revivir Casero", "Revive con 50% HP a un Pokémon debilitado.", cantidad);
    }

    @Override
    public boolean puedeUsar(Pokemon pokemon) {
        return pokemon.isDebilitado();
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        if (!puedeUsar(pokemon)) {
            return ResultadoUso.fallo("No está debilitado.");
        }

        pokemon.recuperarSalud(pokemon.getHpMaximo() * 0.5f);
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("¡" + pokemon.getNombre() + " revivió!");
    }

    @Override
    public List<String> getOpciones() {
        return Arrays.asList("Revivir", "Tirar");
    }
}
