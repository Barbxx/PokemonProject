package com.mypokemon.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.utils.TextureUtils;
import com.badlogic.gdx.Gdx;

/**
 * Handles all UI rendering for the GameScreen.
 * Implements Single Responsibility Principle (SRP) by isolating UI drawing
 * logic.
 */
public class GameUI {
    private BitmapFont font;
    private Texture uiWhitePixel;
    private Texture dialogFrameTexture;
    private Texture menuHintTexture; // indicacionMenu
    private float screenWidth = 800;
    private float screenHeight = 480;

    public GameUI() {
        this.font = new BitmapFont(); // Or pass in a font if shared
        this.uiWhitePixel = TextureUtils.createSolidTexture(1, 1, Color.WHITE);
        try {
            this.dialogFrameTexture = new Texture(Gdx.files.internal("marcoDialogo.png"));
        } catch (Exception e) {
            // Handle error or ignore
        }
        try {
            this.menuHintTexture = new Texture(Gdx.files.internal("indicacionMenu.png"));
        } catch (Exception e) {
            // Ignore if missing
        }
    }

    public void renderHUD(SpriteBatch batch, Explorador explorador, boolean showMenu) {
        if (explorador != null) {
            font.setColor(Color.WHITE);
            float hudX = 780;
            font.draw(batch, "EXPLORADOR: " + explorador.getNombre(), hudX, 460, 0, Align.right, false);

            // Draw Menu Hint Image (Bottom Right)
            if (menuHintTexture != null && !showMenu) {
                batch.draw(menuHintTexture, screenWidth - menuHintTexture.getWidth() - 10, 10);
            }
        }
    }

    public void renderNotification(SpriteBatch batch, String message) {
        if (message != null && !message.isEmpty()) {
            font.setColor(Color.YELLOW);
            font.draw(batch, message, 400, 400, 0, Align.center, false);
            font.setColor(Color.WHITE);
        }
    }

    public void renderMenu(SpriteBatch batch, String[] options, int selectedIndex) {
        float menuW = 180;
        float menuH = 260;
        float menuX = screenWidth - menuW - 20;
        float menuY = screenHeight - menuH - 20;
        float borderSize = 4;

        // Draw Menu Border (Reddish / Orange)
        batch.setColor(new Color(0.8f, 0.2f, 0.1f, 1f));
        if (uiWhitePixel != null) {
            batch.draw(uiWhitePixel, menuX, menuY, menuW, menuH);
        }

        // Draw Menu Background (White)
        batch.setColor(Color.WHITE);
        if (uiWhitePixel != null) {
            batch.draw(uiWhitePixel, menuX + borderSize, menuY + borderSize, menuW - borderSize * 2,
                    menuH - borderSize * 2);
        }

        // Draw Options
        font.setColor(Color.DARK_GRAY);
        font.getData().setScale(0.85f);
        float startY = menuY + menuH - 40;
        float spacing = 35;

        for (int i = 0; i < options.length; i++) {
            float optY = startY - (i * spacing);
            font.draw(batch, options[i], menuX + 45, optY);

            // Draw Selection Arrow
            if (i == selectedIndex) {
                font.draw(batch, ">", menuX + 20, optY);
            }
        }

        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
        batch.setColor(Color.WHITE); // Reset Batch color
    }

    public void renderDialog(SpriteBatch batch, String npcName, String text, Texture portrait, boolean showNextHint) {
        float dialogHeight = 110;
        float portraitSize = 250;
        // Basic logic to determine portrait size if needed, but standardizing is better
        // if (npcName.contains("Harry Potter")) portraitSize = 320;

        if (portrait != null)
            batch.draw(portrait, screenWidth - portraitSize - 20, dialogHeight - 20, portraitSize, portraitSize);

        // Draw Box
        batch.setColor(Color.DARK_GRAY);
        if (uiWhitePixel != null)
            batch.draw(uiWhitePixel, 20, 20, screenWidth - 40, dialogHeight);
        batch.setColor(Color.WHITE);
        if (uiWhitePixel != null)
            batch.draw(uiWhitePixel, 23, 23, screenWidth - 46, dialogHeight - 6);
        // Name Tag
        float nameTagY = dialogHeight + 10;
        batch.setColor(Color.DARK_GRAY);
        if (uiWhitePixel != null)
            batch.draw(uiWhitePixel, 45, nameTagY, 200, 35);
        batch.setColor(Color.WHITE);
        if (uiWhitePixel != null)
            batch.draw(uiWhitePixel, 47, nameTagY + 2, 196, 31);

        batch.setColor(Color.WHITE);
        font.setColor(Color.BLACK);
        font.getData().setScale(0.9f);
        font.draw(batch, npcName, 55, nameTagY + 25);
        font.setColor(Color.BLACK);
        font.getData().setScale(0.85f);
        font.draw(batch, text, 45, dialogHeight - 10, screenWidth - 90, Align.left, true);
        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);

        String hint = showNextHint ? "SIGUIENTE (ENTER)" : "CERRAR (ENTER)";
        font.getData().setScale(0.6f);
        font.draw(batch, hint, 45, 50);
        font.getData().setScale(1.0f);
    }

    public void renderHint(SpriteBatch batch, String text) {
        font.getData().setScale(0.8f);
        font.setColor(Color.YELLOW);
        font.draw(batch, text, 300, 100);
        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
    }

    public void dispose() {
        if (font != null)
            font.dispose();
        if (uiWhitePixel != null)
            uiWhitePixel.dispose();
        if (dialogFrameTexture != null)
            dialogFrameTexture.dispose();
        if (menuHintTexture != null)
            menuHintTexture.dispose();
    }
}
