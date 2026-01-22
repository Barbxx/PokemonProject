package com.mypokemon.game.pantallas;

import com.badlogic.gdx.Screen;

/**
 * Interfaz para pantallas que soportan navegación entre pantallas.
 * Las clases que la implementen deben mantener una referencia a una pantalla de
 * retorno y gestionar la navegación.
 */
public interface INavigable {

    /**
     * Navega hacia atrás a la pantalla anterior.
     * Libera la pantalla actual y establece la pantalla de retorno como activa.
     */
    void navigateBack();

    /**
     * Navega a una nueva pantalla.
     * 
     * @param screen La pantalla a la que navegar.
     */
    void navigateTo(Screen screen);

    /**
     * Obtiene la pantalla a la que regresar al navegar hacia atrás.
     * 
     * @return La pantalla de retorno, o null si no hay ninguna establecida.
     */
    Screen getReturnScreen();
}
