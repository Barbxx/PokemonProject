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
 * Pantalla que permite visualizar, seleccionar y borrar las partidas guardadas.
 */
public class PartidasScreen extends BaseScreen {

    /** Textura de fondo de la pantalla. */
    private Texture background;
    /**
     * Texturas para los botones de jugar y borrar en estado normal y seleccionado.
     */
    private Texture btnJugarNormal, btnJugarSel;
    private Texture btnBorrarNormal, btnBorrarSel;

    /** Lista de archivos de guardado encontrados en el directorio local. */
    private FileHandle[] saveFiles;
    /** Índice del archivo de guardado seleccionado actualmente. */
    private int selectedIndex = 0;
    /** Indica si el jugador está en la fase de elegir una acción (Jugar/Borrar). */
    private boolean selectingAction = false;
    /** Índice de la acción seleccionada (0: Jugar, 1: Borrar). */
    private int actionIndex = 0;

    // Cámara y Viewport para mantener una resolución fija
    private OrthographicCamera camera;
    private Viewport viewport;
    private static final float VIRTUAL_WIDTH = 1280f;
    private static final float VIRTUAL_HEIGHT = 720f;

    /**
     * Constructor de la pantalla de partidas.
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
            Gdx.app.log("PartidasScreen", "No se pudieron cargar las texturas: " + e.getMessage());
        }

        // Buscar archivos de guardado
        FileHandle local = Gdx.files.local(".");
        saveFiles = local.list((dir, name) -> name.contains(" - ") && name.endsWith(".dat"));

        if (saveFiles == null) {
            saveFiles = new FileHandle[0];
        }

        // Configuración de cámara y viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();
    }

    /**
     * Renderiza la lista de partidas, gestiona la entrada de teclado/ratón y las
     * acciones de jugar/borrar.
     * 
     * @param delta Tiempo transcurrido desde el último frame.
     */
    @Override
    public void render(float delta) {
        // Manejo de entrada
        if (saveFiles.length > 0) {
            if (!selectingAction) {
                // FASE DE SELECCIÓN DE ARCHIVO
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
                    actionIndex = 0; // Por defecto Jugar
                }
            } else {
                // FASE DE SELECCIÓN DE ACCIÓN (Jugar / Borrar)
                if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                    actionIndex = 0; // Jugar
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                    actionIndex = 1; // Borrar
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    selectingAction = false;
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    if (actionIndex == 0) {
                        // JUGAR
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
                        // BORRAR
                        FileHandle selectedFile = saveFiles[selectedIndex];
                        selectedFile.delete();
                        Gdx.app.log("PartidasScreen", "Borrada: " + selectedFile.name());

                        // Refrescar lista
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

        // Soporte de ratón para selección y botones
        com.badlogic.gdx.math.Vector3 mousePos = new com.badlogic.gdx.math.Vector3(Gdx.input.getX(), Gdx.input.getY(),
                0);
        viewport.unproject(mousePos);

        if (saveFiles.length > 0) {
            float startY = VIRTUAL_HEIGHT / 2 + 100;
            float spacing = 60; // Aumentado para mayor claridad con letra grande

            // Selección con ratón para la lista
            if (!selectingAction) {
                for (int i = 0; i < saveFiles.length; i++) {
                    float y = startY - (i * spacing);
                    if (mousePos.x > 200 && mousePos.x < 1080 && mousePos.y < y + 15 && mousePos.y > y - 45) {
                        selectedIndex = i;
                        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                            selectingAction = true;
                            actionIndex = 0;
                        }
                    }
                }
            } else {
                // Hover/Click de ratón para botones
                float btnW = 340;
                float btnH = 100;
                float btnY = 100;
                float centerX = VIRTUAL_WIDTH / 2;
                float gap = 40;

                // Límites de botón Jugar
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
                // Límites de botón Borrar
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

        // Renderizado
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        if (background != null) {
            game.batch.draw(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }

        // Dibujar archivos de guardado centrados
        if (saveFiles.length == 0) {
            game.font.setColor(Color.GRAY);
            game.font.getData().setScale(2.5f); // Tamaño aumentado
            game.font.draw(game.batch, "No hay partidas guardadas.", 0, VIRTUAL_HEIGHT / 2, VIRTUAL_WIDTH,
                    com.badlogic.gdx.utils.Align.center, false);
        } else {
            game.font.getData().setScale(2.0f); // Tamaño aumentado de 1.2f a 2.0f
            float startY = VIRTUAL_HEIGHT / 2 + 100;
            float spacing = 60;

            for (int i = 0; i < saveFiles.length; i++) {
                String fullPath = saveFiles[i].nameWithoutExtension();
                String nameDisplay = "";

                // SOLUCIÓN ROBUSTA: Dividir por el separador estándar " - "
                // El formato esperado es "NombrePartida - NombreExplorador"
                if (fullPath.contains(" - ")) {
                    String[] parts = fullPath.split(" - ");
                    String gamePart = parts[0].trim();
                    String userPart = parts[1].trim();

                    // Limpiar cualquier metadato entre corchetes si existiera
                    if (userPart.contains("[")) {
                        userPart = userPart.split("\\[")[0].trim();
                    }
                    if (gamePart.contains("[")) {
                        gamePart = gamePart.split("\\[")[0].trim();
                    }

                    nameDisplay = gamePart + " - " + userPart;
                } else {
                    // Fallback si el formato no es el estándar, pero limpiando corchetes
                    nameDisplay = fullPath;
                    if (nameDisplay.contains(" [")) {
                        nameDisplay = nameDisplay.substring(0, nameDisplay.indexOf(" ["));
                    }
                }

                float y = startY - (i * spacing);

                if (i == selectedIndex) {
                    if (selectingAction)
                        game.font.setColor(Color.LIME);
                    else
                        game.font.setColor(Color.YELLOW);
                    nameDisplay = ">> " + nameDisplay + " <<";
                } else {
                    game.font.setColor(Color.WHITE);
                }

                game.font.draw(game.batch, nameDisplay, 0, y, VIRTUAL_WIDTH, com.badlogic.gdx.utils.Align.center,
                        false);
            }

            // Dibujar botones
            float btnW = 340;
            float btnH = 100;
            float btnY = 100;
            float centerX = VIRTUAL_WIDTH / 2;
            float gap = 40;

            // Botón Jugar
            Texture playTex = (selectingAction && actionIndex == 0) ? btnJugarSel : btnJugarNormal;
            if (playTex != null) {
                game.batch.draw(playTex, centerX - btnW - gap / 2, btnY, btnW, btnH);
            }

            // Botón Borrar
            Texture delTex = (selectingAction && actionIndex == 1) ? btnBorrarSel : btnBorrarNormal;
            if (delTex != null) {
                game.batch.draw(delTex, centerX + gap / 2, btnY, btnW, btnH);
            }
        }

        game.batch.end();
    }

    /**
     * Actualiza el viewport al redimensionar la ventana.
     * 
     * @param width  Nuevo ancho.
     * @param height Nuevo alto.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
    }

    /**
     * Libera las texturas cargadas.
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
