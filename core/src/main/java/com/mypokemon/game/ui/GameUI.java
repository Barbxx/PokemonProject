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
 * Gestiona todo el renderizado de la Interfaz de Usuario (UI) para la pantalla
 * de juego.
 * Implementa el Principio de Responsabilidad Única (SRP) aislando la lógica de
 * dibujo.
 */
public class GameUI {
    private BitmapFont font;
    private Texture uiWhitePixel;
    private Texture dialogFrameTexture;
    private Texture menuHintTexture; // indicacionMenu
    private float screenWidth = 800; // Ancho virtual para UI
    private float screenHeight = 480; // Alto virtual para UI

    /**
     * Constructor que inicializa los recursos gráficos de la UI.
     */
    public GameUI() {
        this.font = new BitmapFont(); // O pasar una fuente si se comparte
        this.uiWhitePixel = TextureUtils.createSolidTexture(1, 1, Color.WHITE);
        try {
            this.dialogFrameTexture = new Texture(Gdx.files.internal("marcoDialogo.png"));
        } catch (Exception e) {
            // Manejar error o ignorar
        }
        try {
            this.menuHintTexture = new Texture(Gdx.files.internal("indicacionMenu.png"));
        } catch (Exception e) {
            // Ignorar si falta
        }
    }

    /**
     * Renderiza el HUD (Heads-Up Display) básico.
     * 
     * @param batch      SpriteBatch para dibujar.
     * @param explorador Datos del jugador.
     * @param showMenu   true si el menú está abierto (oculta la indicación).
     */
    public void renderHUD(SpriteBatch batch, Explorador explorador, boolean showMenu) {
        if (explorador != null) {
            font.setColor(Color.WHITE);
            font.setColor(Color.WHITE);

            // Dibujar imagen de indicación de menú (Abajo Derecha)
            if (menuHintTexture != null && !showMenu) {
                batch.draw(menuHintTexture, screenWidth - menuHintTexture.getWidth() - 10, 10);
            }
        }
    }

    /**
     * Renderiza una notificación en el centro de la pantalla.
     * 
     * @param batch   SpriteBatch para dibujar.
     * @param message Mensaje a mostrar.
     */
    public void renderNotification(SpriteBatch batch, String message) {
        if (message != null && !message.isEmpty()) {
            font.setColor(Color.YELLOW);
            font.draw(batch, message, 400, 400, 0, Align.center, false);
            font.setColor(Color.WHITE);
        }
    }

    /**
     * Renderiza el menú de opciones del juego.
     * 
     * @param batch         SpriteBatch.
     * @param options       Array de strings con las opciones.
     * @param selectedIndex Índice de la opción seleccionada.
     */
    public void renderMenu(SpriteBatch batch, String[] options, int selectedIndex) {
        float menuW = 180;
        float menuH = 260;
        float menuX = screenWidth - menuW - 20;
        float menuY = screenHeight - menuH - 20;
        float borderSize = 4;

        // Dibujar borde del menú (Rojizo / Naranja)
        batch.setColor(new Color(0.8f, 0.2f, 0.1f, 1f));
        if (uiWhitePixel != null) {
            batch.draw(uiWhitePixel, menuX, menuY, menuW, menuH);
        }

        // Dibujar fondo del menú (Blanco)
        batch.setColor(Color.WHITE);
        if (uiWhitePixel != null) {
            batch.draw(uiWhitePixel, menuX + borderSize, menuY + borderSize, menuW - borderSize * 2,
                    menuH - borderSize * 2);
        }

        // Dibujar opciones
        font.setColor(Color.DARK_GRAY);
        font.getData().setScale(0.85f);
        float startY = menuY + menuH - 40;
        float spacing = 35;

        for (int i = 0; i < options.length; i++) {
            float optY = startY - (i * spacing);
            font.draw(batch, options[i], menuX + 45, optY);

            // Dibujar flecha de selección
            if (i == selectedIndex) {
                font.draw(batch, ">", menuX + 20, optY);
            }
        }

        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
        batch.setColor(Color.WHITE); // Restablecer color del Batch
    }

    /**
     * Renderiza un cuadro de diálogo con un NPC.
     * 
     * @param batch        SpriteBatch.
     * @param npcName      Nombre del NPC.
     * @param text         Texto del diálogo.
     * @param portrait     Retrato del NPC.
     * @param showNextHint true para mostrar indicación de "Siguiente".
     */
    public void renderDialog(SpriteBatch batch, String npcName, String text, Texture portrait, boolean showNextHint) {
        float dialogHeight = 110;
        float portraitSize = 250;
        // Lógica básica para determinar el tamaño del retrato si es necesario, pero
        // estandarizar es mejor
        // if (npcName.contains("Harry Potter")) portraitSize = 320;

        if (portrait != null)
            batch.draw(portrait, screenWidth - portraitSize - 20, dialogHeight - 20, portraitSize, portraitSize);

        // Dibujar cuadro
        batch.setColor(Color.DARK_GRAY);
        if (uiWhitePixel != null)
            batch.draw(uiWhitePixel, 20, 20, screenWidth - 40, dialogHeight);
        batch.setColor(Color.WHITE);
        if (uiWhitePixel != null)
            batch.draw(uiWhitePixel, 23, 23, screenWidth - 46, dialogHeight - 6);
        // Etiqueta de nombre
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

    /**
     * Renderiza una pista o mensaje de ayuda.
     * 
     * @param batch SpriteBatch.
     * @param text  Texto de la pista.
     */
    public void renderHint(SpriteBatch batch, String text) {
        font.getData().setScale(0.8f);
        font.setColor(Color.YELLOW);
        font.draw(batch, text, 300, 100);
        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
    }

    /**
     * Libera los recursos (fuentes y texturas).
     */
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
