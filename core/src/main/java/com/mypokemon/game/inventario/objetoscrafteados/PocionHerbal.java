package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.ResultadoUso;
import java.util.Arrays;
import java.util.List;

// Poción Herbal - Cura 20 HP.
public class PocionHerbal extends ItemCurativo {

    public PocionHerbal(int cantidad) {
        super("pocion", "Poción Herbal", "Restaura el 20% de los PS del Pokémon.", cantidad, 0);
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

        float healAmount = pokemon.getHpMaximo() * 0.20f;
        pokemon.recuperarSalud(healAmount);
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("Curaste a " + pokemon.getNombre());
    }

    @Override
    public List<String> getOpciones() {
        return Arrays.asList("Curar", "Tirar");
    }
}
