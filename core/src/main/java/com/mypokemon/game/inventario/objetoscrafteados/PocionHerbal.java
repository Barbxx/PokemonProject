package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.ResultadoUso;
import java.util.Arrays;
import java.util.List;

// Poción Herbal - Restaura el 20% de HP del Pokémon.
public class PocionHerbal extends ItemCurativo {

    public PocionHerbal(int cantidad) {
        super("pocion", "Poción Herbal", "Restaura el 20% de HP del Pokémon.", cantidad, 0);
    }

    @Override
    public boolean puedeUsar(Pokemon pokemon) {
        return pokemon.obtenerHpActual() < pokemon.obtenerHpMaximo();
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        if (!puedeUsar(pokemon))
            return ResultadoUso.fallo("El Pokémon ya tiene la salud al máximo.");
        float cantidadSanacion = pokemon.obtenerHpMaximo() * 0.20f;
        pokemon.curar(cantidadSanacion);
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("¡Curaste a " + pokemon.obtenerNombre() + "!");
    }

    @Override
    public List<String> obtenerOpciones() {
        return Arrays.asList("Curar", "Tirar");
    }
}
