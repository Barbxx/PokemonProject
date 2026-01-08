package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mypokemon.game.utils.BaseScreen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.GL20;

public class LaboratorioScreen extends BaseScreen {
    private GameScreen gameScreen;
    private com.badlogic.gdx.graphics.Texture backgroundTexture;
    private com.badlogic.gdx.graphics.Texture feidSprite; // Sprite for Feid
    // Starter Sprites
    private com.badlogic.gdx.graphics.Texture rowletTexture, cyndaquilTexture, oshawottTexture;

    // Dialog Resources
    private com.badlogic.gdx.graphics.Texture dialogIconTexture;
    private com.badlogic.gdx.graphics.Texture uiWhitePixel;
    private boolean showDialog = false;
    private int currentDialogPage = 0;
    private String[] currentDialogText; // Changed to dynamic array

    // Portrait Textures
    private com.badlogic.gdx.graphics.Texture ferxxoRowlet, ferxxoCyndaquil, ferxxoOshawott;
    private com.badlogic.gdx.graphics.Texture ferxxoCientifico; // Default portrait

    // Default Introduction Text
    private final String[] INTRO_TEXT = {
            "¡Hágale, mor! Llegó a la parte más firme. Aquí tengo estos tres candidatos que están listos para salir a rumbear y a batallar por todo Hisui.",
            "Están guardaditos en las Poké Balls pa' que no se alboroten.",
            "Vea, yo cuando estaba más pelado era un buen entrenador de Pokémon.",
            "Pero ya hoy solo me quedan estos tres... se los presento pa' que escoja uno."
    };

    private final String[] ROWLET_TEXT = {
            "Este es el Rowlet. Es tipo Planta y Volador. Este bicho es bien silencioso, le llega a los otros por la espalda y ¡pum!, los deja sanos.",
            "Entonces, ¿qué dice, mor? ¿Se va a llevar al tipo Planta y Volador Rowlet?"
    };

    private final String[] CYNDAQUIL_TEXT = {
            "Este es el Cyndaquil. Es tipo Fuego. Se ve todo calmado y chill, pero donde se moleste... le sale candela por el lomo y eso quema hasta el alma, mor.",
            "¿Cómo fue entonces? ¿Se queda con el tipo Fuego Cyndaquil?"
    };

    private final String[] OSHAWOTT_TEXT = {
            "Este es el Oshawott. Es tipo Agua. El parcero se ve muy tierno, pero no se confunda, que esa conchita que trae en el pecho la usa como un sable y ¡tome!, no copia de nada.",
            "Entonces, ¿cómo es la vuelta? ¿Se va a montar con el tipo Agua Oshawott?"
    };

    // Success Texts
    private final String[] ROWLET_SUCCESS = { "¡Recibiste el Pokemón Rowlet de manos del Profesor Ferxxo!" };
    private final String[] CYNDAQUIL_SUCCESS = { "¡Recibiste el Pokemón Cyndaquil de manos del Profesor Ferxxo!" };
    private final String[] OSHAWOTT_SUCCESS = { "¡Recibiste el Pokemón Oshawott de manos del Profesor Ferxxo!" };

    // Fade State
    private float fadeAlpha = 1f;
    private boolean fadingIn = true;
    private boolean fadingOut = false;

    // Selection State
    private boolean selectionActive = false;
    private int selectionIndex = 0; // 0 = YES, 1 = NO
    private boolean isTransitioning = false; // To know if we are showing success message

    // Rendering
    private OrthographicCamera camera;
    private Viewport viewport;

    // Player State
    private float posX = 330; // Center horizontal
    private float posY = 60; // Bottom vertical
    private TextureRegion currentFrame;
    private float playerWidth = 40f;
    private float playerHeight = 32f;

    // Feid Bounds
    private float feidX = 350;
    private float feidY = 120;
    private float feidW = 25;
    private float feidH = 35;

    // Starter Bounds (Positioned to the right of Feid)
    private float rowletX = 415, rowletY = 95, starterW = 30, starterH = 30;
    private float cyndaquilX = 445, cyndaquilY = 95;
    private float oshawottX = 480, oshawottY = 95;

    public LaboratorioScreen(PokemonMain game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;

        // Initialize Camera and Viewport (800x480)
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camera);
        camera.position.set(400, 240, 0); // Center camera
        camera.update();

        // Ensure fade alpha starts at 1 (black)
        fadeAlpha = 1f;
        fadingIn = true;
        fadingOut = false;

        currentDialogText = INTRO_TEXT; // Default
    }

    @Override
    public void show() {
        try {
            // Corrected extension to .jpg
            backgroundTexture = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("fondoLaboratorio.png"));
            feidSprite = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("feidSprite.png"));

            // Starter Textures
            rowletTexture = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("rowletLab.png"));
            cyndaquilTexture = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("cyndaquilLab.png"));
            oshawottTexture = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("oshawottLab.png"));

            // Dialog Assets
            ferxxoCientifico = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("ferxxoCientifico.png"));
            // Load specific portraits
            ferxxoRowlet = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("profeFerxxoRowlet.png"));
            ferxxoCyndaquil = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("profeFerxxoCyndaquil.png"));
            ferxxoOshawott = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("profeFerxxoOshawott.png"));

            // Default
            dialogIconTexture = ferxxoCientifico;

            // Create a 1x1 white pixel texture programmatically for the dialog background
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1,
                    com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            uiWhitePixel = new com.badlogic.gdx.graphics.Texture(pixmap);
            pixmap.dispose();

            // Setup Player Animation (Static Frame only)
            if (gameScreen.getPlayerSheet() != null) {
                TextureRegion[][] frames = TextureRegion.split(gameScreen.getPlayerSheet(),
                        gameScreen.getPlayerSheet().getWidth() / gameScreen.getFrameCols(),
                        gameScreen.getPlayerSheet().getHeight() / gameScreen.getFrameRows());

                if (frames.length >= 4) {
                    // Use frame facing UP (Row 3, Col 0) as static sprite
                    currentFrame = frames[3][0];
                }
            }
        } catch (Exception e) {
            Gdx.app.log("LaboratorioScreen", "Error loading textures: " + e.getMessage(), e);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1); // Black background

        // Check if on selection page (Index 1 of starter texts)
        selectionActive = !isTransitioning && currentDialogPage == 1 &&
                (currentDialogText == ROWLET_TEXT || currentDialogText == CYNDAQUIL_TEXT
                        || currentDialogText == OSHAWOTT_TEXT);

        // FADE IN LOGIC
        if (fadingIn) {
            fadeAlpha -= delta * 1.5f; // Fade speed
            if (fadeAlpha <= 0) {
                fadeAlpha = 0;
                fadingIn = false;
            }
        }

        // FADE OUT LOGIC
        if (fadingOut) {
            fadeAlpha += delta * 1.5f;
            if (fadeAlpha >= 1) {
                fadeAlpha = 1;
                game.setScreen(gameScreen);
            }
            // Block input while fading out
            // We can return here but we want to draw the black overlay
        }

        // Dialog Input Logic (Only if not fading out)
        if (showDialog && !fadingOut) {
            if (selectionActive) {
                // Handling selection Input (Arrow Keys)
                if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)
                        || Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                    selectionIndex = 0; // YES
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)
                        || Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                    selectionIndex = 1; // NO
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    if (selectionIndex == 0) {
                        // YES selected
                        isTransitioning = true;
                        String selectedName = "";
                        if (currentDialogText == ROWLET_TEXT) {
                            currentDialogText = ROWLET_SUCCESS;
                            selectedName = "Rowlet";
                        } else if (currentDialogText == CYNDAQUIL_TEXT) {
                            currentDialogText = CYNDAQUIL_SUCCESS;
                            selectedName = "Cyndaquil";
                        } else if (currentDialogText == OSHAWOTT_TEXT) {
                            currentDialogText = OSHAWOTT_SUCCESS;
                            selectedName = "Oshawott";
                        }

                        // Register Pokemon and add to team
                        if (!selectedName.isEmpty()) {
                            Explorador exp = gameScreen.getExplorador();
                            // Level 10 means fully researched data unlocked
                            Pokemon p = new Pokemon(selectedName, 10, 0, false, "");

                            exp.agregarAlEquipo(p);
                            exp.getRegistro().completarInstantaneamente(selectedName);
                            exp.getRegistro().getRegistro().get(selectedName).setCapturado(true);
                        }

                        currentDialogPage = 0; // Show success message
                    } else {
                        // NO selected
                        showDialog = false;
                        currentDialogPage = 0;
                    }
                }
            } else {
                // Normal Dialog Advance (ENTER Only)
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    currentDialogPage++;
                    if (currentDialogPage >= currentDialogText.length) {
                        showDialog = false;
                        currentDialogPage = 0;

                        // If we just finished showing the transition (Success) message, start FADE OUT
                        if (isTransitioning) {
                            fadingOut = true;
                        }
                    }
                }
            }
        } else if (!fadingOut) {
            // RESET FLAGS
            isTransitioning = false;

            // Click detection
            if (Gdx.input.justTouched()) {
                com.badlogic.gdx.math.Vector3 mousePos = new com.badlogic.gdx.math.Vector3(Gdx.input.getX(),
                        Gdx.input.getY(), 0);
                viewport.unproject(mousePos);

                // Check Feid click
                if (mousePos.x >= feidX && mousePos.x <= feidX + feidW &&
                        mousePos.y >= feidY && mousePos.y <= feidY + feidH) {
                    currentDialogText = INTRO_TEXT;
                    showDialog = true;
                    currentDialogPage = 0;
                    dialogIconTexture = ferxxoCientifico; // Default icon
                }
                // Check Rowlet click
                else if (mousePos.x >= rowletX && mousePos.x <= rowletX + starterW &&
                        mousePos.y >= rowletY && mousePos.y <= rowletY + starterH) {
                    currentDialogText = ROWLET_TEXT;
                    showDialog = true;
                    currentDialogPage = 0;
                    selectionIndex = 0; // Default to Yes
                    dialogIconTexture = ferxxoRowlet; // Specific icon (kept name)
                }
                // Check Cyndaquil click
                else if (mousePos.x >= cyndaquilX && mousePos.x <= cyndaquilX + starterW + 10 &&
                        mousePos.y >= cyndaquilY && mousePos.y <= cyndaquilY + starterH + 10) {
                    currentDialogText = CYNDAQUIL_TEXT;
                    showDialog = true;
                    currentDialogPage = 0;
                    selectionIndex = 0;
                    dialogIconTexture = ferxxoCyndaquil; // Specific icon (kept name)
                }
                // Check Oshawott click
                else if (mousePos.x >= oshawottX && mousePos.x <= oshawottX + starterW + 10 &&
                        mousePos.y >= oshawottY && mousePos.y <= oshawottY + starterH + 10) {
                    currentDialogText = OSHAWOTT_TEXT;
                    showDialog = true;
                    currentDialogPage = 0;
                    selectionIndex = 0;
                    dialogIconTexture = ferxxoOshawott; // Specific icon (kept name)
                }
            }
            // Movement input handling and animation update logic removed as player is
            // static.
            // The variables moveX and moveY are no longer defined or used.
        }

        // isMoving is always false, so stateTime will not increment.
        // The player remains static, displaying the initial frame.

        // Clamp to screen bounds (800x480)
        if (posX < 0)
            posX = 0;
        if (posY < 0)
            posY = 0;
        if (posX > 800 - playerWidth)
            posX = 800 - playerWidth;
        if (posY > 480 - playerHeight)
            posY = 480 - playerHeight;

        // Apply Viewport
        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // Reset Color to WHITE to ensure textures draw correctly
        game.batch.setColor(Color.WHITE);

        // Draw Background
        if (backgroundTexture != null) {
            game.batch.draw(backgroundTexture, 0, 0, 800, 480);
        }

        // Draw Feid Sprite (Static, small 25x35)
        if (feidSprite != null) {
            game.batch.draw(feidSprite, feidX, feidY, feidW, feidH);
        }

        // Draw Starter Sprites
        // Rowlet (Default)
        if (rowletTexture != null)
            game.batch.draw(rowletTexture, rowletX, rowletY, starterW, starterH);
        // Cyndaquil (Larger)
        if (cyndaquilTexture != null)
            game.batch.draw(cyndaquilTexture, cyndaquilX, cyndaquilY, starterW + 10, starterH + 10);
        // Oshawott (Larger)
        if (oshawottTexture != null)
            game.batch.draw(oshawottTexture, oshawottX, oshawottY, starterW + 10, starterH + 10);

        // Draw Player
        if (currentFrame != null) {
            game.batch.draw(currentFrame, posX, posY, playerWidth, playerHeight * 1.2f);
        }

        // Draw Dialog
        if (showDialog) {
            drawDialog();
        }

        // DRAW FADE OVERLAY
        if (fadeAlpha > 0 && uiWhitePixel != null) {
            // Enable blending for transparency
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            game.batch.setColor(0, 0, 0, fadeAlpha);
            game.batch.draw(uiWhitePixel, 0, 0, 800, 480);
            game.batch.setColor(Color.WHITE); // Reset color

            // Should we disable blend? LibGDX SpriteBatch handles it, but good practice if
            // changing state manually.
            // Actually SpriteBatch typically sets up blending.
        }

        game.batch.end();

        if (!showDialog && !fadingOut && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            fadingOut = true;
        }
    }

    private void drawDialog() {
        float screenW = 800;
        float dialogHeight = 110;
        float portraitSize = 250;

        // Check if it is a success message
        // Check if it is a success message
        boolean isSuccessMessage = (currentDialogText == ROWLET_SUCCESS || currentDialogText == CYNDAQUIL_SUCCESS
                || currentDialogText == OSHAWOTT_SUCCESS);

        // Draw Portrait (Right side) - Only if NOT a success message
        if (!isSuccessMessage && dialogIconTexture != null) {
            game.batch.draw(dialogIconTexture, screenW - portraitSize - 20, dialogHeight - 20, portraitSize,
                    portraitSize);
        }

        boolean isSuccessMessageSpecial = (currentDialogText == ROWLET_SUCCESS || currentDialogText == CYNDAQUIL_SUCCESS
                || currentDialogText == OSHAWOTT_SUCCESS);

        // Draw Main Dialog Box
        // Border (Dark Gray)
        game.batch.setColor(Color.DARK_GRAY);
        if (uiWhitePixel != null)
            game.batch.draw(uiWhitePixel, 20, 20, screenW - 40, dialogHeight);

        // Body (White)
        game.batch.setColor(Color.WHITE);
        if (uiWhitePixel != null)
            game.batch.draw(uiWhitePixel, 23, 23, screenW - 46, dialogHeight - 6);

        // Name Tag Box (Top-Left of dialog) - Only if NOT a success message
        float nameTagY = dialogHeight + 10;
        if (!isSuccessMessageSpecial) {
            float nameTagW = 200;
            float nameTagH = 35;

            // Name Tag Border (Dark Gray)
            game.batch.setColor(Color.DARK_GRAY);
            if (uiWhitePixel != null)
                game.batch.draw(uiWhitePixel, 45, nameTagY, nameTagW, nameTagH);

            // Name Tag Background (White)
            game.batch.setColor(Color.WHITE);
            if (uiWhitePixel != null)
                game.batch.draw(uiWhitePixel, 47, nameTagY + 2, nameTagW - 4, nameTagH - 4);

            // Reset Color for Text
            game.batch.setColor(Color.WHITE);

            // Draw Text
            // Name
            game.font.setColor(Color.BLACK);
            game.font.getData().setScale(0.9f);
            game.font.draw(game.batch, "Profesor Ferxxo", 55, nameTagY + 25);
        }

        // Dialog Body
        game.font.setColor(Color.BLACK);
        game.font.getData().setScale(0.85f);

        // Wrap text
        String text = currentDialogText[currentDialogPage];
        game.font.draw(game.batch, text, 45, dialogHeight - 10, screenW - 90, com.badlogic.gdx.utils.Align.left, true);

        // Draw Yes/No Selection
        if (selectionActive) {
            float optionX = 600;
            float optionY = 100; // Increased Y position

            // Draw Options

            // YES Option
            if (selectionIndex == 0)
                game.font.setColor(Color.RED);
            else
                game.font.setColor(Color.BLACK);
            game.font.draw(game.batch, "> SÍ", optionX, optionY);

            // NO Option
            if (selectionIndex == 1)
                game.font.setColor(Color.RED);
            else
                game.font.setColor(Color.BLACK);
            game.font.draw(game.batch, "> NO", optionX, optionY - 30);
        }

        game.font.getData().setScale(1.0f);
        game.font.setColor(Color.WHITE);

        // "Continue" hint
        if (!selectionActive) {
            game.font.getData().setScale(0.6f);
            String hint = (currentDialogPage < currentDialogText.length - 1) ? "SIGUIENTE (ENTER)" : "CERRAR (ENTER)";
            game.font.setColor(Color.DARK_GRAY);
            game.font.draw(game.batch, hint, 700, 40, 0, com.badlogic.gdx.utils.Align.right, false);
            game.font.setColor(Color.WHITE);
            game.font.getData().setScale(1.0f);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        if (backgroundTexture != null)
            backgroundTexture.dispose();
        if (feidSprite != null)
            feidSprite.dispose();
        // Dispose specific textures
        if (ferxxoCientifico != null)
            ferxxoCientifico.dispose();
        if (ferxxoRowlet != null)
            ferxxoRowlet.dispose();
        if (ferxxoCyndaquil != null)
            ferxxoCyndaquil.dispose();
        if (ferxxoOshawott != null)
            ferxxoOshawott.dispose();

        if (uiWhitePixel != null)
            uiWhitePixel.dispose();
        if (rowletTexture != null)
            rowletTexture.dispose();
        if (cyndaquilTexture != null)
            cyndaquilTexture.dispose();
        if (oshawottTexture != null)
            oshawottTexture.dispose();
    }
}
