package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;

import com.mypokemon.game.inventario.ResultadoUso;
import java.util.Arrays;
import java.util.List;

/**
 * Reproductor de Música - Activa música de fondo.
 */
public class ReproductorMusica extends ItemConsumible {

    public ReproductorMusica(int cantidad) {
        super("reproductor", "Reproductor de música", "Permite escuchar música de fondo.", cantidad);
    }

    @Override
    public boolean puedeUsar(Pokemon pokemon) {
        return true; // No requiere Pokémon
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("Música de fondo ACTIVADA");
    }

    @Override
    public List<String> getOpciones() {
        return Arrays.asList("Encender", "Tirar");
    }
}
