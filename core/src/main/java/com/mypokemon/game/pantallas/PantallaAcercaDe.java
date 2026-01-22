package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonPrincipal;

/**
 * Screen displaying information "About" the game.
 * Extends PantallaEstatica for automatic background rendering and
 * navigation.
 */
public class PantallaAcercaDe extends PantallaEstatica {

    public PantallaAcercaDe(final PokemonPrincipal game) {
        super(game, new PantallaMenuPrincipal(game), "pantallaAcercaDe.png");
    }
}




