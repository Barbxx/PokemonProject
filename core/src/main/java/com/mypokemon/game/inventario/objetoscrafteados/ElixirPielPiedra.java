package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;

import com.mypokemon.game.inventario.ResultadoUso;
import java.util.Arrays;
import java.util.List;

/**
 * Elixir de Piel de Piedra - Aumenta ataque.
 */
public class ElixirPielPiedra extends ItemConsumible {

    public ElixirPielPiedra(int cantidad) {
        super("elixir", "Elíxir de Piel de Piedra", "Aumenta la potencia del ataque (+3 por ataque).", cantidad);
    }

    @Override
    public boolean puedeUsar(Pokemon pokemon) {
        return !pokemon.isDebilitado();
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        if (!puedeUsar(pokemon)) {
            return ResultadoUso.fallo("No tiene efecto.");
        }

        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("Ataque subió +3 (Efecto visual)");
    }

    @Override
    public List<String> getOpciones() {
        return Arrays.asList("Usar", "Tirar");
    }
}
