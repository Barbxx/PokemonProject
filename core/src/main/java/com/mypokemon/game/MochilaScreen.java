package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mypokemon.game.utils.BaseScreen;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen for the inventory (Mochila).
 * Pure SpriteBatch logic with robust texture loading and coordinate handling.
 */
public class MochilaScreen extends BaseScreen {
    private Texture background;
    private Texture[] buttonsNormal;
    private Texture[] buttonsSelected;
    private String[] buttonNames = { "botonIrojo", "botonIazul", "botonIamarillo", "botonImarron", "botonImorado" };

    // Item Textures
    private Texture texPokeball;
    private Texture texPlanta;
    private Texture texGuijarro;
    private Texture texBaya;

    // Helper class for grid items
    // Data Model
    public class ItemData {
        public String nombre;
        public String descripcion;
        public Texture textura;

        public ItemData(String nombre, String descripcion, Texture textura) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.textura = textura;
        }
    }

    private float buttonWidth = 140; // Increased size as requested
    private float buttonHeight = 140;
    private float[][] buttonPositions;
    private int selectedIndex = 0; // Category Selection

    // User requested fields
    private Texture textureFondoSlot; // Will generate procedurally if missing
    private com.badlogic.gdx.graphics.g2d.BitmapFont fontContador;
    private int indexSeleccionado = 0; // 0 to 20
    private Texture whitePixel; // Kept for selection box if needed

    private OrthographicCamera camera;
    private Viewport viewport;
    private final GameScreen returnScreen;
    private final float VIRTUAL_WIDTH = 1280;
    private final float VIRTUAL_HEIGHT = 720;

    private Vector3 mousePos = new Vector3();

    public MochilaScreen(PokemonMain game, GameScreen returnScreen) {
        super(game);
        this.returnScreen = returnScreen;
        Gdx.app.log("MochilaScreen", "Constructor call started");

        // Initialize Camera and Viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);

        // Initialize arrays early to avoid NullPointerException in render
        buttonsNormal = new Texture[buttonNames.length];
        buttonsSelected = new Texture[buttonNames.length];

        // Load Background and Buttons
        try {
            background = new Texture(Gdx.files.internal("fondoMochila.png"));
            Gdx.app.log("MochilaScreen", "Background loaded successfully");

            for (int i = 0; i < buttonNames.length; i++) {
                buttonsNormal[i] = new Texture(Gdx.files.internal(buttonNames[i] + ".png"));
                buttonsSelected[i] = new Texture(Gdx.files.internal(buttonNames[i] + "_seleccionado.png"));
            }
            Gdx.app.log("MochilaScreen", "Buttons loaded successfully");

            // Load Item Textures
            // Note: User mentioned these are already loaded/available. Assuming filenames.
            try {
                texPokeball = new Texture(Gdx.files.internal("pokeball.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing pokeball.png");
            }
            try {
                texPlanta = new Texture(Gdx.files.internal("planta.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing planta.png");
            }
            try {
                texGuijarro = new Texture(Gdx.files.internal("guijarro.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing guijarro.png");
            }
            try {
                texBaya = new Texture(Gdx.files.internal("baya.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing baya.png");
            }

        } catch (Exception e) {
            Gdx.app.error("MochilaScreen", "Error loading textures: " + e.getMessage());
        }

        // Define Button Positions (top row, centered)
        // Define Button Positions (Horizontal Centered)
        buttonPositions = new float[buttonNames.length][2];
        float totalWidth = (buttonNames.length * buttonWidth) + ((buttonNames.length - 1) * 20);
        float startX = (VIRTUAL_WIDTH - totalWidth) / 2;
        float startY = VIRTUAL_HEIGHT - 220; // Moved down as requested

        for (int i = 0; i < buttonNames.length; i++) {
            buttonPositions[i][0] = startX + i * (buttonWidth + 20);
            buttonPositions[i][1] = startY;
        }

        // Create white pixel for drawing shapes and slot background
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1,
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);

        // Create dark gray pixel for slot background (simulating slot_vacio.png)
        pixmap.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        pixmap.fill();
        textureFondoSlot = new Texture(pixmap);
        pixmap.dispose();

        // Font setup
        fontContador = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        fontContador.getData().setScale(0.8f);

        Gdx.app.log("MochilaScreen", "Positions and assets initialized");
    }

    @Override
    public void render(float delta) {
        // Handle input to return
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            game.setScreen(returnScreen);
            return;
        }

        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);

        // Track mouse position
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mousePos);

        game.batch.begin();

        if (background != null) {
            game.batch.draw(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }

        // REMOVED: Auto-switching category based on grid selection.
        // We want the buttons to control the view, not the other way around.

        // Draw Buttons
        if (buttonsNormal != null && buttonsSelected != null && buttonPositions != null) {
            for (int i = 0; i < buttonNames.length; i++) {
                if (i >= buttonPositions.length)
                    break;

                float bx = buttonPositions[i][0];
                float by = buttonPositions[i][1];

                boolean isHovered = mousePos.x >= bx && mousePos.x <= bx + buttonWidth &&
                        mousePos.y >= by && mousePos.y <= by + buttonHeight;

                if (isHovered && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    selectedIndex = i; // Mouse override
                }

                // Show as selected if it's the current category OR hovered
                boolean isSelected = (i == selectedIndex || isHovered);

                Texture tex = isSelected && i < buttonsSelected.length && buttonsSelected[i] != null
                        ? buttonsSelected[i]
                        : (i < buttonsNormal.length ? buttonsNormal[i] : null);

                if (tex != null) {
                    game.batch.draw(tex, bx, by, buttonWidth, buttonHeight);
                }
            }
        }

        // Handle Input for Selection (Arrow Keys)
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            indexSeleccionado++;
            if (indexSeleccionado > 20)
                indexSeleccionado = 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            indexSeleccionado--;
            if (indexSeleccionado < 0)
                indexSeleccionado = 20;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            indexSeleccionado -= 7;
            if (indexSeleccionado < 0)
                indexSeleccionado += 21;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            indexSeleccionado += 7;
            if (indexSeleccionado > 20)
                indexSeleccionado -= 21;
        }

        // Draw new UI
        dibujarMochila(game.batch);
        dibujarExplicacion(game.batch);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    private void dibujarMochila(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        if (returnScreen == null || returnScreen.getExplorador() == null)
            return;

        int columnas = 7;
        float size = 64f;
        float margen = 12f;

        for (int i = 0; i < 21; i++) {
            float x = 600 + (i % columnas) * (size + margen); // Grid moved to Right
            float y = 350 - (i / columnas) * (size + margen); // Grid moved further down (was 500)

            // 1. Dibujar fondo del slot
            if (textureFondoSlot != null) {
                batch.draw(textureFondoSlot, x, y, size, size);
            }

            // 2. Dibujar marco de selección
            if (i == indexSeleccionado && whitePixel != null) {
                batch.setColor(Color.YELLOW);
                batch.draw(whitePixel, x - 2, y - 2, size + 4, size + 4);
                batch.setColor(Color.WHITE);
                if (textureFondoSlot != null) {
                    batch.draw(textureFondoSlot, x, y, size, size);
                }
            }

            // 3. Dibujar contenido
            dibujarContenido(batch, i, x, y, size);
        }
    }

    private void dibujarContenido(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, int i, float x, float y,
            float size) {
        Inventario inventario = returnScreen.getExplorador().getMochila();

        if (selectedIndex == 0) { // Red Button: Materiales (Planta, Baya, Guijarro)
            if (i == 0) { // Slot 1: Planta
                if (texPlanta != null) {
                    batch.draw(texPlanta, x + 8, y + 8, size - 16, size - 16);
                    if (fontContador != null)
                        fontContador.draw(batch, "x" + inventario.getPlantas(), x + 40, y + 20);
                }
            } else if (i == 1) { // Slot 2: Baya (Requested order: planta, baya, guijarro)
                if (texBaya != null) {
                    batch.draw(texBaya, x + 8, y + 8, size - 16, size - 16);
                    if (fontContador != null)
                        fontContador.draw(batch, "x" + inventario.getBayas(), x + 40, y + 20);
                }
            } else if (i == 2) { // Slot 3: Guijarro
                if (texGuijarro != null) {
                    batch.draw(texGuijarro, x + 8, y + 8, size - 16, size - 16);
                    if (fontContador != null)
                        fontContador.draw(batch, "x" + inventario.getGuijarros(), x + 40, y + 20);
                }
            }
        } else if (selectedIndex == 1) { // Blue Button: Pokebolas
            if (i == 0) { // Slot 1: Pokeball
                if (texPokeball != null) {
                    batch.draw(texPokeball, x + 8, y + 8, size - 16, size - 16);
                    if (fontContador != null)
                        fontContador.draw(batch, "x" + inventario.getPokeBalls(), x + 40, y + 20);
                }
            }
        }
        // Other buttons currently show empty grid, as requested (implied by omission)
    }

    private void dibujarExplicacion(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        String titulo = "";
        String desc = "";

        if (selectedIndex == 0) { // Red / Materiales
            switch (indexSeleccionado) {
                case 0:
                    titulo = "Planta Medicinal";
                    desc = "Recurso básico. Úsalo para craftear con el Prof. Feid.";
                    break;
                case 1:
                    titulo = "Baya Aranja";
                    desc = "Fruto dulce. Restaura salud.";
                    break;
                case 2:
                    titulo = "Guijarro";
                    desc = "Piedra lisa. Esencial para fabricar tus propias Poké Balls.";
                    break;
                default:
                    titulo = "";
                    desc = "";
                    break;
            }
        } else if (selectedIndex == 1) { // Blue / Pokebolas
            if (indexSeleccionado == 0) {
                titulo = "Poké Ball";
                desc = "Dispositivo para atrapar Pokémon.";
            } else {
                titulo = "";
                desc = "";
            }
        } // Add other categories if defined later

        if (!titulo.isEmpty() && game.font != null) {
            game.font.setColor(Color.YELLOW);
            game.font.getData().setScale(1.2f);
            game.font.draw(batch, titulo, 100, 150); // Description at bottom left
            game.font.setColor(Color.WHITE);
            game.font.getData().setScale(1.0f);
            game.font.draw(batch, desc, 100, 110);
        }

        // Draw Exit Instruction
        if (game.font != null) {
            game.font.setColor(Color.LIGHT_GRAY);
            game.font.getData().setScale(0.8f);
            game.font.draw(batch, "Presione ESC para salir", VIRTUAL_WIDTH - 250, 40);
            game.font.setColor(Color.WHITE); // Reset
            game.font.getData().setScale(1.0f);
        }
    }

    @Override
    public void dispose() {
        Gdx.app.log("MochilaScreen", "Disposing textures...");
        if (background != null)
            background.dispose();
        if (buttonsNormal != null) {
            for (Texture t : buttonsNormal)
                if (t != null)
                    t.dispose();
        }
        if (buttonsSelected != null) {
            for (Texture t : buttonsSelected)
                if (t != null)
                    t.dispose();
        }
        if (texPokeball != null)
            texPokeball.dispose();
        if (texPlanta != null)
            texPlanta.dispose();
        if (texGuijarro != null)
            texGuijarro.dispose();
        if (texBaya != null)
            texBaya.dispose();
        if (whitePixel != null)
            whitePixel.dispose();
        if (textureFondoSlot != null)
            textureFondoSlot.dispose();
        if (fontContador != null)
            fontContador.dispose();
    }
}
