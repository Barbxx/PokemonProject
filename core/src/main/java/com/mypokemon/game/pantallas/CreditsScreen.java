package com.mypokemon.game.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.InputAdapter;
import com.mypokemon.game.PokemonMain;

/**
 * Pantalla de créditos finales que se muestra al derrotar a Arceus
 */
public class CreditsScreen implements Screen {
    private final PokemonMain game;
    private final String playerName;
    private SpriteBatch batch;
    private Texture background;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    private String[] messages;
    private int currentMessageIndex;
    private boolean showingMessage;
    private float messageTimer;

    /**
     * Constructor de la pantalla de créditos
     * 
     * @param game       Instancia principal del juego
     * @param playerName Nombre del explorador
     */
    public CreditsScreen(PokemonMain game, String playerName) {
        this.game = game;
        this.playerName = playerName;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
        this.font.getData().setScale(2.0f);
        this.font.setColor(Color.WHITE);

        // Cargar el fondo final
        try {
            this.background = new Texture(Gdx.files.internal("fondoFInal.png"));
        } catch (Exception e) {
            Gdx.app.error("CreditsScreen", "No se pudo cargar fondoFInal.png", e);
        }

        // Inicializar los mensajes
        this.messages = new String[] {
                "Has derrotado a Arceus y su poder ha estabilizado todas las realidades…",
                "el Upside Down retrocede, la magia vuelve a Hogwarts y el ritmo regresa a las calles…",
                "Miras tu Pokédex y entiendes que nunca fue una simple misión, sino que te convertiste en el guardián de todas estas historias.",
                "Misión cumplida, " + playerName
                + ", el destino de los mundos está a salvo, y tu nombre ha quedado grabado, no en el papel, sino en la esencia misma de la historia.",
                "Porque al final, el camino siempre estuvo claro…",
                "¡Atrápalos a todos!"
        };

        this.currentMessageIndex = 0;
        this.showingMessage = true;
        this.messageTimer = 0;
    }

    @Override
    public void show() {
        // Configurar input para avanzar mensajes con clic o tecla
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                advanceMessage();
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                advanceMessage();
                return true;
            }
        });
    }

    /**
     * Avanza al siguiente mensaje o vuelve al menú principal
     */
    private void advanceMessage() {
        if (currentMessageIndex < messages.length - 1) {
            currentMessageIndex++;
            messageTimer = 0;
        } else {
            // Volver al menú principal después del último mensaje
            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override
    public void render(float delta) {
        // Limpiar pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizar timer
        messageTimer += delta;

        batch.begin();

        // Dibujar fondo si está disponible
        if (background != null) {
            batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        // Dibujar cuadro de diálogo (similar al de los NPCs)
        batch.end();

        // Dibujar caja de diálogo con ShapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float boxWidth = Gdx.graphics.getWidth() * 0.8f;
        float boxHeight = 200;
        float boxX = (Gdx.graphics.getWidth() - boxWidth) / 2;
        float boxY = 50;

        // Fondo semi-transparente negro
        shapeRenderer.setColor(0, 0, 0, 0.8f);
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);

        // Borde blanco
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();

        // Dibujar texto
        batch.begin();

        if (showingMessage && currentMessageIndex < messages.length) {
            String message = messages[currentMessageIndex];

            // Dividir el texto en líneas para que quepa en la caja
            float textX = boxX + 20;
            float textY = boxY + boxHeight - 30;
            float maxWidth = boxWidth - 40;

            drawWrappedText(message, textX, textY, maxWidth);

            // Indicador de continuar (parpadeante)
            if (messageTimer % 1.0f < 0.5f) {
                font.draw(batch, "▼", boxX + boxWidth - 40, boxY + 30);
            }
        }

        batch.end();
    }

    /**
     * Dibuja texto con ajuste de línea automático
     * 
     * @param text     Texto a dibujar
     * @param x        Posición X inicial
     * @param y        Posición Y inicial
     * @param maxWidth Ancho máximo antes de hacer salto de línea
     */
    private void drawWrappedText(String text, float x, float y, float maxWidth) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float currentY = y;
        float lineHeight = font.getLineHeight();

        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            float testWidth = font.draw(batch, testLine, 0, 0).width;

            if (testWidth > maxWidth && line.length() > 0) {
                font.draw(batch, line.toString(), x, currentY);
                currentY -= lineHeight;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }

        // Dibujar la última línea
        if (line.length() > 0) {
            font.draw(batch, line.toString(), x, currentY);
        }
    }

    @Override
    public void resize(int width, int height) {
        // No es necesario hacer nada aquí
    }

    @Override
    public void pause() {
        // No es necesario hacer nada aquí
    }

    @Override
    public void resume() {
        // No es necesario hacer nada aquí
    }

    @Override
    public void hide() {
        // No es necesario hacer nada aquí
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (background != null) {
            background.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
