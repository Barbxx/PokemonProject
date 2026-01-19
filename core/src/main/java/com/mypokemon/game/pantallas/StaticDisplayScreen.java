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
 * Abstract base class for static display screens (screens that just show an
 * image).
 * Provides automatic background rendering, orthographic camera with fixed
 * viewport,
 * and ESC key handling to return.
 */
public abstract class StaticDisplayScreen extends BaseScreen implements INavigable {

    protected Texture background;
    protected final Screen returnScreen;

    // Camera and Viewport for fixed aspect ratio
    protected OrthographicCamera camera;
    protected Viewport viewport;
    protected static final float VIRTUAL_WIDTH = 1280f;
    protected static final float VIRTUAL_HEIGHT = 720f;

    public StaticDisplayScreen(PokemonMain game, Screen returnScreen, String backgroundPath) {
        super(game);
        this.returnScreen = returnScreen;
        this.background = loadTexture(backgroundPath);

        // Setup camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        // Handle ESC to go back
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
     * Renders the background image to fill the virtual screen.
     */
    protected void renderBackground() {
        if (background != null) {
            game.batch.draw(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }
    }

    /**
     * Override this to render additional content on top of the background.
     * 
     * @param delta Time since last frame
     */
    protected void renderContent(float delta) {
        // Override in subclasses if needed
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
