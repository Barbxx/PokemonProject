package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.inventario.ObjetoCrafteado;
import com.mypokemon.game.inventario.interfaces.ILanzable;
import java.util.Arrays;
import java.util.List;

// Poké Ball de Peso (Heavy Ball) - Mejor captura para Pokémon de nivel bajo.
public class PokeballPesada extends ObjetoCrafteado implements ILanzable {

    public PokeballPesada(int cantidad) {
        super("heavyball", "Poké Ball de Peso", "Dispositivo con mejor captura en nivel bajo.", cantidad);
    }

    @Override
    public boolean puedeLanzar() {
        return true;
    }

    @Override
    public String getTipoLanzable() {
        return "heavyball";
    }

    @Override
    public List<String> getOpciones() {
        return Arrays.asList("Lanzar", "Tirar");
    }
}
