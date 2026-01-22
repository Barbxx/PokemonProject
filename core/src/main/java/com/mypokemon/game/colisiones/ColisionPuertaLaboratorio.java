package com.mypokemon.game.colisiones;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.pantallas.GameScreen;
import com.mypokemon.game.pantallas.LaboratorioScreen;

/**
 * Colisión específica para la puerta del laboratorio.
 */
public class ColisionPuertaLaboratorio extends ZonaInteractiva {

    private Texture texturaLetrero;
    private float letreroX, letreroY;
    private PokemonMain juego;
    private GameScreen pantallaJuego;
    private Explorador explorador;

    public ColisionPuertaLaboratorio(float x, float y, Texture letrero,
            PokemonMain juego, GameScreen pantallaJuego,
            Explorador explorador) {
        // Sin colisión física, solo interacción cercana
        this.limites = new Rectangle(x + 10, y, 60, 70);
        this.tipo = "INTERACTIVO"; // Cambiado de ZONA para quitar colisión física
        this.rangoInteraccion = 45f; // Reducido de 35 a 25 para interacción muy cercana
        this.mensajeInteraccion = "Presiona [T] para entrar al laboratorio.";
        this.texturaLetrero = letrero;
        // Ajustado para letrero más pequeño
        this.letreroX = x + 80;
        this.letreroY = y + 50;
        this.juego = juego;
        this.pantallaJuego = pantallaJuego;
        this.explorador = explorador;
    }

    @Override
    public void interactuar() {
        if (explorador.getEquipo().isEmpty()) {
            // Iniciar fade out y cambiar a LaboratorioScreen
            pantallaJuego.iniciarFadeOut(new LaboratorioScreen(juego, pantallaJuego));
        } else {
            pantallaJuego.mostrarNotificacion(
                    "La elección es permanente. No puedes volver a entrar.");
        }
    }

    /**
     * Renderiza el letrero del laboratorio con tamaño reducido.
     */
    public void renderizarLetrero(SpriteBatch batch) {
        if (texturaLetrero != null) {
            // Dibuja el letrero muy pequeño: 120x60 píxeles
            batch.draw(texturaLetrero, letreroX, letreroY, 60, 60);
        }
    }
}
