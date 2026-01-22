package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.inventario.ItemCrafteado;
import com.mypokemon.game.inventario.interfaces.ILanzable;
import java.util.Arrays;
import java.util.List;

// Poké Ball de Peso (Heavy Ball) - Mejor tasa de captura para Pokémon en ciertas condiciones.
public class PokeballPesada extends ItemCrafteado implements ILanzable {

    public PokeballPesada(int cantidad) {
        super("heavyball", "Poké Ball de Peso", "Dispositivo con mejor tasa de captura en nivel bajo.", cantidad);
    }

    @Override
    public boolean puedeLanzar() {
        return true;
    }

    @Override
    public String obtenerTipoLanzable() {
        return "heavyball";
    }

    @Override
    public List<String> obtenerOpciones() {
        return Arrays.asList("Lanzar", "Tirar");
    }
}
