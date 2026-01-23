package com.mypokemon.game.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;
import com.mypokemon.game.PokemonMain;

/**
 * Pantalla para mostrar diálogos de historia con una imagen de fondo.
 * Se usa para momentos narrativos importantes como el encuentro con Arceus.
 */
public class StoryDialogScreen extends BaseScreen {

    private Texture backgroundTexture;
    private String[] dialogPages;
    private int currentPage;
    private Screen nextScreen;
    private BitmapFont font;
    private GlyphLayout layout;
    private com.badlogic.gdx.audio.Music backgroundMusic;

    /**
     * Constructor para la pantalla de diálogo de historia.
     * 
     * @param game           Instancia principal del juego
     * @param backgroundPath Ruta a la imagen de fondo
     * @param dialogPages    Array de páginas de diálogo a mostrar
     * @param nextScreen     Pantalla a la que ir después de completar los diálogos
     */
    public StoryDialogScreen(PokemonMain game, String backgroundPath, String[] dialogPages, Screen nextScreen) {
        this(game, backgroundPath, dialogPages, nextScreen, null);
    }

    /**
     * Constructor para la pantalla de diálogo de historia con música.
     * 
     * @param game           Instancia principal del juego
     * @param backgroundPath Ruta a la imagen de fondo
     * @param dialogPages    Array de páginas de diálogo a mostrar
     * @param nextScreen     Pantalla a la que ir después de completar los diálogos
     * @param musicPath      Ruta al archivo de música de fondo (puede ser null)
     */
    public StoryDialogScreen(PokemonMain game, String backgroundPath, String[] dialogPages, Screen nextScreen,
            String musicPath) {
        super(game);
        this.dialogPages = dialogPages;
        this.currentPage = 0;
        this.nextScreen = nextScreen;
        this.font = game.font;
        this.layout = new GlyphLayout();

        // Cargar la textura de fondo
        try {
            this.backgroundTexture = new Texture(Gdx.files.internal(backgroundPath));
            addTexture(this.backgroundTexture);
        } catch (Exception e) {
            Gdx.app.error("StoryDialogScreen", "No se pudo cargar la imagen: " + backgroundPath, e);
        }

        // Req #5: Cargar y reproducir música de fondo si se proporciona
        if (musicPath != null) {
            try {
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath));
                backgroundMusic.setLooping(true);
                backgroundMusic.setVolume(0.5f);
                backgroundMusic.play();
            } catch (Exception e) {
                Gdx.app.error("StoryDialogScreen", "No se pudo cargar la música: " + musicPath, e);
            }
        }
    }

    /**
     * Se llama cuando esta pantalla se muestra.
     * Puede usarse para inicializar lógica de entrada o reiniciar estados.
     */
    @Override
    public void show() {
        // Configurar input processor si es necesario
    }

    /**
     * Renderiza el fondo y los diálogos.
     * Gestiona la entrada para avanzar de página.
     * 
     * @param delta Tiempo transcurrido desde el último frame.
     */
    @Override
    public void render(float delta) {
        // Limpiar pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Manejar input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            currentPage++;
            if (currentPage >= dialogPages.length) {
                // Terminar y pasar a la siguiente pantalla
                // Req #5: Detener música antes de cambiar de pantalla
                if (backgroundMusic != null) {
                    backgroundMusic.stop();
                }
                game.setScreen(nextScreen);
                return;
            }
        }

        game.batch.begin();

        // Dibujar fondo
        if (backgroundTexture != null) {
            game.batch.draw(backgroundTexture, 0, 0, 800, 480);
        }

        // Dibujar cuadro de diálogo
        drawDialogBox();

        game.batch.end();
    }

    /**
     * Dibuja el cuadro de diálogo con el texto actual.
     */
    private void drawDialogBox() {
        if (currentPage >= dialogPages.length) {
            return;
        }

        String currentText = dialogPages[currentPage];

        // Configurar el cuadro de diálogo
        float boxX = 50;
        float boxY = 30;
        float boxWidth = 700;
        float boxHeight = 120;

        // Dibujar fondo semi-transparente del cuadro
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.batch.setColor(0, 0, 0, 0.8f);
        game.batch.draw(game.batch.getShader() != null ? backgroundTexture : createWhitePixel(),
                boxX, boxY, boxWidth, boxHeight);
        game.batch.setColor(Color.WHITE);

        // Dibujar texto
        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);

        // Ajustar texto dentro del cuadro con wrapping
        layout.setText(font, currentText, Color.WHITE, boxWidth - 40, Align.left, true);
        float textX = boxX + 20;
        float textY = boxY + boxHeight - 20;

        font.draw(game.batch, layout, textX, textY);

        // Indicador de continuar
        if (currentPage < dialogPages.length - 1) {
            font.getData().setScale(0.8f);
            font.draw(game.batch, "Presiona ENTER para continuar...", boxX + boxWidth - 250, boxY + 15);
        } else {
            font.getData().setScale(0.8f);
            font.draw(game.batch, "Presiona ENTER...", boxX + boxWidth - 180, boxY + 15);
        }

        font.getData().setScale(1.0f);
    }

    /**
     * Crea un pixel blanco para usar como textura de relleno.
     */
    private Texture createWhitePixel() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1,
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        addTexture(texture);
        return texture;
    }

    /**
     * Se llama al cambiar el tamaño de la ventana.
     * 
     * @param width  Nuevo ancho.
     * @param height Nuevo alto.
     */
    @Override
    public void resize(int width, int height) {
        // Manejar redimensionamiento si es necesario
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    /**
     * Libera los recursos de esta pantalla.
     */
    @Override
    public void dispose() {
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
        super.dispose();
    }
}
