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
 * Pantalla principal del menú del juego.
 * Muestra las opciones principales (Jugar, Cargar, Ayuda, Acerca de, Salir).
 */
public class MainMenuScreen extends BaseScreen {

    Texture background;

    // Arrays to hold textures for each option [0]=Play, [1]=Profile, etc.
    Texture[] normalTextures;
    Texture[] selectedTextures;

    // Menu Options (Keep strings for fallback or logic, though rendering uses
    // images now)
    String[] options = { "PLAY", "CARGAR", "HELP", "ABOUT", "EXIT" };
    // Changed "boton_partida" to "boton_cargar"
    String[] filePrefixes = { "boton_jugar", "boton_cargar", "boton_ayuda", "boton_acercade", "boton_salir" };

    int currentOption = -1;
    float fadeAlpha = 0f;
    boolean isStarting = false;
    String currentSubScreen = null; // null, "PARTIDA", "HELP", "ABOUT" (PARTIDA is now likely PartidasScreen)

    // Layout constants
    float menuBoxWidth = 370;
    float menuBoxHeight = 260;

    // Camera and Viewport for fixed aspect ratio
    private OrthographicCamera camera;
    private Viewport viewport;
    private static final float VIRTUAL_WIDTH = 1280f;
    private static final float VIRTUAL_HEIGHT = 720f;

    /**
     * Constructor de la pantalla del menú principal.
     * 
     * @param game Instancia principal del juego.
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
                // For CARGAR, we expect boton_cargar_normal.png and
                // boton_cargar_seleccionado.png
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
     * Configura la pantalla al mostrarse. Libera el procesador de entrada manual
     * ya que esta pantalla usa polling en render.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    /**
     * Renderiza el menú principal y gestiona la navegación por teclado.
     * 
     * @param delta Tiempo transcurrido desde el último frame.
     */
    @Override
    public void render(float delta) {
        // --- 1. Update Logic ---
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
                currentOption = options.length - 1; // Start at bottom
            } else {
                currentOption--;
                if (currentOption < 0)
                    currentOption = options.length - 1;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (currentOption == -1) {
                currentOption = 0; // Start at top
            } else {
                currentOption++;
                if (currentOption >= options.length)
                    currentOption = 0;
            }
        }

        // Action Logic (Only Enter key)
        boolean enter = Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE);

        if (currentOption != -1 && enter) {
            if (currentOption == 0) { // PLAY -> Go to EleccionJuegoScreen
                game.setScreen(new EleccionJuegoScreen(game));
                dispose();
                return;
            } else if (currentOption == 1) { // CARGAR -> Go to PartidasScreen
                game.setScreen(new PartidasScreen(game));
                dispose();
                return;
            } else if (currentOption == 2) {
                currentSubScreen = "HELP";
            } else if (currentOption == 3) { // ABOUT -> Go to AcercaDeScreen
                game.setScreen(new AcercaDeScreen(game));
                dispose();
                return;
            } else if (currentOption == 4) { // EXIT
                Gdx.app.exit();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            if (currentSubScreen != null) {
                currentSubScreen = null;
            }
        }

        // --- 2. Draw Logic ---

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

            // Show selected if it is the current keyboard option
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

        if (currentSubScreen != null) {
            game.batch.setColor(0, 0, 0, 0.8f);
            game.batch.draw(selectedTextures[0], 50, 50, screenWidth - 100, screenHeight - 100); // Overlay
            game.batch.setColor(Color.WHITE);
            game.font.getData().setScale(1.5f);
            game.font.draw(game.batch, "SCREEN: " + currentSubScreen, 0, screenHeight / 2 + 20, screenWidth,
                    com.badlogic.gdx.utils.Align.center, false);
            game.font.getData().setScale(1.0f);
            game.font.draw(game.batch, "PRESS ESC TO GO BACK", 0, screenHeight / 2 - 40, screenWidth,
                    com.badlogic.gdx.utils.Align.center, false);
        }

        game.batch.end();
    }

    /**
     * Actualiza el viewport al redimensionar la ventana.
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
     * Libera los recursos gráficos (texturas y fondo).
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
