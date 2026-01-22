package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;

/**
 * Screen displaying the Help information.
 * Extends StaticDisplayScreen for automatic background rendering and
 * navigation.
 */
public class AyudaScreen extends StaticDisplayScreen {

    public AyudaScreen(final PokemonMain game) {
        super(game, new MainMenuScreen(game), "pantallaAyuda.png");
    }
}
