package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.ResultadoUso;
import java.util.Arrays;
import java.util.List;

/**
 * Frijol Mágico - Restaura 100% HP.
 */
public class FrijolMagico extends ItemCurativo {

    public FrijolMagico(int cantidad) {
        super("frijol", "Frijol mágico", "Restaura el 100% de los PS del Pokémon.", cantidad, 0);
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

        pokemon.recuperarSalud(pokemon.getHpMaximo());
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("¡HP restaurado al 100%!");
    }

    @Override
    public List<String> getOpciones() {
        return Arrays.asList("Curar", "Tirar");
    }
}
