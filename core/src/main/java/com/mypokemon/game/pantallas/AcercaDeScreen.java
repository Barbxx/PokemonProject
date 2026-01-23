package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;

/**
 * Pantalla que muestra información sobre los desarrolladores y créditos del
 * juego.
 */
public class AcercaDeScreen extends StaticDisplayScreen {

    /**
     * Constructor de AcercaDeScreen.
     * 
     * @param game Instancia principal del juego.
     */
    public AcercaDeScreen(final PokemonMain game) {
        super(game, new MainMenuScreen(game), "pantallaAcercaDe.png");
    }
}
