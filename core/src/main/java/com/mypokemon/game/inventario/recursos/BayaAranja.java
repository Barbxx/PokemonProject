package com.mypokemon.game.inventario.recursos;

import com.mypokemon.game.inventario.Recurso;
import com.mypokemon.game.inventario.interfaces.IUsable;
import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.ResultadoUso;
import java.util.Arrays;
import java.util.List;

// Baya Aranja - Recurso comestible que restaura el 10% de HP del Pokémon.
public class BayaAranja extends Recurso implements IUsable {

    public BayaAranja(int cantidad) {
        super("baya", "Baya Aranja", cantidad);
    }

    @Override
    public boolean puedeUsar(Pokemon pokemon) {
        return pokemon.obtenerHpActual() < pokemon.obtenerHpMaximo();
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        if (!puedeUsar(pokemon))
            return ResultadoUso.fallo("El Pokémon ya tiene la salud al máximo.");
        float cantidadSanacion = pokemon.obtenerHpMaximo() * 0.10f;
        pokemon.curar(cantidadSanacion);
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("¡Curaste a " + pokemon.obtenerNombre() + "!");
    }

    @Override
    public List<String> obtenerOpciones() {
        return Arrays.asList("Curar", "Tirar");
    }
}
