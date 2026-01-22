package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.ResultadoUso;
import java.util.Arrays;
import java.util.List;

// Elíxir de Piel de Piedra - Aumenta temporalmente el ataque del Pokémon.
public class ElixirPielPiedra extends ItemConsumible {

    public ElixirPielPiedra(int cantidad) {
        super("elixir", "Elíxir de Piel de Piedra", "Aumenta la potencia del ataque (+3 por ataque).", cantidad);
    }

    @Override
    public boolean puedeUsar(Pokemon pokemon) {
        return pokemon != null && !pokemon.estaDebilitado();
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        if (!puedeUsar(pokemon))
            return ResultadoUso.fallo("No se puede usar en este Pokémon.");
        pokemon.establecerModificadorAtaqueTemporal(pokemon.obtenerModificadorAtaqueTemporal() + 3);
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("¡Elíxir tomado! Tu ataque subirá +3 en la batalla.");
    }

    @Override
    public List<String> obtenerOpciones() {
        return Arrays.asList("Tomar", "Tirar");
    }
}
