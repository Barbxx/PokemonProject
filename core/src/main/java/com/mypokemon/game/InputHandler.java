package com.mypokemon.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

/**
 * Manejador principal de entradas de teclado para el juego.
 * Intercepta eventos de teclas para ejecutar acciones específicas como
 * abrir menús o interactuar con el entorno.
 */
public class InputHandler extends InputAdapter {
    /** Referencia al jugador para acceder a sus datos (como la Pokédex). */
    private Explorador jugador;

    /**
     * Constructor del manejador de entradas.
     * 
     * @param jugador Instancia del jugador actual.
     */
    public InputHandler(Explorador jugador) {
        this.jugador = jugador;
    }

    /**
     * Se llama cuando una tecla es presionada.
     * 
     * @param keycode El código de la tecla presionada (De Input.Keys).
     * @return true si el evento fue manejado y no debe propagarse, false de lo
     *         contrario.
     */
    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            // MISIÓN 2: Acceso a Pokédex (Harry Potter)
            case Keys.K:
                System.out.println("Especies completadas: " +
                        jugador.getRegistro().verificarProgreso());
                break;
        }
        return false; // Retorna false para permitir que el evento se propague (ej. para el
                      // movimiento)
    }
}
