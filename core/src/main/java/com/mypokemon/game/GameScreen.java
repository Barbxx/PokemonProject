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
import java.util.List;

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
    private float playerWidth = 32f;
    private float playerHeight = 32f;
    private String playerName;

    private Explorador explorador;

    // Resource System
    private static class RecursoMapa {
        int cellX, cellY;
        String tipo;
        int cantidad;
        // Map layers and their corresponding cells at (cellX, cellY)
        java.util.Map<TiledMapTileLayer, TiledMapTileLayer.Cell> cellsPorCapa = new java.util.HashMap<>();
        float timerRespawn = 0;
        boolean recolectado = false;

        public RecursoMapa(int x, int y, String tipo, int cantidad) {
            this.cellX = x;
            this.cellY = y;
            this.tipo = tipo;
            this.cantidad = cantidad;
        }

        public void registrarCapa(TiledMapTileLayer capa, TiledMapTileLayer.Cell cell) {
            cellsPorCapa.put(capa, cell);
        }
    }

    private List<RecursoMapa> recursosMapa = new ArrayList<>();

    // Notification System
    private String notificationMessage = "";
    private float notificationTimer = 0;
    private static final float NOTIFICATION_DURATION = 3f; // 3 seconds

    // Crafting Dialog System
    private enum CraftingState {
        CLOSED, SHOWING_RECIPES, CONFIRMING
    }

    private CraftingState craftingState = CraftingState.CLOSED;
    private String craftingRecipe = "";

    // Mission Completion System
    private boolean showMissionComplete = false;
    private float missionCompleteTimer = 0;
    private static final float MISSION_COMPLETE_DURATION = 5f; // 5 seconds
    private Texture missionCompleteTexture;

    // NPC State
    // NPC State
    private Texture feidSprite;
    private Texture dialogIconTexture;
    private Texture dialogFrameTexture;
    private Texture uiWhitePixel;
    private float feidX, feidY;
    private boolean showDialog = false;
    private boolean isNearFeid = false;
    private int currentDialogPage = 0;
    private String[] feidDialogPages = {
            "¡Epa! ¿Qué más pues, mor? Bienvenido a la región de Hisui, vea que esto por aquí está una chimba pero bien peligroso... Usted no puede andar por ahí \"Normal\" sin con qué defenderse.",
            "Póngase la pilas pues y trabaje esa autosuficiencia.",
            "El flujo está en el inventario, pero ojo que el espacio es limitado, no se me embolete con la capacidad.",
            "Vaya y me recolecta unos Guijarros y Plantas para que se haga unas Poké Balls bien chimbas. ¡Hágale pues, que ese Pokedex no se va a llenar solo, Nea!"
    };

    // Intro Animation State
    private enum IntroState {
        SLIDING_IN, WAITING, SLIDING_OUT, FINISHED
    }

    private Texture introTexture;
    private IntroState introState;
    private float introY;
    private float introSpeed = 200f; // Pixels per second

    public GameScreen(final PokemonMain game, String texturePath, int cols, int rows, String playerName) {
        super(game);
        this.playerName = playerName;
        // Check for saved progress or create new
        this.explorador = Explorador.cargarProgreso(playerName);
        if (this.explorador == null) {
            this.explorador = new Explorador(playerName, 30); // Capacidad inicial
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
        // Higher Z value (e.g., 10) ensures XY plane (Z=0) is within frustum (near=1,
        // far=100)
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
            dialogIconTexture = new Texture(Gdx.files.internal("ferxxoCientifico.png"));
            dialogFrameTexture = new Texture(Gdx.files.internal("marcoDialogo.png"));
            missionCompleteTexture = new Texture(Gdx.files.internal("misionCompletada.png"));
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "NPC/UI textures not found", e);
        }

        // Create UI utilities
        uiWhitePixel = TextureUtils.createSolidTexture(1, 1, Color.WHITE);

        // Place NPC next to the blue house (approximate coordinates from player spawn)
        feidX = posX - 220;
        feidY = posY - 20;

        // Initialize UI projection matrix
        uiMatrix = new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, 800, 480);
        game.font.setUseIntegerPositions(true);
    }

    @Override
    public void show() {
        // Create InputMultiplexer to handle both keyboard and mouse
        com.badlogic.gdx.InputMultiplexer multiplexer = new com.badlogic.gdx.InputMultiplexer();

        // Register InputHandler for keyboard shortcuts
        if (explorador != null) {
            InputHandler inputHandler = new InputHandler(this.explorador);
            multiplexer.addProcessor(inputHandler);
        }

        // Add mouse/touch processor for resource collection
        multiplexer.addProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Convert screen coordinates to world coordinates
                com.badlogic.gdx.math.Vector3 worldCoords = new com.badlogic.gdx.math.Vector3(screenX, screenY, 0);
                camera.unproject(worldCoords);

                // Check if click is near any resource
                for (RecursoMapa r : recursosMapa) {
                    if (!r.recolectado) {
                        float resX = r.cellX * collisionLayer.getTileWidth() + collisionLayer.getTileWidth() / 2;
                        float resY = r.cellY * collisionLayer.getTileHeight() + collisionLayer.getTileHeight() / 2;
                        float d = com.badlogic.gdx.math.Vector2.dst(worldCoords.x, worldCoords.y, resX, resY);

                        // Click radius of ~40 pixels to make it easier to click
                        if (d < 40) {
                            if (explorador.getMochila().recolectarRecurso(r.tipo, r.cantidad)) {
                                r.recolectado = true;
                                r.timerRespawn = 120f; // 120 seconds respawn
                                // Clear all layers at this position
                                for (TiledMapTileLayer layer : r.cellsPorCapa.keySet()) {
                                    layer.setCell(r.cellX, r.cellY, null);
                                }
                                Gdx.app.log("GameScreen", "Collected and hid: " + r.cantidad + " " + r.tipo);
                            } else {
                                // Show inventory full notification
                                notificationMessage = "Inventario lleno";
                                notificationTimer = NOTIFICATION_DURATION;
                            }
                            return true; // Consume the event
                        }
                    }
                }
                return false;
            }
        });

        Gdx.input.setInputProcessor(multiplexer);
    }

    private void createFallback() {
        // Create a simple magenta square as fallback
        playerSheet = TextureUtils.createSolidTexture(239, 256, Color.MAGENTA);

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
        // --- NPC INTERACTION LOGIC ---
        float dist = com.badlogic.gdx.math.Vector2.dst(posX, posY, feidX, feidY);
        isNearFeid = dist < 80;

        // Toggle dialog on interaction key (E)
        if (isNearFeid && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (!showDialog) {
                showDialog = true;
                currentDialogPage = 0;
            } else {
                showDialog = false;
            }
        }

        // --- RESOURCE RESPAWN LOGIC ---
        for (RecursoMapa r : recursosMapa) {
            if (r.recolectado) {
                r.timerRespawn -= delta;
                if (r.timerRespawn <= 0) {
                    r.recolectado = false;
                    // Restore all layers at this position
                    for (java.util.Map.Entry<TiledMapTileLayer, TiledMapTileLayer.Cell> entry : r.cellsPorCapa
                            .entrySet()) {
                        entry.getKey().setCell(r.cellX, r.cellY, entry.getValue());
                    }
                    Gdx.app.log("GameScreen", "Resource respawned: " + r.tipo + " at " + r.cellX + "," + r.cellY);
                }
            }
        }

        // --- CRAFTING DIALOG LOGIC ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.C) && craftingState == CraftingState.CLOSED) {
            // Open crafting dialog
            int plantas = explorador.getMochila().getPlantas();
            int guijarros = explorador.getMochila().getGuijarros();

            if (plantas == 0 && guijarros == 0) {
                notificationMessage = "Inventario vacío";
                notificationTimer = NOTIFICATION_DURATION;
            } else {
                craftingState = CraftingState.SHOWING_RECIPES;
                craftingRecipe = "2 Plantas + 3 Guijarros = 1 Poké Ball";
            }
        }

        // Handle crafting confirmation with S/N keys
        if (craftingState == CraftingState.SHOWING_RECIPES) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                // User wants to craft
                if (explorador.getMochila().fabricarPokeBall()) {
                    notificationMessage = "¡Poké Ball creada!";
                    notificationTimer = NOTIFICATION_DURATION;
                    // Check if this is the first Poké Ball (Mission 1 complete)
                    if (explorador.getMochila().getPokeBalls() == 1) {
                        showMissionComplete = true;
                        missionCompleteTimer = MISSION_COMPLETE_DURATION;
                    }
                } else {
                    notificationMessage = "Materiales insuficientes";
                    notificationTimer = NOTIFICATION_DURATION;
                }
                craftingState = CraftingState.CLOSED;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
                // User cancelled
                craftingState = CraftingState.CLOSED;
            }
        }

        // Update notification timer
        if (notificationTimer > 0) {
            notificationTimer -= delta;
            if (notificationTimer < 0) {
                notificationTimer = 0;
                notificationMessage = "";
            }
        }

        // Update mission complete timer
        if (missionCompleteTimer > 0) {
            missionCompleteTimer -= delta;
            if (missionCompleteTimer < 0) {
                missionCompleteTimer = 0;
                showMissionComplete = false;
            }
        }

        // Advance dialog with Enter only
        if (showDialog && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            currentDialogPage++;
            if (currentDialogPage >= feidDialogPages.length) {
                showDialog = false;
                currentDialogPage = 0;
            }
        }

        // Hide dialog if we walk away
        if (!isNearFeid) {
            showDialog = false;
            currentDialogPage = 0;
        }

        isMoving = false;

        {
            // Handle Input for Movement (only if not showing instructions)
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                Gdx.app.exit();
            }

            float oldX = posX;
            float oldY = posY;

            boolean movementAttempted = false;

            // Handle Input for Movement
            // Only allow movement if intro is NOT sliding in
            if (introState != IntroState.SLIDING_IN) {
                float moveX = 0;
                float moveY = 0;

                if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    moveX = -1;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    moveX = 1;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                    moveY = 1;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    moveY = -1;
                }

                if (moveX != 0 || moveY != 0) {
                    // Start movement logic
                    movementAttempted = true;
                    isMoving = true;

                    // Normalize diagonal speed? Optional. For now, keep it simple.
                    // If we want consistent speed:
                    // if (moveX != 0 && moveY != 0) { moveX *= 0.7071f; moveY *= 0.7071f; }

                    posX += moveX * speed * delta;
                    posY += moveY * speed * delta;

                    // Update Animation
                    if (moveX < 0) {
                        currentFrame = walkLeft.getKeyFrame(stateTime, true);
                    } else if (moveX > 0) {
                        currentFrame = walkRight.getKeyFrame(stateTime, true);
                    } else if (moveY > 0) {
                        currentFrame = walkUp.getKeyFrame(stateTime, true);
                    } else if (moveY < 0) {
                        currentFrame = walkDown.getKeyFrame(stateTime, true);
                    }
                }
            }

            // Update Intro Animation Logic
            // Update Intro Animation Logic
            float introScale = 0.5f;
            float introH = (introTexture != null ? introTexture.getHeight() * introScale : 0);

            if (introState == IntroState.SLIDING_IN) {
                // targetY is near top left area.
                float targetY = 480 - introH;
                if (introY > targetY) {
                    introY -= introSpeed * delta;
                } else {
                    introY = targetY;
                    introState = IntroState.WAITING;
                }
            } else if (introState == IntroState.WAITING) {
                if (movementAttempted) {
                    introState = IntroState.SLIDING_OUT;
                }
            } else if (introState == IntroState.SLIDING_OUT) {
                if (introY < 480) {
                    introY += introSpeed * delta;
                } else {
                    introState = IntroState.FINISHED;
                }
            }

            // Simple Collision Detection
            if (isMoving) {
                if (isColliding(posX, posY)) {
                    // Try sliding (optional, but here we just block)
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

            // Clamping camera within map bounds (3200x3200 map, 800x480 viewport)
            float camX = posX;
            float camY = posY;

            // Half viewport sizes
            float hw = viewport.getWorldWidth() / 2;
            float hh = viewport.getWorldHeight() / 2;

            // camX, camY already initialized

            if (camX < hw)
                camX = hw;
            if (camX > mapWidth - hw)
                camX = mapWidth - hw;
            if (camY < hh)
                camY = hh;
            if (camY > mapHeight - hh)
                camY = mapHeight - hh;

            camera.position.set(camX, camY, 0); // Z=0 is safer for 2D if near/far are standard
            camera.update();
        }

        viewport.apply();
        ScreenUtils.clear(Color.BLACK);

        if (mapRenderer != null) {
            mapRenderer.setView(camera);
            if (backgroundLayers != null && backgroundLayers.length > 0) {
                mapRenderer.render(backgroundLayers);
            } else {
                // Fallback: if dynamic detection failed, render the whole map to avoid black
                // screen
                mapRenderer.render();
            }
        }

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Y-Sorting: Draw the one with higher Y coordinate first (further away)
        // Player's feet are at posY - playerHeight / 2
        // NPC's feet are at feidY
        float playerFeetY = posY - playerHeight / 2;
        float feidFeetY = feidY;

        if (playerFeetY > feidFeetY) {
            // Draw Player first (behind NPC)
            drawPlayer();
            drawNPC();
        } else {
            // Draw NPC first (behind Player)
            drawNPC();
            drawPlayer();
        }

        game.batch.end();

        // Render top layer above player (Objetos_superiores)
        if (mapRenderer != null && foregroundLayers != null) {
            mapRenderer.render(foregroundLayers);
        }

        // Draw HUD/UI in screen coordinates (800x480 virtual resolution)
        game.batch.setProjectionMatrix(uiMatrix);
        game.batch.begin();

        // Draw Intro Texture if active
        if (introState != IntroState.FINISHED && introTexture != null) {
            float introScale = 0.5f;
            float introW = introTexture.getWidth() * introScale;
            float introH = introTexture.getHeight() * introScale;
            game.batch.draw(introTexture, 0, introY, introW, introH);
        }

        // --- HUD / INTERFAZ DE USUARIO ---
        if (explorador != null && game.font != null) {
            game.font.setColor(Color.WHITE);
            float hudX = 780; // Right side margin

            // Títulos y estado del explorador - Top Right
            game.font.draw(game.batch, "EXPLORADOR: " + explorador.getNombre(), hudX, 460, 0,
                    com.badlogic.gdx.utils.Align.right, false);

            // Datos del Inventario (Misión 1)
            game.font.draw(game.batch, "--- INVENTARIO ---", hudX, 430, 0, com.badlogic.gdx.utils.Align.right, false);
            game.font.draw(game.batch, "Plantas: " + explorador.getMochila().getPlantas(), hudX, 410, 0,
                    com.badlogic.gdx.utils.Align.right, false);
            game.font.draw(game.batch, "Guijarros: " + explorador.getMochila().getGuijarros(), hudX, 390, 0,
                    com.badlogic.gdx.utils.Align.right, false);
            game.font.draw(game.batch, "Bayas: " + explorador.getMochila().getBayas(), hudX, 370, 0,
                    com.badlogic.gdx.utils.Align.right, false);
            game.font.draw(game.batch, "Poké Balls: " + explorador.getMochila().getPokeBalls(), hudX, 350, 0,
                    com.badlogic.gdx.utils.Align.right, false);
            game.font.draw(game.batch, "Pociones: " + explorador.getMochila().getPociones(), hudX, 330, 0,
                    com.badlogic.gdx.utils.Align.right, false);

            // Validación de espacio
            int ocupado = explorador.getMochila().getEspacioOcupado();
            int max = explorador.getMochila().getCapacidadMaxima();
            game.font.draw(game.batch, "Carga: " + ocupado + " / " + max, hudX, 300, 0,
                    com.badlogic.gdx.utils.Align.right, false);

            // Guía de controles (Ayuda) - Bottom Right
            game.font.draw(game.batch, "[Click] Recolectar  [C] Craftear  [K] Pokedex", hudX, 40, 0,
                    com.badlogic.gdx.utils.Align.right, false);
        }

        // --- NPC INTERACTION / DIALOG ---
        // Interaction Hint
        if (isNearFeid && !showDialog) {
            game.font.getData().setScale(0.8f);
            game.font.setColor(Color.YELLOW);
            game.font.draw(game.batch, "Presiona [E] para hablar con Feid", 300, 100);
            game.font.getData().setScale(1.0f);
            game.font.setColor(Color.WHITE);
        }

        // Show Dialog
        if (showDialog) {
            // Constants for UI
            float screenW = 800;
            float screenH = 480;
            float dialogHeight = 110;
            float portraitSize = 250;

            // Draw Portrait (Right side)
            if (dialogIconTexture != null) {
                game.batch.draw(dialogIconTexture, screenW - portraitSize - 20, dialogHeight - 20, portraitSize,
                        portraitSize);
            }

            // Draw Main Dialog Box
            // Background (White - slightly offset to leave margin for frame)
            game.batch.setColor(Color.WHITE);
            if (uiWhitePixel != null)
                game.batch.draw(uiWhitePixel, 25, 25, screenW - 50, dialogHeight - 10);

            // Frame (The custom texture)
            game.batch.setColor(Color.WHITE);
            if (dialogFrameTexture != null)
                game.batch.draw(dialogFrameTexture, 20, 20, screenW - 40, dialogHeight);

            // Name Tag Box (Top-Left of dialog, overlapping)
            float nameTagW = 200;
            float nameTagH = 40;
            float nameTagX = 40;
            float nameTagY = dialogHeight + 10;

            // Name Tag Background (White)
            game.batch.setColor(Color.WHITE);
            if (uiWhitePixel != null)
                game.batch.draw(uiWhitePixel, nameTagX, nameTagY, nameTagW, nameTagH);

            // Reset Color for Text
            game.batch.setColor(Color.WHITE);

            // Draw Text
            // Name
            game.font.setColor(Color.BLACK);
            game.font.getData().setScale(0.9f);
            game.font.draw(game.batch, "Profesor Ferxxo", nameTagX + 10, nameTagY + 28);

            // Dialog Body
            game.font.setColor(Color.BLACK);
            game.font.getData().setScale(0.85f);
            float textMargin = 100;
            game.font.draw(game.batch, feidDialogPages[currentDialogPage], textMargin, dialogHeight - 15,
                    screenW - (textMargin * 2),
                    com.badlogic.gdx.utils.Align.left, true);
            game.font.getData().setScale(1.0f);
            game.font.setColor(Color.WHITE);

            // "Continue" hint
            game.font.getData().setScale(0.6f);
            String hint = (currentDialogPage < feidDialogPages.length - 1) ? "SIGUIENTE (ENTER)"
                    : "CERRAR (ENTER / E)";
            game.font.draw(game.batch, hint, 100, 50);
            game.font.getData().setScale(1.0f);
        }

        // --- NOTIFICATION RENDERING ---
        if (notificationTimer > 0 && !notificationMessage.isEmpty()) {
            float notifW = 400;
            float notifH = 60;
            float notifX = (800 - notifW) / 2;
            float notifY = 400;

            // Semi-transparent background
            game.batch.setColor(0, 0, 0, 0.7f);
            if (uiWhitePixel != null) {
                game.batch.draw(uiWhitePixel, notifX, notifY, notifW, notifH);
            }
            game.batch.setColor(Color.WHITE);

            // Notification text
            game.font.setColor(Color.YELLOW);
            game.font.getData().setScale(1.2f);
            game.font.draw(game.batch, notificationMessage, notifX, notifY + notifH / 2 + 10, notifW,
                    com.badlogic.gdx.utils.Align.center, false);
            game.font.getData().setScale(1.0f);
            game.font.setColor(Color.WHITE);
        }

        // --- CRAFTING DIALOG RENDERING ---
        if (craftingState == CraftingState.SHOWING_RECIPES) {
            float craftW = 500;
            float craftH = 150;
            float craftX = (800 - craftW) / 2;
            float craftY = 200;

            // Background
            game.batch.setColor(0.2f, 0.2f, 0.2f, 0.9f);
            if (uiWhitePixel != null) {
                game.batch.draw(uiWhitePixel, craftX, craftY, craftW, craftH);
            }
            game.batch.setColor(Color.WHITE);

            // Title
            game.font.setColor(Color.CYAN);
            game.font.getData().setScale(1.1f);
            game.font.draw(game.batch, "RECETAS DISPONIBLES", craftX, craftY + craftH - 20, craftW,
                    com.badlogic.gdx.utils.Align.center, false);

            // Recipe
            game.font.setColor(Color.WHITE);
            game.font.getData().setScale(0.9f);
            game.font.draw(game.batch, craftingRecipe, craftX, craftY + craftH - 60, craftW,
                    com.badlogic.gdx.utils.Align.center, false);

            // Question
            game.font.setColor(Color.YELLOW);
            game.font.draw(game.batch, "¿Deseas craftear esto?", craftX, craftY + craftH - 90, craftW,
                    com.badlogic.gdx.utils.Align.center, false);

            // Options
            game.font.setColor(Color.GREEN);
            game.font.draw(game.batch, "[S] Sí", craftX + 100, craftY + 30);
            game.font.setColor(Color.RED);
            game.font.draw(game.batch, "[N] No", craftX + craftW - 150, craftY + 30);

            game.font.getData().setScale(1.0f);
            game.font.setColor(Color.WHITE);
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

    private void drawNPC() {
        if (feidSprite != null) {
            game.batch.draw(feidSprite, feidX, feidY, 25, 35);
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

            int cellX = (int) (p[0] / collisionLayer.getTileWidth());
            int cellY = (int) (p[1] / collisionLayer.getTileHeight());

            TiledMapTileLayer.Cell cell = collisionLayer.getCell(cellX, cellY);
            if (cell != null && cell.getTile() != null) {
                // Check if the tile has a "Colision" property set (as String or Boolean)
                Object col = cell.getTile().getProperties().get("Colision");
                if (col != null) {
                    if (col instanceof Boolean && (Boolean) col)
                        return true;
                    if (col instanceof String && "true".equalsIgnoreCase((String) col))
                        return true;
                }
                // Fallback: if no property but it's in the collision layer,
                // we'll assume it's a collision unless it's designated otherwise
                return true;
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
        if (feidSprite != null)
            feidSprite.dispose();
        if (dialogIconTexture != null)
            dialogIconTexture.dispose();
        if (dialogFrameTexture != null)
            dialogFrameTexture.dispose();
        if (uiWhitePixel != null)
            uiWhitePixel.dispose();
        if (missionCompleteTexture != null)
            missionCompleteTexture.dispose();
        if (map != null)
            map.dispose();
        if (mapRenderer != null)
            mapRenderer.dispose();
    }
}
