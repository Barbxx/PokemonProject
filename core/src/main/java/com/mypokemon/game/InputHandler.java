package com.mypokemon.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class InputHandler extends InputAdapter {
    private Explorador jugador;

    public InputHandler(Explorador jugador) {
        this.jugador = jugador;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            // MISIÓN 2: Acceso a Pokédex (Harry Potter)
            case Keys.K:
                System.out.println("Especies completadas: " +
                        jugador.getRegistro().verificarProgreso());
                break;
        }
        return false; // Return false to let event propagate (e.g. for movement)
    }
}
