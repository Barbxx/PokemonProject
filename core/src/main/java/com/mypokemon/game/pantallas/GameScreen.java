package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.Pokemon;

import com.mypokemon.game.GestorEncuentros;
import com.mypokemon.game.InputHandler;
import com.mypokemon.game.RemotePlayer;

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

import com.mypokemon.game.utils.TextureUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;
import com.badlogic.gdx.audio.Sound;
import com.mypokemon.game.colisiones.GestorColisiones;
import com.mypokemon.game.colisiones.ColisionNPC;
import com.mypokemon.game.colisiones.ColisionPuertaLaboratorio;
import com.mypokemon.game.colisiones.IInteractivo;
import com.mypokemon.game.objects.NPC;

/**
 * Pantalla principal del juego donde ocurre la exploración y la aventura.
 * Gestiona el mapa, el jugador, los NPCs, las colisiones y la lógica del mundo.
 */
public class GameScreen extends BaseScreen {
    // Atributos
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
    // Tamaño visual del sprite
    private float playerWidth = 40f;
    private float playerHeight = 32f;
    // Tamaño de colisión
    private float playerCollisionWidth = 20f;
    private float playerCollisionHeight = 24f;
    private String playerName;

    private Explorador explorador;

    // Network
    private com.mypokemon.game.client.NetworkClient client;
    private RemotePlayer otherPlayer;
    private float moveUpdateTimer = 0;
    private String myTexturePath;

    // NPC Manejo
    private com.mypokemon.game.objects.NPCManager npcManager;

    // Colisiones
    private GestorColisiones gestorColisiones;
    private ColisionPuertaLaboratorio puertaLaboratorio;

    // UI Manejo
    private com.mypokemon.game.ui.GameUI gameUI;

    private Texture labSignTexture; // Necesaria para ColisionPuertaLaboratorio
    private Texture currentPortrait;
    private Texture uiWhitePixel;
    private boolean showDialog = false;

    private int currentDialogPage = 0;

    // Estado dinámico dialogo
    private String activeNpcName = "";
    private String[] activeDialogPages;
    // actually, let's keep currentPortrait to pass to GameUI.renderDialog

    // Menu Estado
    private boolean showMenu = false;
    private int menuSelectedIndex = 0;
    private String[] menuOptions = { "POKÉDEX", "CRAFTEO", "MOCHILA", "GUARDAR", "PERFIL", "INICIO" };

    // Intro Animacion estado
    private enum IntroState {
        SLIDING_IN, WAITING, SLIDING_OUT, FINISHED
    }

    private Texture introTexture;
    private IntroState introState;
    private float introY;
    private float introSpeed = 200f;
    private Texture avisoTexture;

    // Game Constantes
    private static final float ENCOUNTER_CHECK_INTERVAL = 1.0f;
    private static final float NOTIFICATION_DURATION = 3.0f;

    // Encounter State
    private float encounterTimer = 0;
    private int lastGrassTileX = -1;
    private int lastGrassTileY = -1;
    private boolean inEncounter = false;
    private Sound grassSound;

    // Resource Manejo
    private List<RecursoMapa> recursosMapa = new ArrayList<>();

    private String notificationMessage = "";
    private float notificationTimer = 0;
    private boolean showMissionComplete = false;
    private float missionCompleteTimer = 0;

    // Logica Musica NPC
    private com.badlogic.gdx.audio.Music currentNpcMusic = null;
    private com.badlogic.gdx.audio.Music backgroundPlayerMusic = null;
    private com.badlogic.gdx.audio.Music backgroundPlayerMusic2 = null; // Req #4: Segunda canción
    private boolean isPlayingFirstTrack = true; // Req #4: Track actual

    private int frameCols;
    private int frameRows;

    // Region Triggers
    private class RegionTrigger {
        Rectangle bounds;
        Texture signTexture;

        public RegionTrigger(float x, float y, float w, float h, Texture t) {
            this.bounds = new Rectangle(x, y, w, h);
            this.signTexture = t;
        }
    }

    private List<RegionTrigger> regions = new ArrayList<>();
    private Texture texCostaCobalto, texPantanal, texLadera, texTundra, texFlorecita;
    private RegionTrigger currentActiveRegion = null;

    /**
     * Constructor de GameScreen.
     * Inicializa los gestores, carga el mapa y configura el estado inicial del
     * jugador.
     *
     * @param game        Instancia principal del juego.
     * @param texturePath Ruta de la textura del jugador.
     * @param cols        Columnas en la hoja de sprites.
     * @param rows        Filas en la hoja de sprites.
     * @param playerName  Nombre del jugador.
     * @param gameName    Nombre de la partida.
     */
    public GameScreen(final PokemonMain game, String texturePath, int cols, int rows, String playerName,
            String gameName) {
        super(game);
        this.frameCols = cols;
        this.frameRows = rows;
        this.playerName = playerName; // Explorer Name
        this.myTexturePath = texturePath; // Store path for Network Identity

        // Inicializar manejadores
        npcManager = new com.mypokemon.game.objects.NPCManager();
        gameUI = new com.mypokemon.game.ui.GameUI();
        gestorColisiones = new GestorColisiones();

        // Determinar genero para la imagen a usar
        String genderStr = "CHICO";
        if (texturePath != null && texturePath.toLowerCase().contains("fem")) {
            genderStr = "CHICA";
        }

        // Chqueo para guardar progreso
        // Intento 1: Formato Solitario "NombrePartida - NombreJugador.dat"
        String soloFilename = gameName + " - " + playerName + ".dat";
        this.explorador = Explorador.cargarProgreso(soloFilename);

        if (this.explorador == null) {
            // Intento 2: Formato Compartida "NombreJugador - NombrePartida.dat"
            String sharedFilename = playerName + " - " + gameName + ".dat";
            this.explorador = Explorador.cargarProgreso(sharedFilename);
        }

        // Intento 3: Nombre exacto (Legacy o carga directa)
        if (this.explorador == null) {
            this.explorador = Explorador.cargarProgreso(gameName);
        }

        if (this.explorador == null) {
            // New Game
            this.explorador = new Explorador(playerName, gameName, 100, genderStr);
        } else {
            // Loaded Game
            if ("CHICA".equals(this.explorador.getGenero())) {
                this.myTexturePath = "protagonistaFemenino.png";
            } else {
                this.myTexturePath = "protagonistaMasculino1.png";
            }
            this.playerName = this.explorador.getNombre();
        }

        // Camara y Viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camera);
        uiMatrix = new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, 800, 480);

        // Cargar imagenes texturas intro
        try {
            introTexture = new Texture(Gdx.files.internal("letreroPraderaObsidiana.png"));
            introState = IntroState.SLIDING_IN;
            // Start completely off-screen (above the viewport)
            introY = 480;
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Could not load letreroPraderaObsidiana.png", e);
            introState = IntroState.FINISHED;
        }

        // Cargar textura aviso
        try {
            avisoTexture = new Texture(Gdx.files.internal("Aviso.png"));
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Could not load Aviso.png", e);
        }

        // Init White Pixel for UI
        uiWhitePixel = TextureUtils.createSolidTexture(1, 1, Color.WHITE);

        // Cargar sonido
        try {
            grassSound = Gdx.audio.newSound(Gdx.files.internal("hierba.mp3"));
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Could not load hierba.mp3");
        }

        // Cargar mapa
        try {
            TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
            TmxMapLoader loader = new TmxMapLoader(new InternalFileHandleResolver());
            map = loader.load("Mapa_Hisui.tmx", params);
            mapRenderer = new OrthogonalTiledMapRenderer(map, game.batch);

            for (MapLayer layer : map.getLayers()) {
                if (layer instanceof TiledMapTileLayer && layer.getName().equalsIgnoreCase("Objetos_colisión")) {
                    collisionLayer = (TiledMapTileLayer) layer;
                    gestorColisiones.establecerCapaColisionTerreno(collisionLayer);
                    break;
                }
            }


            Gdx.app.log("GameScreen", "Map loaded successfully with " + map.getLayers().getCount() + " layers");


            for (TiledMapTileSet tileset : map.getTileSets()) {
                Gdx.app.log("GameScreen", "Tileset: " + tileset.getName() + " with " + tileset.size() + " tiles");
            }


            for (int i = 0; i < map.getLayers().getCount(); i++) {
                MapLayer layer = map.getLayers().get(i);
                Gdx.app.log("GameScreen", "Layer " + i + ": " + layer.getName() +
                        " type: " + layer.getClass().getSimpleName() +
                        " visible: " + layer.isVisible());
            }

            // Dimensiones del mapa
            mapWidth = getIntProperty(map.getProperties(), "width", 100) *
                    getIntProperty(map.getProperties(), "tilewidth", 32);
            mapHeight = getIntProperty(map.getProperties(), "height", 100) *
                    getIntProperty(map.getProperties(), "tileheight", 32);


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

                            for (com.badlogic.gdx.maps.MapLayer layer : map.getLayers()) {
                                if (layer instanceof TiledMapTileLayer) {
                                    String layerName = layer.getName().toLowerCase();

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

        camera.position.set(posX, posY, 10);
        camera.update();

        boolean assetsLoaded = false;
        try {
            playerSheet = new Texture(myTexturePath);
            playerSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            assetsLoaded = true;
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Critical: Could not load " + myTexturePath, e);
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


        try {
            labSignTexture = new Texture(Gdx.files.internal("letreroLaboratorio.png"));
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Lab Sign texture not found", e);
        }

        // NPC POSITIONS and COLLISIONS
        if (npcManager != null) {
            npcManager.addNPC(new com.mypokemon.game.objects.FeidNPC(posX - 220, posY - 20));
            npcManager.addNPC(new com.mypokemon.game.objects.HarryPotterNPC(posX + 2100, posY + 900));
            npcManager.addNPC(new com.mypokemon.game.objects.HarryStylesNPC(posX + 1110, posY - 320));
            npcManager.addNPC(new com.mypokemon.game.objects.BrennerNPC(posX + 480, posY + 620));

            // agregar los npc al manejador de colisiones
            for (NPC npc : npcManager.getAllNPCs()) {
                gestorColisiones.agregarColision(new ColisionNPC(npc));
            }
        }

        // Inicializar colision laboratorio
        float labDoorX = posX - 120;
        float labDoorY = posY - 185;

        puertaLaboratorio = new ColisionPuertaLaboratorio(labDoorX, labDoorY, labSignTexture, game, this, explorador);
        gestorColisiones.agregarColision(puertaLaboratorio);


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

        float spawnX = this.posX;
        float spawnY = this.posY;
        float regionSize = 60f;

        // 1) Costa Cobalto
        regions.add(new RegionTrigger(spawnX + 1250, spawnY - 360, regionSize, regionSize, texCostaCobalto));

        // 2) Pantanal Carmesí
        regions.add(new RegionTrigger(spawnX + 2100 - 50, spawnY + 890, regionSize, regionSize, texPantanal));

        // 3) Ladera Corona
        regions.add(new RegionTrigger(spawnX + 2100 - 1300, spawnY + 1700, regionSize, regionSize, texLadera));

        // 4) Tundra Alba
        regions.add(new RegionTrigger(spawnX + 50, spawnY + 850, regionSize, regionSize, texTundra));

        // Cargar dos pistas del reproductor
        try {
            backgroundPlayerMusic = Gdx.audio.newMusic(Gdx.files.internal("audioReproductor.mp3"));
            backgroundPlayerMusic.setLooping(false); // No loop para permitir la siguiente canción
            backgroundPlayerMusic.setVolume(0.5f);
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Could not load audioReproductor.mp3");
        }

        try {
            backgroundPlayerMusic2 = Gdx.audio.newMusic(Gdx.files.internal("audioReproductor2.mp3"));
            backgroundPlayerMusic2.setLooping(true); // La segunda canción se repite
            backgroundPlayerMusic2.setVolume(0.5f);
        } catch (Exception e) {
            Gdx.app.log("GameScreen", "Could not load audioReproductor2.mp3");
        }
    }


    private float fadeAlpha = 1f;
    private boolean fadingIn = true;
    private boolean fadingOut = false;
    private BaseScreen nextScreen;

    /**
     * Se llama cuando esta pantalla se convierte en la pantalla actual.
     * Inicializa la entrada, el cliente de red y reinicia el estado de fundido
     * (fade).
     */
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

            this.client.setListener(msg -> {
                Gdx.app.postRunnable(() -> handleNetworkMessage(msg));
            });


            String genderStr = "CHICO";
            if (myTexturePath != null && myTexturePath.toLowerCase().contains("fem")) {
                genderStr = "CHICA";
            }
            client.sendMessage("IDENTITY:" + playerName + ":" + genderStr);
        }
    }

    // Métodos de Lógica Interna
    /**
     * Procesa los mensajes recibidos del servidor multijugador.
     *
     * @param msg Mensaje recibido.
     */
    private void handleNetworkMessage(String msg) {
        try {
            if (msg.startsWith("MOVE:")) {
                String[] parts = msg.split(":");
                if (parts.length >= 4) {
                    float tx = Float.parseFloat(parts[1]);
                    float ty = Float.parseFloat(parts[2]);
                    String dir = parts[3];


                    if (otherPlayer != null) {
                        otherPlayer.update(Gdx.graphics.getDeltaTime(), tx, ty, dir);
                    }
                }
            } else if (msg.startsWith("PEER_INFO:")) {
                String[] parts = msg.split(":");
                if (parts.length >= 2) {
                    String peerName = parts[1];
                    String peerGender = parts.length > 2 ? parts[2] : "CHICO";


                    com.badlogic.gdx.graphics.Texture peerSheet;
                    if ("CHICA".equals(peerGender)) {
                        peerSheet = new com.badlogic.gdx.graphics.Texture("protagonistaFemenino.png");
                    } else {
                        peerSheet = new com.badlogic.gdx.graphics.Texture("protagonistaMasculino1.png");
                    }


                    float oldX = 0;
                    float oldY = 0;
                    if (otherPlayer != null) {
                        oldX = otherPlayer.x;
                        oldY = otherPlayer.y;
                    }


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

    /**
     * Elimina un recurso del mapa basado en su ID único.
     * Utilizado para sincronización en multijugador.
     *
     * @param id Identificador del recurso (x_y).
     */
    private void removeResourceById(String id) {
        try {
            String[] parts = id.split("_");
            int cx = Integer.parseInt(parts[0]);
            int cy = Integer.parseInt(parts[1]);

            for (RecursoMapa r : recursosMapa) {
                if (r.cellX == cx && r.cellY == cy) {
                    if (!r.recolectado) {
                        r.recolectado = true;

                        for (TiledMapTileLayer layer : r.cellsPorCapa.keySet())
                            layer.setCell(r.cellX, r.cellY, null);
                    }
                    break;
                }
            }
        } catch (Exception e) {
        }
    }


    /**
     * Método principal de renderizado y lógica del juego.
     * Actualiza el estado del mundo, maneja la entrada y dibuja todos los
     * elementos.
     *
     * @param delta Tiempo transcurrido desde el último frame (en segundos).
     */
    @Override
    public void render(float delta) {

        explorador.actualizarTemporizadores(delta);

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
            // Menu Navegacion
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
                    String pName = explorador.getNombre();
                    String gName = explorador.getNombrePartida();
                    boolean saveSuccess = false;

                    if (client != null) {
                        // Chequear para nombres duplicados
                        if (otherPlayer != null && otherPlayer.name != null
                                && otherPlayer.name.trim().equalsIgnoreCase(pName.trim())) {
                            notificationMessage = "No deben haber nombres iguales";
                            notificationTimer = NOTIFICATION_DURATION;
                            saveSuccess = false;
                        } else {
                            // SHARED MODE: PlayerName - GameName
                            String filename = pName + " - " + gName + ".dat";
                            saveSuccess = explorador.guardarProgreso(filename);
                        }
                    } else {
                        // SOLO MODE: GameName - PlayerName
                        String filename = gName + " - " + pName + ".dat";
                        saveSuccess = explorador.guardarProgreso(filename);
                    }

                    if (saveSuccess) {
                        if (client != null) {
                            client.sendMessage("SAVE_GAME");
                            notificationMessage = "¡Progreso Guardado!";
                        } else {
                            notificationMessage = "¡Partida Guardada!";
                        }
                    } else if (notificationMessage.isEmpty()
                            || !notificationMessage.equals("No deben haber nombres iguales")) {
                        notificationMessage = "¡Error al guardar partida!";
                    }

                    notificationTimer = NOTIFICATION_DURATION;
                    showMenu = false;
                } else if (selected.equals("PERFIL")) {
                    boolean esChico = true;
                    if (myTexturePath != null && myTexturePath.toLowerCase().contains("fem")) {
                        esChico = false;
                    }
                    game.setScreen(new PerfilScreen(game, this, explorador, esChico));
                }
                if (!selected.equals("GUARDAR")) {
                    showMenu = false;
                }
            }
            isMoving = false;
        } else {
            // NORMAL GAMEPLAY
            isMoving = false;

            // Handle Input for Movement
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                Gdx.app.exit();
            }

            // Solo se muestra si introscreen terminó
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

                    // Animacion
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

                    // Network
                    moveUpdateTimer += delta;
                    if (client != null && moveUpdateTimer > 0.05f) { // 20 updates/sec
                        moveUpdateTimer = 0;
                        client.sendMessage("MOVE:" + posX + ":" + posY + ":" + dirStr);
                    }

                    // Colision
                    if (gestorColisiones.verificarTodasLasColisiones(posX, posY, playerCollisionWidth,
                            playerCollisionHeight)) {
                        // Simple colision
                        if (!gestorColisiones.verificarTodasLasColisiones(posX, oldY, playerCollisionWidth,
                                playerCollisionHeight)) {
                            posY = oldY;
                        } else if (!gestorColisiones.verificarTodasLasColisiones(oldX, posY, playerCollisionWidth,
                                playerCollisionHeight)) {
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

            // Intro Logica
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

            // Interaction Input
            if (Gdx.input.isKeyJustPressed(Input.Keys.T) && !showMenu && !fadingOut) {
                IInteractivo interactivo = gestorColisiones.obtenerInteractivoMasCercano(posX, posY);
                if (interactivo != null) {
                    if (interactivo instanceof ColisionNPC) {
                        NPC npc = ((ColisionNPC) interactivo).obtenerNPC();
                        if (!showDialog) {
                            showDialog = true;
                            currentDialogPage = 0;
                            activeNpcName = npc.getName();
                            activeDialogPages = npc.getDialog();
                            currentPortrait = npc.getPortrait();
                        } else {
                            showDialog = false;
                        }
                    } else {
                        interactivo.interactuar();
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

                            try {
                                int cantidadFinal = r.cantidad;
                                if (explorador.isGuanteEquipado()) {
                                    cantidadFinal *= 2;
                                }

                                explorador.getMochila().agregarItem(
                                        com.mypokemon.game.inventario.ItemFactory.crearRecurso(r.tipo, cantidadFinal));
                                r.recolectado = true;
                                r.timerRespawn = r.TIEMPO_RESPAWN;
                                // Remove from layers
                                for (TiledMapTileLayer layer : r.cellsPorCapa.keySet())
                                    layer.setCell(r.cellX, r.cellY, null);

                                if (explorador.isGuanteEquipado()) {
                                    notificationMessage = "¡Doble Recurso! Recogiste " + cantidadFinal + " " + r.tipo;
                                } else {
                                    notificationMessage = "Recogiste " + r.tipo;
                                }
                                notificationTimer = NOTIFICATION_DURATION;

                                // Network Send
                                if (client != null) {
                                    client.sendMessage("COLLECT:" + r.cellX + "_" + r.cellY);
                                }
                            } catch (com.mypokemon.game.inventario.exceptions.EspacioException e) {
                                notificationMessage = "Mochila llena";
                                notificationTimer = NOTIFICATION_DURATION;
                            }
                            break;
                        }
                    }
                }
            }

            // Logica encuentro con pokemon salvaje
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

                        // Check for Encuentro con pokemon salvaje
                        boolean isBoss = false;
                        String bossName = "";

                        if (foundGrass) {
                            // Propiedades pokemon enemigo
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
                            // Force Encounter with Arceus
                            inEncounter = true;
                            Gdx.app.log("GameScreen", "Boss Encounter: " + bossName);

                            // Mostrar diálogo de historia antes de la batalla con Arceus
                            String[] preArceusDialog = {
                                    "A medida que te aproximas a la cueva, el aire se vuelve pesado, gélido, como si el tiempo mismo se detuviera ante tus pies...",
                                    "El pulso se te acelera... ¿Será Arceus?",
                                    "Sin embargo, al cruzar el umbral, el silencio es absoluto. No hay deidades, solo una flauta que se ve muy antigua...",
                                    "Por lo que tomaste una decisión...",
                                    "Tocaste la flauta..."
                            };

                            // Arceus inicia en nivel 0 - Al ganar la batalla, se actualizará a nivel 10
                            Pokemon jefe = new Pokemon(bossName, 0, 130, true, "Normal");

                            BattleScreen battleScreen = new BattleScreen(game, this, explorador, jefe);

                            // Mostrar pantalla de diálogo antes de la batalla
                            game.setScreen(
                                    new StoryDialogScreen(game, "fondoFinal.png", preArceusDialog, battleScreen));
                        } else if (foundGrass && nivelDificultad >= 1 && nivelDificultad <= 5) {

                            if (GestorEncuentros.verificarEncuentro(nivelDificultad)) {

                                if (explorador.getEquipo().isEmpty()) {
                                    notificationMessage = "Debes pasar por el laboratorio primero";
                                    notificationTimer = NOTIFICATION_DURATION;
                                } else {
                                    inEncounter = true;
                                    String pName = GestorEncuentros.obtenerPokemonAleatorio(nivelDificultad);

                                    // Evitar encuentros con el mismo Pokemon que el jugador tiene como principal
                                    // Intentar hasta 5 veces obtener un Pokemon diferente
                                    String playerMainPokemon = explorador.getEquipo().get(0).getNombre().trim();

                                    int attempts = 0;
                                    while (pName.trim().equalsIgnoreCase(playerMainPokemon) && attempts < 10) {
                                        pName = GestorEncuentros.obtenerPokemonAleatorio(nivelDificultad);
                                        attempts++;
                                    }

                                    // Si después de intentos sigue siendo el mismo, cancelar encuentro
                                    if (pName.trim().equalsIgnoreCase(playerMainPokemon)) {
                                        inEncounter = false;
                                        notificationMessage = "El Pokemon salvaje huyó...";
                                        notificationTimer = NOTIFICATION_DURATION;
                                    } else {


                                        // Obtener nivel de investigación actual de la Pokedex
                                        int currentResearchLevel = 0;
                                        if (explorador.getRegistro().getRegistro().containsKey(pName)) {
                                            currentResearchLevel = explorador.getRegistro().getRegistro().get(pName)
                                                    .getNivelInvestigacion();
                                        }

                                        // Crear Pokemon con el nivel de investigación correcto
                                        // Asegura que tengan HP máximo correcto y no aparezcan con 0 HP
                                        Pokemon salvaje = new Pokemon(pName, currentResearchLevel, 0, false, "Normal");

                                        game.setScreen(new BattleScreen(game, this, explorador, salvaje));
                                    }
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
        }

        // Logica florecita y demás
        if (!showMenu && !showDialog && !fadingOut)

        {
            for (RegionTrigger region : regions) {

                float regCenterX = region.bounds.x + region.bounds.width / 2;
                float regCenterY = region.bounds.y + region.bounds.height / 2;

                if (com.badlogic.gdx.math.Vector2.dst(posX, posY, regCenterX, regCenterY) < 60f) {

                    if (currentActiveRegion != region) {
                        currentActiveRegion = region;
                        if (region.signTexture != null) {
                            introTexture = region.signTexture;
                            introState = IntroState.SLIDING_IN;
                            introY = 480;
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

        if (!showMenu && !fadingOut) {
            com.badlogic.gdx.audio.Music targetMusic = null;

            if (npcManager != null) {
                // Chequear cercanía con NPC para que suene la musica
                float minDst = Float.MAX_VALUE;
                for (com.mypokemon.game.objects.NPC npc : npcManager.getAllNPCs()) {
                    if (npc.isClose(posX, posY)) {
                        float dst = com.badlogic.gdx.math.Vector2.dst(posX, posY, npc.getX(), npc.getY());
                        if (dst < minDst) {
                            com.badlogic.gdx.audio.Music m = npc.getMusic();
                            if (m != null) {
                                targetMusic = m;
                                minDst = dst;
                            }
                        }
                    }
                }
            }

            // REPRODUCTOR DE MÚSICA LOGICA
            // Verificar si la primera canción terminó para pasar a la segunda
            if (explorador.isReproductorMusicaActivo()) {
                if (backgroundPlayerMusic != null && !backgroundPlayerMusic.isPlaying() && isPlayingFirstTrack) {
                    // La primera canción terminó, cambiar a la segunda
                    isPlayingFirstTrack = false;
                    targetMusic = backgroundPlayerMusic2;
                } else if (isPlayingFirstTrack) {
                    targetMusic = backgroundPlayerMusic;
                } else {
                    targetMusic = backgroundPlayerMusic2;
                }
            } else {
                // Si se desactiva el reproductor, reiniciar a la primera canción
                isPlayingFirstTrack = true;
            }

            if (targetMusic != null) {

                if (currentNpcMusic != targetMusic) {
                    if (currentNpcMusic != null) {
                        currentNpcMusic.stop();
                    }
                    targetMusic.play();
                    currentNpcMusic = targetMusic;
                } else {

                    if (!currentNpcMusic.isPlaying()) {
                        currentNpcMusic.play();
                    }
                }
            } else {

                if (currentNpcMusic != null) {
                    currentNpcMusic.stop();
                    currentNpcMusic = null;
                }
            }
        } else {

            if (currentNpcMusic != null && currentNpcMusic.isPlaying()) {
                currentNpcMusic.pause();
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

        // RENDER
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
        if (texFlorecita != null)

        {
            for (RegionTrigger r : regions) {
                game.batch.draw(texFlorecita, r.bounds.x, r.bounds.y, r.bounds.width, r.bounds.height);
            }
        }


        game.batch.end();

        if (mapRenderer != null && foregroundLayers != null)
            mapRenderer.render(foregroundLayers);

        game.batch.begin();

        // Draw Lab Sign via Collision Object
        if (puertaLaboratorio != null) {
            puertaLaboratorio.renderizarLetrero(game.batch);
        }
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

        // HUD
        if (gameUI != null) {
            gameUI.renderHUD(game.batch, explorador, showMenu);
        }

        // Notifications
        if (gameUI != null) {
            gameUI.renderNotification(game.batch, notificationMessage);
        }

        // Mision
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

        // NPC ocultos
        if (gameUI != null) {
            // Unified UI Hint
            if (gameUI != null && !showDialog) {
                IInteractivo interactivo = gestorColisiones.obtenerInteractivoMasCercano(posX, posY);
                if (interactivo != null) {
                    gameUI.renderHint(game.batch, interactivo.obtenerMensajeInteraccion());
                }
            }
        }

        // Dialog
        if (showDialog && activeDialogPages != null && currentDialogPage < activeDialogPages.length && gameUI != null) {
            gameUI.renderDialog(game.batch, activeNpcName, activeDialogPages[currentDialogPage], currentPortrait,
                    currentDialogPage < activeDialogPages.length - 1);
        }

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

    /**
     * Dibuja al jugador en su posición actual.
     */
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

    /**
     * Obtiene una propiedad entera de un mapa de propiedades, con un valor por
     * defecto.
     *
     * @param props        Propiedades del mapa.
     * @param key          Clave de la propiedad.
     * @param defaultValue Valor por defecto si no existe o no es válida.
     * @return Valor entero.
     */
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

    /**
     * Se llama cuando cambia el tamaño de la ventana.
     *
     * @param width  Nuevo ancho.
     * @param height Nuevo alto.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Libera los recursos gráficos y de audio cuando la pantalla se cierra.
     */
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
        if (backgroundPlayerMusic != null)
            backgroundPlayerMusic.dispose();

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
        Map<TiledMapTileLayer, TiledMapTileLayer.Cell> cellsPorCapa = new TreeMap<>((a, b) -> {
            // Simple comparator based on hash code or name to satisfy TreeMap requirement
            // for keys
            return Integer.compare(System.identityHashCode(a), System.identityHashCode(b));
        });

        public RecursoMapa(int x, int y, String tipo, int cantidad) {
            this.cellX = x;
            this.cellY = y;
            this.tipo = tipo;
            this.cantidad = cantidad;
        }

        /**
         * Registra una celda del mapa asociada a este recurso.
         *
         * @param layer Capa del mapa.
         * @param cell  Celda del mapa.
         */
        public void registrarCapa(TiledMapTileLayer layer, TiledMapTileLayer.Cell cell) {
            cellsPorCapa.put(layer, cell);
        }
    }

    // Getter for Explorador
    /**
     * Obtiene el objeto Explorador (Jugador).
     *
     * @return Instancia del explorador.
     */
    public Explorador getExplorador() {
        return explorador;
    }

    /**
     * Inicia el efecto de fade out y cambia a la pantalla especificada.
     */
    public void iniciarFadeOut(BaseScreen pantallaDestino) {
        this.fadingOut = true;
        this.nextScreen = pantallaDestino;
    }

    /**
     * Muestra un mensaje de notificación temporal.
     */
    public void mostrarNotificacion(String mensaje) {
        this.notificationMessage = mensaje;
        this.notificationTimer = NOTIFICATION_DURATION;
    }

    /**
     * Obtiene la textura del jugador.
     *
     * @return Textura (Texture).
     */
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
