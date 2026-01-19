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

    // Network
    private com.mypokemon.game.client.NetworkClient client;
    private RemotePlayer otherPlayer;
    private float moveUpdateTimer = 0;
    private String myTexturePath;

    // NPC Management (Refactored)
    private com.mypokemon.game.objects.NPCManager npcManager;

    // UI Management (Refactored)
    private com.mypokemon.game.ui.GameUI gameUI;

    private Texture labSignTexture; // Lab Sign might belong to map objects eventually
    private Texture currentPortrait;
    private Texture uiWhitePixel; // Keep for now if used elsewhere or move to UI
    private boolean showDialog = false;

    // Proximity flags REMOVED (Handled by NPCManager)
    private boolean isNearLab = false; // Keep Lab logic separate for now

    private float labSignX, labSignY; // Fixed Lab Sign coordinates

    // Laboratory Zone (Estimated relative to spawn)
    private Rectangle labZone;
    private int currentDialogPage = 0;

    // Dynamic Dialog State
    private String activeNpcName = "";
    private String[] activeDialogPages;
    // actually, let's keep currentPortrait to pass to GameUI.renderDialog

    // Menu State
    private boolean showMenu = false;
    private int menuSelectedIndex = 0;
    private String[] menuOptions = { "POKÉDEX", "CRAFTEO", "MOCHILA", "GUARDAR", "OPCIONES", "INICIO" };

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

    // Region Triggers
    private class RegionTrigger {
        Rectangle bounds;
        Color color;
        Texture signTexture;
        boolean active;
        String name;

        public RegionTrigger(float x, float y, float w, float h, Color c, Texture t, String n) {
            this.bounds = new Rectangle(x, y, w, h);
            this.color = c;
            this.signTexture = t;
            this.name = n;
            this.active = false;
        }
    }

    private List<RegionTrigger> regions = new ArrayList<>();
    private Texture texCostaCobalto, texPantanal, texLadera, texTundra, texFlorecita;
    private RegionTrigger currentActiveRegion = null; // Track which one is currently "on" to avoid re-triggering
                                                      // constantly

    public GameScreen(final PokemonMain game, String texturePath, int cols, int rows, String playerName,
            String gameName) {
        super(game);
        this.frameCols = cols;
        this.frameRows = rows;
        this.playerName = playerName; // Explorer Name
        this.myTexturePath = texturePath; // Store path for Network Identity

        // Initialize Managers
        npcManager = new com.mypokemon.game.objects.NPCManager();
        gameUI = new com.mypokemon.game.ui.GameUI();

        // Check for saved progress using GAME NAME (Partida), not Player Name
        this.explorador = Explorador.cargarProgreso(gameName);
        if (this.explorador == null) {
            // New Game: Create with Explorer Name (for display) and Game Name (for saving)
            this.explorador = new Explorador(playerName, gameName, 40);
        }

        // Initialize Camera and Viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camera);
        uiMatrix = new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, 800, 480);

        // Load Intro Texture
        try {
            introTexture = new Texture(Gdx.files.internal("letreroPraderaObsidiana.png"));
            introState = IntroState.SLIDING_IN;
            // Start completely off-screen (above the viewport)
            introY = 480;
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Could not load letreroPraderaObsidiana.png", e);
            introState = IntroState.FINISHED;
        }

        // Load Aviso Texture
        try {
            avisoTexture = new Texture(Gdx.files.internal("Aviso.png"));
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Could not load Aviso.png", e);
        }

        // Init White Pixel for UI
        uiWhitePixel = TextureUtils.createSolidTexture(1, 1, Color.WHITE);

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

        // Initialize Lab Sign Texture (Map Object)
        try {
            labSignTexture = new Texture(Gdx.files.internal("letreroLaboratorio.png"));
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Lab Sign texture not found", e);
        }

        // NOMENCLATURE: NPC POSITIONS
        // Modify the coordinates (X, Y) here to change NPC locations.
        if (npcManager != null) {
            npcManager.addNPC(new com.mypokemon.game.objects.FeidNPC(posX - 220, posY - 20));
            npcManager.addNPC(new com.mypokemon.game.objects.HarryPotterNPC(posX + 2100, posY + 900));
            npcManager.addNPC(new com.mypokemon.game.objects.HarryStylesNPC(posX + 1110, posY - 320));
            npcManager.addNPC(new com.mypokemon.game.objects.BrennerNPC(posX + 480, posY + 620));
        }

        // Initialize Lab Zone
        // Assuming Lab door is roughly at spawnX, spawnY - 140
        float labDoorX = posX - 120;
        float labDoorY = posY - 185;
        labZone = new Rectangle(labDoorX, labDoorY, 80, 50);

        // Initialize Lab Sign Position (Fixed)
        labSignX = posX - 45;
        labSignY = posY - 125;

        // Initialize UI projection matrix

        game.font.setUseIntegerPositions(true);

        // Load Region Sign Textures
        try {
            texCostaCobalto = new Texture(Gdx.files.internal("letreroCostaCobalto.png"));
            texPantanal = new Texture(Gdx.files.internal("letreroPantanalCarmesí.png"));
            texLadera = new Texture(Gdx.files.internal("letreroLaderaCorona.png"));
            texTundra = new Texture(Gdx.files.internal("letreroTundraAlba.png"));
            texFlorecita = new Texture(Gdx.files.internal("florecita.png"));
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Error loading region signs", e);
        }

        // Initialize Regions (Offsets relative to Spawn Point)
        // The actual posX and posY are set in loadMap() which is called in show().
        // For constructor initialization, we'll use 0,0 and update in show() or
        // render().
        // However, the previous code used this.posX and this.posY directly, implying
        // they might be set by Explorador.
        // Let's stick to the previous logic for now, assuming posX/posY are correctly
        // initialized by the time
        // this block is reached, or that the relative offsets are what matters.
        float spawnX = this.posX;
        float spawnY = this.posY;
        float regionSize = 60f;

        // 1. Costa Cobalto (Orange) - Near Harry Styles (Spawn + 1110, -320)
        regions.add(new RegionTrigger(spawnX + 1170, spawnY - 320, regionSize, regionSize,
                Color.ORANGE, texCostaCobalto, "Costa Cobalto"));

        // 2. Pantanal Carmesí (Pink) - Near Harry Potter (Spawn + 2100, + 900) - LEFT
        regions.add(new RegionTrigger(spawnX + 2100 - 50, spawnY + 890, regionSize, regionSize,
                Color.PINK, texPantanal, "Pantanal Carmesí"));

        // 3. Ladera Corona (Yellow) - Near Harry Potter - RIGHT
        regions.add(new RegionTrigger(spawnX + 2100 - 1300, spawnY + 1700, regionSize, regionSize,
                Color.YELLOW, texLadera, "Ladera Corona"));

        // 4. Tundra Alba (Purple) - Near Harry Potter - BOTTOM
        regions.add(new RegionTrigger(spawnX - 100, spawnY + 950, regionSize, regionSize,
                Color.PURPLE, texTundra, "Tundra Alba"));
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

        // Network Init
        this.client = game.networkClient;
        if (this.client != null) {
            String genderStr = "CHICO";
            if (myTexturePath != null && myTexturePath.toLowerCase().contains("fem")) {
                genderStr = "CHICA";
            }
            client.sendMessage("IDENTITY:" + playerName + ":" + genderStr);

            this.client.setListener(msg -> {
                Gdx.app.postRunnable(() -> handleNetworkMessage(msg));
            });
        }
    }

    private void handleNetworkMessage(String msg) {
        try {
            if (msg.startsWith("MOVE:")) {
                String[] parts = msg.split(":");
                if (parts.length >= 4) {
                    float tx = Float.parseFloat(parts[1]);
                    float ty = Float.parseFloat(parts[2]);
                    String dir = parts[3];

                    if (otherPlayer == null && playerSheet != null) {
                        otherPlayer = new RemotePlayer(playerSheet, frameCols, frameRows);
                    }
                    if (otherPlayer != null) {
                        otherPlayer.update(Gdx.graphics.getDeltaTime(), tx, ty, dir);
                    }
                }
            } else if (msg.startsWith("PEER_INFO:")) {
                String[] parts = msg.split(":");
                if (parts.length >= 2) {
                    String peerName = parts[1];
                    String peerGender = parts.length > 2 ? parts[2] : "CHICO";

                    // Load correct texture for peer
                    com.badlogic.gdx.graphics.Texture peerSheet;
                    if ("CHICA".equals(peerGender)) {
                        peerSheet = new com.badlogic.gdx.graphics.Texture("protagonistaFemenino.png");
                    } else {
                        peerSheet = new com.badlogic.gdx.graphics.Texture("protagonistaMasculino1.png");
                    }

                    // Preserve position if refreshing
                    float oldX = 0;
                    float oldY = 0;
                    if (otherPlayer != null) {
                        oldX = otherPlayer.x;
                        oldY = otherPlayer.y;
                    }

                    // Re-create remote player with correct sprite
                    otherPlayer = new RemotePlayer(peerSheet, frameCols, frameRows);
                    otherPlayer.name = peerName;
                    otherPlayer.x = oldX;
                    otherPlayer.y = oldY;
                }
            } else if (msg.equals("SAVE_CONFIRMED")) {
                notificationMessage = "¡Partida Compartida Guardada!";
                notificationTimer = NOTIFICATION_DURATION;
            } else if (msg.startsWith("RESOURCE_REMOVED:")) {
                String id = msg.substring(17);
                removeResourceById(id);
            } else if (msg.startsWith("SYNC_RESOURCES:")) {
                String[] ids = msg.substring(15).split(",");
                for (String id : ids) {
                    if (!id.isEmpty())
                        removeResourceById(id);
                }
            }
        } catch (Exception e) {
            Gdx.app.log("Network", "Error parsing message: " + msg);
        }
    }

    private void removeResourceById(String id) {
        try {
            String[] parts = id.split("_");
            int cx = Integer.parseInt(parts[0]);
            int cy = Integer.parseInt(parts[1]);

            for (RecursoMapa r : recursosMapa) {
                if (r.cellX == cx && r.cellY == cy) {
                    if (!r.recolectado) {
                        r.recolectado = true;
                        // Remove visual tiles
                        for (TiledMapTileLayer layer : r.cellsPorCapa.keySet())
                            layer.setCell(r.cellX, r.cellY, null);
                    }
                    break;
                }
            }
        } catch (Exception e) {
        }
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
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
                if (selected.equals("INICIO")) {
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                } else if (selected.equals("MOCHILA")) {
                    game.setScreen(new MochilaScreen(game, this, explorador));
                } else if (selected.equals("CRAFTEO")) {
                    game.setScreen(new CrafteoScreen(game, this));
                } else if (selected.equals("POKÉDEX")) {
                    game.setScreen(new PokedexScreen(game, this, explorador));
                } else if (selected.equals("GUARDAR")) {
                    explorador.guardarProgreso(); // Guardado local individual

                    if (client != null) {
                        client.sendMessage("SAVE_GAME");
                        notificationMessage = "Esperando al otro jugador...";
                    } else {
                        notificationMessage = "¡Partida Guardada!";
                    }
                    notificationTimer = NOTIFICATION_DURATION;
                    showMenu = false;
                }
                if (!selected.equals("GUARDAR")) {
                    showMenu = false;
                }
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
                    String dirStr = "DOWN";
                    if (moveX < 0) {
                        currentFrame = walkLeft.getKeyFrame(stateTime, true);
                        dirStr = "LEFT";
                    } else if (moveX > 0) {
                        currentFrame = walkRight.getKeyFrame(stateTime, true);
                        dirStr = "RIGHT";
                    } else if (moveY > 0) {
                        currentFrame = walkUp.getKeyFrame(stateTime, true);
                        dirStr = "UP";
                    } else if (moveY < 0) {
                        currentFrame = walkDown.getKeyFrame(stateTime, true);
                        dirStr = "DOWN";
                    }

                    // Network Update
                    moveUpdateTimer += delta;
                    if (client != null && moveUpdateTimer > 0.05f) { // 20 updates/sec
                        moveUpdateTimer = 0;
                        client.sendMessage("MOVE:" + posX + ":" + posY + ":" + dirStr);
                    }

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
            isNearLab = false;
            // Use distance check for interaction prompt so it works even if we can't walk
            // "inside"
            if (labZone != null && com.badlogic.gdx.math.Vector2.dst(posX, posY, labZone.x + labZone.width / 2,
                    labZone.y + labZone.height / 2) < 60f) {
                isNearLab = true;
                if (!fadingOut && Gdx.input.isKeyJustPressed(Input.Keys.T)) {
                    if (explorador.getEquipo().isEmpty()) {
                        fadingOut = true;
                        // Pre-create screen
                        nextScreen = new LaboratorioScreen(game, this);
                    } else {
                        notificationMessage = "La elección es permanente. No puedes volver a entrar.";
                        notificationTimer = NOTIFICATION_DURATION;
                    }
                }
            }

            // NPC Interaction Input
            // Toggle dialog on interaction key (E)
            // Toggle dialog on interaction key (E)
            if (Gdx.input.isKeyJustPressed(Input.Keys.T) && !showMenu) {
                com.mypokemon.game.objects.NPC closeNPC = (npcManager != null) ? npcManager.getCloseNPC(posX, posY)
                        : null;
                if (closeNPC != null) {
                    if (!showDialog) {
                        showDialog = true;
                        currentDialogPage = 0;
                        activeNpcName = closeNPC.getName();
                        activeDialogPages = closeNPC.getDialog();
                        currentPortrait = closeNPC.getPortrait();
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

                                // Network Send
                                if (client != null) {
                                    client.sendMessage("COLLECT:" + r.cellX + "_" + r.cellY);
                                }
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

                        // Check for Boss Encounter
                        boolean isBoss = false;
                        String bossName = "";

                        // We need to re-check the current tile for boss properties since the previous
                        // loop breaks on any 'ZonaEncuentro'
                        // Actually, let's just use the logic if 'foundGrass' is true, assuming boss
                        // tiles also have 'ZonaEncuentro' property.
                        // Or better, let's just check the specific layer/tile properties for the boss
                        // condition regardless of grass loop structure if needed,
                        // but sticking to the current structure:

                        if (foundGrass) {
                            // Check for specific BOSS properties
                            for (com.badlogic.gdx.maps.MapLayer layer : map.getLayers()) {
                                if (layer instanceof TiledMapTileLayer) {
                                    TiledMapTileLayer.Cell cell = ((TiledMapTileLayer) layer).getCell(playerTileX,
                                            playerTileY);
                                    if (cell != null && cell.getTile() != null) {
                                        Object enemigoProp = cell.getTile().getProperties().get("Enemigo");
                                        Object nivelProp = cell.getTile().getProperties().get("NivelDificultad");

                                        if (enemigoProp != null && "JefeFinal".equals(enemigoProp.toString())) {
                                            if (explorador.getRegistro().verificarRequisitosArceus()) {
                                                isBoss = true;
                                                bossName = "Arceus";
                                            } else {
                                                notificationMessage = "Sientes una presencia divina... pero te falta conocimiento.";
                                                notificationTimer = 3.0f;
                                                foundGrass = false; // Prevent normal encounter
                                            }
                                        } else if (nivelProp != null && "HitoFinal".equals(nivelProp.toString())) {
                                            if (explorador.getRegistro().verificarRequisitosArceus()) {
                                                isBoss = true;
                                                bossName = "Arceus";
                                            } else {
                                                notificationMessage = "Sientes una presencia divina... pero te falta conocimiento.";
                                                notificationTimer = 3.0f;
                                                foundGrass = false; // Prevent normal encounter
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (isBoss) {
                            // Force Encounter with Arceus (100% probability)
                            inEncounter = true;
                            Gdx.app.log("GameScreen", "Boss Encounter: " + bossName);
                            // Arceus created with stats from BasePokemonData (130 HP, moves included)
                            Pokemon jefe = new Pokemon(bossName, 10, 130, true, "Normal");
                            game.setScreen(new BattleScreen(game, this, explorador, jefe));
                        } else if (foundGrass && nivelDificultad >= 1 && nivelDificultad <= 5) {
                            // Check if an encounter happens based on probability
                            if (GestorEncuentros.verificarEncuentro(nivelDificultad)) {
                                // Check if player has a starter Pokemon
                                if (explorador.getEquipo().isEmpty()) {
                                    notificationMessage = "Debes pasar por el laboratorio primero";
                                    notificationTimer = NOTIFICATION_DURATION;
                                } else {
                                    inEncounter = true;
                                    String pName = GestorEncuentros.obtenerPokemonAleatorio(nivelDificultad);
                                    Gdx.app.log("GameScreen", "Encounter: " + pName);
                                    Pokemon salvaje = new Pokemon(pName, 0, 0, false, "Normal");
                                    salvaje.agregarMovimiento(new Movimiento("Tackle", 0, "Normal", 40));
                                    game.setScreen(new BattleScreen(game, this, explorador, salvaje));
                                }
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

        // Region Trigger Logic
        if (!showMenu && !showDialog && !fadingOut) {
            for (RegionTrigger region : regions) {
                // Check distance to center of region
                float regCenterX = region.bounds.x + region.bounds.width / 2;
                float regCenterY = region.bounds.y + region.bounds.height / 2;

                if (com.badlogic.gdx.math.Vector2.dst(posX, posY, regCenterX, regCenterY) < 60f) {
                    // Trigger!
                    if (currentActiveRegion != region) {
                        currentActiveRegion = region;
                        if (region.signTexture != null) {
                            introTexture = region.signTexture;
                            introState = IntroState.SLIDING_IN;
                            introY = 480; // Reset position
                        }
                    }
                } else {
                    if (com.badlogic.gdx.math.Vector2.dst(posX, posY, regCenterX, regCenterY) > 100f) {
                        if (currentActiveRegion == region) {
                            currentActiveRegion = null;
                        }
                    }
                }
            }
        }

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
        // NPC Range Check for Dialog Closing
        com.mypokemon.game.objects.NPC closeNPC = (npcManager != null) ? npcManager.getCloseNPC(posX, posY)
                : null;
        if (closeNPC == null) {
            showDialog = false;
        }

        // Timers
        if (notificationTimer > 0) {
            notificationTimer -= delta;
            if (notificationTimer <= 0) {
                notificationMessage = "";
            }
        }
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
        float hw = viewport.getWorldWidth() / 2, hh = viewport.getWorldHeight()
                / 2;
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
        // Render NPCs
        npcManager.render(game.batch);

        // Render Remote Player
        if (otherPlayer != null && otherPlayer.currentFrame != null) {
            game.batch.draw(otherPlayer.currentFrame, otherPlayer.x - playerWidth / 2, otherPlayer.y - playerHeight / 2,
                    playerWidth,
                    playerHeight * 1.2f);

            if (otherPlayer.name != null && !otherPlayer.name.isEmpty()) {
                game.font.getData().setScale(0.8f);
                game.font.setColor(Color.YELLOW);
                game.font.draw(game.batch, otherPlayer.name, otherPlayer.x - playerWidth / 2,
                        otherPlayer.y + playerHeight * 0.8f, playerWidth, com.badlogic.gdx.utils.Align.center, false);
                game.font.setColor(Color.WHITE);
            }
        }

        drawPlayer();

        // Draw Region Triggers (Florecitas)
        if (texFlorecita != null) {
            for (RegionTrigger r : regions) {
                game.batch.draw(texFlorecita, r.bounds.x, r.bounds.y, r.bounds.width, r.bounds.height);
            }
        }

        // remove drawBrenner() call as it's now in npcManager
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
        if (introState != IntroState.FINISHED && introTexture != null)

        {
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
        if (gameUI != null) {
            gameUI.renderHUD(game.batch, explorador, showMenu);
        }

        // Notifications
        if (gameUI != null) {
            gameUI.renderNotification(game.batch, notificationMessage);
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
        if (gameUI != null) {
            com.mypokemon.game.objects.NPC hintNPC = (npcManager != null) ? npcManager.getCloseNPC(posX, posY) : null;
            if (hintNPC != null && !showDialog) {
                gameUI.renderHint(game.batch, "Presiona [T] para hablar con " + hintNPC.getName());
            } else if (isNearLab && !showDialog) {
                gameUI.renderHint(game.batch, "Presiona [T] para entrar");
            }
        }

        // Dialog
        // Dialog
        if (showDialog && activeDialogPages != null && currentDialogPage < activeDialogPages.length && gameUI != null) {
            gameUI.renderDialog(game.batch, activeNpcName, activeDialogPages[currentDialogPage], currentPortrait,
                    currentDialogPage < activeDialogPages.length - 1);
        }

        // CRAFTING UI
        if (craftingState == CraftingState.SHOWING_RECIPES) {
            game.font.setColor(Color.CYAN);
            game.font.draw(game.batch, craftingRecipe, 400, 250, 0, com.badlogic.gdx.utils.Align.center, false);
            game.font.draw(game.batch, "¿Fabricar? [S/N]", 400, 220, 0, com.badlogic.gdx.utils.Align.center, false);
            game.font.setColor(Color.WHITE);
        }

        // Menu
        // Menu
        if (showMenu && gameUI != null) {
            gameUI.renderMenu(game.batch, menuOptions, menuSelectedIndex);
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
        // Define collision box for player feet
        float minX = x - playerWidth / 3;
        float minY = y - playerHeight / 2;
        float w = playerWidth * 2 / 3;
        float h = playerHeight / 4;

        // Lab Collision (Door/Entrance area)
        if (labZone != null && labZone.overlaps(new Rectangle(minX, minY, w, h))) {
            return true;
        }

        // Colisión con Otro Jugador (Online)
        if (otherPlayer != null) {
            // Full Body Collision (Requested: "todo el borde")
            Rectangle myRect = new Rectangle(x - playerWidth / 2, y - playerHeight / 2, playerWidth, playerHeight);
            Rectangle otherRect = new Rectangle(otherPlayer.x - playerWidth / 2, otherPlayer.y - playerHeight / 2,
                    playerWidth, playerHeight);

            if (myRect.overlaps(otherRect)) {
                return true;
            }
        }

        // Check NPC Collision with a slightly larger box for better feel (Body
        // collision)
        if (npcManager != null && npcManager.checkCollision(x - playerWidth / 2, y - playerHeight / 2, playerWidth,
                playerHeight / 2)) {
            return true;
        }

        if (collisionLayer == null)
            return false;

        float[][] points = {
                { minX, minY },
                { minX + w, minY },
                { minX, minY + h },
                { minX + w, minY + h }
        };

        for (float[] p : points) {
            int cellX = (int) (p[0] / collisionLayer.getTileWidth());
            int cellY = (int) (p[1] / collisionLayer.getTileHeight());

            TiledMapTileLayer.Cell cell = collisionLayer.getCell(cellX, cellY);
            if (cell != null && cell.getTile() != null) {
                Object col = cell.getTile().getProperties().get("Colision");
                if (col != null) {
                    if (col instanceof Boolean && (Boolean) col)
                        return true;
                    if (col instanceof String && "true".equalsIgnoreCase((String) col))
                        return true;
                }
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
        if (labSignTexture != null)
            labSignTexture.dispose();

        if (mapRenderer != null)
            mapRenderer.dispose();
        if (grassSound != null)
            grassSound.dispose();

        if (npcManager != null)
            npcManager.dispose();
        if (gameUI != null)
            gameUI.dispose();

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
