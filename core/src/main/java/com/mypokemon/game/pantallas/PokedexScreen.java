package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.BasePokemonData;
import com.mypokemon.game.EspeciePokemon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

// Pantalla que muestra la Pokédex del jugador con Pokémon encontrados, capturados y nivel de investigación.
public class PokedexScreen extends NavigableScreen {
    private Texture texturaFondo, texturaFondoEntrada, pixelBlanco;
    private List<String> nombresCapturados;
    private Map<String, Texture> cacheTexturas = new TreeMap<>();
    private int indiceSeleccionado = 0;
    private OrthographicCamera camara;
    private Viewport vista;
    private final float ANCHO_VIRTUAL = 1280, ALTO_VIRTUAL = 720;
    private final Explorador explorador;
    private int gridSeleccionadoX = 0, gridSeleccionadoY = 0;
    private final int COLUMNAS_GRID = 6, FILAS_GRID = 4;

    public PokedexScreen(PokemonMain juego, Screen pantallaRetorno, Explorador explorador) {
        super(juego, pantallaRetorno);
        this.explorador = explorador;
        camara = new OrthographicCamera();
        vista = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL, camara);
        nombresCapturados = explorador.obtenerRegistro().getEncounterOrder();

        texturaFondo = cargarTextura("fondoPokedex.png");
        com.badlogic.gdx.graphics.Pixmap px = new com.badlogic.gdx.graphics.Pixmap(1, 1,
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        px.setColor(0, 0, 0, 0.7f);
        px.fill();
        texturaFondoEntrada = new Texture(px);
        agregarTextura(texturaFondoEntrada);
        px.setColor(Color.WHITE);
        px.fill();
        pixelBlanco = new Texture(px);
        agregarTextura(pixelBlanco);
        px.dispose();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            navegarAtras();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
            gridSeleccionadoY = (gridSeleccionadoY <= 0) ? FILAS_GRID - 1 : gridSeleccionadoY - 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
            gridSeleccionadoY = (gridSeleccionadoY >= FILAS_GRID - 1) ? 0 : gridSeleccionadoY + 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
            gridSeleccionadoX = (gridSeleccionadoX <= 0) ? COLUMNAS_GRID - 1 : gridSeleccionadoX - 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
            gridSeleccionadoX = (gridSeleccionadoX >= COLUMNAS_GRID - 1) ? 0 : gridSeleccionadoX + 1;
        indiceSeleccionado = gridSeleccionadoY * COLUMNAS_GRID + gridSeleccionadoX;

        ScreenUtils.clear(0, 0, 0, 1);
        vista.apply();
        juego.batch.setProjectionMatrix(camara.combined);
        juego.batch.begin();
        if (texturaFondo != null)
            juego.batch.draw(texturaFondo, 0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);
        juego.batch.setColor(Color.WHITE);

        float ix = 70, iy = 50, iw = 340, ih = 190;
        String nPkmn = (indiceSeleccionado < nombresCapturados.size()) ? nombresCapturados.get(indiceSeleccionado)
                : null;

        if (nPkmn != null) {
            BasePokemonData d = BasePokemonData.get(nPkmn);
            EspeciePokemon reg = explorador.obtenerRegistro().getRegistro().get(nPkmn);
            if (!cacheTexturas.containsKey(nPkmn)) {
                try {
                    String l = nPkmn.toLowerCase().replace(" h.", "").replace(" jr.", "-jr").replace(" ", "-");
                    cacheTexturas.put(nPkmn, new Texture(Gdx.files.internal(l + ".png")));
                } catch (Exception e) {
                }
            }
            Texture tB = cacheTexturas.get(nPkmn);
            if (tB != null)
                juego.batch.draw(tB, ix + (iw - 450) / 2, iy + ih - 130, 450, 450);
            juego.fuente.setColor(Color.WHITE);
            juego.fuente.getData().setScale(1.2f);
            juego.fuente.draw(juego.batch, nPkmn.toUpperCase(), ix + 20, iy + ih - 30);
            juego.fuente.setColor(Color.CYAN);
            juego.fuente.getData().setScale(1.3f);
            juego.fuente.draw(juego.batch, "REGIÓN: OneFerxxo", ix + 20, iy + ih - 70);
            juego.fuente.setColor(Color.ORANGE);
            int nInv = (reg != null ? reg.obtenerNivelInvestigacion() : 0);
            juego.fuente.draw(juego.batch, "NIVEL INV: " + nInv + "/10", ix + 20, iy + ih - 95);
            juego.fuente.setColor(Color.WHITE);
            juego.fuente.getData().setScale(0.9f);
            if (d != null && d.descripcion != null)
                juego.fuente.draw(juego.batch, d.descripcion, ix + 20, iy + ih - 145, iw - 40,
                        com.badlogic.gdx.utils.Align.left, true);
        }

        float gsx = 530, gsy = 420, bs = 100, esp = 15;
        for (int r = 0; r < FILAS_GRID; r++)
            for (int c = 0; c < COLUMNAS_GRID; c++) {
                int i = r * COLUMNAS_GRID + c;
                float bx = gsx + c * (bs + esp), by = gsy - r * (bs + esp);
                juego.batch.setColor(0, 0, 0, 0.4f);
                juego.batch.draw(pixelBlanco, bx, by, bs, bs);
                juego.batch.setColor(Color.WHITE);
                if (r == gridSeleccionadoY && c == gridSeleccionadoX) {
                    juego.batch.setColor(Color.YELLOW);
                    juego.batch.draw(pixelBlanco, bx - 3, by - 3, bs + 6, 3);
                    juego.batch.draw(pixelBlanco, bx - 3, by + bs, bs + 6, 3);
                    juego.batch.draw(pixelBlanco, bx - 3, by - 3, 3, bs + 6);
                    juego.batch.draw(pixelBlanco, bx + bs, by - 3, 3, bs + 6);
                    juego.batch.setColor(Color.WHITE);
                }
                if (i < nombresCapturados.size()) {
                    String name = nombresCapturados.get(i);
                    if (!cacheTexturas.containsKey(name)) {
                        try {
                            String l = name.toLowerCase().replace(" h.", "").replace(" jr.", "-jr").replace(" ", "-");
                            cacheTexturas.put(name, new Texture(Gdx.files.internal(l + ".png")));
                        } catch (Exception e) {
                        }
                    }
                    Texture t = cacheTexturas.get(name);
                    if (t != null)
                        juego.batch.draw(t, bx + 10, by + 10, bs - 20, bs - 20);
                }
            }
        juego.batch.end();
        juego.fuente.getData().setScale(1.0f);
    }

    @Override
    public void resize(int w, int h) {
        vista.update(w, h, true);
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Texture t : cacheTexturas.values())
            if (t != null)
                t.dispose();
    }
}
