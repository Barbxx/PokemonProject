package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.inventario.exceptions.SpaceException;
import com.mypokemon.game.inventario.ObjectFactory;
import com.mypokemon.game.inventario.Crafteo;
import com.mypokemon.game.inventario.Receta;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Vector3;
import java.util.List;

// Pantalla de crafteo (fabricación de objetos). Permite crear nuevos ítems combinando recursos.
public class CrafteoScreen extends BaseScreen {

    private Texture fondo;
    private GameScreen pantallaRetorno;
    private OrthographicCamera camara;
    private Viewport viewport;
    private final float ANCHO_VIRTUAL = 1280, ALTO_VIRTUAL = 720;
    private Texture texBotonGris, texBotonGrisSel;
    private Crafteo logicaCrafteo;
    private List<Receta> listaRecetas;
    private Texture texPlanta, texGuijarro, texBaya;
    private Texture texPokebola, texPokebolaPeso, texPocionHerbal, texElixir, texRevivir, texReproductor, texGuante,
            texFrijol;
    private Texture texturaFondoSlot, texturaBlanca;
    private int indiceRecetaSeleccionada = -1;
    private String mensajeCrafteo = "";
    private float temporizadorMensajeCrafteo = 0;

    public CrafteoScreen(PokemonMain juego, GameScreen pantallaRetorno) {
        super(juego);
        this.pantallaRetorno = pantallaRetorno;
        camara = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL, camara);
        camara.position.set(ANCHO_VIRTUAL / 2, ALTO_VIRTUAL / 2, 0);

        this.logicaCrafteo = pantallaRetorno.obtenerExplorador().obtenerSistemaCrafteo();
        cargarRecursos();
        this.listaRecetas = logicaCrafteo.obtenerTodasLasRecetas();
    }

    private void cargarRecursos() {
        try {
            fondo = cargarTextura("fondoCrafteo.png");
            texBotonGris = cargarTextura("botonCrafteoGris.png");
            texBotonGrisSel = cargarTextura("botonCrafteoGris_seleccionado.png");
            texPlanta = cargarTextura("planta.png");
            texGuijarro = cargarTextura("guijarro.png");
            texBaya = cargarTextura("baya.png");
            texPokebola = cargarTextura("pokeball.png");
            texPokebolaPeso = cargarTextura("pokeballPeso.png");
            texPocionHerbal = cargarTextura("pocionHerbal.png");
            texElixir = cargarTextura("elixirPielPiedra.png");
            texRevivir = cargarTextura("revivirCasero.png");
            texReproductor = cargarTextura("reproductor.png");
            texGuante = cargarTextura("guanteReflejo.png");
            texFrijol = cargarTextura("frijolMagico.png");

            Pixmap px = new Pixmap(1, 1, Format.RGBA8888);
            px.setColor(0, 0, 0, 0.4f);
            px.fill();
            texturaFondoSlot = new Texture(px);
            agregarTextura(texturaFondoSlot);
            px.setColor(Color.WHITE);
            px.fill();
            texturaBlanca = new Texture(px);
            agregarTextura(texturaBlanca);
            px.dispose();
        } catch (Exception e) {
            Gdx.app.error("CrafteoScreen", "Error de recursos: " + e.getMessage());
        }
    }

    @Override
    public void render(float delta) {
        gestionarEntrada();
        ScreenUtils.clear(0, 0, 0, 1);
        if (temporizadorMensajeCrafteo > 0) {
            temporizadorMensajeCrafteo -= delta;
            if (temporizadorMensajeCrafteo < 0)
                temporizadorMensajeCrafteo = 0;
        }
        viewport.apply();
        juego.batch.setProjectionMatrix(camara.combined);
        juego.batch.begin();
        if (fondo != null)
            juego.batch.draw(fondo, 0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);
        dibujarCuadricula();
        dibujarBotonCrafteo();
        dibujarPanelDetalles();
        juego.batch.end();
    }

    private void gestionarEntrada() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            juego.setScreen(pantallaRetorno);
            return;
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 m = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(m);
            float gx = 100, gy = ALTO_VIRTUAL - 350, sl = 120, gp = 20;
            int cl = 4;
            for (int i = 0; i < listaRecetas.size(); i++) {
                float x = gx + (i % cl) * (sl + gp), y = gy - (i / cl) * (sl + gp);
                if (m.x >= x && m.x <= x + sl && m.y >= y && m.y <= y + sl)
                    indiceRecetaSeleccionada = i;
            }
            float bw = 320, bh = 120, bx = 200, by = 60;
            if (m.x >= bx && m.x <= bx + bw && m.y >= by && m.y <= by + bh) {
                if (indiceRecetaSeleccionada != -1)
                    intentarCrafteo(listaRecetas.get(indiceRecetaSeleccionada));
                else {
                    mensajeCrafteo = "¡Selecciona una receta primero!";
                    temporizadorMensajeCrafteo = 3;
                }
            }
        }
    }

    private void intentarCrafteo(Receta r) {
        try {
            logicaCrafteo.craftear(r.getIdResultado(), pantallaRetorno.obtenerExplorador().obtenerMochila());
            mensajeCrafteo = "¡Crafteaste con éxito!";
        } catch (SpaceException e) {
            mensajeCrafteo = "¡Inventario lleno!";
        } catch (IllegalArgumentException e) {
            mensajeCrafteo = "¡No tienes los materiales!";
        } catch (Exception e) {
            mensajeCrafteo = "Error inesperado";
        }
        temporizadorMensajeCrafteo = 3;
    }

    private void dibujarBotonCrafteo() {
        float bw = 320, bh = 120, x = 200, y = 60;
        Texture t = texBotonGris;
        Vector3 m = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(m);
        if (m.x >= x && m.x <= x + bw && m.y >= y && m.y <= y + bh)
            t = texBotonGrisSel;
        if (t != null)
            juego.batch.draw(t, x, y, bw, bh);
    }

    private void dibujarCuadricula() {
        float gx = 100, gy = ALTO_VIRTUAL - 350, sl = 120, gp = 20;
        int cl = 4;
        for (int i = 0; i < listaRecetas.size(); i++) {
            Receta r = listaRecetas.get(i);
            float x = gx + (i % cl) * (sl + gp), y = gy - (i / cl) * (sl + gp);
            if (texturaFondoSlot != null) {
                juego.batch.setColor(Color.WHITE);
                juego.batch.draw(texturaFondoSlot, x, y, sl, sl);
            }
            Texture ic = obtenerTexturaPorId(r.getIdResultado());
            if (ic != null) {
                float s = sl * 0.7f;
                juego.batch.draw(ic, x + (sl - s) / 2, y + (sl - s) / 2, s, s);
            }
            if (i == indiceRecetaSeleccionada) {
                juego.batch.setColor(Color.YELLOW);
                if (texturaBlanca != null) {
                    juego.batch.draw(texturaBlanca, x - 3, y + sl, sl + 6, 3);
                    juego.batch.draw(texturaBlanca, x - 3, y - 3, sl + 6, 3);
                    juego.batch.draw(texturaBlanca, x - 3, y - 3, 3, sl + 6);
                    juego.batch.draw(texturaBlanca, x + sl, y - 3, 3, sl + 6);
                }
                juego.batch.setColor(Color.WHITE);
            }
        }
    }

    private void dibujarPanelDetalles() {
        if (indiceRecetaSeleccionada >= 0 && indiceRecetaSeleccionada < listaRecetas.size()) {
            Receta sel = listaRecetas.get(indiceRecetaSeleccionada);
            float dx = 700, dy = ALTO_VIRTUAL - 300;
            Texture ic = obtenerTexturaPorId(sel.getIdResultado());
            if (ic != null)
                juego.batch.draw(ic, dx + 200, dy - 20, 130, 130);
            juego.fuente.setColor(Color.CYAN);
            juego.fuente.getData().setScale(1.5f);
            juego.fuente.draw(juego.batch, sel.getNombreResultado(), dx + 90, dy - 40);
            juego.fuente.setColor(Color.LIGHT_GRAY);
            juego.fuente.getData().setScale(1.0f);
            juego.fuente.draw(juego.batch, ObjectFactory.crearCrafteado(sel.getIdResultado(), 1).obtenerDescripcion(),
                    dx + 90, dy - 80);
            float iy = dy - 200;
            if (sel.reqPlantas > 0) {
                dibujarIngrediente("planta", sel.reqPlantas, dx, iy);
                iy -= 50;
            }
            if (sel.reqGuijarros > 0) {
                dibujarIngrediente("guijarro", sel.reqGuijarros, dx, iy);
                iy -= 50;
            }
            if (sel.reqBayas > 0) {
                dibujarIngrediente("baya", sel.reqBayas, dx, iy);
                iy -= 50;
            }
        }
        if (temporizadorMensajeCrafteo > 0) {
            juego.fuente.getData().setScale(1.2f);
            juego.fuente.setColor(mensajeCrafteo.contains("éxito") ? Color.GREEN : Color.RED);
            juego.fuente.draw(juego.batch, mensajeCrafteo, 100, 200);
            juego.fuente.setColor(Color.WHITE);
            juego.fuente.getData().setScale(1.0f);
        }
    }

    private void dibujarIngrediente(String id, int req, float dx, float iy) {
        int pos = pantallaRetorno.obtenerExplorador().obtenerMochila().obtenerCantidad(id);
        Texture ic = null;
        if (id.equals("planta"))
            ic = texPlanta;
        else if (id.equals("guijarro"))
            ic = texGuijarro;
        else if (id.equals("baya"))
            ic = texBaya;
        if (ic != null) {
            juego.batch.setColor(Color.WHITE);
            juego.batch.draw(ic, dx + 100, iy - 30, 30, 30);
        }
        juego.fuente.setColor(Color.WHITE);
        juego.fuente.draw(juego.batch, id.substring(0, 1).toUpperCase() + id.substring(1), dx + 150, iy - 10);
        juego.fuente.setColor(pos >= req ? Color.GREEN : Color.RED);
        juego.fuente.draw(juego.batch, pos + "/" + req, dx + 390, iy - 10);
    }

    private Texture obtenerTexturaPorId(String id) {
        switch (id.toLowerCase()) {
            case "pokeball":
                return texPokebola;
            case "heavyball":
                return texPokebolaPeso;
            case "pocion":
                return texPocionHerbal;
            case "elixir":
                return texElixir;
            case "revivir":
                return texRevivir;
            case "reproductor":
                return texReproductor;
            case "guante":
                return texGuante;
            case "frijol":
                return texFrijol;
            default:
                return null;
        }
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h);
    }
}
