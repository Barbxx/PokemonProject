package com.mypokemon.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

/**
 * Manejador principal de entradas del teclado.
 * Procesa teclas globales como la apertura de la Pokédex fuera de las pantallas
 * específicas.
 */
public class InputHandler extends InputAdapter {
    private Explorador jugador;

    /**
     * Constructor del manejador de entrada.
     * 
     * @param jugador Instancia del jugador para acceder a sus datos.
     */
    public InputHandler(Explorador jugador) {
        this.jugador = jugador;
    }

    /**
     * Se llama cuando una tecla es presionada.
     * 
     * @param keycode Código de la tecla presionada.
     * @return true si el evento fue manejado, false para propagarlo.
     */
    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.K:
                System.out.println("Especies completadas: " +
                        jugador.getRegistro().verificarProgreso());
                break;
        }
        return false; 
    }
}
