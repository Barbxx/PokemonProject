package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.EspeciePokemon;
import com.mypokemon.game.Pokemon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;

// Pantalla que representa el Laboratorio del Profesor, donde se elige el Pokémon inicial.
public class LaboratorioScreen extends BaseScreen {
    private GameScreen pantallaJuegoRef;
    private Texture texturaFondo, spriteFeid;
    private Texture texturaRowlet, texturaCyndaquil, texturaOshawott;
    private Texture texturaIconoDialogo, pixelBlancoUi;
    private Texture retratoFerxxoRowlet, retratoFerxxoCyndaquil, retratoFerxxoOshawott, retratoFerxxoCientifico;

    private boolean mostrarDialogo = false;
    private int paginaDialogoActual = 0;
    private String[] textoDialogoActual;

    private final String[] TEXTO_INTRO = {
            "¡Hágale, mor! Llegó a la parte más firme. Aquí tengo estos tres candidatos que están listos para acompañarte en tu aventura por todo OneFerxxo.",
            "Están guardaditos en las Poké Balls pa' que no se alboroten.",
            "Vea, desde que Arceus me trajo acá me he convertido en un buen entrenador de estas chimbitas...",
            "lo va a necesitar si lo quiere poner en su lugar... se lo digo yo",
            "Vea, hay que ver que tipo de Pokemón le gusta...agarrese uno"
    };

    private final String[] TEXTO_ROWLET = {
            "Este es el Rowlet. Es tipo Planta y Volador. Este bicho es bien silencioso, le llega a los otros por la espalda y ¡pum!, los deja sanos.",
            "Entonces, ¿qué dice, mor? ¿Se va a llevar al tipo Planta y Volador Rowlet?"
    };

    private final String[] TEXTO_CYNDAQUIL = {
            "Este es el Cyndaquil. Es tipo Fuego. Se ve todo calmado y chill, pero donde se moleste... le sale candela por el lomo y eso quema hasta el alma, mor.",
            "¿Cómo fue entonces? ¿Se queda con el tipo Fuego Cyndaquil?"
    };

    private final String[] TEXTO_OSHAWOTT = {
            "Este es el Oshawott. Es tipo Agua. El parcero se ve muy tierno, pero no se confunda, que esa conchita que trae en el pecho la usa como un sable y ¡tome!, no copia de nada.",
            "Entonces, ¿cómo es la vuelta? ¿Se va a montar con el tipo Agua Oshawott?"
    };

    private final String[] EXITO_ROWLET = { "¡Recibiste el Pokemón Rowlet de manos del Profesor Ferxxo!" };
    private final String[] EXITO_CYNDAQUIL = { "¡Recibiste el Pokemón Cyndaquil de manos del Profesor Ferxxo!" };
    private final String[] EXITO_OSHAWOTT = { "¡Recibiste el Pokemón Oshawott de manos del Profesor Ferxxo!" };

    private float alfaFade = 1f;
    private boolean apareciendo = true, desapareciendo = false;
    private boolean seleccionActiva = false;
    private int indiceSeleccion = 0;
    private boolean enTransicion = false;

    private OrthographicCamera camara;
    private Viewport viewport;

    private float posX = 330, posY = 60;
    private TextureRegion cuadroActual;
    private float anchoJugador = 40f, altoJugador = 32f;
    private float feidX = 350, feidY = 120, anchoFeid = 25, altoFeid = 35;
    private float rowletX = 415, rowletY = 95, anchoInicial = 30, altoInicial = 30;
    private float cyndaquilX = 445, cyndaquilY = 95;
    private float oshawottX = 480, oshawottY = 95;

    public LaboratorioScreen(PokemonMain juego, GameScreen pantallaJuego) {
        super(juego);
        this.pantallaJuegoRef = pantallaJuego;
        camara = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camara);
        camara.position.set(400, 240, 0);
        camara.update();
        alfaFade = 1f;
        apareciendo = true;
        desapareciendo = false;
        textoDialogoActual = TEXTO_INTRO;
    }

    @Override
    public void show() {
        texturaFondo = cargarTextura("fondoLaboratorio.png");
        spriteFeid = cargarTextura("feidSprite.png");
        texturaRowlet = cargarTextura("rowletLab.png");
        texturaCyndaquil = cargarTextura("cyndaquilLab.png");
        texturaOshawott = cargarTextura("oshawottLab.png");
        retratoFerxxoCientifico = cargarTextura("ferxxoCientifico.png");
        retratoFerxxoRowlet = cargarTextura("profeFerxxoRowlet.png");
        retratoFerxxoCyndaquil = cargarTextura("profeFerxxoCyndaquil.png");
        retratoFerxxoOshawott = cargarTextura("profeFerxxoOshawott.png");
        texturaIconoDialogo = retratoFerxxoCientifico;

        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(Color.WHITE);
        p.fill();
        pixelBlancoUi = new Texture(p);
        agregarTextura(pixelBlancoUi);
        p.dispose();

        if (pantallaJuegoRef.obtenerHojaJugador() != null) {
            TextureRegion[][] frames = TextureRegion.split(pantallaJuegoRef.obtenerHojaJugador(),
                    pantallaJuegoRef.obtenerHojaJugador().getWidth() / pantallaJuegoRef.obtenerColsFrames(),
                    pantallaJuegoRef.obtenerHojaJugador().getHeight() / pantallaJuegoRef.obtenerFilasFrames());
            if (frames.length >= 4)
                cuadroActual = frames[3][0];
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        seleccionActiva = !enTransicion && paginaDialogoActual == 1 && (textoDialogoActual == TEXTO_ROWLET
                || textoDialogoActual == TEXTO_CYNDAQUIL || textoDialogoActual == TEXTO_OSHAWOTT);

        if (apareciendo) {
            alfaFade -= delta * 1.5f;
            if (alfaFade <= 0) {
                alfaFade = 0;
                apareciendo = false;
            }
        }
        if (desapareciendo) {
            alfaFade += delta * 1.5f;
            if (alfaFade >= 1) {
                alfaFade = 1;
                juego.setScreen(pantallaJuegoRef);
            }
        }

        if (mostrarDialogo && !desapareciendo) {
            if (seleccionActiva) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)
                        || Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W))
                    indiceSeleccion = 0;
                else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)
                        || Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S))
                    indiceSeleccion = 1;
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    if (indiceSeleccion == 0) {
                        enTransicion = true;
                        String nombreSel = "";
                        if (textoDialogoActual == TEXTO_ROWLET) {
                            textoDialogoActual = EXITO_ROWLET;
                            nombreSel = "Rowlet";
                        } else if (textoDialogoActual == TEXTO_CYNDAQUIL) {
                            textoDialogoActual = EXITO_CYNDAQUIL;
                            nombreSel = "Cyndaquil";
                        } else if (textoDialogoActual == TEXTO_OSHAWOTT) {
                            textoDialogoActual = EXITO_OSHAWOTT;
                            nombreSel = "Oshawott";
                        }
                        if (!nombreSel.isEmpty()) {
                            Explorador exp = pantallaJuegoRef.obtenerExplorador();
                            Pokemon pk = new Pokemon(nombreSel, 0, 0, false, "");
                            exp.agregarAlEquipo(pk);
                            exp.obtenerRegistro().getRegistro().putIfAbsent(nombreSel, new EspeciePokemon(nombreSel));
                            EspeciePokemon esp = exp.obtenerRegistro().getRegistro().get(nombreSel);
                            if (!esp.estaCapturado()) {
                                exp.obtenerRegistro().getCapturedOrder().add(nombreSel);
                                if (!exp.obtenerRegistro().getEncounterOrder().contains(nombreSel))
                                    exp.obtenerRegistro().getEncounterOrder().add(nombreSel);
                                esp.establecerCapturado(true);
                            }
                        }
                        paginaDialogoActual = 0;
                    } else {
                        mostrarDialogo = false;
                        paginaDialogoActual = 0;
                    }
                }
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                paginaDialogoActual++;
                if (paginaDialogoActual >= textoDialogoActual.length) {
                    mostrarDialogo = false;
                    paginaDialogoActual = 0;
                    if (enTransicion)
                        desapareciendo = true;
                }
            }
        } else if (!desapareciendo) {
            enTransicion = false;
            if (Gdx.input.justTouched()) {
                com.badlogic.gdx.math.Vector3 m = new com.badlogic.gdx.math.Vector3(Gdx.input.getX(), Gdx.input.getY(),
                        0);
                viewport.unproject(m);
                if (m.x >= feidX && m.x <= feidX + anchoFeid && m.y >= feidY && m.y <= feidY + altoFeid) {
                    textoDialogoActual = TEXTO_INTRO;
                    mostrarDialogo = true;
                    paginaDialogoActual = 0;
                    texturaIconoDialogo = retratoFerxxoCientifico;
                } else if (m.x >= rowletX && m.x <= rowletX + anchoInicial && m.y >= rowletY
                        && m.y <= rowletY + altoInicial) {
                    textoDialogoActual = TEXTO_ROWLET;
                    mostrarDialogo = true;
                    paginaDialogoActual = 0;
                    indiceSeleccion = 0;
                    texturaIconoDialogo = retratoFerxxoRowlet;
                } else if (m.x >= cyndaquilX && m.x <= cyndaquilX + anchoInicial + 10 && m.y >= cyndaquilY
                        && m.y <= cyndaquilY + altoInicial + 10) {
                    textoDialogoActual = TEXTO_CYNDAQUIL;
                    mostrarDialogo = true;
                    paginaDialogoActual = 0;
                    indiceSeleccion = 0;
                    texturaIconoDialogo = retratoFerxxoCyndaquil;
                } else if (m.x >= oshawottX && m.x <= oshawottX + anchoInicial + 10 && m.y >= oshawottY
                        && m.y <= oshawottY + altoInicial + 10) {
                    textoDialogoActual = TEXTO_OSHAWOTT;
                    mostrarDialogo = true;
                    paginaDialogoActual = 0;
                    indiceSeleccion = 0;
                    texturaIconoDialogo = retratoFerxxoOshawott;
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
                desapareciendo = true;
        }

        viewport.apply();
        juego.batch.setProjectionMatrix(camara.combined);
        juego.batch.begin();
        juego.batch.setColor(Color.WHITE);
        if (texturaFondo != null)
            juego.batch.draw(texturaFondo, 0, 0, 800, 480);
        if (spriteFeid != null)
            juego.batch.draw(spriteFeid, feidX, feidY, anchoFeid, altoFeid);
        if (texturaRowlet != null)
            juego.batch.draw(texturaRowlet, rowletX, rowletY, anchoInicial, altoInicial);
        if (texturaCyndaquil != null)
            juego.batch.draw(texturaCyndaquil, cyndaquilX, cyndaquilY, anchoInicial + 10, altoInicial + 10);
        if (texturaOshawott != null)
            juego.batch.draw(texturaOshawott, oshawottX, oshawottY, anchoInicial + 10, altoInicial + 10);
        if (cuadroActual != null)
            juego.batch.draw(cuadroActual, posX, posY, anchoJugador, altoJugador * 1.2f);

        if (mostrarDialogo)
            dibujarDialogo();

        if (alfaFade > 0 && pixelBlancoUi != null) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            juego.batch.setColor(0, 0, 0, alfaFade);
            juego.batch.draw(pixelBlancoUi, 0, 0, 800, 480);
            juego.batch.setColor(Color.WHITE);
        }
        juego.batch.end();
    }

    private void dibujarDialogo() {
        float sw = 800, dh = 110, ps = 250;
        boolean exito = (textoDialogoActual == EXITO_ROWLET || textoDialogoActual == EXITO_CYNDAQUIL
                || textoDialogoActual == EXITO_OSHAWOTT);
        if (!exito && texturaIconoDialogo != null)
            juego.batch.draw(texturaIconoDialogo, sw - ps - 20, dh - 20, ps, ps);

        juego.batch.setColor(Color.DARK_GRAY);
        if (pixelBlancoUi != null)
            juego.batch.draw(pixelBlancoUi, 20, 20, sw - 40, dh);
        juego.batch.setColor(Color.WHITE);
        if (pixelBlancoUi != null)
            juego.batch.draw(pixelBlancoUi, 23, 23, sw - 46, dh - 6);

        if (!exito) {
            float ntw = 200, nth = 35, nty = dh + 10;
            juego.batch.setColor(Color.DARK_GRAY);
            if (pixelBlancoUi != null)
                juego.batch.draw(pixelBlancoUi, 45, nty, ntw, nth);
            juego.batch.setColor(Color.WHITE);
            if (pixelBlancoUi != null)
                juego.batch.draw(pixelBlancoUi, 47, nty + 2, ntw - 4, nth - 4);
            juego.fuente.setColor(Color.BLACK);
            juego.fuente.getData().setScale(0.9f);
            juego.fuente.draw(juego.batch, "Profesor Ferxxo", 55, nty + 25);
        }

        juego.fuente.setColor(Color.BLACK);
        juego.fuente.getData().setScale(0.85f);
        juego.fuente.draw(juego.batch, textoDialogoActual[paginaDialogoActual], 45, dh - 10, sw - 90,
                com.badlogic.gdx.utils.Align.left, true);

        if (seleccionActiva) {
            float ox = 600, oy = 100;
            juego.fuente.setColor(indiceSeleccion == 0 ? Color.RED : Color.BLACK);
            juego.fuente.draw(juego.batch, "> SÍ", ox, oy);
            juego.fuente.setColor(indiceSeleccion == 1 ? Color.RED : Color.BLACK);
            juego.fuente.draw(juego.batch, "> NO", ox, oy - 30);
        }
        juego.fuente.getData().setScale(1.0f);
        juego.fuente.setColor(Color.WHITE);
        if (!seleccionActiva) {
            juego.fuente.getData().setScale(0.6f);
            String pista = (paginaDialogoActual < textoDialogoActual.length - 1) ? "SIGUIENTE (ENTER)"
                    : "CERRAR (ENTER)";
            juego.fuente.setColor(Color.DARK_GRAY);
            juego.fuente.draw(juego.batch, pista, 700, 40, 0, com.badlogic.gdx.utils.Align.right, false);
            juego.fuente.setColor(Color.WHITE);
            juego.fuente.getData().setScale(1.0f);
        }
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
