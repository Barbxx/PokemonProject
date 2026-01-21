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

    private boolean activo = false;

    public ReproductorMusica(int cantidad) {
        super("reproductor", "Reproductor de música", "Permite escuchar música de fondo.", cantidad);
    }

    public boolean isActivo() {
        // El reproductor solo puede estar activo si tenemos al menos uno
        return activo && cantidad > 0;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public boolean puedeUsar(Pokemon pokemon) {
        return true; // No requiere Pokémon
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        // Toglear estado
        activo = !activo;

        if (activo) {
            return ResultadoUso.exito(
                    "¡Reproductor ENCENDIDO! Ahora podrás escuchar algunas canciones que estén en el reproductor guardadas.");
        } else {
            return ResultadoUso.exito("¡Reproductor APAGADO!");
        }
    }

    @Override
    public List<String> getOpciones() {
        if (activo && cantidad > 0) {
            return Arrays.asList("Apagar", "Tirar");
        } else {
            return Arrays.asList("Encender", "Tirar");
        }
    }
}
