package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Clase base abstracta para pantallas estáticas (pantallas que solo muestran
 * una imagen).
 * Proporciona renderizado automático de fondo, cámara ortográfica con viewport
 * fijo
 * y manejo de tecla ESC para regresar.
 */
public abstract class StaticDisplayScreen extends BaseScreen implements INavigable {

    protected Texture background;
    protected final Screen returnScreen;

    // Cámara y Viewport para relación de aspecto fija
    protected OrthographicCamera camera;
    protected Viewport viewport;
    protected static final float VIRTUAL_WIDTH = 1280f;
    protected static final float VIRTUAL_HEIGHT = 720f;

    public StaticDisplayScreen(PokemonMain game, Screen returnScreen, String backgroundPath) {
        super(game);
        this.returnScreen = returnScreen;
        this.background = loadTexture(backgroundPath);

        // Configurar cámara y viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        // Manejar ESC para volver atrás
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            navigateBack();
            return;
        }

        ScreenUtils.clear(0f, 0f, 0f, 1f);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        renderBackground();
        renderContent(delta);
        game.batch.end();
    }

    /**
     * Renderiza la imagen de fondo para llenar la pantalla virtual.
     */
    protected void renderBackground() {
        if (background != null) {
            game.batch.draw(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }
    }

    /**
     * Sobrescribe esto para renderizar contenido adicional sobre el fondo.
     * 
     * @param delta Tiempo desde el último frame
     */
    protected void renderContent(float delta) {
        // Sobrescribir en subclases si es necesario
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
    }

    @Override
    public void navigateBack() {
        if (returnScreen != null) {
            game.setScreen(returnScreen);
            dispose();
        }
    }

    @Override
    public void navigateTo(Screen screen) {
        game.setScreen(screen);
        dispose();
    }

    @Override
    public Screen getReturnScreen() {
        return returnScreen;
    }
}
