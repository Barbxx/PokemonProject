package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.ResultadoUso;
import java.util.Arrays;
import java.util.List;

// Reproductor de Música - Permite activar o desactivar la música de fondo del juego.
public class ReproductorMusica extends ItemConsumible {
    private boolean activo = false;

    public ReproductorMusica(int cantidad) {
        super("reproductor", "Reproductor de música", "Permite escuchar música de fondo personalizada.", cantidad);
    }

    public boolean estaActivo() {
        return activo && cantidad > 0;
    }

    public void establecerActivo(boolean a) {
        this.activo = a;
    }

    @Override
    public boolean puedeUsar(Pokemon pokemon) {
        return true;
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        activo = !activo;
        if (activo)
            return ResultadoUso.exito("¡Reproductor ENCENDIDO! Disfruta de la música, mor.");
        else
            return ResultadoUso.exito("¡Reproductor APAGADO!");
    }

    @Override
    public List<String> obtenerOpciones() {
        if (activo && cantidad > 0)
            return Arrays.asList("Apagar", "Tirar");
        else
            return Arrays.asList("Encender", "Tirar");
    }
}
