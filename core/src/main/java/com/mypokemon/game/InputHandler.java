package com.mypokemon.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

// Manejador central de entradas de teclado para el control del Explorador.
public class InputHandler extends InputAdapter {
    private final Explorador explorador;

    public InputHandler(Explorador explorador) {
        this.explorador = explorador;
    }

    @Override
    public boolean keyDown(int codigoTecla) {
        if (codigoTecla == Keys.K) {
            System.out.println("Progreso de Pokédex: " + explorador.obtenerRegistro().verificarProgreso());
            return true;
        }
        return false;
    }
}
