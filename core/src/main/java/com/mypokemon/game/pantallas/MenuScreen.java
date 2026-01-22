package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.utils.IInputHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;

// Clase base abstracta para pantallas de menú con navegación por teclado y mouse.
public abstract class MenuScreen extends BaseScreen implements IInputHandler {

    protected int opcionActual = -1;
    protected String[] opciones;
    protected Texture[] texturasNormales;
    protected Texture[] texturasSeleccionadas;

    public MenuScreen(PokemonMain juego) {
        super(juego);
    }

    // Inicializa las opciones del menú y carga sus texturas.
    protected void inicializarOpcionesMenu(String[] opciones, String[] prefijosArchivos) {
        this.opciones = opciones;
        this.texturasNormales = new Texture[opciones.length];
        this.texturasSeleccionadas = new Texture[opciones.length];

        for (int i = 0; i < opciones.length; i++) {
            texturasNormales[i] = cargarTextura(prefijosArchivos[i] + "_normal.png");
            texturasSeleccionadas[i] = cargarTextura(prefijosArchivos[i] + "_seleccionado.png");
        }
    }

    // Gestiona la navegación por teclado a través de las opciones del menú.
    protected void gestionarNavegacionMenu() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            opcionActual = (opcionActual == -1) ? opciones.length - 1
                    : (opcionActual == 0 ? opciones.length - 1 : opcionActual - 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            opcionActual = (opcionActual == -1) ? 0 : (opcionActual >= opciones.length - 1 ? 0 : opcionActual + 1);
        }
    }

    // Verifica qué opción del menú está bajo el puntero del mouse.
    protected int obtenerOpcionBajoMouse(float botonX, float[] botonY, float anchoBoton, float altoBoton) {
        float mouseX = Gdx.input.getX();
        float mouseY = (float) Gdx.graphics.getHeight() - Gdx.input.getY();

        for (int i = 0; i < opciones.length; i++) {
            if (mouseX >= botonX && mouseX <= botonX + anchoBoton && mouseY >= botonY[i]
                    && mouseY <= botonY[i] + altoBoton) {
                return i;
            }
        }
        return -1;
    }

    // Se llama cuando se selecciona una opción del menú.
    protected abstract void gestionarOpcionSeleccionada(int indiceOpcion);

    @Override
    public boolean handleKeyPress(int keycode) {
        if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
            if (opcionActual != -1) {
                gestionarOpcionSeleccionada(opcionActual);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleMouseClick(float x, float y) {
        return false;
    }

    @Override
    public void setupInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                return handleKeyPress(keycode);
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return handleMouseClick(screenX, (float) Gdx.graphics.getHeight() - screenY);
            }
        });
    }

    @Override
    public void show() {
        setupInputProcessor();
    }
}
