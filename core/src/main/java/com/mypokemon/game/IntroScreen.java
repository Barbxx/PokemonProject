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

public class IntroScreen implements Screen {
    final PokemonMain game;

    // States
    private enum State {
        INTRO_1, INTRO_2, INTRO_3, SELECT_GENDER, ASK_NAME, ENTER_NAME, PRE_CLOSING, CLOSING
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
    private static final String TEXT_1 = "¡Epaaa, qué más pues, mor! Bienvenido a la región 'One Ferxxo', el lugar más chimba de todos.";
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

    // Movement in CLOSING state
    private Animation<TextureRegion> playerWalkDown, playerWalkUp, playerWalkLeft, playerWalkRight;
    private float playerPosX, playerPosY;
    private float playerSpeed = 150f;
    private TextureRegion playerCurrentFrame;
    private float playerAnimTime = 0;
    private boolean isPlayerMoving = false;
    private Texture playerMascSheet, playerFemSheet;

    public IntroScreen(final PokemonMain game) {
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

            // Load Walking Sheets
            playerMascSheet = new Texture("protagonistaMasculino1.png");
            playerFemSheet = new Texture("protagonistaFemenino.png");
            playerMascSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            playerFemSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        } catch (Exception e) {
            Gdx.app.log("IntroScreen", "Could not load assets", e);
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
                // Global mapping

                if (currentState == State.ENTER_NAME) {
                    if (keycode == Input.Keys.ENTER) {
                        advanceState();
                        return true;
                    }
                    if (keycode == Input.Keys.LEFT) {
                        if (caretPosition > 0) {
                            caretPosition--;
                        } else {
                            goBackState();
                        }
                        return true;
                    }
                    if (keycode == Input.Keys.RIGHT) {
                        if (caretPosition < playerName.length()) {
                            caretPosition++;
                        } else {
                            advanceState();
                        }
                        return true;
                    }
                    return false;
                }

                if (currentState == State.SELECT_GENDER) {
                    if (keycode == Input.Keys.UP || keycode == Input.Keys.DOWN) {
                        isMale = !isMale;
                        return true;
                    }
                }

                // Navigation
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    advanceState();
                    return true;
                }

                if (currentState != State.CLOSING) {
                    if (keycode == Input.Keys.RIGHT) {
                        advanceState();
                        return true;
                    }
                    if (keycode == Input.Keys.LEFT) {
                        goBackState();
                        return true;
                    }
                }

                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // float y = Gdx.graphics.getHeight() - screenY; // Unused now

                if (currentState == State.CLOSING) {
                    startGame();
                    return true;
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
                currentState = State.PRE_CLOSING;
                break;
            case PRE_CLOSING:
                currentState = State.CLOSING;
                setupPlayerClosing();
                break;
            case CLOSING:
                startGame();
                break;
        }

    }

    private void setupPlayerClosing() {
        float sysW = Gdx.graphics.getWidth();
        float sysH = Gdx.graphics.getHeight();
        float vpX = sysW * VP_X_PCT;
        float vpY = sysH * VP_Y_PCT;
        float vpW = sysW * VP_W_PCT;

        // Initial Position
        playerPosX = vpX + (vpW / 4f);
        playerPosY = vpY + TEXT_BOX_HEIGHT + 10;

        // Setup animations based on isMale
        Texture sheet = isMale ? playerMascSheet : playerFemSheet;
        int frameW = sheet.getWidth() / 4;
        int frameH = sheet.getHeight() / 4;
        TextureRegion[][] frames = TextureRegion.split(sheet, frameW, frameH);

        playerWalkDown = new Animation<>(0.15f, frames[0]);
        playerWalkLeft = new Animation<>(0.15f, frames[1]);
        playerWalkRight = new Animation<>(0.15f, frames[2]);
        playerWalkUp = new Animation<>(0.15f, frames[3]);
        playerCurrentFrame = frames[0][0]; // Starting idle frame
        playerAnimTime = 0;
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
            case PRE_CLOSING:
                currentState = State.ENTER_NAME;
                break;
            case CLOSING:
                currentState = State.PRE_CLOSING;
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
        if (currentState != State.CLOSING && currentState != State.PRE_CLOSING) {
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

                // JIGGLYPUFF Logic (Intro 2 & 3)
                if ((currentState == State.INTRO_2 || currentState == State.INTRO_3) && jigglyPoses != null) {
                    TextureRegion jigFrame = jigglyPoses.getKeyFrame(jigglyStateTime, true);

                    float jW = 60;
                    float ratio = (float) jigFrame.getRegionHeight() / jigFrame.getRegionWidth();
                    float jH = jW * ratio;

                    float jX = imgX + imgW * -0.02f;
                    float jY = imgY + imgH * 0.62f;

                    game.batch.draw(jigFrame, jX, jY, jW, jH);
                }
            }
        } else if (currentState == State.PRE_CLOSING) {
            // PRE_CLOSING: Show both Feid and Protagonist side by side
            float availableH = vpH - TEXT_BOX_HEIGHT - 20;

            // Draw Protagonist on the left
            Texture protaImg = isMale ? protaMasc : protaFem;
            if (protaImg != null) {
                float imgScale = (protaImg.getHeight() > availableH) ? availableH / protaImg.getHeight() : 1.0f;
                if (protaImg.getHeight() * 1.2f <= availableH)
                    imgScale = 1.2f;

                float imgW = protaImg.getWidth() * imgScale;
                float imgH = protaImg.getHeight() * imgScale;
                float imgX = vpX + (vpW / 4f) - imgW / 2f;
                float imgY = vpY + TEXT_BOX_HEIGHT + 10;

                game.batch.draw(protaImg, imgX, imgY, imgW, imgH);
            }

            // Draw Feid on the right
            if (feidImage != null) {
                float imgScale = (feidImage.getHeight() > availableH) ? availableH / feidImage.getHeight() : 1.0f;
                if (feidImage.getHeight() * 1.2f <= availableH)
                    imgScale = 1.2f;

                float imgW = feidImage.getWidth() * imgScale;
                float imgH = feidImage.getHeight() * imgScale;
                float imgX = vpX + (vpW * 3f / 4f) - imgW / 2f;
                float imgY = vpY + TEXT_BOX_HEIGHT + 10;

                game.batch.draw(feidImage, imgX, imgY, imgW, imgH);
            }
        } else {
            // CLOSING STATE - Interactive Movement within Green Viewport
            game.batch.end();
            shapeRenderer.setProjectionMatrix(game.batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.4f, 0.7f, 0.4f, 1f); // Green background
            shapeRenderer.rect(vpX, vpY + TEXT_BOX_HEIGHT, vpW, vpH - TEXT_BOX_HEIGHT);
            shapeRenderer.end();
            game.batch.begin();

            float availableH = vpH - TEXT_BOX_HEIGHT - 20;
            isPlayerMoving = false;

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
                playerPosX -= playerSpeed * delta;
                playerCurrentFrame = playerWalkLeft.getKeyFrame(playerAnimTime, true);
                isPlayerMoving = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
                playerPosX += playerSpeed * delta;
                playerCurrentFrame = playerWalkRight.getKeyFrame(playerAnimTime, true);
                isPlayerMoving = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
                playerPosY += playerSpeed * delta;
                playerCurrentFrame = playerWalkUp.getKeyFrame(playerAnimTime, true);
                isPlayerMoving = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
                playerPosY -= playerSpeed * delta;
                playerCurrentFrame = playerWalkDown.getKeyFrame(playerAnimTime, true);
                isPlayerMoving = true;
            }

            if (isPlayerMoving) {
                playerAnimTime += delta;
            } else {
                playerAnimTime = 0;
            }

            // Draw Animated Player
            if (playerCurrentFrame != null) {
                float imgH = availableH * 0.9f;
                float scale = imgH / playerCurrentFrame.getRegionHeight();
                float imgW = playerCurrentFrame.getRegionWidth() * scale;

                game.batch.draw(playerCurrentFrame, playerPosX - imgW / 2f, playerPosY, imgW, imgH);
            }

            // CLOSING: No Professor (Professor leaves)
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

        // 3. Gender Selection Menu (Text only, removed buttons)

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
            case PRE_CLOSING:
                currentText = String.format(
                        "¡Ah, listo! Un gusto conocerte, %s. ¡Vea pues, que te espera un mundo de aventuras bien chimbas! ¡Vacílala, nea!",
                        playerName);
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
        game.font.getData().setScale(0.9f);
        game.font.setColor(Color.BLACK);
        String helper;
        if (currentState == State.CLOSING) {
            helper = "MOVE (ARROWS/WASD)   START (ENTER)";
        } else if (currentState == State.PRE_CLOSING) {
            helper = "CONTINUE (ENTER / ->)   BACK (<-)";
        } else {
            helper = "NEXT (->)   BACK (<-)";
        }
        game.font.draw(game.batch, helper, boxX, boxY - 10);

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
        if (playerMascSheet != null)
            playerMascSheet.dispose();
        if (playerFemSheet != null)
            playerFemSheet.dispose();
    }

}
