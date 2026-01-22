package com.mypokemon.game.pantallas;

import com.badlogic.gdx.Screen;

/**
 * Interfaz para pantallas que soportan navegación entre pantallas.
 * Las clases que la implementen deben mantener una pantalla de retorno y
 * manejar la navegación.
 */
public interface INavigable {

    /**
     * Navega de regreso a la pantalla anterior.
     * Elimina la pantalla actual y establece la pantalla de retorno como activa.
     */
    void navigateBack();

    /**
     * Navega hacia una nueva pantalla.
     * 
     * @param screen La pantalla a la cual navegar.
     */
    void navigateTo(Screen screen);

    /**
     * Obtiene la pantalla a la cual regresar cuando se navega hacia atrás.
     * 
     * @return La pantalla de retorno, o null si no hay ninguna establecida.
     */
    Screen getReturnScreen();
}
