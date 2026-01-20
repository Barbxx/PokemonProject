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
        super("frijol", "Frijol mágico", "Restaura el 100% de HP de un Pokémon.", cantidad, 0);
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        if (!puedeUsar(pokemon)) {
            return ResultadoUso.fallo("No tiene efecto.");
        }

        pokemon.recuperarSalud(pokemon.getHpMaximo());
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("¡HP restaurado al 100%!");
    }

    @Override
    public List<String> getOpciones() {
        return Arrays.asList("Comer", "Tirar");
    }
}
