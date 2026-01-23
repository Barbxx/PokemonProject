package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Pantalla del menú principal del juego.
 * Permite iniciar nueva partida, cargar progreso, ver ayuda y configurar
 * opciones.
 */
public class MainMenuScreen extends BaseScreen {

    Texture background;

    // Arrays to hold textures for each option
    Texture[] normalTextures;
    Texture[] selectedTextures;

    // Menu Options
    String[] options = { "PLAY", "CARGAR", "HELP", "ABOUT", "EXIT" };
    String[] filePrefixes = { "boton_jugar", "boton_cargar", "boton_ayuda", "boton_acercade", "boton_salir" };

    int currentOption = -1;
    float fadeAlpha = 0f;
    boolean isStarting = false;

    // Layout constants
    float menuBoxWidth = 370;
    float menuBoxHeight = 260;

    // Camera and Viewport for fixed aspect ratio
    private OrthographicCamera camera;
    private Viewport viewport;
    private static final float VIRTUAL_WIDTH = 1280f;
    private static final float VIRTUAL_HEIGHT = 720f;

    /**
     * Constructor del menú principal.
     * Carga las texturas de los botones y configura la cámara.
     *
     * @param game Referencia a la clase principal del juego.
     */
    public MainMenuScreen(final PokemonMain game) {
        super(game);

        try {
            background = new Texture("menu_bg.jpg");
        } catch (Exception e) {
            Gdx.app.log("MainMenu", "Could not load menu_bg.jpg: " + e.getMessage());
        }

        // Load button textures
        normalTextures = new Texture[options.length];
        selectedTextures = new Texture[options.length];

        for (int i = 0; i < options.length; i++) {
            try {

                normalTextures[i] = new Texture(filePrefixes[i] + "_normal.png");
                selectedTextures[i] = new Texture(filePrefixes[i] + "_seleccionado.png");
            } catch (Exception e) {
                Gdx.app.log("MainMenu", "Could not load images for option " + i + ": " + e.getMessage());
            }
        }

        // Setup camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();
    }

    /**
     * Se llama cuando la pantalla se muestra.
     * Desactiva el procesador de entrada global.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    /**
     * Renderiza el menú principal y maneja la navegación por teclado.
     *
     * @param delta Tiempo transcurrido.
     */
    @Override
    public void render(float delta) {
        // Update Logic
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // Calculate layout variables for rendering
        float screenWidth = VIRTUAL_WIDTH;
        float screenHeight = VIRTUAL_HEIGHT;

        float buttonWidth = 300;
        float buttonHeight = 80;
        float spacing = -15; // Negative spacing to bring them closer
        float totalMenuHeight = (options.length * buttonHeight) + ((options.length - 1) * spacing);
        float startY = (screenHeight + totalMenuHeight) / 2 - 100;

        // Keyboard Selection Logic
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (currentOption == -1) {
                currentOption = options.length - 1;
            } else {
                currentOption--;
                if (currentOption < 0)
                    currentOption = options.length - 1;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (currentOption == -1) {
                currentOption = 0;
            } else {
                currentOption++;
                if (currentOption >= options.length)
                    currentOption = 0;
            }
        }

        // Action Logic
        boolean enter = Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE);

        if (currentOption != -1 && enter) {
            if (currentOption == 0) {
                game.setScreen(new EleccionJuegoScreen(game));
                dispose();
                return;
            } else if (currentOption == 1) {
                game.setScreen(new PartidasScreen(game));
                dispose();
                return;
            } else if (currentOption == 2) {
                game.setScreen(new AyudaScreen(game));
                dispose();
                return;
            } else if (currentOption == 3) {
                game.setScreen(new AcercaDeScreen(game));
                dispose();
                return;
            } else if (currentOption == 4) {
                Gdx.app.exit();
            }
        }

        // Draw Logic
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        if (background != null) {
            game.batch.draw(background, 0, 0, screenWidth, screenHeight);
        }

        for (int i = 0; i < options.length; i++) {
            float buttonY = startY - (i * (buttonHeight + spacing)) - buttonHeight;
            float buttonX = (screenWidth - buttonWidth) / 2;

            Texture textureToDraw = null;
            boolean showSelected = false;


            if (i == currentOption) {
                showSelected = true;
            }

            if (showSelected) {
                if (selectedTextures[i] != null)
                    textureToDraw = selectedTextures[i];
            } else {
                if (normalTextures[i] != null)
                    textureToDraw = normalTextures[i];
            }

            if (textureToDraw != null) {
                game.batch.draw(textureToDraw, buttonX, buttonY, buttonWidth, buttonHeight);
            } else {
                // FALLBACK
                GlyphLayout layout = new GlyphLayout(game.font, options[i]);
                float textX = (screenWidth - layout.width) / 2;
                float textY = buttonY + (buttonHeight + layout.height) / 2;

                if (i == currentOption)
                    game.font.setColor(Color.YELLOW);
                else
                    game.font.setColor(Color.WHITE);

                game.font.draw(game.batch, layout, textX, textY);
            }
        }

        game.batch.end();
    }

    /**
     * Se llama cuando cambia el tamaño de la ventana.
     * Mantiene la relación de aspecto usando un FitViewport.
     *
     * @param width  Nuevo ancho.
     * @param height Nuevo alto.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
    }

    /**
     * Libera las texturas cargadas al cerrar el menú.
     */
    @Override
    public void dispose() {
        if (background != null)
            background.dispose();
        if (normalTextures != null) {
            for (Texture t : normalTextures)
                if (t != null)
                    t.dispose();
        }
        if (selectedTextures != null) {
            for (Texture t : selectedTextures)
                if (t != null)
                    t.dispose();
        }
    }
}
