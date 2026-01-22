package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;

/**
 * Clase base abstracta para pantallas que soportan navegación hacia atrás.
 * Proporciona manejo automático de la tecla ESC para volver a la pantalla
 * anterior.
 */
public abstract class NavigableScreen extends BaseScreen implements INavigable {

    protected final Screen returnScreen;

    public NavigableScreen(PokemonMain game, Screen returnScreen) {
        super(game);
        this.returnScreen = returnScreen;
    }

    /**
     * Navega de regreso a la pantalla anterior definida.
     * Libera los recursos de la pantalla actual automáticamente.
     */
    @Override
    public void navigateBack() {
        if (returnScreen != null) {
            game.setScreen(returnScreen);
            dispose();
        } else {
            Gdx.app.log(this.getClass().getSimpleName(), "Advertencia: No hay pantalla de retorno configurada");
        }
    }

    /**
     * Navega hacia una nueva pantalla.
     * Libera los recursos de la pantalla actual automáticamente.
     * 
     * @param screen La pantalla a la cual navegar.
     */
    @Override
    public void navigateTo(Screen screen) {
        game.setScreen(screen);
        dispose();
    }

    /**
     * Obtiene la pantalla de retorno configurada.
     * 
     * @return La pantalla de retorno, o null si no se ha configurado.
     */
    @Override
    public Screen getReturnScreen() {
        return returnScreen;
    }

    /**
     * Maneja la pulsación de la tecla ESC para navegar hacia atrás.
     * Las subclases pueden sobrescribir esto para personalizar el comportamiento.
     * 
     * @return true si la tecla ESC fue manejada, false en caso contrario.
     */
    protected boolean handleEscapeKey() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            navigateBack();
            return true;
        }
        return false;
    }
}
