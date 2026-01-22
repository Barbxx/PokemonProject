package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.inventario.ItemCrafteado;
import com.mypokemon.game.inventario.interfaces.ILanzable;
import java.util.Arrays;
import java.util.List;

// Poké Ball estándar - Dispositivo para atrapar Pokémon.
public class Pokeball extends ItemCrafteado implements ILanzable {

    public Pokeball(int cantidad) {
        super("pokeball", "Poké Ball", "Dispositivo para atrapar Pokémon.", cantidad);
    }

    @Override
    public boolean puedeLanzar() {
        return true;
    }

    @Override
    public String obtenerTipoLanzable() {
        return "pokeball";
    }

    @Override
    public List<String> obtenerOpciones() {
        return Arrays.asList("Lanzar", "Tirar");
    }
}
