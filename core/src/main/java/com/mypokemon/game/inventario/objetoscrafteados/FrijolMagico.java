package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.ResultadoUso;
import java.util.Arrays;
import java.util.List;

// Frijol Mágico - Restaura el 100% de HP del Pokémon.
public class FrijolMagico extends ItemCurativo {

    public FrijolMagico(int cantidad) {
        super("frijol", "Frijol mágico", "Restaura el total de HP del Pokémon.", cantidad, 0);
    }

    @Override
    public boolean puedeUsar(Pokemon pokemon) {
        return pokemon.obtenerHpActual() < pokemon.obtenerHpMaximo();
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        if (!puedeUsar(pokemon))
            return ResultadoUso.fallo("El Pokémon ya tiene la salud al máximo.");
        pokemon.curar(pokemon.obtenerHpMaximo());
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("¡HP restaurado al 100%!");
    }

    @Override
    public List<String> obtenerOpciones() {
        return Arrays.asList("Curar", "Tirar");
    }
}
