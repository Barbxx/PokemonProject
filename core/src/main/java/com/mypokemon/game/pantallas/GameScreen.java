package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.Pokemon;
import com.mypokemon.game.Movimiento;
import com.mypokemon.game.GestorEncuentros;
import com.mypokemon.game.InputHandler;
import com.mypokemon.game.JugadorRemoto;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
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
import com.mypokemon.game.colisiones.IInteractivo;
import com.mypokemon.game.colisiones.NPCCollision;
import com.mypokemon.game.colisiones.ColisionPuertaLaboratorio;
import com.mypokemon.game.objects.NPC;

// Pantalla principal del juego donde ocurre la exploración y el movimiento.
public class GameScreen extends BaseScreen {
    private OrthographicCamera camara;
    private Viewport viewport;
    private com.badlogic.gdx.math.Matrix4 matrizUi;

    private TiledMap mapa;
    private OrthogonalTiledMapRenderer renderizadorMapa;
    private TiledMapTileLayer capaColisionesTerreno;
    private int[] capasFondo;
    private int[] capasPrimerPlano;
    private int anchoMapa, altoMapa;

    private float posX, posY;
    private float velocidad = 150f;

    private Texture texturaJugador;
    private Animation<TextureRegion> animAbajo, animIzquierda, animDerecha, animArriba;
    private TextureRegion cuadroActual;
    private float tiempoEstado;
    private boolean enMovimiento;
    private float anchoVisualJugador = 40f, altoVisualJugador = 32f;
    private float anchoColisionJugador = 20f, altoColisionJugador = 24f;
    private String nombreJugador;

    private Explorador explorador;
    private com.mypokemon.game.client.NetworkClient cliente;
    private JugadorRemoto otroJugador;
    private float temporizadorActMov = 0;
    private String rutaMiTextura;

    private com.mypokemon.game.objects.NPCManager gestorNpc;
    private GestorColisiones gestorColisiones;
    private ColisionPuertaLaboratorio puertaLaboratorio;
    private com.mypokemon.game.ui.GameUI interfazJuego;

    private Texture texturaLetreroLab;
    private Texture pixelBlancoUi;
    private boolean mostrarDialogo = false;
    private int paginaDialogoActual = 0;
    private String[] paginasDialogoActivo;

    private boolean mostrarMenu = false;
    private int indiceMenuSeleccionado = 0;
    private String[] opcionesMenu = { "POKÉDEX", "CRAFTEO", "MOCHILA", "GUARDAR", "PERFIL", "INICIO" };

    private enum EstadoIntro {
        DESLIZANDO_ENTRADA, ESPERANDO, DESLIZANDO_SALIDA, FINALIZADO
    }

    private Texture texturaIntro;
    private EstadoIntro estadoIntro;
    private float introY;
    private float velocidadIntro = 200f;
    private Texture texturaAviso;

    private static final float INTERVALO_HIERBA = 1.0f;
    private static final float DURACION_NOTIFICACION = 3.0f;

    private float temporizadorEncuentro = 0;
    private int ultimoTileHierbaX = -1, ultimoTileHierbaY = -1;
    private boolean enEncuentro = false;
    private Sound sonidoHierba;

    private List<RecursoMapa> recursosMapa = new ArrayList<>();
    private String mensajeNotificacion = "";
    private float temporizadorNotificacion = 0;
    private float temporizadorMisionFinalizada = 0;

    private com.badlogic.gdx.audio.Music musicaReproductorFondo = null;
    private int numColsFrames, numFilasFrames;

    private class DisparadorRegion {
        Rectangle limites;
        Texture texturaLetrero;

        public DisparadorRegion(float x, float y, float w, float h, Texture t) {
            this.limites = new Rectangle(x, y, w, h);
            this.texturaLetrero = t;
        }
    }

    private List<DisparadorRegion> regiones = new ArrayList<>();
    private Texture texCostaCobalto, texPantanal, texLadera, texTundra;
    private DisparadorRegion regionActivaActual = null;

    private float alfaFade = 1f;
    private boolean apareciendo = true, desapareciendo = false;
    private BaseScreen pantallaSiguiente;

    public GameScreen(final PokemonMain juego, String rutaTextura, int cols, int filas, String nombreJugador,
            String nombrePartida) {
        super(juego);
        this.numColsFrames = cols;
        this.numFilasFrames = filas;
        this.nombreJugador = nombreJugador;
        this.rutaMiTextura = rutaTextura;

        gestorNpc = new com.mypokemon.game.objects.NPCManager();
        interfazJuego = new com.mypokemon.game.ui.GameUI();
        gestorColisiones = new GestorColisiones();

        com.mypokemon.game.utils.Genero generoEnum = (rutaTextura != null && rutaTextura.toLowerCase().contains("fem"))
                ? com.mypokemon.game.utils.Genero.CHICA
                : com.mypokemon.game.utils.Genero.CHICO;
        this.explorador = Explorador.cargarProgreso(nombrePartida + " - " + nombreJugador + ".dat");
        if (this.explorador == null)
            this.explorador = Explorador.cargarProgreso(nombreJugador + " - " + nombrePartida + ".dat");
        if (this.explorador == null)
            this.explorador = Explorador.cargarProgreso(nombrePartida);
        if (this.explorador == null)
            this.explorador = new Explorador(nombreJugador, nombrePartida, 80, generoEnum);
        else {
            this.rutaMiTextura = com.mypokemon.game.utils.Genero.CHICA.equals(this.explorador.obtenerGenero())
                    ? "protagonistaFemenino.png"
                    : "protagonistaMasculino1.png";
            this.nombreJugador = this.explorador.obtenerNombre();
        }

        camara = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camara);
        matrizUi = new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, 800, 480);

        try {
            texturaIntro = cargarTextura("letreroPraderaObsidiana.png");
            estadoIntro = EstadoIntro.DESLIZANDO_ENTRADA;
            introY = 480;
            texturaAviso = cargarTextura("Aviso.png");
            sonidoHierba = Gdx.audio.newSound(Gdx.files.internal("hierba.mp3"));
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error cargando texturas intro: " + e.getMessage());
        }

        pixelBlancoUi = TextureUtils.createSolidTexture(1, 1, Color.WHITE);
        agregarTextura(pixelBlancoUi);

        try {
            TmxMapLoader loader = new TmxMapLoader(new InternalFileHandleResolver());
            mapa = loader.load("Mapa_Hisui.tmx");
            renderizadorMapa = new OrthogonalTiledMapRenderer(mapa, juego.batch);

            for (MapLayer capa : mapa.getLayers()) {
                if (capa instanceof TiledMapTileLayer && capa.getName().equalsIgnoreCase("Objetos_colisión")) {
                    capaColisionesTerreno = (TiledMapTileLayer) capa;
                    gestorColisiones.establecerCapaColisionTerreno(capaColisionesTerreno);
                    break;
                }
            }

            anchoMapa = obtenerPropiedadEntera(mapa.getProperties(), "width", 100)
                    * obtenerPropiedadEntera(mapa.getProperties(), "tilewidth", 32);
            altoMapa = obtenerPropiedadEntera(mapa.getProperties(), "height", 100)
                    * obtenerPropiedadEntera(mapa.getProperties(), "tileheight", 32);

            com.badlogic.gdx.utils.IntArray listaFondo = new com.badlogic.gdx.utils.IntArray();
            com.badlogic.gdx.utils.IntArray listaPrimerPlano = new com.badlogic.gdx.utils.IntArray();
            for (int i = 0; i < mapa.getLayers().getCount(); i++) {
                MapLayer capa = mapa.getLayers().get(i);
                if (capa instanceof TiledMapTileLayer) {
                    if (capa.getName().toLowerCase().contains("superior"))
                        listaPrimerPlano.add(i);
                    else
                        listaFondo.add(i);
                }
            }
            capasFondo = listaFondo.toArray();
            capasPrimerPlano = listaPrimerPlano.toArray();

            MapLayer capaSpawn = mapa.getLayers().get("Spawn_Player");
            if (capaSpawn != null) {
                for (MapObject obj : capaSpawn.getObjects()) {
                    if ("inicio".equals(obj.getName()) && obj instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                        posX = rect.x + rect.width / 2;
                        posY = rect.y + rect.height / 2;
                        break;
                    }
                }
            } else {
                posX = anchoMapa / 2;
                posY = altoMapa / 2;
            }
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error cargando mapa Mapa_Hisui.tmx: " + e.getMessage());
            e.printStackTrace();
            posX = 1600;
            posY = 1600;
            anchoMapa = 3200;
            altoMapa = 3200;
        }

        if (capaColisionesTerreno != null) {
            for (int y = 0; y < capaColisionesTerreno.getHeight(); y++) {
                for (int x = 0; x < capaColisionesTerreno.getWidth(); x++) {
                    TiledMapTileLayer.Cell celda = capaColisionesTerreno.getCell(x, y);
                    if (celda != null && celda.getTile() != null) {
                        Object tipoRecurso = celda.getTile().getProperties().get("TipoRecurso");
                        if (tipoRecurso != null) {
                            int cantidad = 1;
                            Object cantObj = celda.getTile().getProperties().get("Cantidad");
                            if (cantObj instanceof Integer)
                                cantidad = (Integer) cantObj;
                            RecursoMapa recurso = new RecursoMapa(x, y, tipoRecurso.toString(), cantidad);
                            for (MapLayer capa : mapa.getLayers()) {
                                if (capa instanceof TiledMapTileLayer) {
                                    TiledMapTileLayer tLayer = (TiledMapTileLayer) capa;
                                    TiledMapTileLayer.Cell lCell = tLayer.getCell(x, y);
                                    if (lCell != null)
                                        recurso.registrarCapa(tLayer, lCell);
                                }
                            }
                            recursosMapa.add(recurso);
                        }
                    }
                }
            }
        }

        camara.position.set(posX, posY, 10);
        camara.update();

        try {
            texturaJugador = cargarTextura(rutaMiTextura);
            if (texturaJugador != null) {
                texturaJugador.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                int frameW = texturaJugador.getWidth() / cols, frameH = texturaJugador.getHeight() / filas;
                TextureRegion[][] frames = TextureRegion.split(texturaJugador, frameW, frameH);
                if (frames.length >= 4) {
                    animAbajo = new Animation<>(0.15f, frames[0]);
                    animIzquierda = new Animation<>(0.15f, frames[1]);
                    animDerecha = new Animation<>(0.15f, frames[2]);
                    animArriba = new Animation<>(0.15f, frames[3]);
                    cuadroActual = frames[0][0];
                } else
                    crearRespaldo();
            } else
                crearRespaldo();
        } catch (Exception e) {
            crearRespaldo();
        }

        texturaLetreroLab = cargarTextura("letreroLaboratorio.png");
        gestorNpc.agregarNpc(new com.mypokemon.game.objects.FeidNPC(posX - 220, posY - 20));
        gestorNpc.agregarNpc(new com.mypokemon.game.objects.HarryPotterNPC(posX + 2100, posY + 900));
        gestorNpc.agregarNpc(new com.mypokemon.game.objects.HarryStylesNPC(posX + 1110, posY - 320));
        gestorNpc.agregarNpc(new com.mypokemon.game.objects.BrennerNPC(posX + 480, posY + 620));
        for (NPC npc : gestorNpc.getTodosLosNpcs())
            gestorColisiones.agregarColision(new NPCCollision(npc));

        puertaLaboratorio = new ColisionPuertaLaboratorio(posX - 120, posY - 185, texturaLetreroLab, juego, this,
                explorador);
        gestorColisiones.agregarColision(puertaLaboratorio);

        juego.fuente.setUseIntegerPositions(true);

        texCostaCobalto = cargarTextura("letreroCostaCobalto.png");
        texPantanal = cargarTextura("letreroPantanalCarmesí.png");
        texLadera = cargarTextura("letreroLaderaCorona.png");
        texTundra = cargarTextura("letreroTundraAlba.png");

        regiones.add(new DisparadorRegion(posX + 1250, posY - 360, 60, 60, texCostaCobalto));
        regiones.add(new DisparadorRegion(posX + 2050, posY + 890, 60, 60, texPantanal));
        regiones.add(new DisparadorRegion(posX + 800, posY + 1700, 60, 60, texLadera));
        regiones.add(new DisparadorRegion(posX + 50, posY + 850, 60, 60, texTundra));

        try {
            musicaReproductorFondo = Gdx.audio.newMusic(Gdx.files.internal("audioReproductor.mp3"));
            musicaReproductorFondo.setLooping(true);
            musicaReproductorFondo.setVolume(0.5f);
        } catch (Exception e) {
        }

        Gdx.app.log("GameScreen", "GameScreen constructor completado exitosamente para jugador: " + nombreJugador);
    }

    @Override
    public void show() {
        enEncuentro = false;
        if (explorador != null)
            Gdx.input.setInputProcessor(new InputHandler(this.explorador));
        apareciendo = true;
        desapareciendo = false;
        alfaFade = 1f;
        pantallaSiguiente = null;
        this.cliente = juego.clienteRed;
        if (this.cliente != null) {
            this.cliente.establecerEscucha(msg -> Gdx.app.postRunnable(() -> procesarMensajeRed(msg)));
            cliente.enviarMensaje(
                    "IDENTITY:" + nombreJugador + ":" + (rutaMiTextura.contains("fem") ? "CHICA" : "CHICO"));
        }
    }

    private void procesarMensajeRed(String msg) {
        try {
            if (msg.startsWith("MOVE:")) {
                String[] p = msg.split(":");
                if (p.length >= 4 && otroJugador != null)
                    otroJugador.actualizar(Gdx.graphics.getDeltaTime(), Float.parseFloat(p[1]), Float.parseFloat(p[2]),
                            com.mypokemon.game.utils.Direccion.fromString(p[3]));
            } else if (msg.startsWith("PEER_INFO:")) {
                String[] p = msg.split(":");
                if (p.length >= 2) {
                    Texture t = new Texture("CHICA".equals(p.length > 2 ? p[2] : "CHICO") ? "protagonistaFemenino.png"
                            : "protagonistaMasculino1.png");
                    float ox = otroJugador != null ? otroJugador.x : 0, oy = otroJugador != null ? otroJugador.y : 0;
                    otroJugador = new JugadorRemoto(t, numColsFrames, numFilasFrames);
                    otroJugador.nombre = p[1];
                    otroJugador.x = ox;
                    otroJugador.y = oy;
                }
            } else if (msg.equals("SAVE_CONFIRMED"))
                mostrarNotificacion("¡Partida Guardada!");
            else if (msg.startsWith("RESOURCE_REMOVED:"))
                eliminarRecursoPorId(msg.substring(17));
        } catch (Exception e) {
        }
    }

    private void eliminarRecursoPorId(String id) {
        try {
            String[] p = id.split("_");
            int cx = Integer.parseInt(p[0]), cy = Integer.parseInt(p[1]);
            for (RecursoMapa r : recursosMapa)
                if (r.cellX == cx && r.cellY == cy && !r.recolectado) {
                    r.recolectado = true;
                    for (TiledMapTileLayer c : r.capasPorCelda.keySet())
                        c.setCell(r.cellX, r.cellY, null);
                    break;
                }
        } catch (Exception e) {
        }
    }

    @Override
    public void render(float delta) {
        explorador.actualizarTemporizadores(delta);
        if (apareciendo) {
            alfaFade -= delta * 1.5f;
            if (alfaFade <= 0) {
                alfaFade = 0;
                apareciendo = false;
            }
        }
        if (desapareciendo) {
            alfaFade += delta * 1.5f;
            if (alfaFade >= 1) {
                alfaFade = 1;
                if (pantallaSiguiente != null)
                    juego.setScreen(pantallaSiguiente);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            mostrarMenu = !mostrarMenu;
            if (mostrarMenu)
                mostrarDialogo = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.K))
            juego.setScreen(new PokedexScreen(juego, this, explorador));

        if (mostrarMenu) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
                indiceMenuSeleccionado = (indiceMenuSeleccionado <= 0) ? opcionesMenu.length - 1
                        : indiceMenuSeleccionado - 1;
            else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
                indiceMenuSeleccionado = (indiceMenuSeleccionado >= opcionesMenu.length - 1) ? 0
                        : indiceMenuSeleccionado + 1;
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                String sel = opcionesMenu[indiceMenuSeleccionado];
                if (sel.equals("INICIO")) {
                    juego.setScreen(new MainMenuScreen(juego));
                    dispose();
                } else if (sel.equals("MOCHILA"))
                    juego.setScreen(new MochilaScreen(juego, this, explorador));
                else if (sel.equals("CRAFTEO"))
                    juego.setScreen(new CrafteoScreen(juego, this));
                else if (sel.equals("PERFIL"))
                    juego.setScreen(new PerfilScreen(juego, this, explorador));
                else if (sel.equals("GUARDAR")) {
                    if (cliente != null)
                        cliente.enviarMensaje("SAVE_GAME:" + explorador.guardarProgreso()); // Wait, this is serialize,
                                                                                            // but let's check
                    else if (explorador.guardarProgreso())
                        mostrarNotificacion("¡Partida Guardada!");
                }
                mostrarMenu = false;
            }
        }

        if (!mostrarMenu && !mostrarDialogo && !enEncuentro && !apareciendo && !desapareciendo)
            actualizarMovimiento(delta);
        actualizarCamara();
        renderizarMundo();
        renderizarInterfaz(delta);
    }

    private void actualizarMovimiento(float delta) {
        float dx = 0, dy = 0;
        com.mypokemon.game.utils.Direccion dir = null;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx = -velocidad * delta;
            dir = com.mypokemon.game.utils.Direccion.IZQUIERDA;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx = velocidad * delta;
            dir = com.mypokemon.game.utils.Direccion.DERECHA;
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            dy = velocidad * delta;
            dir = com.mypokemon.game.utils.Direccion.ARRIBA;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            dy = -velocidad * delta;
            dir = com.mypokemon.game.utils.Direccion.ABAJO;
        }

        enMovimiento = (dx != 0 || dy != 0);
        if (enMovimiento) {
            float nx = posX + dx, ny = posY + dy;
            if (gestorColisiones.verificarTodasLasColisiones(nx, ny, anchoColisionJugador, altoColisionJugador)) {
                if (!gestorColisiones.verificarTodasLasColisiones(posX, ny, anchoColisionJugador, altoColisionJugador))
                    posY = ny;
                else if (!gestorColisiones.verificarTodasLasColisiones(nx, posY, anchoColisionJugador,
                        altoColisionJugador))
                    posX = nx;
            } else {
                posX = nx;
                posY = ny;
            }
            tiempoEstado += delta;
            switch (dir) {
                case IZQUIERDA:
                    cuadroActual = animIzquierda.getKeyFrame(tiempoEstado, true);
                    break;
                case DERECHA:
                    cuadroActual = animDerecha.getKeyFrame(tiempoEstado, true);
                    break;
                case ARRIBA:
                    cuadroActual = animArriba.getKeyFrame(tiempoEstado, true);
                    break;
                case ABAJO:
                    cuadroActual = animAbajo.getKeyFrame(tiempoEstado, true);
                    break;
            }
            temporizadorActMov += delta;
            if (temporizadorActMov >= 0.05f && cliente != null) {
                cliente.enviarMensaje("MOVE:" + posX + ":" + posY + ":" + dir.toString());
                temporizadorActMov = 0;
            }
            verificarEncuentroHierba(delta);
        } else {
            if (animAbajo != null)
                cuadroActual = animAbajo.getKeyFrame(0);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            IInteractivo inter = gestorColisiones.obtenerInteractivoMasCercano(posX, posY);
            if (inter != null)
                inter.interactuar();
            else
                recolectarRecurso();
        }
    }

    private void recolectarRecurso() {
        for (RecursoMapa r : recursosMapa) {
            if (!r.recolectado && Math.abs(posX - (r.cellX * 32 + 16)) < 32
                    && Math.abs(posY - (r.cellY * 32 + 16)) < 32) {
                r.recolectado = true;
                for (TiledMapTileLayer c : r.capasPorCelda.keySet())
                    c.setCell(r.cellX, r.cellY, null);
                try {
                    explorador.obtenerMochila()
                            .agregarItem(com.mypokemon.game.inventario.ObjectFactory.crearObjeto(r.tipo, r.cantidad));
                    mostrarNotificacion("Recolectaste " + r.cantidad + " " + r.tipo);
                } catch (com.mypokemon.game.inventario.exceptions.SpaceException e) {
                    mostrarNotificacion("¡Mochila llena! No pudiste recolectar.");
                    r.recolectado = false; // Revertir para que se pueda intentar de nuevo
                    // Re-add to map layers
                    for (Map.Entry<TiledMapTileLayer, TiledMapTileLayer.Cell> entry : r.capasPorCelda.entrySet()) {
                        entry.getKey().setCell(r.cellX, r.cellY, entry.getValue());
                    }
                    if (cliente != null)
                        cliente.enviarMensaje("CANCEL_REMOVE_RESOURCE:" + r.cellX + "_" + r.cellY);
                    break;
                }
                if (cliente != null)
                    cliente.enviarMensaje("REMOVE_RESOURCE:" + r.cellX + "_" + r.cellY);
                break;
            }
        }
    }

    private void verificarEncuentroHierba(float delta) {
        int tx = (int) (posX / 32), ty = (int) (posY / 32);
        if (capaColisionesTerreno != null) {
            TiledMapTileLayer.Cell c = capaColisionesTerreno.getCell(tx, ty);
            if (c != null && c.getTile() != null && "Hierba".equals(c.getTile().getProperties().get("tipo"))) {
                temporizadorEncuentro += delta;
                if (temporizadorEncuentro >= INTERVALO_HIERBA && (tx != ultimoTileHierbaX || ty != ultimoTileHierbaY)) {
                    if (sonidoHierba != null)
                        sonidoHierba.play(0.3f);
                    if (GestorEncuentros.verificarEncuentro(1)) {
                        enEncuentro = true;
                        String pk = GestorEncuentros.obtenerPokemonAleatorio(1);
                        juego.setScreen(new BattleScreen(juego, this, explorador, new Pokemon(pk, 5, 100, false, "")));
                    }
                    temporizadorEncuentro = 0;
                    ultimoTileHierbaX = tx;
                    ultimoTileHierbaY = ty;
                }
            }
        }
    }

    private void actualizarCamara() {
        float cx = Math.max(viewport.getWorldWidth() / 2, Math.min(posX, anchoMapa - viewport.getWorldWidth() / 2));
        float cy = Math.max(viewport.getWorldHeight() / 2, Math.min(posY, altoMapa - viewport.getWorldHeight() / 2));
        camara.position.set(cx, cy, 0);
        camara.update();
    }

    private void renderizarMundo() {
        if (renderizadorMapa != null) {
            renderizadorMapa.setView(camara);
            renderizadorMapa.render(capasFondo);
            juego.batch.begin();
            if (otroJugador != null) {
                juego.batch.draw(otroJugador.cuadroActual, otroJugador.x - 20, otroJugador.y - 16, 40, 32 * 1.2f);
                juego.fuente.getData().setScale(0.7f);
                juego.fuente.draw(juego.batch, otroJugador.nombre, otroJugador.x - 20, otroJugador.y + 30, 40, 1,
                        false);
            }
            dibujarJugador();
            gestorNpc.renderizar(juego.batch);
            juego.batch.end();
            renderizadorMapa.render(capasPrimerPlano);
        }
    }

    private void dibujarJugador() {
        if (cuadroActual != null) {
            juego.batch.draw(cuadroActual, posX - anchoVisualJugador / 2, posY - altoVisualJugador / 2,
                    anchoVisualJugador, altoVisualJugador * 1.2f);
            juego.fuente.getData().setScale(0.8f);
            juego.fuente.setColor(Color.WHITE);
            juego.fuente.draw(juego.batch, nombreJugador, posX - anchoVisualJugador / 2,
                    posY + altoVisualJugador * 0.8f, anchoVisualJugador, 1, false);
        }
    }

    private void renderizarInterfaz(float delta) {
        juego.batch.setProjectionMatrix(matrizUi);
        juego.batch.begin();
        if (mostrarDialogo) {
            juego.batch.setColor(0, 0, 0, 0.7f);
            juego.batch.draw(pixelBlancoUi, 100, 50, 600, 100);
            juego.batch.setColor(Color.WHITE);
            juego.fuente.getData().setScale(1.2f);
            juego.fuente.draw(juego.batch, paginasDialogoActivo[paginaDialogoActual], 120, 115, 560, 1, true);
        }
        if (mostrarMenu) {
            juego.batch.setColor(0, 0, 0, 0.8f);
            juego.batch.draw(pixelBlancoUi, 600, 150, 180, 240);
            for (int i = 0; i < opcionesMenu.length; i++) {
                juego.fuente.setColor(i == indiceMenuSeleccionado ? Color.YELLOW : Color.WHITE);
                juego.fuente.draw(juego.batch, opcionesMenu[i], 620, 370 - i * 40);
            }
        }
        if (temporizadorNotificacion > 0) {
            temporizadorNotificacion -= delta;
            juego.fuente.getData().setScale(1.5f);
            juego.fuente.draw(juego.batch, mensajeNotificacion, 0, 70, 800, 1, false);
        }
        juego.batch.end();
    }

    public Explorador obtenerExplorador() {
        return explorador;
    }

    public void iniciarFadeOut(BaseScreen destino) {
        desapareciendo = true;
        pantallaSiguiente = destino;
    }

    public void mostrarNotificacion(String m) {
        mensajeNotificacion = m;
        temporizadorNotificacion = DURACION_NOTIFICACION;
    }

    public Texture obtenerHojaJugador() {
        return texturaJugador;
    }

    public int obtenerColsFrames() {
        return numColsFrames;
    }

    public int obtenerFilasFrames() {
        return numFilasFrames;
    }

    private int obtenerPropiedadEntera(com.badlogic.gdx.maps.MapProperties p, String k, int d) {
        Object v = p.get(k);
        if (v instanceof Integer)
            return (Integer) v;
        return d;
    }

    private void crearRespaldo() {
        texturaJugador = TextureUtils.createSolidTexture(239, 256, Color.MAGENTA);
        TextureRegion r = new TextureRegion(texturaJugador);
        animAbajo = animIzquierda = animDerecha = animArriba = new Animation<>(0.15f, new TextureRegion[] { r });
        cuadroActual = r;
    }

    private static class RecursoMapa {
        int cellX, cellY, cantidad;
        String tipo;
        boolean recolectado = false;
        Map<TiledMapTileLayer, TiledMapTileLayer.Cell> capasPorCelda = new TreeMap<>(
                (a, b) -> Integer.compare(System.identityHashCode(a), System.identityHashCode(b)));

        public RecursoMapa(int x, int y, String t, int c) {
            this.cellX = x;
            this.cellY = y;
            this.tipo = t;
            this.cantidad = c;
        }

        public void registrarCapa(TiledMapTileLayer l, TiledMapTileLayer.Cell c) {
            capasPorCelda.put(l, c);
        }
    }
}
