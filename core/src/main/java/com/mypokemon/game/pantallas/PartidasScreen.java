package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Pantalla que lista las partidas guardadas.
 * Permite seleccionar una partida para cargarla o borrarla.
 */
public class PartidasScreen extends BaseScreen {

    private Texture background;
    private Texture btnJugarNormal, btnJugarSel;
    private Texture btnBorrarNormal, btnBorrarSel;

    private FileHandle[] saveFiles;
    private int selectedIndex = 0;
    private boolean selectingAction = false;
    private int actionIndex = 0; // 0: Play, 1: Delete

    // Camera and Viewport for fixed aspect ratio
    private OrthographicCamera camera;
    private Viewport viewport;
    private static final float VIRTUAL_WIDTH = 1280f;
    private static final float VIRTUAL_HEIGHT = 720f;

    /**
     * Constructor de la pantalla de partidas.
     * Carga las partidas guardadas (.dat) del directorio local.
     * 
     * @param game Instancia principal del juego.
     */
    public PartidasScreen(PokemonMain game) {
        super(game);
        try {
            background = new Texture("pantallaPartidas.png");
            btnJugarNormal = new Texture("boton_jugarPartida_normal.png");
            btnJugarSel = new Texture("boton_jugarPartida_seleccionado.png");
            btnBorrarNormal = new Texture("boton_borrarPartida_normal.png");
            btnBorrarSel = new Texture("boton_borrarPartida_seleccionado.png");
        } catch (Exception e) {
            Gdx.app.log("PartidasScreen", "Could not load textures: " + e.getMessage());
        }

        // Find Save Files
        FileHandle local = Gdx.files.local(".");
        saveFiles = local.list((dir, name) -> name.contains(" - ") && name.endsWith(".dat"));

        if (saveFiles == null) {
            saveFiles = new FileHandle[0];
        }

        // Setup camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();
    }

    /**
     * Renderiza la lista de partidas y maneja la interacción del usuario.
     * Permite navegar con teclado o ratón y seleccionar acciones.
     * 
     * @param delta Tiempo transcurrido del frame actual.
     */
    @Override
    public void render(float delta) {
        // Input Handling
        if (saveFiles.length > 0) {
            if (!selectingAction) {
                // FILE SELECTION PHASE
                if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                    selectedIndex--;
                    if (selectedIndex < 0)
                        selectedIndex = saveFiles.length - 1;
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                    selectedIndex++;
                    if (selectedIndex >= saveFiles.length)
                        selectedIndex = 0;
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    selectingAction = true;
                    actionIndex = 0; // Default to Play
                }
            } else {
                // ACTION SELECTION PHASE (Play / Delete)
                if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                    actionIndex = 0; // Play
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                    actionIndex = 1; // Delete
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    selectingAction = false;
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    if (actionIndex == 0) {
                        // PLAY
                        FileHandle selectedFile = saveFiles[selectedIndex];
                        String fullFileName = selectedFile.name();
                        Explorador exp = Explorador.cargarProgreso(fullFileName);
                        String skin = "protagonistaMasculino1.png";
                        if (exp != null && "CHICA".equals(exp.getGenero())) {
                            skin = "protagonistaFemenino.png";
                        }
                        game.setScreen(new GameScreen(game, skin, 4, 4, "", fullFileName));
                        dispose();
                        return;
                    } else {
                        // DELETE
                        FileHandle selectedFile = saveFiles[selectedIndex];
                        selectedFile.delete();
                        Gdx.app.log("PartidasScreen", "Deleted: " + selectedFile.name());

                        // Refresh List
                        FileHandle local = Gdx.files.local(".");
                        saveFiles = local.list((dir, name) -> name.contains(" - ") && name.endsWith(".dat"));
                        if (saveFiles == null)
                            saveFiles = new FileHandle[0];

                        selectedIndex = 0;
                        selectingAction = false;
                    }
                }
            }
        }

        if (!selectingAction && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return;
        }

        // Mouse Support for Selection and Buttons
        com.badlogic.gdx.math.Vector3 mousePos = new com.badlogic.gdx.math.Vector3(Gdx.input.getX(), Gdx.input.getY(),
                0);
        viewport.unproject(mousePos);

        if (saveFiles.length > 0) {
            float startY = VIRTUAL_HEIGHT / 2 + 100;
            float spacing = 50;

            // Mouse selection for list
            if (!selectingAction) {
                for (int i = 0; i < saveFiles.length; i++) {
                    float y = startY - (i * spacing);
                    // Approximation of text bounds for hover
                    if (mousePos.x > 300 && mousePos.x < 980 && mousePos.y < y + 10 && mousePos.y > y - 40) {
                        selectedIndex = i;
                        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                            selectingAction = true;
                            actionIndex = 0;
                        }
                    }
                }
            } else {
                // Mouse hover/click for buttons
                float btnW = 340;
                float btnH = 100;
                float btnY = 100;
                float centerX = VIRTUAL_WIDTH / 2;
                float gap = 40;

                // Jugar Button bounds
                if (mousePos.x > centerX - btnW - gap / 2 && mousePos.x < centerX - gap / 2 &&
                        mousePos.y > btnY && mousePos.y < btnY + btnH) {
                    actionIndex = 0;
                    if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                        FileHandle selectedFile = saveFiles[selectedIndex];
                        Explorador exp = Explorador.cargarProgreso(selectedFile.name());
                        String skin = "protagonistaMasculino1.png";
                        if (exp != null && "CHICA".equals(exp.getGenero())) {
                            skin = "protagonistaFemenino.png";
                        }
                        game.setScreen(
                                new GameScreen(game, skin, 4, 4, "", selectedFile.name()));
                        dispose();
                        return;
                    }
                }
                // Borrar Button bounds
                if (mousePos.x > centerX + gap / 2 && mousePos.x < centerX + btnW + gap / 2 &&
                        mousePos.y > btnY && mousePos.y < btnY + btnH) {
                    actionIndex = 1;
                    if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                        saveFiles[selectedIndex].delete();
                        saveFiles = Gdx.files.local(".")
                                .list((dir, name) -> name.contains(" - ") && name.endsWith(".dat"));
                        if (saveFiles == null)
                            saveFiles = new FileHandle[0];
                        selectedIndex = 0;
                        selectingAction = false;
                    }
                }
            }
        }

        // Render
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        if (background != null) {
            game.batch.draw(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }

        // Draw Save Files Centered
        if (saveFiles.length == 0) {
            game.font.setColor(Color.GRAY);
            game.font.getData().setScale(1.5f);
            game.font.draw(game.batch, "No hay partidas guardadas.", 0, VIRTUAL_HEIGHT / 2, VIRTUAL_WIDTH,
                    com.badlogic.gdx.utils.Align.center, false);
        } else {
            game.font.getData().setScale(1.2f);
            float startY = VIRTUAL_HEIGHT / 2 + 100;
            float spacing = 50;

            for (int i = 0; i < saveFiles.length; i++) {
                String fileName = saveFiles[i].name();
                String nameDisplay = saveFiles[i].nameWithoutExtension();
                float y = startY - (i * spacing);

                // Load Explorador for info
                Explorador exp = Explorador.cargarProgreso(fileName);
                String info = "";
                if (exp != null) {
                    int dexCount = exp.getRegistro().verificarProgreso();
                    int invCount = exp.getMochila().getEspacioOcupado();
                    info = String.format(" [%s - Dex: %d - Items: %d]", exp.getNombre(), dexCount, invCount);
                }

                if (i == selectedIndex) {
                    if (selectingAction)
                        game.font.setColor(Color.LIME);
                    else
                        game.font.setColor(Color.YELLOW);
                    nameDisplay = "> " + nameDisplay + info + " <";
                } else {
                    game.font.setColor(Color.WHITE);
                    nameDisplay = nameDisplay + info;
                }

                game.font.draw(game.batch, nameDisplay, 0, y, VIRTUAL_WIDTH, com.badlogic.gdx.utils.Align.center,
                        false);
            }

            // Draw Buttons
            float btnW = 340;
            float btnH = 100;
            float btnY = 100;
            float centerX = VIRTUAL_WIDTH / 2;
            float gap = 40;

            // Play Button
            Texture playTex = (selectingAction && actionIndex == 0) ? btnJugarSel : btnJugarNormal;
            if (playTex != null) {
                game.batch.draw(playTex, centerX - btnW - gap / 2, btnY, btnW, btnH);
            }

            // Delete Button
            Texture delTex = (selectingAction && actionIndex == 1) ? btnBorrarSel : btnBorrarNormal;
            if (delTex != null) {
                game.batch.draw(delTex, centerX + gap / 2, btnY, btnW, btnH);
            }
        }

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
    }

    /**
     * Libera los recursos (texturas) utilizados por la pantalla.
     */
    @Override
    public void dispose() {
        if (background != null)
            background.dispose();
        if (btnJugarNormal != null)
            btnJugarNormal.dispose();
        if (btnJugarSel != null)
            btnJugarSel.dispose();
        if (btnBorrarNormal != null)
            btnBorrarNormal.dispose();
        if (btnBorrarSel != null)
            btnBorrarSel.dispose();
    }
}
