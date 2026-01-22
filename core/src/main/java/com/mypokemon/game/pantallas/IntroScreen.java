package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

// Pantalla de introducción que gestiona la selección de género y nombre del jugador.
public class IntroScreen extends BaseScreen {

    private enum Estado {
        INTRO_1, INTRO_2, INTRO_3, ELEGIR_GENERO, PREGUNTAR_NOMBRE, INGRESAR_NOMBRE, CONFIRMAR_NOMBRE, PRE_CIERRE,
        FADE_OUT
    }

    private Estado estadoActual;
    private ShapeRenderer renderizadorFormas;
    private float alfaFade = 0f;

    private OrthographicCamera camara;
    private Viewport viewport;

    private String nombrePartida;
    private String nombreJugador = "";
    private boolean esHombre = true;
    private boolean nombreOcupado = false;
    private float tiempoEstado = 0;

    private Texture imagenFeid, imagenFerxxo, imagenJigglypuff, protaFem, protaMasc;
    private Texture texturaMarco, texturaPokebola, texturaPoof;
    private Animation<TextureRegion> animPoof, animJigglyPoses;

    private static final float TEXT_BOX_HEIGHT = 140f;
    private static final String TEXTO_1 = "¡Epaaa, qué más pues, mor! Bienvenido a la región 'One Ferxxo', el lugar más chimba de todos.";
    private static final String TEXTO_2 = "Yo soy el Profesor Ferxxo, el que pone a todas estas chimbitas a vacilar. Este mundo está lleno de Pokémon; unos son para parchar y otros para dar lora peleando. Yo me encargo de estudiarlos para que todo esté bien chimba pues.";
    private static final String TEXTO_3 = "Pero antes de empezar el vacile... mor, dime, ¿eres un parcero o una parcera? ¡Hágale pues!";
    private static final String TEXT_PREG_NOMBRE = "Y dígame mor... ¿cuál es tu nombre, nea?";
    private static final String TEXT_NOMBRE_OCUPADO = "Mor, me parece que ya hay alguien aquí con ese nombre.";
    private static final String TEXT_CIERRE_FMT = "¡Ah, listo! Un gusto conocerte, %s. ¡Vea pues, que te espera un mundo de aventuras bien chimbas! ¡Vacílatela, nea!";

    private Rectangle limitesMenuGenero;
    private float tiempoJiggly = 0;
    private int posicionCursor = 0;
    private boolean nombreConfirmado = true;

    // Constructor de la pantalla de introducción.
    public IntroScreen(final PokemonMain juego, String nombrePartida) {
        super(juego);
        this.nombrePartida = nombrePartida;
        this.renderizadorFormas = new ShapeRenderer();
        this.estadoActual = Estado.INTRO_1;
        this.camara = new OrthographicCamera();
        this.viewport = new StretchViewport(800, 600, camara);
        this.viewport.apply();

        try {
            imagenFeid = cargarTextura("feid.png");
            imagenFerxxo = cargarTextura("ferxxo.png");
            imagenJigglypuff = cargarTextura("Jigglypuff.png");
            imagenJigglypuff.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            TextureRegion[][] tmp = TextureRegion.split(imagenJigglypuff, imagenJigglypuff.getWidth() / 4,
                    imagenJigglypuff.getHeight() / 4);
            TextureRegion[] framesPoses = new TextureRegion[16];
            int index = 0;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    TextureRegion celda = tmp[i][j];
                    framesPoses[index++] = new TextureRegion(celda, 2, 2, celda.getRegionWidth() - 4,
                            celda.getRegionHeight() - 4);
                }
            animJigglyPoses = new Animation<>(0.8f, framesPoses);
            protaFem = cargarTextura("ProtaFem.png");
            protaMasc = cargarTextura("ProtaMasc.png");
            texturaMarco = cargarTextura("marcoPokemon.png");
            texturaPokebola = cargarTextura("pokeball.png");
            texturaPoof = cargarTextura("poof.png");
            TextureRegion[][] poofTmp = TextureRegion.split(texturaPoof, texturaPoof.getWidth() / 2,
                    texturaPoof.getHeight() / 2);
            TextureRegion[] poofFrames = new TextureRegion[4];
            int pIdx = 0;
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 2; j++)
                    poofFrames[pIdx++] = poofTmp[i][j];
            animPoof = new Animation<>(0.1f, poofFrames);
        } catch (Exception e) {
            Gdx.app.log("IntroScreen", "Error cargando recursos");
        }

        limitesMenuGenero = new Rectangle(800 * 0.12f + 800 * 0.76f - 210, 600 * 0.12f + (600 * 0.76f) / 2 - 60, 200,
                120);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char caracter) {
                if (estadoActual == Estado.INGRESAR_NOMBRE) {
                    if (Character.isLetterOrDigit(caracter) || caracter == ' ') {
                        if (nombreJugador.length() < 12) {
                            nombreJugador = nombreJugador.substring(0, posicionCursor) + caracter
                                    + nombreJugador.substring(posicionCursor);
                            posicionCursor++;
                        }
                    } else if (caracter == '\r' || caracter == '\n')
                        avanzarEstado();
                    else if (caracter == '\b' && nombreJugador.length() > 0 && posicionCursor > 0) {
                        nombreJugador = nombreJugador.substring(0, posicionCursor - 1)
                                + nombreJugador.substring(posicionCursor);
                        posicionCursor--;
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (estadoActual == Estado.INGRESAR_NOMBRE) {
                    if (keycode == Input.Keys.ENTER) {
                        avanzarEstado();
                        return true;
                    }
                    if (keycode == Input.Keys.LEFT) {
                        if (posicionCursor > 0)
                            posicionCursor--;
                        return true;
                    }
                    if (keycode == Input.Keys.RIGHT) {
                        if (posicionCursor < nombreJugador.length())
                            posicionCursor++;
                        else
                            avanzarEstado();
                        return true;
                    }
                }
                if (estadoActual == Estado.ELEGIR_GENERO && (keycode == Input.Keys.UP || keycode == Input.Keys.DOWN)) {
                    esHombre = !esHombre;
                    return true;
                }
                if (estadoActual == Estado.CONFIRMAR_NOMBRE
                        && (keycode == Input.Keys.UP || keycode == Input.Keys.DOWN)) {
                    nombreConfirmado = !nombreConfirmado;
                    return true;
                }
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE || keycode == Input.Keys.RIGHT) {
                    avanzarEstado();
                    return true;
                }
                if (keycode == Input.Keys.LEFT && estadoActual == Estado.ELEGIR_GENERO) {
                    retrocederEstado();
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int x, int y, int p, int b) {
                if (estadoActual == Estado.INTRO_1 || estadoActual == Estado.INTRO_2 || estadoActual == Estado.INTRO_3)
                    avanzarEstado();
                return true;
            }
        });
    }

    private void avanzarEstado() {
        switch (estadoActual) {
            case INTRO_1:
                estadoActual = Estado.INTRO_2;
                tiempoJiggly = 0;
                break;
            case INTRO_2:
                estadoActual = Estado.INTRO_3;
                break;
            case INTRO_3:
                estadoActual = Estado.ELEGIR_GENERO;
                break;
            case ELEGIR_GENERO:
                estadoActual = Estado.PREGUNTAR_NOMBRE;
                posicionCursor = 0;
                break;
            case PREGUNTAR_NOMBRE:
                estadoActual = Estado.INGRESAR_NOMBRE;
                nombreOcupado = false;
                break;
            case INGRESAR_NOMBRE:
                if (nombreJugador.trim().isEmpty())
                    nombreJugador = "Directioner";
                estadoActual = Estado.CONFIRMAR_NOMBRE;
                nombreConfirmado = true;
                break;
            case CONFIRMAR_NOMBRE:
                if (nombreConfirmado) {
                    if ("SharedGame".equals(nombrePartida)) {
                        if (juego.clienteRed != null) {
                            juego.clienteRed.establecerEscucha(msg -> {
                                if (msg.equals("NAME_OK"))
                                    Gdx.app.postRunnable(() -> estadoActual = Estado.PRE_CIERRE);
                                else if (msg.equals("NAME_TAKEN"))
                                    Gdx.app.postRunnable(() -> {
                                        nombreOcupado = true;
                                        estadoActual = Estado.PREGUNTAR_NOMBRE;
                                        nombreJugador = "";
                                        posicionCursor = 0;
                                    });
                            });
                            juego.clienteRed.enviarMensaje("CHECK_NAME:" + nombreJugador);
                        } else
                            estadoActual = Estado.PRE_CIERRE;
                    } else
                        estadoActual = Estado.PRE_CIERRE;
                } else {
                    estadoActual = Estado.INGRESAR_NOMBRE;
                    nombreJugador = "";
                    posicionCursor = 0;
                }
                break;
            case PRE_CIERRE:
                estadoActual = Estado.FADE_OUT;
                break;
            default:
                break;
        }
    }

    private void retrocederEstado() {
        switch (estadoActual) {
            case INTRO_2:
                estadoActual = Estado.INTRO_1;
                break;
            case INTRO_3:
                estadoActual = Estado.INTRO_2;
                break;
            case ELEGIR_GENERO:
                estadoActual = Estado.INTRO_3;
                break;
            case PREGUNTAR_NOMBRE:
                estadoActual = Estado.ELEGIR_GENERO;
                break;
            case INGRESAR_NOMBRE:
                estadoActual = Estado.PREGUNTAR_NOMBRE;
                break;
            case CONFIRMAR_NOMBRE:
                estadoActual = Estado.INGRESAR_NOMBRE;
                break;
            default:
                break;
        }
    }

    @Override
    public void render(float delta) {
        tiempoEstado += delta;
        tiempoJiggly += delta;
        ScreenUtils.clear(1f, 1f, 1f, 1);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        camara.update();
        juego.batch.setProjectionMatrix(camara.combined);

        float sW = viewport.getWorldWidth(), sH = viewport.getWorldHeight();
        float vpX = sW * 0.12f, vpY = sH * 0.12f, vpW = sW * 0.76f, vpH = sH * 0.76f;

        juego.batch.begin();
        if (texturaMarco != null)
            juego.batch.draw(texturaMarco, 0, 0, sW, sH);

        if (estadoActual != Estado.PRE_CIERRE && estadoActual != Estado.FADE_OUT) {
            Texture img = (estadoActual == Estado.INTRO_1 || estadoActual == Estado.INTRO_2
                    || estadoActual == Estado.INTRO_3) ? imagenFerxxo : (esHombre ? protaMasc : protaFem);
            if (img != null) {
                float dispH = vpH - TEXT_BOX_HEIGHT - 20,
                        e = (img.getHeight() > dispH) ? dispH / img.getHeight() : (img == imagenFerxxo ? 1.8f : 1.5f);
                float iW = img.getWidth() * e, iH = img.getHeight() * e, iX = vpX + (vpW - iW) / 2,
                        iY = vpY + TEXT_BOX_HEIGHT + 10;
                juego.batch.draw(img, iX, iY, iW, iH);

                if ((estadoActual == Estado.INTRO_2 || estadoActual == Estado.INTRO_3) && animJigglyPoses != null) {
                    TextureRegion reg = null;
                    float dW = 60;
                    if (tiempoJiggly < 0.8f) {
                        reg = new TextureRegion(texturaPokebola);
                        dW = 45;
                    } else if (tiempoJiggly < 1.2f) {
                        reg = animPoof.getKeyFrame(tiempoJiggly - 0.8f, false);
                        dW = 50;
                    } else
                        reg = animJigglyPoses.getKeyFrame(tiempoJiggly - 1.2f, true);

                    if (reg != null) {
                        float jH = dW * (float) reg.getRegionHeight() / reg.getRegionWidth();
                        float jX = iX + iW * 0.02f + (tiempoJiggly < 1.2f ? (60 - dW) / 2 : 0);
                        float jY = iY + iH * 0.62f - (tiempoJiggly < 0.8f ? 8 : 0);
                        juego.batch.draw(reg, jX, jY, dW, jH);
                    }
                }
            }
        } else {
            float dispH = vpH - TEXT_BOX_HEIGHT - 20;
            Texture tP = esHombre ? protaMasc : protaFem;
            if (tP != null) {
                float e = Math.min(1.2f, dispH / tP.getHeight());
                juego.batch.draw(tP, vpX + vpW / 4f - (tP.getWidth() * e) / 2f, vpY + TEXT_BOX_HEIGHT + 10,
                        tP.getWidth() * e, tP.getHeight() * e);
            }
            if (imagenFeid != null) {
                float e = Math.min(1.2f, dispH / imagenFeid.getHeight());
                juego.batch.draw(imagenFeid, vpX + vpW * 3f / 4f - (imagenFeid.getWidth() * e) / 2f,
                        vpY + TEXT_BOX_HEIGHT + 10, imagenFeid.getWidth() * e, imagenFeid.getHeight() * e);
            }
        }
        juego.batch.end();

        renderizadorFormas.setProjectionMatrix(camara.combined);
        renderizadorFormas.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        float bX = vpX + 10, bY = vpY + 10, bW = vpW - 20, bH = TEXT_BOX_HEIGHT;
        renderizadorFormas.setColor(0.3f, 0.5f, 0.6f, 1f);
        renderizadorFormas.rect(bX, bY, bW, bH);
        renderizadorFormas.setColor(0.6f, 0.8f, 1.0f, 1f);
        renderizadorFormas.rect(bX + 6, bY + 6, bW - 12, bH - 12);
        if (estadoActual == Estado.INGRESAR_NOMBRE) {
            renderizadorFormas.setColor(Color.DARK_GRAY);
            renderizadorFormas.rect(bX + 100, bY + 45, 400, 3);
        }
        if (tiempoEstado % 1.0f > 0.5f && estadoActual != Estado.INGRESAR_NOMBRE) {
            renderizadorFormas.setColor(0.8f, 0.1f, 0.1f, 1f);
            renderizadorFormas.triangle(bX + bW - 40, bY + 30, bX + bW - 25, bY + 30, bX + bW - 32.5f, bY + 15);
        }
        renderizadorFormas.end();

        juego.batch.begin();
        juego.fuente.setColor(Color.BLACK);
        juego.fuente.getData().setScale(1.2f);
        juego.fuente.draw(juego.batch, "Profesor Ferxxo", bX, bY + bH + 20, 220, Align.center, false);
        juego.fuente.getData().setScale(1.0f);
        String txt = "";
        switch (estadoActual) {
            case INTRO_1:
                txt = TEXTO_1;
                break;
            case INTRO_2:
                txt = TEXTO_2;
                break;
            case INTRO_3:
            case ELEGIR_GENERO:
                txt = TEXTO_3;
                break;
            case PREGUNTAR_NOMBRE:
                txt = nombreOcupado ? TEXT_NOMBRE_OCUPADO : TEXT_PREG_NOMBRE;
                break;
            case INGRESAR_NOMBRE:
                txt = nombreOcupado ? TEXT_NOMBRE_OCUPADO : TEXT_PREG_NOMBRE;
                juego.fuente.draw(juego.batch, nombreJugador, bX + 110, bY + 65);
                if (tiempoEstado % 1f > 0.5f)
                    juego.fuente.draw(juego.batch, "_", bX + 110 + nombreJugador.length() * 14, bY + 65);
                break;
            case CONFIRMAR_NOMBRE:
                txt = "¡Bien! ¿Así que te llamas " + nombreJugador + "?";
                break;
            case PRE_CIERRE:
            case FADE_OUT:
                txt = String.format(TEXT_CIERRE_FMT, nombreJugador);
                break;
        }
        juego.fuente.draw(juego.batch, txt, bX + 50, bY + bH - 25, bW - 100, Align.left, true);

        if (estadoActual == Estado.ELEGIR_GENERO) {
            juego.fuente.getData().setScale(1.5f);
            float mX = limitesMenuGenero.x + 40, mY = limitesMenuGenero.y + limitesMenuGenero.height - 35;
            juego.fuente.draw(juego.batch, "CHICO", mX, mY);
            juego.fuente.draw(juego.batch, "CHICA", mX, mY - 40);
            juego.fuente.setColor(0.8f, 0.2f, 0.2f, 1f);
            juego.fuente.draw(juego.batch, ">", mX - 25, esHombre ? mY : mY - 40);
        } else if (estadoActual == Estado.CONFIRMAR_NOMBRE) {
            float mX = bX + 680, mY = bY + bH + 20;
            juego.fuente.getData().setScale(1.6f);
            juego.fuente.draw(juego.batch, "SÍ", mX + 40, mY + 75);
            juego.fuente.draw(juego.batch, "NO", mX + 40, mY + 35);
            juego.fuente.setColor(0.8f, 0.2f, 0.2f, 1f);
            juego.fuente.draw(juego.batch, ">", mX + 15, nombreConfirmado ? mY + 75 : mY + 35);
        }
        juego.fuente.getData().setScale(0.9f);
        juego.fuente.setColor(Color.BLACK);
        String ayuda = (estadoActual == Estado.CONFIRMAR_NOMBRE) ? "SELECT (ARROWS)   CONFIRM (ENTER)"
                : (estadoActual == Estado.PRE_CIERRE ? "CONTINUE (ENTER / ->)"
                        : (estadoActual == Estado.ELEGIR_GENERO ? "NEXT (->)   BACK (<-)" : "NEXT (->)"));
        if (estadoActual != Estado.FADE_OUT)
            juego.fuente.draw(juego.batch, ayuda, bX + bW - 250, bY + 30, 200, Align.right, false);
        juego.batch.end();

        if (estadoActual == Estado.FADE_OUT) {
            alfaFade += delta;
            if (alfaFade >= 1f) {
                juego.setScreen(
                        new GameScreen(juego, esHombre ? "protagonistaMasculino1.png" : "protagonistaFemenino.png", 4,
                                4, nombreJugador, nombrePartida));
                dispose();
                return;
            }
            renderizadorFormas.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
            renderizadorFormas.setColor(0, 0, 0, alfaFade);
            renderizadorFormas.rect(0, 0, sW, sH);
            renderizadorFormas.end();
        }
        juego.fuente.setColor(Color.WHITE);
        juego.fuente.getData().setScale(1.0f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        super.dispose();
        renderizadorFormas.dispose();
    }
}
