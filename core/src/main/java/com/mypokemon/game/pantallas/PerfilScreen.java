package com.mypokemon.game.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;

// Pantalla que muestra el perfil del entrenador con información de progreso.
public class PerfilScreen extends BaseScreen {

    private final BaseScreen pantallaPadre;
    private final Explorador explorador;
    private Texture texturaFondo;

    public PerfilScreen(PokemonMain juego, BaseScreen pantallaPadre, Explorador explorador) {
        super(juego);
        this.pantallaPadre = pantallaPadre;
        this.explorador = explorador;

        boolean esChico = com.mypokemon.game.utils.Genero.CHICO.equals(explorador.obtenerGenero());
        String nFondo = esChico ? "fondoPerfilChico.png" : "fondoPerfilChica.png";
        texturaFondo = cargarTextura(nFondo);
        if (texturaFondo == null)
            texturaFondo = cargarTextura("fondoMochila.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            juego.setScreen(pantallaPadre);
            dispose();
            return;
        }

        juego.batch.begin();
        if (texturaFondo != null)
            juego.batch.draw(texturaFondo, 0, 0, 800, 480);

        juego.fuente.setColor(Color.GOLD);
        juego.fuente.getData().setScale(2.0f);
        dibujarTextoCentrado("PERFIL DE ENTRENADOR", 400);

        juego.fuente.setColor(Color.WHITE);
        juego.fuente.getData().setScale(1.5f);
        float y = 300, esp = 40;
        juego.fuente.draw(juego.batch, "Nombre: " + explorador.obtenerNombre(), 100, y);
        boolean esChico = com.mypokemon.game.utils.Genero.CHICO.equals(explorador.obtenerGenero());
        juego.fuente.draw(juego.batch, "Género: " + (esChico ? "Chico" : "Chica"), 100, y - esp);
        juego.fuente.draw(juego.batch, "Mochila: " + explorador.obtenerMochila().obtenerEspacioOcupado() + "/"
                + explorador.obtenerMochila().obtenerCapacidadMaxima(), 100, y - esp * 2);
        juego.fuente.draw(juego.batch, "Pokémon Capturados: " + explorador.obtenerRegistro().getCapturedOrder().size(),
                100,
                y - esp * 3);

        juego.fuente.getData().setScale(1.0f);
        juego.fuente.setColor(Color.LIGHT_GRAY);
        juego.fuente.draw(juego.batch, "Presiona ESC para volver", 20, 30);
        juego.batch.end();
    }

    private void dibujarTextoCentrado(String t, float y) {
        GlyphLayout l = new GlyphLayout(juego.fuente, t);
        juego.fuente.draw(juego.batch, t, (800 - l.width) / 2, y);
    }
}
