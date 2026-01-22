package com.mypokemon.game.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.InputAdapter;
import com.mypokemon.game.PokemonMain;

// Pantalla de créditos finales que se muestra al derrotar a Arceus.
public class CreditsScreen extends BaseScreen {
    private Texture fondo;
    private ShapeRenderer renderizadorFormas;
    private String[] mensajes;
    private int indiceMensajeActual;

    public CreditsScreen(PokemonMain juego, String nombreJugador) {
        super(juego);
        this.renderizadorFormas = new ShapeRenderer();
        this.fondo = cargarTextura("fondoFInal.png");

        this.mensajes = new String[] {
                "Has derrotado a Arceus y su poder ha estabilizado todas las realidades…",
                "el Upside Down retrocede, la magia vuelve a Hogwarts y el ritmo regresa a las calles…",
                "Miras tu Pokédex y entiendes que nunca fue una simple misión, sino que te convertiste en el guardián de todas estas historias.",
                "Misión cumplida, " + nombreJugador
                        + ", el destino de los mundos está a salvo, y tu nombre ha quedado grabado en la esencia misma de la historia.",
                "Porque al final, el camino siempre estuvo claro…",
                "¡Atrápalos a todos!"
        };
        this.indiceMensajeActual = 0;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int x, int y, int p, int b) {
                avanzarMensaje();
                return true;
            }

            @Override
            public boolean keyDown(int k) {
                avanzarMensaje();
                return true;
            }
        });
    }

    private void avanzarMensaje() {
        if (indiceMensajeActual < mensajes.length - 1)
            indiceMensajeActual++;
        else {
            juego.setScreen(new MainMenuScreen(juego));
            dispose();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        juego.batch.begin();
        if (fondo != null)
            juego.batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        juego.batch.end();

        float ac = Gdx.graphics.getWidth() * 0.8f, px = (Gdx.graphics.getWidth() - ac) / 2, py = 50, alc = 200;
        renderizadorFormas.begin(ShapeRenderer.ShapeType.Filled);
        renderizadorFormas.setColor(0, 0, 0, 0.7f);
        renderizadorFormas.rect(px, py, ac, alc);
        renderizadorFormas.end();

        juego.batch.begin();
        juego.fuente.setColor(Color.WHITE);
        juego.fuente.getData().setScale(2.0f);
        juego.fuente.draw(juego.batch, mensajes[indiceMensajeActual], px + 20, py + alc - 40, ac - 40,
                com.badlogic.gdx.utils.Align.center, true);
        juego.batch.end();
        juego.fuente.getData().setScale(1.0f);
    }

    @Override
    public void dispose() {
        super.dispose();
        renderizadorFormas.dispose();
    }
}
