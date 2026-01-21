package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.inventario.Recurso;
import com.mypokemon.game.inventario.interfaces.IUsable;
import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.ResultadoUso;
import java.util.Arrays;
import java.util.List;

/**
 * Baya Aranja - Cura 10% HP.
 * Ahora es un Recurso para que funcione correctamente en el
 * crafteo/recolección.
 */
public class BayaAranja extends Recurso implements IUsable {

    public BayaAranja(int cantidad) {
        super("baya", "Baya Aranja", cantidad);
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

        float healAmount = pokemon.getHpMaximo() * 0.10f;
        pokemon.recuperarSalud(healAmount);
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("Curaste a " + pokemon.getNombre());
    }

    @Override
    public List<String> getOpciones() {
        return Arrays.asList("Curar", "Tirar");
    }
}
