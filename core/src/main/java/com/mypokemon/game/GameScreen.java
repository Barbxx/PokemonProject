package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mypokemon.game.utils.BaseScreen;
import com.mypokemon.game.utils.TextureUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.badlogic.gdx.audio.Sound;

// Main Game Screen
public class GameScreen extends BaseScreen {

    private OrthographicCamera camera;
    private Viewport viewport;
    private com.badlogic.gdx.math.Matrix4 uiMatrix;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMapTileLayer collisionLayer;
    private int[] backgroundLayers;
    private int[] foregroundLayers;
    private int mapWidth;
    private int mapHeight;

    private float posX;
    private float posY;
    private float speed = 150f;

    private Texture playerSheet;
    private Animation<TextureRegion> walkDown, walkLeft, walkRight, walkUp;
    private TextureRegion currentFrame;
    private float stateTime;
    private boolean isMoving;
    private float playerWidth = 40f;
    private float playerHeight = 32f;
    private String playerName;

    private Explorador explorador;

    // NPC State
    private Texture feidSprite;
    private Texture harrySprite;
    private Texture brennerSprite; // New Brenner Sprite
    private Texture labSignTexture; // Lab Sign
    private Texture dialogIconTexture; // Feid default
    private Texture harryPortraitTexture; // Harry default
    private Texture brennerPortraitTexture; // New Brenner Portrait
    private Texture currentPortrait; // Active portrait
    private Texture dialogFrameTexture;
    private Texture uiWhitePixel;
    private float feidX, feidY;
    private float harryX, harryY;
    private float brennerX, brennerY; // New Brenner coords
    private boolean showDialog = false;
    private boolean isNearFeid = false;
    private boolean isNearHarry = false;
    private boolean isNearBrenner = false; // New Brenner flag
    private boolean isNearLab = false; // New Lab flag

    private float labSignX, labSignY; // Fixed Lab Sign coordinates

    // Laboratory Zone (Estimated relative to spawn)
    // Feid is at (posX - 220, posY - 20)
    // Lab appears to be to the right of Feid (near spawnX) and slightly below/level
    // We'll define a rectangle around spawnX, spawnY - 100
    private Rectangle labZone;
    private int currentDialogPage = 0;

    // Dynamic Dialog State
    private String activeNpcName = "";
    private String[] activeDialogPages;

    private String[] feidDialogPages = {
            "¡Epa! ¿Qué más pues, mor? Bienvenido a la región de Hisui.",
            "Vea, Hisui está una chimba pero la vuelta está peligrosa. Usted no puede andar por ahí normal sin con qué defenderse...",
            "¡En la hierba viven Pokémon que están es locos, mor! Usted necesita su propio Pokémon pa' que lo cuide.",
            "¡Hágale pues, arranque para mi laboratorio que allá lo espero!"
    };

    private String[] harryDialogPages = {
            "¡Expecto Patronum!... Rayos, aquí tampoco sale el ciervo.",
            "Definitivamente creo que ya no estoy en Hogwarts. Este lugar es más extraño que el Departamento de Misterios.",
            "Escucha, no sirve de nada detenerse en los sueños y olvidarse de vivir, así que ponte a trabajar.",
            "Para registrar a estos Pokémon necesito que subas su Nivel de Investigación a 10.",
            "Recuerda: +2 puntos si logras una captura exitosa usando una Poké Ball y +1 punto si los vences en lucha.",
            "¡Ánimo!, que no tengo un Giratiempo para repetir el día."
    };

    private String[] brennerDialogPages = {
            "Has llegado lejos, 'Once', o como sea que te llamen aquí.",
            "Has recolectado datos de 5 especies diferentes, un progreso fascinante. Pero ahora debes completar una última misión.",
            "El sujeto Arceus te espera en esta cavidad que me recuerda a la Cueva de Vecna; siento la misma oscuridad y el tic-tac de un reloj.",
            "Si logras vencerlo, tu investigación será completada instantáneamente.",
            "Recuerda: los errores tienen consecuencias, y si pierdes, el 'Upside Down' reclamará tus pertenencias."
    };

    // Menu State
    private boolean showMenu = false;
    private int menuSelectedIndex = 0;
    private String[] menuOptions = { "POKÉDEX", "MOCHILA", "GUARDAR", "OPCIONES", "SALIR" };

    // Intro Animation State
    private enum IntroState {
        SLIDING_IN, WAITING, SLIDING_OUT, FINISHED
    }

    private Texture introTexture;
    private IntroState introState;
    private float introY;
    private float introSpeed = 200f; // Pixels per second
    private Texture avisoTexture;

    // Game Constants
    private static final float ENCOUNTER_CHECK_INTERVAL = 1.0f;
    private static final float NOTIFICATION_DURATION = 3.0f;

    // Encounter State
    private float encounterTimer = 0;
    private int lastGrassTileX = -1;
    private int lastGrassTileY = -1;
    private boolean inEncounter = false;
    private Sound grassSound;

    // Resource Management
    private List<RecursoMapa> recursosMapa = new ArrayList<>();

    // Crafting and Notifications
    private enum CraftingState {
        CLOSED, SHOWING_RECIPES
    }

    private CraftingState craftingState = CraftingState.CLOSED;
    private String craftingRecipe = "";
    private String notificationMessage = "";
    private float notificationTimer = 0;
    private boolean showMissionComplete = false;
    private float missionCompleteTimer = 0;

    private int frameCols;
    private int frameRows;

    public GameScreen(final PokemonMain game, String texturePath, int cols, int rows, String playerName) {
        super(game);
        this.frameCols = cols;
        this.frameRows = rows;
        this.playerName = playerName;
        // Check for saved progress or create new
        this.explorador = Explorador.cargarProgreso(playerName);
        if (this.explorador == null) {
            this.explorador = new Explorador(playerName, 40); // Capacidad inicial aumentada
        }

        // Initialize Camera and Viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camera);
        uiMatrix = new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, 800, 480);

        // Load Intro Texture
        try {
            introTexture = new Texture(Gdx.files.internal("praderaObsidiana.png"));
            introState = IntroState.SLIDING_IN;
            // Start completely off-screen (above the viewport)
            introY = 480;
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Could not load praderaObsidiana.png", e);
            introState = IntroState.FINISHED;
        }

        // Load Aviso Texture
        try {
            avisoTexture = new Texture(Gdx.files.internal("Aviso.png"));
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Could not load Aviso.png", e);
        }

        // Load Sounds
        try {
            grassSound = Gdx.audio.newSound(Gdx.files.internal("hierba.mp3"));
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Could not load hierba.mp3");
        }

        // Load Map with explicit file resolver
        try {
            TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
            TmxMapLoader loader = new TmxMapLoader(new InternalFileHandleResolver());
            map = loader.load("Mapa_Hisui.tmx", params);
            // Use the game's shared batch for the map renderer
            mapRenderer = new OrthogonalTiledMapRenderer(map, game.batch);

            // Find collision layer (case insensitive)
            for (MapLayer layer : map.getLayers()) {
                if (layer instanceof TiledMapTileLayer && layer.getName().equalsIgnoreCase("Objetos_colisión")) {
                    collisionLayer = (TiledMapTileLayer) layer;
                    break;
                }
            }

            // Log map information for debugging
            Gdx.app.log("GameScreen", "Map loaded successfully with " + map.getLayers().getCount() + " layers");

            // Log tileset information
            for (TiledMapTileSet tileset : map.getTileSets()) {
                Gdx.app.log("GameScreen", "Tileset: " + tileset.getName() + " with " + tileset.size() + " tiles");
            }

            // Log all layers and identify indices
            for (int i = 0; i < map.getLayers().getCount(); i++) {
                MapLayer layer = map.getLayers().get(i);
                Gdx.app.log("GameScreen", "Layer " + i + ": " + layer.getName() +
                        " type: " + layer.getClass().getSimpleName() +
                        " visible: " + layer.isVisible());
            }

            // Get map dimensions safely
            mapWidth = getIntProperty(map.getProperties(), "width", 100) *
                    getIntProperty(map.getProperties(), "tilewidth", 32);
            mapHeight = getIntProperty(map.getProperties(), "height", 100) *
                    getIntProperty(map.getProperties(), "tileheight", 32);

            // Dynamically identify layers
            com.badlogic.gdx.utils.IntArray bgList = new com.badlogic.gdx.utils.IntArray();
            com.badlogic.gdx.utils.IntArray fgList = new com.badlogic.gdx.utils.IntArray();

            for (int i = 0; i < map.getLayers().getCount(); i++) {
                MapLayer layer = map.getLayers().get(i);
                Gdx.app.log("GameScreen", "Layer " + i + ": " + layer.getName() +
                        " type: " + layer.getClass().getSimpleName() +
                        " visible: " + layer.isVisible());

                if (layer instanceof TiledMapTileLayer) {
                    String name = layer.getName().toLowerCase();
                    if (name.contains("superior") || name.contains("objeto") && i > 2) {
                        fgList.add(i);
                    } else {
                        bgList.add(i);
                    }
                }
            }
            backgroundLayers = bgList.toArray();
            foregroundLayers = fgList.toArray();

            Gdx.app.log("GameScreen", "Categorized " + backgroundLayers.length + " background layers and " +
                    foregroundLayers.length + " foreground layers");

            // Get spawn point from Spawn_Player object layer
            MapLayer spawnLayer = null;
            for (MapLayer l : map.getLayers()) {
                if (l.getName().equalsIgnoreCase("Spawn_Player")) {
                    spawnLayer = l;
                    break;
                }
            }

            if (spawnLayer != null) {
                MapObjects objects = spawnLayer.getObjects();
                Gdx.app.log("GameScreen", "Spawn_Player layer found with " + objects.getCount() + " objects");
                for (MapObject obj : objects) {
                    if ("inicio".equals(obj.getName()) && obj instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                        posX = rect.x + rect.width / 2;
                        posY = rect.y + rect.height / 2;
                        Gdx.app.log("GameScreen", "Spawn point found at: " + posX + ", " + posY);
                        break;
                    }
                }
            } else {
                Gdx.app.log("GameScreen", "Spawn_Player layer not found, using fallback position");
                posX = mapWidth / 2;
                posY = mapHeight / 2;
            }
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Critical: Could not load Mapa_Hisui.tmx", e);
            // Fallback position on error
            posX = 1600;
            posY = 1600;
            mapWidth = 3200;
            mapHeight = 3200;
        }

        // Scan for collectable resources in the collision layer
        if (collisionLayer != null) {
            for (int y = 0; y < collisionLayer.getHeight(); y++) {
                for (int x = 0; x < collisionLayer.getWidth(); x++) {
                    TiledMapTileLayer.Cell cell = collisionLayer.getCell(x, y);
                    if (cell != null && cell.getTile() != null) {
                        Object tipoRecurso = cell.getTile().getProperties().get("TipoRecurso");
                        if (tipoRecurso != null) {
                            String tipo = tipoRecurso.toString();
                            Object cantObj = cell.getTile().getProperties().get("Cantidad");
                            int cantidad = 1;
                            if (cantObj != null) {
                                if (cantObj instanceof Integer) {
                                    cantidad = (Integer) cantObj;
                                } else {
                                    try {
                                        cantidad = Integer.parseInt(cantObj.toString());
                                    } catch (NumberFormatException e) {
                                        cantidad = 1;
                                    }
                                }
                            }
                            RecursoMapa recurso = new RecursoMapa(x, y, tipo, cantidad);
                            // Find and store cells from resource layers, EXCEPT background and foreground
                            // decorations
                            for (com.badlogic.gdx.maps.MapLayer layer : map.getLayers()) {
                                if (layer instanceof TiledMapTileLayer) {
                                    String layerName = layer.getName().toLowerCase();
                                    // Skip background layer and superior objects layer to preserve them
                                    if (layerName.contains("suelo_fondo") || layerName.contains("objetos_superiores")) {
                                        continue;
                                    }
                                    TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
                                    TiledMapTileLayer.Cell layerCell = tileLayer.getCell(x, y);
                                    if (layerCell != null) {
                                        recurso.registrarCapa(tileLayer, layerCell);
                                    }
                                }
                            }
                            recursosMapa.add(recurso);
                            Gdx.app.log("GameScreen",
                                    "Found resource: " + tipo + " (qty: " + cantidad + ") at " + x + "," + y);
                        }
                    }
                }
            }
        }

        // Set camera to player spawn position
        camera.position.set(posX, posY, 10);
        camera.update();

        boolean assetsLoaded = false;
        try {
            playerSheet = new Texture(texturePath);
            playerSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            assetsLoaded = true;
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Critical: Could not load " + texturePath, e);
        }

        if (assetsLoaded) {
            try {
                int frameWidth = playerSheet.getWidth() / cols;
                int frameHeight = playerSheet.getHeight() / rows;
                TextureRegion[][] frames = TextureRegion.split(playerSheet, frameWidth, frameHeight);

                if (frames.length >= 4) {
                    walkDown = new Animation<>(0.15f, frames[0]);
                    walkLeft = new Animation<>(0.15f, frames[1]);
                    walkRight = new Animation<>(0.15f, frames[2]);
                    walkUp = new Animation<>(0.15f, frames[3]);
                    currentFrame = frames[0][0];
                } else {
                    createFallback();
                }
            } catch (Exception e) {
                Gdx.app.log("GameScreen", "Error splitting texture", e);
                createFallback();
            }
        } else {
            createFallback();
        }

        // Initialize NPC and UI Assets
        try {
            feidSprite = new Texture(Gdx.files.internal("feidSprite.png"));
            harrySprite = new Texture(Gdx.files.internal("harrySprite.png"));
            brennerSprite = new Texture(Gdx.files.internal("drBrennerSprite.png")); // Load Brenner sprite for map
            labSignTexture = new Texture(Gdx.files.internal("letreroLaboratorio.png")); // Load Lab Sign
            dialogIconTexture = new Texture(Gdx.files.internal("ferxxoCientifico.png"));
            harryPortraitTexture = new Texture(Gdx.files.internal("harry.png"));
            brennerPortraitTexture = new Texture(Gdx.files.internal("drBrenner.png")); // Load Brenner portrait for
                                                                                       // dialog
                                                                                       // dialog
            dialogFrameTexture = new Texture(Gdx.files.internal("marcoDialogo.png"));
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "NPC/UI textures not found", e);
        }

        // Create UI utilities
        uiWhitePixel = TextureUtils.createSolidTexture(1, 1, Color.WHITE);

        // Place NPC next to the blue house (approximate coordinates from player spawn)
        feidX = posX - 220;
        feidY = posY - 20;

        // Place Harry much further to the right and up
        harryX = posX + 1600;
        harryY = posY + 250;

        // Place Brenner (distinct location)
        brennerX = posX + 480; // Somewhere in between
        brennerY = posY + 600; // Further down

        // Initialize Lab Zone
        // Assuming Lab door is roughly at spawnX, spawnY - 140
        float labDoorX = posX - 120;
        float labDoorY = posY - 185;
        labZone = new Rectangle(labDoorX, labDoorY, 80, 50);

        // Initialize Lab Sign Position (Fixed)
        labSignX = posX - 45;
        labSignY = posY - 125;

        // Initialize UI projection matrix

        // Initialize UI projection matrix
        uiMatrix = new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, 800, 480);
        game.font.setUseIntegerPositions(true);
    }

    // Fade State
    private float fadeAlpha = 1f;
    private boolean fadingIn = true;
    private boolean fadingOut = false;
    private BaseScreen nextScreen; // Screen to switch to after fade

    // ... (keep class definition)

    // In Constructor or Init
    // ...
    // fadeAlpha = 1f;
    // fadingIn = true;

    @Override
    public void show() {
        inEncounter = false;
        // Register InputHandler
        if (explorador != null) {
            InputHandler inputHandler = new InputHandler(this.explorador);
            Gdx.input.setInputProcessor(inputHandler);
        }

        // Reset Fade for when returning to this screen
        fadingIn = true;
        fadingOut = false;
        fadeAlpha = 1f;
        nextScreen = null;
    }

    // ...

    @Override
    public void render(float delta) {
        // --- 1. UPDATE AND LOGIC ---

        // FADE IN LOGIC
        if (fadingIn) {
            fadeAlpha -= delta * 1.5f;
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
                if (nextScreen != null) {
                    game.setScreen(nextScreen);
                }
            }
        }

        // Menu Toggle
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            showMenu = !showMenu;
            if (showMenu)
                showDialog = false;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            game.setScreen(new PokedexScreen(game, this, explorador));
        }

        if (showMenu) {
            // Menu Navigation
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                menuSelectedIndex--;
                if (menuSelectedIndex < 0)
                    menuSelectedIndex = menuOptions.length - 1;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                menuSelectedIndex++;
                if (menuSelectedIndex >= menuOptions.length)
                    menuSelectedIndex = 0;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                String selected = menuOptions[menuSelectedIndex];
                if (selected.equals("SALIR")) {
                    Gdx.app.exit();
                } else if (selected.equals("MOCHILA")) {
                    game.setScreen(new MochilaScreen(game, this));
                } else if (selected.equals("CRAFTEO")) {
                    game.setScreen(new CrafteoScreen(game, this));
                } else if (selected.equals("POKÉDEX")) {
                    game.setScreen(new PokedexScreen(game, this, explorador));
                }
                showMenu = false;
            }
            isMoving = false;
        } else {
            // NORMAL GAMEPLAY (Not in Menu)
            isMoving = false;

            // Handle Input for Movement (only if not showing instructions)
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                Gdx.app.exit();
            }

            // Only move if not fading out and intro finished
            if (introState != IntroState.SLIDING_IN && !fadingOut) {
                float moveX = 0;
                float moveY = 0;
                if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))
                    moveX = -1;
                if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))
                    moveX = 1;
                if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))
                    moveY = 1;
                if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))
                    moveY = -1;

                if (moveX != 0 || moveY != 0) {
                    isMoving = true;
                    float oldX = posX;
                    float oldY = posY;

                    posX += moveX * speed * delta;
                    posY += moveY * speed * delta;

                    // Animation Update
                    if (moveX < 0)
                        currentFrame = walkLeft.getKeyFrame(stateTime, true);
                    else if (moveX > 0)
                        currentFrame = walkRight.getKeyFrame(stateTime, true);
                    else if (moveY > 0)
                        currentFrame = walkUp.getKeyFrame(stateTime, true);
                    else if (moveY < 0)
                        currentFrame = walkDown.getKeyFrame(stateTime, true);

                    // Collision
                    if (isColliding(posX, posY)) {
                        // Simple collision resolve
                        if (!isColliding(posX, oldY)) {
                            posY = oldY;
                        } else if (!isColliding(oldX, posY)) {
                            posX = oldX;
                        } else {
                            posX = oldX;
                            posY = oldY;
                        }
                    }
                    stateTime += delta;
                } else {
                    stateTime = 0;
                }
            }

            // Intro Logic
            float introH = (introTexture != null ? introTexture.getHeight() * 0.5f : 0);
            if (introState == IntroState.SLIDING_IN) {
                float targetY = 480 - introH;
                if (introY > targetY)
                    introY -= introSpeed * delta;
                else {
                    introY = targetY;
                    introState = IntroState.WAITING;
                }
            } else if (introState == IntroState.WAITING) {
                if (isMoving)
                    introState = IntroState.SLIDING_OUT;
            } else if (introState == IntroState.SLIDING_OUT) {
                if (introY < 480)
                    introY += introSpeed * delta;
                else
                    introState = IntroState.FINISHED;
            }

            // Lab Entrance Check
            isNearLab = false;
            if (labZone != null && labZone.contains(posX, posY)) {
                isNearLab = true;
                if (!fadingOut && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                    fadingOut = true;
                    // Pre-create screen
                    nextScreen = new LaboratorioScreen(game, this);
                }
            }

            // NPC Interaction Input
            // Toggle dialog on interaction key (E)
            if (Gdx.input.isKeyJustPressed(Input.Keys.E) && !showMenu) {
                if (isNearFeid) {
                    if (!showDialog) {
                        showDialog = true;
                        currentDialogPage = 0;
                        activeNpcName = "Profesor Ferxxo";
                        activeDialogPages = feidDialogPages;
                        currentPortrait = dialogIconTexture;
                    } else {
                        showDialog = false;
                    }
                } else if (isNearHarry) {
                    if (!showDialog) {
                        showDialog = true;
                        currentDialogPage = 0;
                        activeNpcName = "General Harry Potter";
                        activeDialogPages = harryDialogPages;
                        currentPortrait = harryPortraitTexture;
                    } else {
                        showDialog = false;
                    }
                } else if (isNearBrenner) {
                    if (!showDialog) {
                        showDialog = true;
                        currentDialogPage = 0;
                        activeNpcName = "Dr. Martin Brenner";
                        activeDialogPages = brennerDialogPages;
                        currentPortrait = brennerPortraitTexture;
                    } else {
                        showDialog = false;
                    }
                }
            }

            // Resource Collection
            if (!fadingOut && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                com.badlogic.gdx.math.Vector3 mousePos = new com.badlogic.gdx.math.Vector3(Gdx.input.getX(),
                        Gdx.input.getY(), 0);
                viewport.unproject(mousePos);

                float tileW = (collisionLayer != null) ? collisionLayer.getTileWidth() : 32;
                float tileH = (collisionLayer != null) ? collisionLayer.getTileHeight() : 32;

                for (RecursoMapa r : recursosMapa) {
                    if (!r.recolectado) {
                        float rx = r.cellX * tileW + tileW / 2;
                        float ry = r.cellY * tileH + tileH / 2;
                        if (com.badlogic.gdx.math.Vector2.dst(posX, posY, rx, ry) < 60f &&
                                mousePos.x >= rx - tileW / 2 && mousePos.x <= rx + tileW / 2 &&
                                mousePos.y >= ry - tileH / 2 && mousePos.y <= ry + tileH / 2) {
                            if (explorador.getMochila().recolectarRecurso(r.tipo, r.cantidad)) {
                                r.recolectado = true;
                                r.timerRespawn = r.TIEMPO_RESPAWN;
                                // Remove from layers
                                for (TiledMapTileLayer layer : r.cellsPorCapa.keySet())
                                    layer.setCell(r.cellX, r.cellY, null);
                                notificationMessage = "Recogiste " + r.tipo;
                                notificationTimer = NOTIFICATION_DURATION;
                            } else {
                                notificationMessage = "Mochila llena";
                                notificationTimer = NOTIFICATION_DURATION;
                            }
                            break;
                        }
                    }
                }
            }

            // Crafting disabled as requested
            /*
             * if (Gdx.input.isKeyJustPressed(Input.Keys.C) && craftingState ==
             * CraftingState.CLOSED) {
             * int plantas = explorador.getMochila().getPlantas();
             * int guijarros = explorador.getMochila().getGuijarros();
             * if (plantas == 0 && guijarros == 0) {
             * notificationMessage = "Inventario vacío";
             * notificationTimer = NOTIFICATION_DURATION;
             * } else {
             * craftingState = CraftingState.SHOWING_RECIPES;
             * craftingRecipe = "2 Plantas + 3 Guijarros = 1 Poké Ball";
             * }
             * }
             */

            // Encounter Logic
            if (!inEncounter && !showDialog) {
                encounterTimer += delta;
                if (encounterTimer >= ENCOUNTER_CHECK_INTERVAL) {
                    encounterTimer = 0;
                    int tileW = (collisionLayer != null) ? (int) collisionLayer.getTileWidth() : 32;
                    int tileH = (collisionLayer != null) ? (int) collisionLayer.getTileHeight() : 32;
                    int playerTileX = (int) (posX / tileW);
                    int playerTileY = (int) (posY / tileH);

                    boolean foundGrass = false;
                    int nivelDificultad = 0;
                    if (map != null) {
                        for (com.badlogic.gdx.maps.MapLayer layer : map.getLayers()) {
                            if (layer instanceof TiledMapTileLayer) {
                                TiledMapTileLayer.Cell cell = ((TiledMapTileLayer) layer).getCell(playerTileX,
                                        playerTileY);
                                if (cell != null && cell.getTile() != null) {
                                    Object zona = cell.getTile().getProperties().get("ZonaEncuentro");
                                    if (zona != null) {
                                        foundGrass = true;
                                        Object nd = cell.getTile().getProperties().get("NivelDificultad");
                                        if (nd instanceof Integer)
                                            nivelDificultad = (Integer) nd;
                                        else
                                            nivelDificultad = 1;

                                        if (playerTileX != lastGrassTileX || playerTileY != lastGrassTileY) {
                                            if (grassSound != null)
                                                grassSound.play(0.5f);
                                            lastGrassTileX = playerTileX;
                                            lastGrassTileY = playerTileY;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        if (foundGrass && nivelDificultad >= 1 && nivelDificultad <= 5) {
                            if (GestorEncuentros.verificarEncuentro(nivelDificultad)) {
                                inEncounter = true;
                                String pName = GestorEncuentros.obtenerPokemonAleatorio(nivelDificultad);
                                Gdx.app.log("GameScreen", "Encounter: " + pName);
                                Pokemon salvaje = new Pokemon(pName, 0, 0, false, "Normal");
                                salvaje.agregarMovimiento(new Movimiento("Tackle", 0, "Normal", 40));
                                game.setScreen(new BattleScreen(game, this, explorador, salvaje));
                            }
                        }
                        if (!foundGrass) {
                            lastGrassTileX = -1;
                            lastGrassTileY = -1;
                        }
                    }
                }
            }
        } // End !showMenu

        // Resource Respawn Logic (Always run)
        // ...
        for (RecursoMapa r : recursosMapa) {
            if (r.recolectado) {
                r.timerRespawn -= delta;
                if (r.timerRespawn <= 0) {
                    r.recolectado = false;
                    for (java.util.Map.Entry<TiledMapTileLayer, TiledMapTileLayer.Cell> e : r.cellsPorCapa.entrySet())
                        e.getKey().setCell(r.cellX, r.cellY, e.getValue());
                }
            }
        }

        // Crafting Input
        if (craftingState == CraftingState.SHOWING_RECIPES) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                if (explorador.getMochila().fabricarPokeBall()) {
                    notificationMessage = "¡Poké Ball creada!";
                    notificationTimer = NOTIFICATION_DURATION;
                    if (explorador.getMochila().getPokeBalls() == 1) {
                        showMissionComplete = true;
                        missionCompleteTimer = 6.0f;
                    }
                } else {
                    notificationMessage = "Materiales insuficientes";
                    notificationTimer = NOTIFICATION_DURATION;
                }
                craftingState = CraftingState.CLOSED;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
                craftingState = CraftingState.CLOSED;
            }
        }

        // Dialog Advance
        if (showDialog && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            currentDialogPage++;
            if (activeDialogPages != null && currentDialogPage >= activeDialogPages.length) {
                showDialog = false;
                currentDialogPage = 0;
            }
        }

        // NPC Range Check for Dialog Closing
        // ...
        float distFeid = com.badlogic.gdx.math.Vector2.dst(posX, posY, feidX, feidY);
        isNearFeid = distFeid < 80;
        float distHarry = com.badlogic.gdx.math.Vector2.dst(posX, posY, harryX, harryY);
        isNearHarry = distHarry < 80;
        float distBrenner = com.badlogic.gdx.math.Vector2.dst(posX, posY, brennerX, brennerY);
        isNearBrenner = distBrenner < 80;

        if (!isNearFeid && !isNearHarry && !isNearBrenner) {
            showDialog = false;
        }

        // Timers
        if (notificationTimer > 0)
            notificationTimer -= delta;
        if (missionCompleteTimer > 0) {
            missionCompleteTimer -= delta;
            if (missionCompleteTimer <= 0)
                showMissionComplete = false;
        }

        // --- RENDER ---

        // Camera Clamp & Zoom
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            if (camera.zoom == 1.0f)
                camera.zoom = 2.0f;
            else if (camera.zoom == 2.0f)
                camera.zoom = (float) mapHeight / viewport.getWorldHeight();
            else
                camera.zoom = 1.0f;
        }
        // Clamp
        float camX = posX, camY = posY;
        float hw = viewport.getWorldWidth() / 2, hh = viewport.getWorldHeight() / 2;
        if (camX < hw)
            camX = hw;
        if (camX > mapWidth - hw)
            camX = mapWidth - hw;
        if (camY < hh)
            camY = hh;
        if (camY > mapHeight - hh)
            camY = mapHeight - hh;
        camera.position.set(camX, camY, 0);
        camera.update();

        viewport.apply();
        ScreenUtils.clear(Color.BLACK);

        if (mapRenderer != null) {
            mapRenderer.setView(camera);
            if (backgroundLayers != null && backgroundLayers.length > 0)
                mapRenderer.render(backgroundLayers);
            else
                mapRenderer.render();
        }

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        drawNPC();
        drawPlayer();
        drawBrenner();
        game.batch.end();

        if (mapRenderer != null && foregroundLayers != null)
            mapRenderer.render(foregroundLayers);

        game.batch.begin();
        drawLabSign();
        game.batch.end();

        // UI
        game.batch.setProjectionMatrix(uiMatrix);
        game.batch.begin();

        // Intro
        if (introState != IntroState.FINISHED && introTexture != null) {
            float introScale = 0.5f;
            game.batch.draw(introTexture, 0, introY, introTexture.getWidth() * introScale,
                    introTexture.getHeight() * introScale);
        }

        // Debug
        /*
         * game.font.setColor(Color.RED);
         * game.font.getData().setScale(0.8f);
         * game.font.draw(game.batch, "X: " + (int) posX + " Y: " + (int) posY, 10,
         * 470);
         * game.font.setColor(Color.WHITE);
         * game.font.getData().setScale(1.0f);
         */

        // HUD
        if (explorador != null) {
            game.font.setColor(Color.WHITE);
            float hudX = 780;
            game.font.draw(game.batch, "EXPLORADOR: " + explorador.getNombre(), hudX, 460, 0,
                    com.badlogic.gdx.utils.Align.right, false);
            game.font.draw(game.batch, "--- INVENTARIO ---", hudX, 430, 0, com.badlogic.gdx.utils.Align.right, false);
            game.font.draw(game.batch, "Plantas: " + explorador.getMochila().getPlantas(), hudX, 410, 0,
                    com.badlogic.gdx.utils.Align.right, false);
            game.font.draw(game.batch, "Guijarros: " + explorador.getMochila().getGuijarros(), hudX, 390, 0,
                    com.badlogic.gdx.utils.Align.right, false);
            game.font.draw(game.batch, "Poké Balls: " + explorador.getMochila().getPokeBalls(), hudX, 370, 0,
                    com.badlogic.gdx.utils.Align.right, false);
            game.font.draw(game.batch,
                    "Carga: " + explorador.getMochila().getEspacioOcupado() + "/"
                            + explorador.getMochila().getCapacidadMaxima(),
                    hudX, 340, 0, com.badlogic.gdx.utils.Align.right, false);
            game.font.draw(game.batch, "[I] MENU   [CLICK]   [ESC] SALIR", hudX, 40, 0,
                    com.badlogic.gdx.utils.Align.right, false);
        }

        // Notifications
        if (notificationTimer > 0) {
            game.font.setColor(Color.YELLOW);
            game.font.draw(game.batch, notificationMessage, 400, 400, 0, com.badlogic.gdx.utils.Align.center, false);
            game.font.setColor(Color.WHITE);
        }

        // Mission
        if (showMissionComplete) {
            if (avisoTexture != null) {
                float targetWidth = 300f;
                float scale = targetWidth / avisoTexture.getWidth();
                float targetHeight = avisoTexture.getHeight() * scale;
                game.batch.draw(avisoTexture, (800 - targetWidth) / 2, (480 - targetHeight) / 2, targetWidth,
                        targetHeight);
            } else {
                game.font.setColor(Color.GREEN);
                game.font.draw(game.batch, "¡MISIÓN COMPLETADA!", 400, 350, 0, com.badlogic.gdx.utils.Align.center,
                        false);
                game.font.setColor(Color.WHITE);
            }
        }

        // NPC Hints
        if (isNearFeid && !showDialog) {
            game.font.getData().setScale(0.8f);
            game.font.setColor(Color.YELLOW);
            game.font.draw(game.batch, "Presiona [E] para hablar con Feid", 300, 100);
            game.font.getData().setScale(1.0f);
            game.font.setColor(Color.WHITE);
        } else if (isNearHarry && !showDialog) {
            game.font.getData().setScale(0.8f);
            game.font.setColor(Color.YELLOW);
            game.font.draw(game.batch, "Presiona [E] para hablar con Harry", 300, 100);
            game.font.getData().setScale(1.0f);
            game.font.setColor(Color.WHITE);
        } else if (isNearBrenner && !showDialog) {
            game.font.getData().setScale(0.8f);
            game.font.setColor(Color.YELLOW);
            game.font.draw(game.batch, "Presiona [E] para hablar con Dr. Brenner", 300, 100);
            game.font.getData().setScale(1.0f);
            game.font.setColor(Color.WHITE);
        } else if (isNearLab && !showDialog) {
            game.font.getData().setScale(0.8f);
            game.font.setColor(Color.YELLOW);
            game.font.draw(game.batch, "Presiona [E] para entrar", 300, 100);
            game.font.getData().setScale(1.0f);
            game.font.setColor(Color.WHITE);
        }

        // Dialog
        if (showDialog && activeDialogPages != null && currentDialogPage < activeDialogPages.length) {
            float screenW = 800;
            float dialogHeight = 110;
            float portraitSize = 250;
            if (currentPortrait == harryPortraitTexture)
                portraitSize = 320;
            if (currentPortrait != null)
                game.batch.draw(currentPortrait, screenW - portraitSize - 20, dialogHeight - 20, portraitSize,
                        portraitSize);

            // Draw Box
            game.batch.setColor(Color.DARK_GRAY);
            if (uiWhitePixel != null)
                game.batch.draw(uiWhitePixel, 20, 20, screenW - 40, dialogHeight);
            game.batch.setColor(Color.WHITE);
            if (uiWhitePixel != null)
                game.batch.draw(uiWhitePixel, 23, 23, screenW - 46, dialogHeight - 6);
            // Name Tag
            float nameTagY = dialogHeight + 10;
            game.batch.setColor(Color.DARK_GRAY);
            if (uiWhitePixel != null)
                game.batch.draw(uiWhitePixel, 45, nameTagY, 200, 35);
            game.batch.setColor(Color.WHITE);
            if (uiWhitePixel != null)
                game.batch.draw(uiWhitePixel, 47, nameTagY + 2, 196, 31);

            game.batch.setColor(Color.WHITE);
            game.font.setColor(Color.BLACK);
            game.font.getData().setScale(0.9f);
            game.font.draw(game.batch, activeNpcName, 55, nameTagY + 25);
            game.font.setColor(Color.BLACK);
            game.font.getData().setScale(0.85f);
            game.font.draw(game.batch, activeDialogPages[currentDialogPage], 45, dialogHeight - 10, screenW - 90,
                    com.badlogic.gdx.utils.Align.left, true);
            game.font.getData().setScale(1.0f);
            game.font.setColor(Color.WHITE);
            String hint = (currentDialogPage < activeDialogPages.length - 1) ? "SIGUIENTE (ENTER)" : "CERRAR (ENTER)";
            game.font.getData().setScale(0.6f);
            game.font.draw(game.batch, hint, 45, 50);
            game.font.getData().setScale(1.0f);
        }

        // CRAFTING UI
        if (craftingState == CraftingState.SHOWING_RECIPES) {
            game.font.setColor(Color.CYAN);
            game.font.draw(game.batch, craftingRecipe, 400, 250, 0, com.badlogic.gdx.utils.Align.center, false);
            game.font.draw(game.batch, "¿Fabricar? [S/N]", 400, 220, 0, com.badlogic.gdx.utils.Align.center, false);
            game.font.setColor(Color.WHITE);
        }

        // Menu
        if (showMenu) {
            drawMenu();
        }

        // FADE OVERLAY
        if (fadeAlpha > 0 && uiWhitePixel != null) {
            game.batch.end();
            game.batch.begin();
            Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                    com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
            game.batch.setColor(0, 0, 0, fadeAlpha);
            game.batch.draw(uiWhitePixel, 0, 0, 800, 480);
            game.batch.setColor(Color.WHITE);
        }

        game.batch.end();
    }

    private void drawMenu() {
        float menuW = 180;
        float menuH = 260;
        float menuX = 800 - menuW - 20;
        float menuY = 480 - menuH - 20;
        float borderSize = 4;

        // Draw Menu Border (Reddish / Orange)
        game.batch.setColor(new Color(0.8f, 0.2f, 0.1f, 1f));
        if (uiWhitePixel != null) {
            game.batch.draw(uiWhitePixel, menuX, menuY, menuW, menuH);
        }

        // Draw Menu Background (White)
        game.batch.setColor(Color.WHITE);
        if (uiWhitePixel != null) {
            game.batch.draw(uiWhitePixel, menuX + borderSize, menuY + borderSize, menuW - borderSize * 2,
                    menuH - borderSize * 2);
        }

        // Draw Options
        game.font.setColor(Color.DARK_GRAY);
        game.font.getData().setScale(0.85f);
        float startY = menuY + menuH - 40;
        float spacing = 35;

        for (int i = 0; i < menuOptions.length; i++) {
            float optY = startY - (i * spacing);
            game.font.draw(game.batch, menuOptions[i], menuX + 45, optY);

            // Draw Selection Arrow
            if (i == menuSelectedIndex) {
                // Simplified triangle using character or just a small circle/rect for now
                // Let's use a small arrow symbol if possible or geometric shape
                game.font.draw(game.batch, ">", menuX + 20, optY);
            }
        }

        game.font.getData().setScale(1.0f);
        game.font.setColor(Color.WHITE);
        game.batch.setColor(Color.WHITE); // Reset Batch color
    }

    private void drawPlayer() {
        if (currentFrame != null) {
            game.batch.draw(currentFrame, posX - playerWidth / 2, posY - playerHeight / 2, playerWidth,
                    playerHeight * 1.2f);

            // Draw player name above the sprite
            if (playerName != null && !playerName.isEmpty()) {
                game.font.getData().setScale(0.8f);
                game.font.setColor(Color.WHITE);
                game.font.draw(game.batch, playerName, posX - playerWidth / 2, posY + playerHeight * 0.8f, playerWidth,
                        com.badlogic.gdx.utils.Align.center, false);
            }
        }
    }

    private void drawNPC() {
        if (feidSprite != null) {
            game.batch.draw(feidSprite, feidX, feidY, 25, 35);
        }
        if (harrySprite != null) {
            game.batch.draw(harrySprite, harryX, harryY, 25, 35);
        }
    }

    private void drawBrenner() {
        if (brennerSprite != null) {
            game.batch.draw(brennerSprite, brennerX, brennerY, 25, 35);
        }
    }

    private void drawLabSign() {
        // Draw Lab Sign
        if (labSignTexture != null) {
            game.batch.draw(labSignTexture, labSignX, labSignY, 80, 53);
        }
    }

    private int getIntProperty(com.badlogic.gdx.maps.MapProperties props, String key, int defaultValue) {
        Object val = props.get(key);
        if (val instanceof Integer)
            return (Integer) val;
        if (val instanceof Float)
            return ((Float) val).intValue();
        if (val instanceof String) {
            try {
                return Integer.parseInt((String) val);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private boolean isColliding(float x, float y) {
        // Check collision with Feid NPC (25x35 size)
        float feidW = 25;
        float feidH = 35;

        // Define Feid's bounding box
        float feidLeft = feidX;
        float feidRight = feidX + feidW;
        float feidBottom = feidY;
        float feidTop = feidY + feidH;

        float harryLeft = harryX;
        float harryRight = harryX + feidW; // Assuming same size
        float harryBottom = harryY;
        float harryTop = harryY + feidH;

        float brennerLeft = brennerX;
        float brennerRight = brennerX + feidW;
        float brennerBottom = brennerY;
        float brennerTop = brennerY + feidH;

        if (collisionLayer == null)
            return false;

        // Check bottom edge and sides of player base
        float[][] points = {
                { x - playerWidth / 3, y - playerHeight / 2 },
                { x + playerWidth / 3, y - playerHeight / 2 },
                { x - playerWidth / 3, y - playerHeight / 4 },
                { x + playerWidth / 3, y - playerHeight / 4 }
        };

        for (float[] p : points) {
            // Check NPC collision
            if (p[0] >= feidLeft && p[0] <= feidRight && p[1] >= feidBottom && p[1] <= feidTop) {
                return true;
            }
            if (p[0] >= harryLeft && p[0] <= harryRight && p[1] >= harryBottom && p[1] <= harryTop) {
                return true;
            }
            if (p[0] >= brennerLeft && p[0] <= brennerRight && p[1] >= brennerBottom && p[1] <= brennerTop) {
                return true;
            }

            int cellX = (int) (p[0] / collisionLayer.getTileWidth());
            int cellY = (int) (p[1] / collisionLayer.getTileHeight());

            TiledMapTileLayer.Cell cell = collisionLayer.getCell(cellX, cellY);
            if (cell != null && cell.getTile() != null) {
                // Check if the tile has a "Colision" property set (as String or Boolean)
                Object col = cell.getTile().getProperties().get("Colision");
                if (col != null) {
                    // Only block if Colision is explicitly true
                    if (col instanceof Boolean && (Boolean) col)
                        return true;
                    if (col instanceof String && "true".equalsIgnoreCase((String) col))
                        return true;
                    // If Colision is false or any other value, allow movement
                    return false;
                }
                // If no Colision property, don't block (allow grass with nivel to be walkable)
                return false;
            }
        }

        return false;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        if (playerSheet != null)
            playerSheet.dispose();
        if (introTexture != null)
            introTexture.dispose();
        if (avisoTexture != null)
            avisoTexture.dispose();
        if (feidSprite != null)
            feidSprite.dispose();
        if (harrySprite != null)
            harrySprite.dispose();
        if (brennerSprite != null)
            brennerSprite.dispose();
        if (labSignTexture != null)
            labSignTexture.dispose();
        if (dialogIconTexture != null)
            dialogIconTexture.dispose();
        if (harryPortraitTexture != null)
            harryPortraitTexture.dispose();
        if (brennerPortraitTexture != null)
            brennerPortraitTexture.dispose();
        if (dialogFrameTexture != null)
            dialogFrameTexture.dispose();
        if (uiWhitePixel != null)
            uiWhitePixel.dispose();
        if (mapRenderer != null)
            mapRenderer.dispose();
        if (grassSound != null)
            grassSound.dispose();
    }

    // Inner class for map resources
    private static class RecursoMapa {
        int cellX, cellY;
        String tipo;
        int cantidad;
        boolean recolectado = false;
        float timerRespawn = 0;
        final float TIEMPO_RESPAWN = 120.0f;
        Map<TiledMapTileLayer, TiledMapTileLayer.Cell> cellsPorCapa = new HashMap<>();

        public RecursoMapa(int x, int y, String tipo, int cantidad) {
            this.cellX = x;
            this.cellY = y;
            this.tipo = tipo;
            this.cantidad = cantidad;
        }

        public void registrarCapa(TiledMapTileLayer layer, TiledMapTileLayer.Cell cell) {
            cellsPorCapa.put(layer, cell);
        }
    }

    // Getter for Explorador
    public Explorador getExplorador() {
        return explorador;
    }

    public Texture getPlayerSheet() {
        return playerSheet;
    }

    public int getFrameCols() {
        return frameCols;
    }

    public int getFrameRows() {
        return frameRows;
    }

    private void createFallback() {
        // Create a simple magenta square as fallback
        playerSheet = TextureUtils.createSolidTexture(239, 256, Color.MAGENTA);
        TextureRegion fallbackRegion = new TextureRegion(playerSheet);
        TextureRegion[] fallbackArray = new TextureRegion[] { fallbackRegion };
        walkDown = new Animation<>(0.15f, fallbackArray);
        walkLeft = new Animation<>(0.15f, fallbackArray);
        walkRight = new Animation<>(0.15f, fallbackArray);
        walkUp = new Animation<>(0.15f, fallbackArray);
        currentFrame = fallbackRegion;
    }
}
