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

public class GameScreen extends BaseScreen {

    // Map details
    TiledMap map;
    OrthogonalTiledMapRenderer mapRenderer;
    OrthographicCamera camera;
    Viewport viewport;
    TiledMapTileLayer collisionLayer;
    com.badlogic.gdx.math.Matrix4 uiMatrix;
    String playerName;
    int[] backgroundLayers;
    int[] foregroundLayers;

    float mapWidth, mapHeight;
    float posX = 1600;
    float posY = 1600;
    float speed = 150;
    float playerWidth = 32;
    float playerHeight = 32;

    // Movement state
    boolean isMoving = false;
    float stateTime = 0f;

    // Animations
    Animation<TextureRegion> walkDown, walkUp, walkLeft, walkRight;
    TextureRegion currentFrame;
    Texture playerSheet;

    // UI elements
    boolean showInstructions = false; // Disabled - player can move immediately
    Texture blackTexture;

    public GameScreen(final PokemonMain game, String texturePath, int cols, int rows, String playerName) {
        super(game);
        this.playerName = playerName;

        // Initialize Camera and Viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camera);
        uiMatrix = new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, 800, 480);

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

        // Create black texture for dim effect
        blackTexture = TextureUtils.createSolidTexture(1, 1, Color.BLACK);

        // Initialize UI projection matrix
        uiMatrix = new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, 800, 480);
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

            float oldX = posX;
            float oldY = posY;

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

        // Debug Info
        if (game.font != null) {
            game.font.getData().setScale(0.8f);
            game.font.setColor(com.badlogic.gdx.graphics.Color.YELLOW);
            String debugMsg = String.format("Pos: %.1f,%.1f | BG: %d", posX, posY,
                    (backgroundLayers != null ? backgroundLayers.length : 0));
            game.font.draw(game.batch, debugMsg, posX - 150, posY - 50, 300, com.badlogic.gdx.utils.Align.center,
                    false);

            // Restaurar matriz de cámara para otros posibles dibujos
            game.batch.setProjectionMatrix(camera.combined);
        }

        game.batch.end();

        // Render top layer above player (Objetos_superiores)
        if (mapRenderer != null && foregroundLayers != null) {
            mapRenderer.render(foregroundLayers);
        }

        // Draw HUD in screen coordinates (800x480 virtual resolution)
        game.batch.setProjectionMatrix(uiMatrix);

        // Instruction Overlay
        if (showInstructions) {
            game.batch.begin();
            game.batch.setColor(0, 0, 0, 0.8f);
            game.batch.draw(blackTexture, 0, 0, 800, 480);
            game.batch.setColor(Color.WHITE);
            if (game.font != null) {
                game.font.draw(game.batch, "PRESS 'O' TO CONTINUE", 400 - 70, 240 + 20);
                game.font.draw(game.batch, "PRESS 'B' TO GO BACK", 400 - 70, 240 - 20);
            }
            game.batch.end();
            game.batch.setColor(Color.WHITE); // Reset color for safety
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
        if (blackTexture != null)
            blackTexture.dispose();
        if (map != null)
            map.dispose();
        if (mapRenderer != null)
            mapRenderer.dispose();
    }
}
