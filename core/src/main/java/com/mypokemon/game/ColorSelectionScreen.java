package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private Texture frameTexture; // New Frame

    // Layout constants
    // Viewport relative to screen size (Estimated from image)
    private static final float VP_X_PCT = 0.185f;
    private static final float VP_Y_PCT = 0.19f;
    private static final float VP_W_PCT = 0.63f;
    private static final float VP_H_PCT = 0.62f; // Bottom padding slightly larger

    private static final float TEXT_BOX_HEIGHT = 140f;

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

    // Jigglypuff Animation
    private Animation<TextureRegion> jigglyPoses;
    private float jigglyStateTime = 0;

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

            // Fix: Set Nearest filter to prevent blurring/bleeding
            jigglypuffImage.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            // Split Jigglypuff (4x4)
            TextureRegion[][] tmp = TextureRegion.split(jigglypuffImage,
                    jigglypuffImage.getWidth() / 4,
                    jigglypuffImage.getHeight() / 4);

            // Flatten frames for "posing" animation and apply TRIM (Inset)
            TextureRegion[] poseFrames = new TextureRegion[16];
            int index = 0;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    // Apply a specific "Trim" or Inset to remove messy edges/whitespace
                    // We take the split cell and shave off pixels from edges
                    TextureRegion cell = tmp[i][j];
                    int inset = 2; // Trim 2 pixels from each side
                    if (cell.getRegionWidth() > 4 && cell.getRegionHeight() > 4) {
                        poseFrames[index++] = new TextureRegion(cell, inset, inset,
                                cell.getRegionWidth() - inset * 2,
                                cell.getRegionHeight() - inset * 2);
                    } else {
                        poseFrames[index++] = cell; // Fallback if too small
                    }
                }
            }
            // Slower animation for poses (0.8s per pose)
            jigglyPoses = new Animation<>(0.8f, poseFrames);

            protaFem = new Texture("ProtaFem.png");
            protaMasc = new Texture("ProtaMasc.png");
            frameTexture = new Texture("marcoPokemon.png");
        } catch (Exception e) {
            Gdx.app.log("ColorSelectionScreen", "Could not load assets", e);
        }

        // Initialize button positions
        float sysW = Gdx.graphics.getWidth();
        float sysH = Gdx.graphics.getHeight();

        // Calculate Viewport
        float vpX = sysW * VP_X_PCT;
        float vpY = sysH * VP_Y_PCT; // Bottom Y start
        float vpW = sysW * VP_W_PCT;
        float vpH = sysH * VP_H_PCT;

        // Gender Menu Box (Right side of specific viewport)
        // Position relative to viewport
        genderMenuBounds = new Rectangle(vpX + vpW - 210, vpY + vpH / 2 - 60, 200, 120);

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
                    if (keycode == Input.Keys.ENTER) {
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
        jigglyStateTime += delta;

        Gdx.gl.glClearColor(1f, 1f, 1f, 1); // White background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        float sysW = Gdx.graphics.getWidth();
        float sysH = Gdx.graphics.getHeight();

        // Calculate Viewport
        float vpX = sysW * VP_X_PCT;
        float vpY = sysH * VP_Y_PCT;
        float vpW = sysW * VP_W_PCT;
        float vpH = sysH * VP_H_PCT;

        game.batch.begin();

        // DRAW FRAME (FULL SCREEN)
        if (frameTexture != null) {
            game.batch.draw(frameTexture, 0, 0, sysW, sysH);
        }

        // Draw Content within Viewport
        if (currentState != State.CLOSING) {
            Texture imgToDraw = null;
            if (currentState == State.INTRO_1 || currentState == State.INTRO_2 || currentState == State.INTRO_3) {
                // Use ferxxo.png instead of feid.png for the scientist intro
                imgToDraw = ferxxoImage;
            } else if (currentState == State.SELECT_GENDER || currentState == State.ASK_NAME
                    || currentState == State.ENTER_NAME) {
                imgToDraw = isMale ? protaMasc : protaFem;
            }

            if (imgToDraw != null) {
                float availableH = vpH - TEXT_BOX_HEIGHT - 20; // Space above Text Box
                float imgScale = 0;

                // Scale to fit available height properly
                if (imgToDraw.getHeight() > availableH) {
                    imgScale = availableH / imgToDraw.getHeight();
                } else {
                    imgScale = 1.0f;
                    // Scale Up Ferxxo (Scientist) slightly more if space allows
                    if (imgToDraw == ferxxoImage) {
                        if (imgToDraw.getHeight() * 1.5f <= availableH)
                            imgScale = 1.5f;
                        else
                            imgScale = availableH / imgToDraw.getHeight(); // fallback
                    } else {
                        if (imgToDraw.getHeight() * 1.5f <= availableH)
                            imgScale = 1.5f;
                    }
                }

                float imgW = imgToDraw.getWidth() * imgScale;
                float imgH = imgToDraw.getHeight() * imgScale;

                float imgX = vpX + (vpW - imgW) / 2;
                float imgY = vpY + TEXT_BOX_HEIGHT + 10; // Sit on top of text box area

                game.batch.draw(imgToDraw, imgX, imgY, imgW, imgH);

                // JIGGLYPUFF Logic (Intro 2)
                if (currentState == State.INTRO_2 && jigglyPoses != null) {
                    TextureRegion currentFrame = jigglyPoses.getKeyFrame(jigglyStateTime, true);

                    float jW = 60; // "Pequeñito" (Smaller)
                    // Keep aspect ratio
                    float ratio = (float) currentFrame.getRegionHeight() / currentFrame.getRegionWidth();
                    float jH = jW * ratio;

                    // Position on "Right Hand" (Viewer's Left)
                    float jX = imgX + imgW * -0.02f; // Even more to the left
                    float jY = imgY + imgH * 0.62f; // Higher up

                    game.batch.draw(currentFrame, jX, jY, jW, jH);
                }
            }
        } else {
            // CLOSING STATE - SPLIT VIEW within Viewport
            Texture playerImg = isMale ? protaMasc : protaFem;
            float availableH = vpH - TEXT_BOX_HEIGHT - 20;

            if (playerImg != null) {
                float imgH = availableH * 0.9f;
                float scale = imgH / playerImg.getHeight();
                float imgW = playerImg.getWidth() * scale;

                float imgX = vpX + (vpW / 4f) - imgW / 2f;
                float imgY = vpY + TEXT_BOX_HEIGHT + 10;
                game.batch.draw(playerImg, imgX, imgY, imgW, imgH);
            }

            // CLOSING: Use Feid Image
            Texture profImg = feidImage;
            if (profImg != null) {
                // FERXXO BIGGER
                // Original scale was matching player. Now we force it bigger.
                // Let's use 1.2x height of player or max available height
                float imgH = availableH * 1.0f; // Use full available height
                float scale = imgH / profImg.getHeight();
                float imgW = profImg.getWidth() * scale;

                // Allow it to slightly overlap top if needed or just fit tight

                float imgX = vpX + (vpW * 3f / 4f) - imgW / 2f;
                float imgY = vpY + TEXT_BOX_HEIGHT + 10;

                game.batch.draw(profImg, imgX, imgY, imgW, imgH);
            }
        }

        game.batch.end();

        shapeRenderer.setProjectionMatrix(game.batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // REMOVED ORANGE BORDERS (Background is handled by Frame Texture now)

        // 2. Text Box with Rounded Corners (Inside Viewport)
        float boxX = vpX + 10;
        float boxY = vpY + 10;
        float boxW = vpW - 20;
        float boxH = TEXT_BOX_HEIGHT;

        drawRoundedRect(boxX, boxY, boxW, boxH, 20f, new Color(0.3f, 0.5f, 0.6f, 1f)); // Frame
        drawRoundedRect(boxX + 6, boxY + 6, boxW - 12, boxH - 12, 16f, new Color(0.95f, 1.0f, 1.0f, 1f)); // Inner

        // 2.5 Name Tag "Profesor Ferxxo"
        float nameTagW = 220;
        float nameTagH = 50;
        float nameTagX = boxX;
        float nameTagY = boxY + boxH - 5;
        drawRoundedRect(nameTagX, nameTagY, nameTagW, nameTagH, 10f, new Color(0.3f, 0.5f, 0.6f, 1f)); // Frame
        drawRoundedRect(nameTagX + 3, nameTagY + 3, nameTagW - 6, nameTagH - 6, 8f, Color.WHITE); // Inner White

        // 3. Gender Selection Menu
        if (currentState == State.SELECT_GENDER) {
            drawRoundedRect(genderMenuBounds.x, genderMenuBounds.y, genderMenuBounds.width, genderMenuBounds.height,
                    10f, Color.WHITE);
            // Draw Border
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

        // 5. Red Arrow Indicator
        if (stateTime % 1.0f > 0.5f) {
            if (currentState != State.ENTER_NAME) {
                shapeRenderer.setColor(new Color(0.8f, 0.1f, 0.1f, 1f));
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

        // Name Tag Text
        game.font.setColor(Color.BLACK);
        game.font.getData().setScale(1.2f);
        game.font.draw(game.batch, "Profesor Ferxxo", nameTagX, nameTagY + nameTagH / 2 + 10, nameTagW, Align.center,
                false);

        // Main Text
        game.font.setColor(Color.BLACK);
        game.font.getData().setScale(1.3f); // Slightly smaller to fit frame

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
                // Draw entered name
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

        // Helper Text
        if (currentState != State.CLOSING) {
            game.font.getData().setScale(0.9f);
            game.font.setColor(Color.WHITE); // White text on Frame might look better or use Black if on UI
            // Frame art usually has dark area? Or just draw simple text at bottom of
            // Viewport
            game.font.setColor(Color.BLACK);
            game.font.draw(game.batch, "NEXT (->)   BACK (<-)", boxX, boxY - 10);
        } else {
            game.font.getData().setScale(0.9f);
            game.font.setColor(Color.BLACK);
            game.font.draw(game.batch, "PRESS ENTER TO START", boxX, boxY - 10);
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
        if (frameTexture != null)
            frameTexture.dispose();
    }
}
