package com.mypokemon.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mypokemon.game.pantallas.MainMenuScreen;
import com.mypokemon.game.client.ClienteRed;

/**
 * Clase principal del juego que extiende de Game (LibGDX).
 * Actúa como el núcleo que gestiona las transiciones entre pantallas
 * y mantiene recursos globales como el SpriteBatch y la Fuente.
 */
public class PokemonMain extends Game {
    /** Batch para dibujar texturas y sprites de forma eficiente. */
    public SpriteBatch batch;

    /** Fuente principal utilizada para el texto en las pantallas. */
    public BitmapFont font;

    /** Cliente de red para la funcionalidad multijugador. */
    public com.mypokemon.game.client.ClienteRed clienteRed;

    /**
     * Inicializa los recursos globales del juego (batch, fuentes) al arrancar.
     * Establece la pantalla inicial (Menú Principal).
     */
    @Override
    public void create() {
        batch = new SpriteBatch();

        // Inicialización de la fuente por defecto de LibGDX
        font = new BitmapFont();

        // Configuración de filtro para que el texto se vea nítido (pixel art style)
        font.getRegion().getTexture().setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest,
                com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest);

        // Iniciar con la pantalla del menú principal
        this.setScreen(new MainMenuScreen(this));
    }

    /**
     * Ciclo principal de renderizado. Delega en la pantalla activa.
     */
    @Override
    public void render() {
        // Llama al render de la pantalla actual
        super.render();
    }

    /**
     * Libera los recursos globales (batch y fuentes) al cerrar la aplicación.
     */
    @Override
    public void dispose() {
        // Liberar recursos al cerrar el juego
        batch.dispose();
        font.dispose();
    }
}
