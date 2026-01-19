package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;

/**
 * Screen displaying information "About" the game.
 * Extends StaticDisplayScreen for automatic background rendering and
 * navigation.
 */
public class AcercaDeScreen extends StaticDisplayScreen {

    public AcercaDeScreen(final PokemonMain game) {
        super(game, new MainMenuScreen(game), "pantallaAcercaDe.png");
    }
}
