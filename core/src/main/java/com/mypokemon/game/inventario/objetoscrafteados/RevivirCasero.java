package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.ResultadoUso;
import java.util.Arrays;
import java.util.List;

// Revivir Casero - Revive a un Pokémon debilitado o restaura el 50% de su salud.
public class RevivirCasero extends ItemConsumible {

    public RevivirCasero(int cantidad) {
        super("revivir", "Revivir Casero", "Restaura la mitad de HP del Pokémon.", cantidad);
    }

    @Override
    public boolean puedeUsar(Pokemon pokemon) {
        return pokemon.obtenerHpActual() < pokemon.obtenerHpMaximo();
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        if (!puedeUsar(pokemon))
            return ResultadoUso.fallo("El Pokémon ya tiene la salud al máximo.");
        pokemon.curar(pokemon.obtenerHpMaximo() * 0.5f);
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("¡" + pokemon.obtenerNombre() + " recuperó energía!");
    }

    @Override
    public List<String> obtenerOpciones() {
        return Arrays.asList("Curar", "Tirar");
    }
}
