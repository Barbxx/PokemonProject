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
            // MISIÓN 1: Recolección Manual (Simulación)
            case Keys.P: // 'P' de Planta
                recolectar("Planta");
                break;
            case Keys.G: // 'G' de Guijarro
                recolectar("Guijarro");
                break;

            // MISIÓN 1: Crafteo de Feid
            case Keys.C:
                fabricarItem();
                break;

            // MISIÓN 2: Acceso a Pokédex (Harry Potter)
            case Keys.K:
                System.out.println("Especies completadas: " +
                        jugador.getRegistro().verificarProgreso());
                break;
        }
        return false; // Return false to let event propagate (e.g. for movement)
    }

    private void recolectar(String tipo) {
        if (jugador.getMochila().puedeAgregar(1)) {
            jugador.getMochila().recolectarRecurso(tipo);
            System.out.println("Recogiste: " + tipo);
        } else {
            System.err.println("¡Límite de espacio superado!");
        }
    }

    private void fabricarItem() {
        boolean exito = jugador.getMochila().fabricarPokeBall();
        if (exito) {
            System.out.println("¡Profesor Feid: Poké Ball creada con éxito!");
        } else {
            System.out.println("No tienes materiales suficientes (2 Plantas, 3 Guijarros).");
        }
    }
}
