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

    // NPC State
    // NPC State
    private Texture feidSprite;
    private Texture dialogIconTexture;
    private Texture uiWhitePixel;
    private float feidX, feidY;
    private boolean showDialog;
    private String npcDialogText = "¡Epa, qué más pues, mor! Bienvenido a la región de Hisui, vea que esto por aquí está una chimba pero bien peligroso. Usted no puede andar por ahí 'Normal' sin con qué defenderse. Necesito que se ponga las pilas y me trabaje esa autosuficiencia. El flujo está en el inventario, pero ojo que el espacio es limitado, no se me embolete con la capacidad. Vaya y me recolecta unos Guijarros y Plantas para que se haga unas Poké Balls bien melas. ¡Hágale pues, que ese Pokedex no se va a llenar solo, Nea!";

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
            this.explorador = new Explorador(playerName, 20); // Capacidad inicial
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
            feidSprite = new Texture(Gdx.files.internal("ferxxo.png"));
            dialogIconTexture = new Texture(Gdx.files.internal("ferxxoCientifico.png"));
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "NPC textures not found", e);
        }

        // Create UI utilities
        uiWhitePixel = TextureUtils.createSolidTexture(1, 1, Color.WHITE);

        // Place NPC near spawn but slightly to the right
        feidX = posX + 60;
        feidY = posY;

        // Initialize UI projection matrix
        uiMatrix = new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, 800, 480);
    }

    @Override
    public void show() {
        // Register InputHandler for shortcuts
        if (explorador != null) {
            InputHandler inputHandler = new InputHandler(this.explorador);
            Gdx.input.setInputProcessor(inputHandler);
        }
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
        // Draw the player. size scaled to match tiles somewhat (32x34 approx)
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

        // Draw NPC
        if (feidSprite != null) {
            game.batch.draw(feidSprite, feidX, feidY, 32, 48); // Assuming 32x48 size
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
            // Títulos y estado del explorador
            game.font.draw(game.batch, "EXPLORADOR: " + explorador.getNombre(), 20, 460);

            // Datos del Inventario (Misión 1)
            game.font.draw(game.batch, "--- INVENTARIO ---", 20, 430);
            game.font.draw(game.batch, "Plantas: " + explorador.getMochila().getPlantas(), 20, 410);
            game.font.draw(game.batch, "Guijarros: " + explorador.getMochila().getGuijarros(), 20, 390);
            game.font.draw(game.batch, "Poké Balls: " + explorador.getMochila().getPokeBalls(), 20, 370);

            // Validación de espacio
            int ocupado = explorador.getMochila().getEspacioOcupado();
            int max = explorador.getMochila().getCapacidadMaxima();
            game.font.draw(game.batch, "Carga: " + ocupado + " / " + max, 20, 340);

            // Guía de controles (Ayuda) - Bottom Left
            game.font.draw(game.batch, "[P] Planta  [G] Guijarro  [C] Craftear  [K] Pokedex", 20, 40);
        }

        // --- NPC DIALOG ---
        // Check proximity
        float dist = com.badlogic.gdx.math.Vector2.dst(posX, posY, feidX, feidY);
        if (dist < 80) { // 80px range
            // Constants for UI
            float screenW = 800;
            float screenH = 480;
            float dialogHeight = 150;
            float portraitSize = 250;

            // Draw Portrait (Right side)
            if (dialogIconTexture != null) {
                game.batch.draw(dialogIconTexture, screenW - portraitSize - 20, dialogHeight - 20, portraitSize,
                        portraitSize);
            }

            // Draw Main Dialog Box Background (Blue-ish/White style)
            // Outer border (Dark Blue)
            game.batch.setColor(com.badlogic.gdx.graphics.Color.valueOf("355D80")); // Dark blue
            if (uiWhitePixel != null)
                game.batch.draw(uiWhitePixel, 20, 20, screenW - 40, dialogHeight);

            // Inner content (Light Blue)
            game.batch.setColor(com.badlogic.gdx.graphics.Color.valueOf("84C5FA")); // Light blue
            if (uiWhitePixel != null)
                game.batch.draw(uiWhitePixel, 25, 25, screenW - 50, dialogHeight - 10);

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
            game.font.draw(game.batch, npcDialogText, 40, dialogHeight - 10, screenW - 80,
                    com.badlogic.gdx.utils.Align.left, true);
            game.font.getData().setScale(1.0f);
            game.font.setColor(Color.WHITE);

            // "Continue" hint
            game.font.getData().setScale(0.6f);
            game.font.draw(game.batch, "CONTINUE (ENTER / ->)", 40, 50);
            game.font.getData().setScale(1.0f);
        }

        game.batch.end();
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
        if (map != null)
            map.dispose();
        if (mapRenderer != null)
            mapRenderer.dispose();
    }
}
