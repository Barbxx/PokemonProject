package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Pantalla que muestra las partidas guardadas y permite cargar, borrar o crear nuevas.
public class PartidasScreen extends BaseScreen {

    private Texture fondo;
    private Texture botonJugarNormal, botonJugarSel;
    private Texture botonBorrarNormal, botonBorrarSel;
    private List<String> archivosPartidas;
    private int indiceSeleccionado = 0;
    private int modoBoton = 0; // 0 = Jugar, 1 = Borrar

    private OrthographicCamera camara;
    private Viewport viewport;
    private static final float ANCHO_VIRTUAL = 1280f;
    private static final float ALTO_VIRTUAL = 720f;

    public PartidasScreen(PokemonMain juego) {
        super(juego);
        this.fondo = cargarTextura("pantallaPartidas.png");
        this.botonJugarNormal = cargarTextura("boton_jugar_normal.png");
        this.botonJugarSel = cargarTextura("boton_jugar_seleccionado.png");
        this.botonBorrarNormal = cargarTextura("boton_borrar_normal.png");
        this.botonBorrarSel = cargarTextura("boton_borrar_seleccionado.png");

        camara = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL, camara);
        viewport.apply();
        camara.position.set(ANCHO_VIRTUAL / 2, ALTO_VIRTUAL / 2, 0);
        camara.update();

        archivosPartidas = new ArrayList<>();
        actualizarListaPartidas();
    }

    private void actualizarListaPartidas() {
        archivosPartidas.clear();
        File folder = new File(".");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".dat"));
        if (files != null) {
            for (File f : files) {
                String n = f.getName();
                if (n.equals("config.dat"))
                    continue;
                archivosPartidas.add(n.substring(0, n.length() - 4));
            }
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            juego.setScreen(new MainMenuScreen(juego));
            dispose();
            return;
        }

        if (!archivosPartidas.isEmpty()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
                indiceSeleccionado = (indiceSeleccionado <= 0) ? archivosPartidas.size() - 1 : indiceSeleccionado - 1;
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
                indiceSeleccionado = (indiceSeleccionado >= archivosPartidas.size() - 1) ? 0 : indiceSeleccionado + 1;
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
                modoBoton = (modoBoton == 0) ? 1 : 0;

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                String partida = archivosPartidas.get(indiceSeleccionado);
                if (modoBoton == 0) {
                    Explorador exp = Explorador.cargarProgreso(partida + ".dat");
                    if (exp != null) {
                        String tex = (exp.obtenerGenero() == com.mypokemon.game.utils.Genero.CHICA)
                                ? "protagonistaFemenino.png"
                                : "protagonistaMasculino1.png";
                        juego.setScreen(new GameScreen(juego, tex, 4, 4, exp.obtenerNombre(), partida));
                        dispose();
                        return;
                    }
                } else {
                    File f = new File(partida + ".dat");
                    if (f.delete()) {
                        actualizarListaPartidas();
                        if (indiceSeleccionado >= archivosPartidas.size())
                            indiceSeleccionado = Math.max(0, archivosPartidas.size() - 1);
                    }
                }
            }
        }

        camara.update();
        juego.batch.setProjectionMatrix(camara.combined);
        juego.batch.begin();
        if (fondo != null)
            juego.batch.draw(fondo, 0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        if (archivosPartidas.isEmpty()) {
            juego.fuente.getData().setScale(2.5f);
            juego.fuente.setColor(Color.WHITE);
            juego.fuente.draw(juego.batch, "NO HAY PARTIDAS GUARDADAS", 0, ALTO_VIRTUAL / 2, ANCHO_VIRTUAL, 1, false);
        } else {
            float startY = ALTO_VIRTUAL - 200, itemH = 100;
            for (int i = 0; i < archivosPartidas.size(); i++) {
                boolean sel = (i == indiceSeleccionado);
                juego.fuente.getData().setScale(1.8f);
                juego.fuente.setColor(sel ? Color.YELLOW : Color.WHITE);
                juego.fuente.draw(juego.batch, archivosPartidas.get(i), 150, startY - i * itemH);

                if (sel) {
                    float bx = 800, by = startY - i * itemH - 40, bw = 180, bh = 60;
                    juego.batch.draw(modoBoton == 0 ? botonJugarSel : botonJugarNormal, bx, by, bw, bh);
                    juego.batch.draw(modoBoton == 1 ? botonBorrarSel : botonBorrarNormal, bx + 220, by, bw, bh);
                }
            }
        }
        juego.batch.end();
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }
}
