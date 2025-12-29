package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    final PokemonMain game;

    Texture playerSheet;
    TextureRegion currentFrame;
    Animation<TextureRegion> walkDown, walkUp, walkLeft, walkRight;
    float stateTime = 0;
    boolean isMoving = false;

    // Popup state
    boolean showInstructions = false;
    Texture blackTexture;

    float posX = 300;
    float posY = 200;
    float speed = 200;

    public GameScreen(final PokemonMain game, String texturePath, int cols, int rows) {
        this.game = game;

        boolean assetsLoaded = false;
        try {
            playerSheet = new Texture(texturePath);
            // Use Nearest filter for pixel art to stay sharp
            playerSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            assetsLoaded = true;
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Critical: Could not load " + texturePath, e);
        }

        if (assetsLoaded) {
            try {
                // Determine frame size automatically
                int frameWidth = playerSheet.getWidth() / cols;
                int frameHeight = playerSheet.getHeight() / rows;

                TextureRegion[][] frames = TextureRegion.split(playerSheet, frameWidth, frameHeight);

                // Validate we have enough rows
                if (frames.length >= 4) {
                    walkDown = new Animation<>(0.15f, frames[0]);
                    walkLeft = new Animation<>(0.15f, frames[1]);
                    walkRight = new Animation<>(0.15f, frames[2]);
                    walkUp = new Animation<>(0.15f, frames[3]);
                    currentFrame = frames[0][0];
                } else {
                    Gdx.app.log("GameScreen",
                            "Error: " + texturePath + " dimensions are too small or incorrect layout.");
                    createFallback();
                }
            } catch (Exception e) {
                Gdx.app.log("GameScreen", "Error splitting texture", e);
                createFallback();
            }
        } else {
            createFallback();
        }

        // Create black texture for popup background
        com.badlogic.gdx.graphics.Pixmap pm = new com.badlogic.gdx.graphics.Pixmap(1, 1,
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pm.setColor(com.badlogic.gdx.graphics.Color.BLACK);
        pm.fill();
        blackTexture = new Texture(pm);
        pm.dispose();
    }

    private void createFallback() {
        // Create a simple magenta square as fallback
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(239, 256,
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(com.badlogic.gdx.graphics.Color.MAGENTA);
        pixmap.fill();
        playerSheet = new Texture(pixmap);
        pixmap.dispose();

        // Create a single region from it
        TextureRegion fallbackRegion = new TextureRegion(playerSheet);
        TextureRegion[] fallbackArray = new TextureRegion[] { fallbackRegion };

        // Assign to all animations
        walkDown = new Animation<>(0.15f, fallbackArray);
        walkLeft = new Animation<>(0.15f, fallbackArray);
        walkRight = new Animation<>(0.15f, fallbackArray);
        walkUp = new Animation<>(0.15f, fallbackArray);
        currentFrame = fallbackRegion;
    }

    @Override
    public void render(float delta) {
        isMoving = false;

        if (showInstructions) {
            // Logic for instruction popup
            if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
                showInstructions = false;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
                game.setScreen(new IntroScreen(game));
                dispose();
                return;
            }
        } else {
            // Handle Input for Movement (only if not showing instructions)
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                Gdx.app.exit();
            }

            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                posX -= speed * delta;
                currentFrame = walkLeft.getKeyFrame(stateTime, true);
                isMoving = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                posX += speed * delta;
                currentFrame = walkRight.getKeyFrame(stateTime, true);
                isMoving = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                posY += speed * delta;
                currentFrame = walkUp.getKeyFrame(stateTime, true);
                isMoving = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                posY -= speed * delta;
                currentFrame = walkDown.getKeyFrame(stateTime, true);
                isMoving = true;
            }

            if (isMoving) {
                stateTime += delta;
            } else {
                stateTime = 0;
            }
        }

        ScreenUtils.clear(0.4f, 0.7f, 0.4f, 1f);

        game.batch.begin();
        // Draw the player. Size 100x107
        game.batch.draw(currentFrame, posX, posY, 100, 107);

        // Draw Instructions Popup
        if (showInstructions) {
            // Draw semi-transparent dim
            if (blackTexture != null) {
                game.batch.setColor(0, 0, 0, 0.8f);
                game.batch.draw(blackTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                game.batch.setColor(com.badlogic.gdx.graphics.Color.WHITE); // Reset
            }

            // Draw text
            if (game.font != null) {
                game.font.getColor().set(com.badlogic.gdx.graphics.Color.WHITE);
                // Simple centering logic
                String text1 = "PRESS 'O' TO CONTINUE";
                String text2 = "PRESS 'B' TO GO BACK";
                float w = Gdx.graphics.getWidth();
                float h = Gdx.graphics.getHeight();

                game.font.draw(game.batch, text1, w / 2 - 70, h / 2 + 20);
                game.font.draw(game.batch, text2, w / 2 - 70, h / 2 - 20);
            }
        }

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
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
        if (playerSheet != null)
            playerSheet.dispose();
        if (blackTexture != null)
            blackTexture.dispose();
    }
}
