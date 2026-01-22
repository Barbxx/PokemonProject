package com.mypokemon.game.colisiones;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.pantallas.GameScreen;
import com.mypokemon.game.pantallas.LaboratorioScreen;

// Colisión para la puerta del laboratorio. Permite al jugador entrar si aún no tiene un Pokémon inicial.
public class ColisionPuertaLaboratorio extends ZonaInteractiva {
    private Texture texturaLetrero;
    private float letreroX, letreroY;
    private PokemonMain juego;
    private GameScreen pantallaJuego;
    private Explorador explorador;

    public ColisionPuertaLaboratorio(float x, float y, Texture letrero, PokemonMain juego, GameScreen pantallaJuego,
            Explorador explorador) {
        this.limites = new Rectangle(x + 10, y, 60, 70);
        this.tipo = "INTERACTIVO";
        this.rangoInteraccion = 45f;
        this.mensajeInteraccion = "Presiona [T] para entrar al laboratorio.";
        this.texturaLetrero = letrero;
        this.letreroX = x + 80;
        this.letreroY = y + 50;
        this.juego = juego;
        this.pantallaJuego = pantallaJuego;
        this.explorador = explorador;
    }

    @Override
    public void interactuar() {
        if (explorador.obtenerEquipo().isEmpty()) {
            pantallaJuego.iniciarFadeOut(new LaboratorioScreen(juego, pantallaJuego));
        } else {
            pantallaJuego.mostrarNotificacion("La elección es permanente. No puedes volver a entrar.");
        }
    }

    public void renderizarLetrero(SpriteBatch batch) {
        if (texturaLetrero != null)
            batch.draw(texturaLetrero, letreroX, letreroY, 60, 60);
    }
}
