package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;

/**
 * Clase base abstracta para pantallas que soportan navegación hacia atrás.
 * Proporciona manejo automático de la tecla ESC para regresar a la pantalla
 * anterior.
 */
public abstract class NavigableScreen extends BaseScreen implements INavigable {

    protected final Screen returnScreen;

    public NavigableScreen(PokemonMain game, Screen returnScreen) {
        super(game);
        this.returnScreen = returnScreen;
    }

    /**
     * Navega hacia atrás a la pantalla de retorno.
     * Libera automáticamente la pantalla actual.
     */
    @Override
    public void navigateBack() {
        if (returnScreen != null) {
            game.setScreen(returnScreen);
            dispose();
        } else {
            Gdx.app.log(this.getClass().getSimpleName(), "Warning: No return screen set");
        }
    }

    /**
     * Navega a una nueva pantalla.
     * Libera automáticamente la pantalla actual.
     * 
     * @param screen La pantalla a la que navegar.
     */
    @Override
    public void navigateTo(Screen screen) {
        game.setScreen(screen);
        dispose();
    }

    /**
     * Obtiene la pantalla de retorno.
     * 
     * @return La pantalla a la que regresar.
     */
    @Override
    public Screen getReturnScreen() {
        return returnScreen;
    }

    /**
     * Maneja la pulsación de la tecla ESC para navegar hacia atrás.
     * Las subclases pueden sobrescribir esto para personalizar el comportamiento.
     * 
     * @return true si se manejó ESC, false en caso contrario.
     */
    protected boolean handleEscapeKey() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            navigateBack();
            return true;
        }
        return false;
    }
}
