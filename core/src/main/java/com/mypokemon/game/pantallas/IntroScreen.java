package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

import com.mypokemon.game.utils.RenderUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Pantalla de introducción del juego.
 * Gestiona la selección de género, el nombre del jugador y la animación
 * inicial.
 */
public class IntroScreen extends BaseScreen {

    // States
    private enum State {
        INTRO_1, INTRO_2, INTRO_3, SELECT_GENDER, ASK_NAME, ENTER_NAME, CONFIRM_NAME, PRE_CLOSING, FADE_OUT
    }

    private State currentState;
    private ShapeRenderer shapeRenderer;
    private float fadeAlpha = 0f;

    private OrthographicCamera camera;
    private Viewport viewport;

    // Data
    private String gameName;
    private String playerName = "";
    private boolean isMale = true; // Default
    private boolean statusNameTaken = false;
    // Timer for blink effects
    private float stateTime = 0;

    // Field Declarations (Restored)
    private Texture feidImage;
    private Texture ferxxoImage;
    private Texture jigglypuffImage;
    private Texture protaFem, protaMasc;

    private Texture frameTexture; // Nuevo Marco
    private Texture pokeballImage;
    private Texture poofImage;
    private Animation<TextureRegion> poofAnim;

    // Layout constants
    // Viewport relative to screen size (Estimated from image)
    private static final float VP_X_PCT = 0.12f;
    private static final float VP_Y_PCT = 0.12f;
    private static final float VP_W_PCT = 0.76f;
    private static final float VP_H_PCT = 0.76f; // Bottom padding slightly larger

    private static final float TEXT_BOX_HEIGHT = 140f;

    // Textos de Diálogo
    private static final String TEXT_1 = "¡Epaaa, qué más pues, mor! Bienvenido a la región 'One Ferxxo', el lugar más chimba de todos.";
    private static final String TEXT_2 = "Yo soy el Profesor Ferxxo, el que pone a todas estas chimbitas a vacilar. Este mundo está lleno de Pokémon; unos son para parchar y otros para dar lora peleando. Yo me encargo de estudiarlos para que todo esté bien chimba pues.";
    private static final String TEXT_3 = "Pero antes de empezar el vacile... mor, dime, ¿eres un parcero o una parcera? ¡Hágale pues!";
    private static final String TEXT_NAME_Q = "Y dígame mor... ¿cuál es tu nombre, nea?";
    private static final String TEXT_NAME_TAKEN = "Mor, me parece que ya hay alguien aquí con ese nombre.";
    private static final String TEXT_CLOSING_FMT = "¡Ah, listo! Un gusto conocerte, %s. ¡Vea pues, que te espera un mundo de aventuras bien chimbas! ¡Vacílatela, nea!";

    // Botones/Rectángulos (Usados para comprobaciones de ratón, dibujos separados)
    private Rectangle genderMenuBounds;

    // Timer for blink effects

    // Jigglypuff Animation
    private Animation<TextureRegion> jigglyPoses;
    private float jigglyStateTime = 0;

    // Name Entry Cursor
    private int caretPosition = 0;
    private boolean isNameConfirmed = true; // For SÍ/NO selection

    /**
     * Constructor de la pantalla de introducción.
     * 
     * @param game     Instancia principal del juego.
     * @param gameName Nombre de la partida.
     */
    public IntroScreen(final PokemonMain game, String gameName) {
        super(game);
        this.gameName = gameName;
        this.shapeRenderer = new ShapeRenderer();
        this.currentState = State.INTRO_1;

        this.camera = new OrthographicCamera();
        this.viewport = new StretchViewport(800, 600, camera);
        this.viewport.apply();

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
            pokeballImage = new Texture("pokeball.png");
            poofImage = new Texture("poof.png"); // New smoke asset

            // Setup Poof Animation (2x2 from sheet)
            TextureRegion[][] poofTmp = TextureRegion.split(poofImage, poofImage.getWidth() / 2,
                    poofImage.getHeight() / 2);
            TextureRegion[] poofFrames = new TextureRegion[4];
            int pIndex = 0;
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    poofFrames[pIndex++] = poofTmp[i][j];
                }
            }
            poofAnim = new Animation<>(0.1f, poofFrames); // 0.4s total duration

        } catch (Exception e) {
            Gdx.app.log("IntroScreen", "Could not load assets", e);
        }

        // Initialize button positions
        float sysW = 800; // Virtual width
        float sysH = 600; // Virtual height

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
                        }
                        // Removed goBackState() for strict navigation
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

                if (currentState == State.CONFIRM_NAME) {
                    if (keycode == Input.Keys.UP || keycode == Input.Keys.DOWN) {
                        isNameConfirmed = !isNameConfirmed;
                        return true;
                    }
                }

                // Navigation
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    advanceState();
                    return true;
                }

                if (keycode == Input.Keys.RIGHT) {
                    advanceState();
                    return true;
                }
                if (keycode == Input.Keys.LEFT) {
                    // Restrict Back Navigation to only Gender Selection
                    if (currentState == State.SELECT_GENDER) {
                        goBackState();
                    }
                    return true;
                }

                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Default click acts as next
                if (currentState == State.INTRO_1 || currentState == State.INTRO_2 || currentState == State.INTRO_3) {
                    advanceState();
                }
                return true;
            }
        });
    }

    /**
     * Avanza al siguiente estado de la introducción.
     */
    private void advanceState() {
        switch (currentState) {
            case INTRO_1:
                currentState = State.INTRO_2;
                jigglyStateTime = 0; // Reset animation to show Pokeball first
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
                statusNameTaken = false; // Reset error when moving to typing
                break;
            case ENTER_NAME:
                if (playerName.trim().isEmpty())
                    playerName = "Directioner";
                currentState = State.CONFIRM_NAME;
                isNameConfirmed = true; // Default to SÍ
                break;
            case CONFIRM_NAME:
                if (isNameConfirmed) {
                    if ("SharedGame".equals(gameName)) {
                        // Check if name is taken via Network
                        if (game.clienteRed != null) {
                            game.clienteRed.setListener(msg -> {
                                if (msg.equals("NAME_OK")) {
                                    Gdx.app.postRunnable(() -> {
                                        this.currentState = State.PRE_CLOSING;
                                    });
                                } else if (msg.equals("NAME_TAKEN")) {
                                    Gdx.app.postRunnable(() -> {
                                        this.statusNameTaken = true;
                                        this.currentState = State.ASK_NAME;
                                        this.playerName = "";
                                        this.caretPosition = 0;
                                    });
                                }
                            });
                            game.clienteRed.sendMessage("CHECK_NAME:" + playerName);
                        } else {
                            // Fallback if no network (shouldn't happen in SharedGame)
                            currentState = State.PRE_CLOSING;
                        }
                    } else {
                        currentState = State.PRE_CLOSING;
                    }
                } else {
                    currentState = State.ENTER_NAME;
                    playerName = ""; // Clear name
                    caretPosition = 0; // Reset cursor
                }
                break;
            case PRE_CLOSING:
                // Start Fade Out
                currentState = State.FADE_OUT;
                break;
            case FADE_OUT:
                break;
        }

    }

    /**
     * Retrocede al estado anterior (navegación básica).
     */
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
            case CONFIRM_NAME:
                currentState = State.ENTER_NAME;
                break;
            case PRE_CLOSING:
                currentState = State.CONFIRM_NAME;
                break;
            case FADE_OUT:
                break;
            case INTRO_1:
                break;
        }
    }

    /**
     * Renderiza la pantalla y maneja la lógica visual frame a frame.
     * 
     * @param delta Tiempo transcurrido desde el último frame.
     */
    @Override
    public void render(float delta) {
        stateTime += delta;
        jigglyStateTime += delta;
        // ... (render logic would be here, but using tool for doc only, so keeping existing code by using replace_file with smaller scope if needed, or ensuring full replacement. Since scope is large, I'm just adding javadoc before methods)

        // **Wait**, I cannot replace 3 methods in one chunk if they are far apart or large.
        // It's safer to target the method signature.
    }

    // RETRACTING complex multi-method replacement. Will do one by one.

    stateTime+=delta;jigglyStateTime+=delta;

    Gdx.gl.glClearColor(1f,1f,1f,1); // White background
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    Gdx.gl.glEnable(GL20.GL_BLEND);Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);

    camera.update();game.batch.setProjectionMatrix(camera.combined);

    float sysW = viewport.getWorldWidth();
    float sysH = viewport.getWorldHeight();

    // Calculate Viewport
    float vpX = sysW * VP_X_PCT;
    float vpY = sysH * VP_Y_PCT;
    float vpW = sysW * VP_W_PCT;
    float vpH = sysH * VP_H_PCT;

    game.batch.begin();

    // DRAW FRAME (FULL SCREEN)
    if(frameTexture!=null)
    {
        game.batch.draw(frameTexture, 0, 0, sysW, sysH);
    }

    // Draw Content within Viewport
    if(currentState!=State.PRE_CLOSING&&currentState!=State.FADE_OUT)
    {
        Texture imgToDraw = null;
        if (currentState == State.INTRO_1 || currentState == State.INTRO_2 || currentState == State.INTRO_3) {
            // Use ferxxo.png instead of feid.png for the scientist intro
            imgToDraw = ferxxoImage;
        } else if (currentState == State.SELECT_GENDER || currentState == State.ASK_NAME
                || currentState == State.ENTER_NAME || currentState == State.CONFIRM_NAME) {
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
                    if (imgToDraw.getHeight() * 1.8f <= availableH)
                        imgScale = 1.8f; // Increased from 1.5f
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
                TextureRegion regionToDraw = null;
                float drawW = 60f; // Default Jigglypuff size
                float drawXOffset = 0.02f;
                float drawYOffset = 0.62f;

                // PHASE 1: Pokeball (0.0 - 0.8s)
                if (jigglyStateTime < 0.8f) {
                    regionToDraw = new TextureRegion(pokeballImage);
                    drawW = 45f;
                    // Center Pokeball on hand
                    drawXOffset = 0f; // Will calculate manually
                }
                // PHASE 2: Poof (0.8s - 1.2s)
                else if (jigglyStateTime < 1.2f) {
                    regionToDraw = poofAnim.getKeyFrame(jigglyStateTime - 0.8f, false);
                    drawW = 50f;
                }
                // PHASE 3: Jigglypuff (1.2s+)
                else {
                    regionToDraw = jigglyPoses.getKeyFrame(jigglyStateTime - 1.2f, true);
                }

                if (regionToDraw != null) {
                    float ratio = (float) regionToDraw.getRegionHeight() / regionToDraw.getRegionWidth();
                    float jH = drawW * ratio;

                    float jX = imgX + imgW * drawXOffset;
                    float jY = imgY + imgH * drawYOffset;

                    // Custom Centering for Pokeball & Poof
                    if (jigglyStateTime < 1.2f) { // Both phases
                        jX = imgX + imgW * 0.02f + (60 - drawW) / 2; // Center relative to Jigglypuff slot
                        if (jigglyStateTime < 0.8f) {
                            jY -= 8; // Pokeball specific offset
                        }
                    }

                    game.batch.draw(regionToDraw, jX, jY, drawW, jH);
                }
            }
        }
    }else if(currentState==State.PRE_CLOSING||currentState==State.FADE_OUT)
    {
        // PRE_CLOSING: Show both Feid and Protagonist side by side
        // Keep drawing this during FADE_OUT
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
    }

    game.batch.end();

    shapeRenderer.setProjectionMatrix(camera.combined);shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

    // REMOVED ORANGE BORDERS (Background is handled by Frame Texture now)
    // 2. Text Box with Rounded Corners (Inside Viewport)
    float boxX = vpX + 10;
    float boxY = vpY + 10;
    float boxW = vpW - 20;
    float boxH = TEXT_BOX_HEIGHT;

    RenderUtils.drawRoundedRect(shapeRenderer,boxX,boxY,boxW,boxH,20f,new Color(0.3f,0.5f,0.6f,1f)); // Frame
    RenderUtils.drawRoundedRect(shapeRenderer,boxX+6,boxY+6,boxW-12,boxH-12,16f,new Color(0.6f,0.8f,1.0f,1f)); // Inner
                                                                                                               // (Light
                                                                                                               // Blue)

    // 2.5 Name Tag "Profesor Ferxxo"
    float nameTagW = 220;
    float nameTagH = 50;
    float nameTagX = boxX;
    float nameTagY = boxY + boxH
            - 5;RenderUtils.drawRoundedRect(shapeRenderer,nameTagX,nameTagY,nameTagW,nameTagH,10f,new Color(0.3f,0.5f,0.6f,1f)); // Frame
    RenderUtils.drawRoundedRect(shapeRenderer,nameTagX+3,nameTagY+3,nameTagW-6,nameTagH-6,8f,Color.WHITE); // Inner
                                                                                                           // White

    // 3. Gender Selection Menu (Text only, removed buttons)

    // 4. Name Input UI
    if(currentState==State.ENTER_NAME)
    {
        shapeRenderer.setColor(Color.DARK_GRAY);
        float lineY = boxY + 45;
        float lineX = boxX + 100;
        shapeRenderer.rect(lineX, lineY, 400, 3);
    }

    // 5. Red Arrow Indicator
    if(stateTime%1.0f>0.5f)
    {
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
    game.font.setColor(Color.BLACK);game.font.getData().setScale(1.2f);game.font.draw(game.batch,"Profesor Ferxxo",nameTagX,nameTagY+nameTagH/2+10,nameTagW,Align.center,false);

    // Main Text
    game.font.setColor(Color.BLACK);game.font.getData().setScale(1.0f); // Scale 1.0 for clearer text

    float textX = boxX + 50;
    float textY = boxY + boxH - 25; // Adjusted Y for smaller box
    float textWidth = boxW - 100;

    String currentText = "";switch(currentState)
    {
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
                if (statusNameTaken) {
                    currentText = TEXT_NAME_TAKEN;
                } else {
                    currentText = TEXT_NAME_Q;
                }
                break;
            case ENTER_NAME:
                if (statusNameTaken) {
                    currentText = TEXT_NAME_TAKEN;
                } else {
                    currentText = TEXT_NAME_Q;
                }
                // Draw entered name
                float nameY = boxY + 65;
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
            case CONFIRM_NAME:
                currentText = String.format("¡Bien! ¿Así que te llamas %s?", playerName);
                break;
            case PRE_CLOSING:
                currentText = String.format(TEXT_CLOSING_FMT, playerName);
                break;
            case FADE_OUT:
                currentText = String.format(TEXT_CLOSING_FMT, playerName);
                break;
        }

    game.font.draw(game.batch,currentText,textX,textY,textWidth,Align.left,true);

    // Gender Menu Text
    if(currentState==State.SELECT_GENDER)
    {
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

    // 3.1 Confirmation Menu (SÍ/NO) Text
    if(currentState==State.CONFIRM_NAME)
    {
        float menuX = boxX + 680; // Moved right (was 610)
        float menuY = boxY + boxH + 20;
        float menuH = 100;

        game.font.getData().setScale(1.6f); // Larger text (was 1.2)
        game.font.setColor(Color.BLACK);
        game.font.draw(game.batch, "SÍ", menuX + 40, menuY + menuH - 25);
        game.font.draw(game.batch, "NO", menuX + 40, menuY + 35);

        // Cursor Arrow
        game.font.setColor(new Color(0.8f, 0.2f, 0.2f, 1f));
        game.font.draw(game.batch, ">", menuX + 15, isNameConfirmed ? menuY + menuH - 25 : menuY + 35);
        game.font.setColor(Color.BLACK);
    }

    // Helper Text
    game.font.getData().setScale(0.9f);game.font.setColor(Color.BLACK);
    String helper;if(currentState==State.CONFIRM_NAME)
    {
        helper = "SELECT (ARROWS)   CONFIRM (ENTER)";
    }else if(currentState==State.PRE_CLOSING)
    {
        helper = "CONTINUE (ENTER / ->)";
    }else if(currentState==State.SELECT_GENDER)
    {
        helper = "NEXT (->)   BACK (<-)";
    }else
    {
        helper = "NEXT (->)";
    }

    if(currentState!=State.FADE_OUT)
    {
        game.font.draw(game.batch, helper, boxX + boxW - 250, boxY + 30, 200, Align.right, false);
    }

    game.batch.end();

    // 7. FADE OUT OVERLAY
    if(currentState==State.FADE_OUT)
    {
        fadeAlpha += delta * 1.0f; // 1 second fade
        if (fadeAlpha >= 1f) {
            fadeAlpha = 1f;
            // Transition directly to GameScreen (Bypassing IntroWalkScreen as requested)
            String texturePath = isMale ? "protagonistaMasculino1.png" : "protagonistaFemenino.png";
            // Pass gameName (Partida Name) AND playerName (Explorer Name)
            game.setScreen(new GameScreen(game, texturePath, 4, 4, playerName, gameName));
            dispose();
            return;
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, fadeAlpha);
        shapeRenderer.rect(0, 0, sysW, sysH);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    // Reset Font
    game.font.setColor(Color.WHITE);game.font.getData().setScale(1.0f);
    }

    /**
     * Ajusta el viewport cuando se redimensiona la ventana.
     * 
     * @param width Nuevo ancho.
     * @param height Nuevo alto.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Libera los recursos gráficos cargados.
     */
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
        if (pokeballImage != null)
            pokeballImage.dispose();
        if (poofImage != null)
            poofImage.dispose();
    }
}
