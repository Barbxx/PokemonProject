package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

public class ColorSelectionScreen implements Screen {
    final PokemonMain game;

    // States
    private enum State {
        INTRO_1, INTRO_2, INTRO_3, SELECT_GENDER, ASK_NAME, ENTER_NAME, CLOSING
    }

    private State currentState;
    private ShapeRenderer shapeRenderer;

    // Data
    private String playerName = "";
    private boolean isMale = true; // Default
    private Texture feidImage;
    private Texture ferxxoImage;
    private Texture jigglypuffImage;
    private Texture protaFem, protaMasc;

    // Layout constants
    private static final float BORDER_WIDTH = 40f;
    private static final float TEXT_BOX_HEIGHT = 150f;

    // Dialogue Texts
    private static final String TEXT_1 = "¡Epaaa, qué más pues, mor! Bienvenido a la región 'One Ferxxo', el lugar más hito de todos.";
    private static final String TEXT_2 = "Yo soy el Profesor Ferxxo, el que pone a todas estas chimbitas a vacilar. Este mundo está lleno de Pokémon; unos son para parchar y otros para dar lora peleando. Yo me encargo de estudiarlos para que todo esté bien chimba pues.";
    private static final String TEXT_3 = "Pero antes de empezar el vacile... mor, dime, ¿eres un parcero o una parcera? ¡Hágale pues!";
    private static final String TEXT_NAME_Q = "Y dígame mor... ¿cuál es tu nombre, nea?";
    private static final String TEXT_CLOSING_FMT = "¡Ah, listo! Un gusto conocerte, %s. ¡Vea pues, que te espera un mundo de aventuras bien chimbas! ¡Vacílatela, nea!";

    // Buttons/Rectangles (Used for mouse checks, visuals drawn separately)
    private Rectangle genderMenuBounds;

    // Timer for blink effects
    private float stateTime = 0;
    private float jigglyTimer = 0;

    // Name Entry Cursor
    private int caretPosition = 0;

    public ColorSelectionScreen(final PokemonMain game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();
        this.currentState = State.INTRO_1;

        try {
            feidImage = new Texture("feid.png");
            ferxxoImage = new Texture("ferxxo.png");
            jigglypuffImage = new Texture("Jigglypuff.png");
            protaFem = new Texture("ProtaFem.png");
            protaMasc = new Texture("ProtaMasc.png");
        } catch (Exception e) {
            Gdx.app.log("ColorSelectionScreen", "Could not load assets", e);
        }

        // Initialize button positions
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        // Gender Menu Box (Right side, like FireRed)
        genderMenuBounds = new Rectangle(Gdx.graphics.getWidth() - BORDER_WIDTH - 220, centerY - 50, 200, 120);

        // Setup Input Processor for typing and navigation
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (currentState == State.ENTER_NAME) {
                    if (Character.isLetterOrDigit(character) || character == ' ') {
                        if (playerName.length() < 12) {
                            // Insert at caret
                            String before = playerName.substring(0, caretPosition);
                            String after = playerName.substring(caretPosition);
                            playerName = before + character + after;
                            caretPosition++;
                        }
                    } else if (character == '\r' || character == '\n') {
                        advanceState(); // Go to closing
                    }
                    if (character == '\b') {
                        if (playerName.length() > 0 && caretPosition > 0) {
                            String before = playerName.substring(0, caretPosition - 1);
                            String after = playerName.substring(caretPosition);
                            playerName = before + after;
                            caretPosition--;
                        }
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (currentState == State.CLOSING) {
                    if (keycode == Input.Keys.RIGHT) {
                        startGame();
                        return true;
                    }
                    return false;
                }

                // Global Navigation
                if (currentState != State.ENTER_NAME) {
                    if (keycode == Input.Keys.RIGHT) {
                        advanceState();
                        return true;
                    }
                    if (keycode == Input.Keys.LEFT) {
                        goBackState();
                        return true;
                    }
                } else {
                    // In Enter Name mode
                    if (keycode == Input.Keys.ENTER) {
                        advanceState(); // Confirm and showing closing
                        return true;
                    }
                    if (keycode == Input.Keys.LEFT) {
                        if (caretPosition > 0)
                            caretPosition--;
                        return true;
                    }
                    if (keycode == Input.Keys.RIGHT) {
                        if (caretPosition < playerName.length())
                            caretPosition++;
                        else {
                            advanceState();
                        }
                        return true;
                    }
                }

                // Gender Selection Specifics
                if (currentState == State.SELECT_GENDER) {
                    if (keycode == Input.Keys.UP || keycode == Input.Keys.DOWN) {
                        isMale = !isMale; // Toggle
                        return true;
                    }
                    if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                        advanceState();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                float y = Gdx.graphics.getHeight() - screenY;

                if (currentState == State.CLOSING) {
                    startGame();
                    return true;
                }

                // Gender Selection Clicks
                if (currentState == State.SELECT_GENDER) {
                    float itemHeight = 40;
                    float menuY = genderMenuBounds.y;
                    Rectangle chicoRect = new Rectangle(genderMenuBounds.x, menuY + itemHeight, genderMenuBounds.width,
                            itemHeight);
                    Rectangle chicaRect = new Rectangle(genderMenuBounds.x, menuY, genderMenuBounds.width, itemHeight);

                    if (chicoRect.contains(screenX, y)) {
                        isMale = true;
                    } else if (chicaRect.contains(screenX, y)) {
                        isMale = false;
                    }
                }

                if (currentState == State.ENTER_NAME) {
                    // No confirm button to click
                }

                // Default click acts as next
                if (currentState == State.INTRO_1 || currentState == State.INTRO_2 || currentState == State.INTRO_3) {
                    advanceState();
                }
                return true;
            }
        });
    }

    private void advanceState() {
        switch (currentState) {
            case INTRO_1:
                currentState = State.INTRO_2;
                jigglyTimer = 0;
                break;
            case INTRO_2:
                currentState = State.INTRO_3;
                break;
            case INTRO_3:
                currentState = State.SELECT_GENDER;
                break;
            case SELECT_GENDER:
                currentState = State.ASK_NAME;
                caretPosition = 0; // Reset caret
                break;
            case ASK_NAME:
                currentState = State.ENTER_NAME;
                break;
            case ENTER_NAME:
                if (playerName.trim().isEmpty())
                    playerName = "FerxxoFan";
                currentState = State.CLOSING;
                break;
            case CLOSING:
                startGame();
                break;
        }
    }

    private void goBackState() {
        switch (currentState) {
            case INTRO_2:
                currentState = State.INTRO_1;
                break;
            case INTRO_3:
                currentState = State.INTRO_2;
                break;
            case SELECT_GENDER:
                currentState = State.INTRO_3;
                break;
            case ASK_NAME:
                currentState = State.SELECT_GENDER;
                break;
            case ENTER_NAME:
                currentState = State.ASK_NAME;
                break;
            case CLOSING:
                currentState = State.ENTER_NAME;
                break;
            case INTRO_1:
                break;
        }
    }

    private void startGame() {
        String texturePath = isMale ? "protagonistaMasculino1.png" : "protagonistaFemenino.png";
        game.setScreen(new GameScreen(game, texturePath, 4, 4));
        dispose();
    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        jigglyTimer += delta;

        Gdx.gl.glClearColor(0.85f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        game.batch.begin();

        // STANDARD IMAGES LOGIC
        if (currentState != State.CLOSING) {
            Texture imgToDraw = null;
            if (currentState == State.INTRO_1 || currentState == State.INTRO_2 || currentState == State.INTRO_3) {
                imgToDraw = feidImage;
            } else if (currentState == State.SELECT_GENDER || currentState == State.ASK_NAME
                    || currentState == State.ENTER_NAME) {
                imgToDraw = isMale ? protaMasc : protaFem;
            }

            if (imgToDraw != null) {
                float imgW = 400;
                float scale = imgW / imgToDraw.getWidth();
                float imgH = imgToDraw.getHeight() * scale;
                float imgX = (Gdx.graphics.getWidth() - imgW) / 2;
                float imgY = (Gdx.graphics.getHeight() - imgH) / 2 + 50;
                game.batch.draw(imgToDraw, imgX, imgY, imgW, imgH);

                // JIGGLYPUFF Logic (Intro 2)
                if (currentState == State.INTRO_2 && jigglypuffImage != null) {
                    float t = (jigglyTimer % 1.0f);
                    float hop = 0;
                    if (t < 0.5f) {
                        float jumpT = t * 2;
                        hop = 70f * (4f * jumpT * (1f - jumpT));
                    }

                    float jW = 150;
                    float jScale = jW / jigglypuffImage.getWidth();
                    float jH = jigglypuffImage.getHeight() * jScale;

                    float jX = imgX + imgW - 80;
                    float jY = imgY + hop;

                    game.batch.draw(jigglypuffImage, jX, jY, jW, jH);
                }
            }
        } else {
            // CLOSING STATE - SPLIT VIEW
            Texture playerImg = isMale ? protaMasc : protaFem;
            if (playerImg != null) {
                float imgW = 300;
                float scale = imgW / playerImg.getWidth();
                float imgH = playerImg.getHeight() * scale;
                float imgX = Gdx.graphics.getWidth() / 4f - imgW / 2;
                float imgY = (Gdx.graphics.getHeight() - imgH) / 2 + 50;
                game.batch.draw(playerImg, imgX, imgY, imgW, imgH);
            }

            if (ferxxoImage != null) {
                float imgW = 300;
                float scale = imgW / ferxxoImage.getWidth();
                float imgH = ferxxoImage.getHeight() * scale;
                float imgX = (Gdx.graphics.getWidth() * 3f / 4f) - imgW / 2;
                float imgY = (Gdx.graphics.getHeight() - imgH) / 2 + 50;
                game.batch.draw(ferxxoImage, imgX, imgY, imgW, imgH);
            } else if (feidImage != null) {
                float imgW = 300;
                float scale = imgW / feidImage.getWidth();
                float imgH = feidImage.getHeight() * scale;
                float imgX = (Gdx.graphics.getWidth() * 3f / 4f) - imgW / 2;
                float imgY = (Gdx.graphics.getHeight() - imgH) / 2 + 50;
                game.batch.draw(feidImage, imgX, imgY, imgW, imgH);
            }
        }

        game.batch.end();

        shapeRenderer.setProjectionMatrix(game.batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // 1. Draw Background Borders (Orange)
        shapeRenderer.setColor(new Color(0.9f, 0.5f, 0.1f, 1f));
        shapeRenderer.rect(0, 0, BORDER_WIDTH, Gdx.graphics.getHeight());
        shapeRenderer.rect(Gdx.graphics.getWidth() - BORDER_WIDTH, 0, BORDER_WIDTH, Gdx.graphics.getHeight());

        // 2. Text Box with Rounded Corners
        float boxX = BORDER_WIDTH + 10;
        float boxY = 10;
        float boxW = Gdx.graphics.getWidth() - (BORDER_WIDTH * 2) - 20;
        float boxH = TEXT_BOX_HEIGHT;

        drawRoundedRect(boxX, boxY, boxW, boxH, 20f, new Color(0.3f, 0.5f, 0.6f, 1f)); // Frame
        drawRoundedRect(boxX + 6, boxY + 6, boxW - 12, boxH - 12, 16f, new Color(0.95f, 1.0f, 1.0f, 1f)); // Inner

        // 2.5 Name Tag "Profesor Ferxxo" - WHITE BG
        float nameTagW = 300;
        float nameTagH = 60;
        float nameTagX = boxX;
        float nameTagY = boxY + boxH - 5;
        drawRoundedRect(nameTagX, nameTagY, nameTagW, nameTagH, 10f, new Color(0.3f, 0.5f, 0.6f, 1f)); // Frame
        drawRoundedRect(nameTagX + 3, nameTagY + 3, nameTagW - 6, nameTagH - 6, 8f, Color.WHITE); // Inner White

        // 3. Gender Selection Menu
        if (currentState == State.SELECT_GENDER) {
            drawRoundedRect(genderMenuBounds.x, genderMenuBounds.y, genderMenuBounds.width, genderMenuBounds.height,
                    10f, Color.WHITE);
            // Draw Border (manual approx - FireRed styling)
            shapeRenderer.setColor(new Color(0.8f, 0.2f, 0.2f, 1f));
            float b = 4f;
            shapeRenderer.rect(genderMenuBounds.x, genderMenuBounds.y, genderMenuBounds.width, b);
            shapeRenderer.rect(genderMenuBounds.x, genderMenuBounds.y + genderMenuBounds.height - b,
                    genderMenuBounds.width, b);
            shapeRenderer.rect(genderMenuBounds.x, genderMenuBounds.y, b, genderMenuBounds.height);
            shapeRenderer.rect(genderMenuBounds.x + genderMenuBounds.width - b, genderMenuBounds.y, b,
                    genderMenuBounds.height);
        }

        // 4. Name Input UI
        if (currentState == State.ENTER_NAME) {
            shapeRenderer.setColor(Color.DARK_GRAY);
            float lineY = boxY + 50;
            float lineX = boxX + 100;
            shapeRenderer.rect(lineX, lineY, 400, 3);
        }

        // 5. Red Arrow Indicator (Blinking) - For Next logic
        // Show in closing too (instead of button)
        if (stateTime % 1.0f > 0.5f) {
            if (currentState != State.ENTER_NAME) {
                // Standard arrow
                shapeRenderer.setColor(new Color(0.8f, 0.1f, 0.1f, 1f)); // Darker Red
                float arrowSize = 15f;
                float arrowX = boxX + boxW - 40;
                float arrowY = boxY + 30;
                shapeRenderer.triangle(arrowX, arrowY, arrowX + arrowSize, arrowY, arrowX + arrowSize / 2,
                        arrowY - arrowSize);
            }
        }

        shapeRenderer.end();

        // 6. Text Rendering
        game.batch.begin();

        // Name Tag Text - BLACK
        game.font.setColor(Color.BLACK);
        game.font.getData().setScale(1.4f);
        game.font.draw(game.batch, "Profesor Ferxxo", nameTagX, nameTagY + nameTagH / 2 + 10, nameTagW, Align.center,
                false);

        // Main Text - BLACK
        game.font.setColor(Color.BLACK);
        game.font.getData().setScale(1.6f);

        float textX = boxX + 40;
        float textY = boxY + boxH - 40;
        float textWidth = boxW - 80;

        String currentText = "";
        switch (currentState) {
            case INTRO_1:
                currentText = TEXT_1;
                break;
            case INTRO_2:
                currentText = TEXT_2;
                break;
            case INTRO_3:
            case SELECT_GENDER:
                currentText = TEXT_3;
                break;
            case ASK_NAME:
                currentText = TEXT_NAME_Q;
                break;
            case ENTER_NAME:
                currentText = TEXT_NAME_Q;
                // Draw entered name with caret logic
                float nameY = boxY + 80;
                float nameX = boxX + 110;

                String display = playerName;

                game.font.setColor(0f, 0f, 0f, 1f);
                game.font.draw(game.batch, display, nameX, nameY);

                if (stateTime % 1f > 0.5f) {
                    float approxWidth = display.length() * 14;
                    game.font.setColor(Color.BLACK);
                    game.font.draw(game.batch, "_", nameX + approxWidth, nameY);
                }

                game.font.setColor(Color.BLACK);
                break;
            case CLOSING:
                currentText = String.format(TEXT_CLOSING_FMT, playerName);
                break;
        }

        // Wrap main text
        game.font.draw(game.batch, currentText, textX, textY, textWidth, Align.left, true);

        // Gender Menu Text
        if (currentState == State.SELECT_GENDER) {
            game.font.getData().setScale(1.5f);
            float menuX = genderMenuBounds.x + 40;
            float menuTopY = genderMenuBounds.y + genderMenuBounds.height - 35;

            game.font.draw(game.batch, "CHICO", menuX, menuTopY);
            game.font.draw(game.batch, "CHICA", menuX, menuTopY - 40);

            // Draw Cursor Arrow
            game.font.setColor(new Color(0.8f, 0.2f, 0.2f, 1f));
            game.font.draw(game.batch, ">", menuX - 25, isMale ? menuTopY : menuTopY - 40);
            game.font.setColor(Color.BLACK);
        }

        // Helper Text - ARROWS ONLY
        if (currentState != State.CLOSING) {
            game.font.getData().setScale(1.0f);
            game.font.setColor(Color.BLACK);
            game.font.draw(game.batch, "NEXT (->)   BACK (<-)", boxX, boxY + boxH + 70);
        } else {
            game.font.getData().setScale(1.0f);
            game.font.setColor(Color.BLACK);
            game.font.draw(game.batch, "NEXT (->)", boxX, boxY + boxH + 70);
        }

        game.batch.end();

        // Reset Font
        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(1.0f);
    }

    // Custom Rounded Rect Helper
    private void drawRoundedRect(float x, float y, float width, float height, float radius, Color color) {
        shapeRenderer.setColor(color);
        // Central Rect
        shapeRenderer.rect(x + radius, y + radius, width - 2 * radius, height - 2 * radius);
        // Side Rects
        shapeRenderer.rect(x + radius, y, width - 2 * radius, radius);
        shapeRenderer.rect(x + radius, y + height - radius, width - 2 * radius, radius);
        shapeRenderer.rect(x, y + radius, radius, height - 2 * radius);
        shapeRenderer.rect(x + width - radius, y + radius, radius, height - 2 * radius);
        // Corner Circles
        shapeRenderer.circle(x + radius, y + radius, radius);
        shapeRenderer.circle(x + width - radius, y + radius, radius);
        shapeRenderer.circle(x + radius, y + height - radius, radius);
        shapeRenderer.circle(x + width - radius, y + height - radius, radius);
    }

    @Override
    public void resize(int width, int height) {
        // Recalculate positions if needed
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        if (feidImage != null)
            feidImage.dispose();
        if (ferxxoImage != null)
            ferxxoImage.dispose();
        if (jigglypuffImage != null)
            jigglypuffImage.dispose();
        if (protaFem != null)
            protaFem.dispose();
        if (protaMasc != null)
            protaMasc.dispose();
    }
}
