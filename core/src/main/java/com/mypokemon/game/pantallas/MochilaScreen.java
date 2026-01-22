package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.Pokemon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Screen;
import com.mypokemon.game.inventario.Item;

// Pantalla del inventario (Mochila). Permite gestionar objetos y el equipo Pokémon.
public class MochilaScreen extends BaseScreen {
    private final Explorador explorador;
    private Texture texturaFondo;
    private Texture[] botonesNormales, botonesSeleccionados;
    private String[] nombresBotones = { "botonIrojo", "botonIazul", "botonIamarillo", "botonImorado" };

    private Texture texturaPokebola, texturaPlanta, texturaGuijarro, texturaBaya;
    private Texture texturaPocionHerbal, texturaElixir, texturaRevivir, texturaReproductor, texturaGuante;
    private Texture texturaHeavyBall, texturaFrijol, texturaFondoEspacio, pixelBlanco;
    private com.badlogic.gdx.graphics.g2d.BitmapFont fuenteContador;

    private java.util.Map<String, Texture> cacheTexturasPokemon = new java.util.TreeMap<>();

    public class DatosElemento {
        public String nombre, descripcion;
        public Texture textura;
        public int cantidad;
        public Item objetoReal;

        public DatosElemento(String n, String d, Texture t, int c, Item o) {
            this.nombre = n;
            this.descripcion = d;
            this.textura = t;
            this.cantidad = c;
            this.objetoReal = o;
        }

        public DatosElemento(String n, String d, Texture t, int c) {
            this(n, d, t, c, null);
        }
    }

    private List<DatosElemento> elementosVisibles = new ArrayList<>();
    private float anchoBoton = 140, altoBoton = 140;
    private float[][] posicionesBotones;
    private int indiceCategoriaSeleccionada = 0, indiceElementoSeleccionado = 0;
    private OrthographicCamera camara;
    private Viewport viewport;
    private final Screen pantallaRetorno;
    private Vector3 mousePos = new Vector3();

    private enum EstadoInventario {
        NAVEGANDO, MENU_OPCIONES, SELECCIONAR_POKEMON_OBJETIVO
    }

    private EstadoInventario estadoActual = EstadoInventario.NAVEGANDO;
    private List<String> opcionesActuales = new ArrayList<>();
    private int indiceOpcionSeleccionada = 0;
    private DatosElemento elementoSeleccionadoParaAccion = null;
    private String mensajeFeedback = "";
    private float temporizadorFeedback = 0;

    public MochilaScreen(PokemonMain juego, Screen pantallaRetorno, Explorador explorador) {
        super(juego);
        this.pantallaRetorno = pantallaRetorno;
        this.explorador = explorador;
        camara = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camara);
        camara.position.set(640, 360, 0);

        botonesNormales = new Texture[nombresBotones.length];
        botonesSeleccionados = new Texture[nombresBotones.length];
        try {
            texturaFondo = cargarTextura("fondoMochila.png");
            for (int i = 0; i < nombresBotones.length; i++) {
                botonesNormales[i] = cargarTextura(nombresBotones[i] + ".png");
                botonesSeleccionados[i] = cargarTextura(nombresBotones[i] + "_seleccionado.png");
            }
            texturaPokebola = cargarTextura("pokeball.png");
            texturaPlanta = cargarTextura("planta.png");
            texturaGuijarro = cargarTextura("guijarro.png");
            texturaBaya = cargarTextura("baya.png");
            texturaPocionHerbal = cargarTextura("pocionherbal.png");
            texturaElixir = cargarTextura("elixirPielPiedra.png");
            texturaRevivir = cargarTextura("revivircasero.png");
            texturaReproductor = cargarTextura("reproductor.png");
            texturaGuante = cargarTextura("guanteReflejo.png");
            texturaHeavyBall = cargarTextura("pokeballpeso.png");
            texturaFrijol = cargarTextura("frijolMagico.png");
        } catch (Exception e) {
            Gdx.app.error("Mochila", "Error cargando texturas");
        }

        posicionesBotones = new float[nombresBotones.length][2];
        float anchoTotal = (nombresBotones.length * anchoBoton) + ((nombresBotones.length - 1) * 20);
        float startX = (1280 - anchoTotal) / 2, startY = 720 - 220;
        for (int i = 0; i < nombresBotones.length; i++) {
            posicionesBotones[i][0] = startX + i * (anchoBoton + 20);
            posicionesBotones[i][1] = startY;
        }

        com.badlogic.gdx.graphics.Pixmap px = new com.badlogic.gdx.graphics.Pixmap(1, 1,
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        px.setColor(Color.WHITE);
        px.fill();
        pixelBlanco = new Texture(px);
        agregarTextura(pixelBlanco);
        px.setColor(0, 0, 0, 0.5f);
        px.fill();
        texturaFondoEspacio = new Texture(px);
        agregarTextura(texturaFondoEspacio);
        px.dispose();

        fuenteContador = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        fuenteContador.getData().setScale(2.0f);
        actualizarElementosVisibles();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    public void establecerIndiceSeleccion(int i) {
        this.indiceCategoriaSeleccionada = i;
        actualizarElementosVisibles();
        this.indiceElementoSeleccionado = 0;
    }

    @Override
    public void render(float delta) {
        if (temporizadorFeedback > 0)
            temporizadorFeedback -= delta;
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        juego.batch.setProjectionMatrix(camara.combined);
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mousePos);

        juego.batch.begin();
        if (texturaFondo != null)
            juego.batch.draw(texturaFondo, 0, 0, 1280, 720);

        if (estadoActual == EstadoInventario.NAVEGANDO)
            gestionarEntradaNavegacion();
        else if (estadoActual == EstadoInventario.MENU_OPCIONES)
            gestionarEntradaMenuOpciones();
        else if (estadoActual == EstadoInventario.SELECCIONAR_POKEMON_OBJETIVO)
            gestionarEntradaSeleccionPokemon();

        if (estadoActual == EstadoInventario.SELECCIONAR_POKEMON_OBJETIVO && indiceCategoriaSeleccionada != 3) {
            indiceCategoriaSeleccionada = 3;
            actualizarElementosVisibles();
        }

        dibujarPestanas(juego.batch);
        dibujarEspaciosCuadricula(juego.batch);
        dibujarContenidoCuadricula(juego.batch);

        if (estadoActual == EstadoInventario.NAVEGANDO)
            dibujarExplicacion(juego.batch);
        else if (estadoActual == EstadoInventario.MENU_OPCIONES) {
            dibujarMenuOpciones(juego.batch);
            dibujarExplicacion(juego.batch);
        } else if (estadoActual == EstadoInventario.SELECCIONAR_POKEMON_OBJETIVO)
            dibujarInstruccionSeleccion(juego.batch);

        if (temporizadorFeedback > 0) {
            juego.fuente.setColor(Color.RED);
            juego.fuente.getData().setScale(2.0f);
            juego.fuente.draw(juego.batch, mensajeFeedback, 100, 80);
            juego.fuente.getData().setScale(1.0f);
            juego.fuente.setColor(Color.WHITE);
        }
        juego.batch.end();
    }

    private void gestionarEntradaNavegacion() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            juego.setScreen(pantallaRetorno);
            return;
        }
        gestionarNavegacionCuadricula();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            int total = (indiceCategoriaSeleccionada == 3) ? explorador.obtenerEquipo().size()
                    : elementosVisibles.size();
            if (indiceElementoSeleccionado < total) {
                elementoSeleccionadoParaAccion = (indiceCategoriaSeleccionada < 3)
                        ? elementosVisibles.get(indiceElementoSeleccionado)
                        : null;
                abrirMenuOpciones(elementoSeleccionadoParaAccion);
            }
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (mousePos.x >= 100 && mousePos.x <= 250 && mousePos.y >= 160 && mousePos.y <= 200) {
                int total = (indiceCategoriaSeleccionada == 3) ? explorador.obtenerEquipo().size()
                        : elementosVisibles.size();
                if (indiceElementoSeleccionado < total) {
                    elementoSeleccionadoParaAccion = (indiceCategoriaSeleccionada < 3)
                            ? elementosVisibles.get(indiceElementoSeleccionado)
                            : null;
                    abrirMenuOpciones(elementoSeleccionadoParaAccion);
                }
            }
        }
    }

    private void gestionarEntradaMenuOpciones() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            estadoActual = EstadoInventario.NAVEGANDO;
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            indiceOpcionSeleccionada = (indiceOpcionSeleccionada <= 0) ? opcionesActuales.size() - 1
                    : indiceOpcionSeleccionada - 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            indiceOpcionSeleccionada = (indiceOpcionSeleccionada >= opcionesActuales.size() - 1) ? 0
                    : indiceOpcionSeleccionada + 1;
        }
        float mx = 100, my = 720 - 150;
        for (int i = 0; i < opcionesActuales.size(); i++) {
            float y = my - (i * 50);
            if (mousePos.x >= mx && mousePos.x <= mx + 250 && mousePos.y >= y - 40 && mousePos.y <= y) {
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    indiceOpcionSeleccionada = i;
                    ejecutarOpcion(opcionesActuales.get(i));
                    return;
                }
                indiceOpcionSeleccionada = i;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
            ejecutarOpcion(opcionesActuales.get(indiceOpcionSeleccionada));
    }

    private void gestionarEntradaSeleccionPokemon() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            estadoActual = EstadoInventario.MENU_OPCIONES;
            indiceCategoriaSeleccionada = 0;
            actualizarElementosVisibles();
            return;
        }
        gestionarNavegacionCuadricula();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            List<Pokemon> e = explorador.obtenerEquipo();
            if (indiceElementoSeleccionado < e.size())
                aplicarObjetoAPokemon(e.get(indiceElementoSeleccionado));
        }
    }

    private void abrirMenuOpciones(DatosElemento el) {
        opcionesActuales.clear();
        if (indiceCategoriaSeleccionada == 3)
            opcionesActuales.add("Cambiar a principal");
        else if (el != null && el.objetoReal != null)
            opcionesActuales.addAll(el.objetoReal.obtenerOpciones());
        else
            opcionesActuales.add("Tirar");
        indiceOpcionSeleccionada = 0;
        estadoActual = EstadoInventario.MENU_OPCIONES;
    }

    private void ejecutarOpcion(String op) {
        if (elementoSeleccionadoParaAccion == null && indiceCategoriaSeleccionada != 3)
            return;
        if (op.equals("Cambiar a principal")) {
            int idx = indiceElementoSeleccionado;
            List<Pokemon> eq = explorador.obtenerEquipo();
            if (idx > 0 && idx < eq.size()) {
                Pokemon sel = eq.get(idx), act = eq.get(0);
                eq.set(0, sel);
                eq.set(idx, act);
                mostrarFeedback("¡" + sel.obtenerNombre() + " es ahora el principal!");
                if (pantallaRetorno instanceof BattleScreen)
                    ((BattleScreen) pantallaRetorno).cambiarPokemon(sel);
            } else
                mostrarFeedback("Ya es el principal.");
            estadoActual = EstadoInventario.NAVEGANDO;
            return;
        }
        if (op.equals("Tirar")) {
            explorador.obtenerMochila().consumirItem(mapearNombreAInterno(elementoSeleccionadoParaAccion.nombre), 1);
            mostrarFeedback("Tiraste 1 " + elementoSeleccionadoParaAccion.nombre);
            actualizarElementosVisibles();
            estadoActual = EstadoInventario.NAVEGANDO;
            return;
        }
        if (op.equals("Lanzar")) {
            if (pantallaRetorno instanceof BattleScreen) {
                if (explorador.obtenerMochila().consumirItem(
                        mapearNombreAInterno(elementoSeleccionadoParaAccion.nombre),
                        1)) {
                    ((BattleScreen) pantallaRetorno)
                            .usarItemEnBatalla(mapearNombreAInterno(elementoSeleccionadoParaAccion.nombre));
                    juego.setScreen(pantallaRetorno);
                }
            } else {
                mostrarFeedback("Solo en batalla.");
                estadoActual = EstadoInventario.NAVEGANDO;
            }
            return;
        }
        if (op.equals("Información")) {
            estadoActual = EstadoInventario.NAVEGANDO;
            return;
        }
        if (elementoSeleccionadoParaAccion.objetoReal != null && elementoSeleccionadoParaAccion.objetoReal.esUsable()) {
            if (op.equals("Equipar") || op.equals("Encender") || op.equals("Apagar") || op.equals("Tomar")) {
                if (op.equals("Tomar") && !(pantallaRetorno instanceof BattleScreen)) {
                    mostrarFeedback("Solo en batalla.");
                    estadoActual = EstadoInventario.NAVEGANDO;
                    return;
                }
                if (op.equals("Equipar") && elementoSeleccionadoParaAccion.objetoReal.obtenerId().equals("guante"))
                    explorador.activarGuante(300f);
                Pokemon target = (op.equals("Tomar") && pantallaRetorno instanceof BattleScreen)
                        ? ((BattleScreen) pantallaRetorno).obtenerPokemonJugador()
                        : null;
                com.mypokemon.game.inventario.ResultadoUso res = ((com.mypokemon.game.inventario.interfaces.IUsable) elementoSeleccionadoParaAccion.objetoReal)
                        .usar(target, explorador.obtenerMochila());
                mostrarFeedback(res.getMensaje());
                actualizarElementosVisibles();
                estadoActual = EstadoInventario.NAVEGANDO;
                return;
            }
            if (op.equals("Curar") || op.equals("Revivir") || op.equals("Usar") || op.equals("Comer")) {
                indiceCategoriaSeleccionada = 3;
                actualizarElementosVisibles();
                indiceElementoSeleccionado = 0;
            }
            estadoActual = EstadoInventario.SELECCIONAR_POKEMON_OBJETIVO;
        } else {
            mostrarFeedback("No se puede usar.");
            estadoActual = EstadoInventario.NAVEGANDO;
        }
    }

    private void aplicarObjetoAPokemon(Pokemon p) {
        if (elementoSeleccionadoParaAccion != null && elementoSeleccionadoParaAccion.objetoReal != null
                && elementoSeleccionadoParaAccion.objetoReal.esUsable()) {
            com.mypokemon.game.inventario.ResultadoUso res = ((com.mypokemon.game.inventario.interfaces.IUsable) elementoSeleccionadoParaAccion.objetoReal)
                    .usar(p, explorador.obtenerMochila());
            mostrarFeedback(res.getMensaje());
        } else
            mostrarFeedback("Error con el objeto.");
        indiceCategoriaSeleccionada = 0;
        actualizarElementosVisibles();
        estadoActual = EstadoInventario.NAVEGANDO;
    }

    private void gestionarNavegacionCuadricula() {
        int total = (estadoActual == EstadoInventario.SELECCIONAR_POKEMON_OBJETIVO || indiceCategoriaSeleccionada == 3)
                ? explorador.obtenerEquipo().size()
                : elementosVisibles.size();
        if (estadoActual == EstadoInventario.SELECCIONAR_POKEMON_OBJETIVO)
            total = 6;
        if (total == 0)
            return;
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
            indiceElementoSeleccionado = (indiceElementoSeleccionado + 1) % total;
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
            indiceElementoSeleccionado = (indiceElementoSeleccionado <= 0) ? total - 1 : indiceElementoSeleccionado - 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            indiceElementoSeleccionado += 3;
            if (indiceElementoSeleccionado >= total)
                indiceElementoSeleccionado %= 3;
            if (indiceElementoSeleccionado >= total)
                indiceElementoSeleccionado = 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            indiceElementoSeleccionado -= 3;
            if (indiceElementoSeleccionado < 0) {
                int f = (int) Math.ceil((double) total / 3);
                int b = indiceElementoSeleccionado + (f * 3);
                if (b >= total)
                    b -= 3;
                indiceElementoSeleccionado = b;
            }
        }
    }

    private String mapearNombreAInterno(String d) {
        if (d.equals("Planta Medicinal"))
            return "planta";
        if (d.equals("Baya Aranja"))
            return "baya";
        if (d.equals("Guijarro"))
            return "guijarro";
        if (d.equals("Poké Ball"))
            return "pokeball";
        if (d.equals("Poké Ball de Peso"))
            return "heavyball";
        if (d.equals("Poción Herbal"))
            return "pocion";
        if (d.equals("Elíxir de Piel de Piedra"))
            return "elixir";
        if (d.equals("Revivir Casero"))
            return "revivir";
        if (d.equals("Reproductor de música"))
            return "reproductor";
        if (d.equals("Guante de reflejo cuarcítico"))
            return "guante";
        if (d.equals("Frijol mágico"))
            return "frijol";
        return d.toLowerCase();
    }

    private void mostrarFeedback(String m) {
        mensajeFeedback = m;
        temporizadorFeedback = 3f;
    }

    private void dibujarPestanas(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        for (int i = 0; i < nombresBotones.length; i++) {
            float bx = posicionesBotones[i][0], by = posicionesBotones[i][1];
            boolean sel = (i == indiceCategoriaSeleccionada);
            if (estadoActual == EstadoInventario.NAVEGANDO) {
                if (mousePos.x >= bx && mousePos.x <= bx + anchoBoton && mousePos.y >= by
                        && mousePos.y <= by + altoBoton) {
                    sel = true;
                    if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                        indiceCategoriaSeleccionada = i;
                        actualizarElementosVisibles();
                        indiceElementoSeleccionado = 0;
                    }
                }
            }
            Texture t = sel ? botonesSeleccionados[i] : botonesNormales[i];
            if (t != null)
                batch.draw(t, bx, by, anchoBoton, altoBoton);
        }
    }

    private void dibujarEspaciosCuadricula(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        for (int i = 0; i < 6; i++) {
            float x = 700 + (i % 3) * 165, y = 300 - (i / 3) * 165;
            if (texturaFondoEspacio != null)
                batch.draw(texturaFondoEspacio, x, y, 150, 150);
            if (i == indiceElementoSeleccionado && pixelBlanco != null) {
                batch.setColor(Color.YELLOW);
                batch.draw(pixelBlanco, x, y, 150, 4);
                batch.draw(pixelBlanco, x, y + 146, 150, 4);
                batch.draw(pixelBlanco, x, y, 4, 150);
                batch.draw(pixelBlanco, x + 146, y, 4, 150);
                batch.setColor(Color.WHITE);
            }
        }
    }

    private void dibujarContenidoCuadricula(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        if (indiceCategoriaSeleccionada == 3) {
            List<Pokemon> eq = explorador.obtenerEquipo();
            for (int i = 0; i < eq.size(); i++) {
                float x = 700 + (i % 3) * 165, y = 300 - (i / 3) * 165;
                dibujarSlotPokemon(batch, eq.get(i), x, y, 150);
            }
        } else {
            for (int i = 0; i < elementosVisibles.size(); i++) {
                float x = 700 + (i % 3) * 165, y = 300 - (i / 3) * 165;
                DatosElemento d = elementosVisibles.get(i);
                if (d.textura != null)
                    batch.draw(d.textura, x + 5, y + 5, 140, 140);
                if (fuenteContador != null)
                    fuenteContador.draw(batch, "x" + d.cantidad, x + 40, y + 5);
            }
        }
    }

    private void dibujarSlotPokemon(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, Pokemon p, float x, float y,
            float s) {
        String n = p.obtenerNombre();
        if (!cacheTexturasPokemon.containsKey(n)) {
            try {
                String c = n.toLowerCase().replace(" h.", "").replace(" jr.", "-jr").replace(" ", "-");
                cacheTexturasPokemon.put(n, new Texture(Gdx.files.internal(c + ".png")));
            } catch (Exception e) {
            }
        }
        Texture t = cacheTexturasPokemon.get(n);
        if (t != null)
            batch.draw(t, x + 5, y + 5, s - 10, s - 10);
        else
            fuenteContador.draw(batch, n, x + 5, y + s / 2);
        batch.setColor(Color.RED);
        batch.draw(pixelBlanco, x + 5, y + 5, s - 10, 5);
        batch.setColor(Color.GREEN);
        batch.draw(pixelBlanco, x + 5, y + 5, (s - 10) * (p.obtenerHpActual() / (float) p.obtenerHpMaximo()), 5);
        batch.setColor(Color.WHITE);
    }

    private void dibujarMenuOpciones(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        float x = 100, y = 120, w = 250, h = 40, th = opcionesActuales.size() * h + 10;
        batch.setColor(Color.BLACK);
        batch.draw(pixelBlanco, x, y, w, th);
        batch.setColor(Color.WHITE);
        batch.draw(pixelBlanco, x, y, w, 2);
        batch.draw(pixelBlanco, x, y + th, w, 2);
        batch.draw(pixelBlanco, x, y, 2, th);
        batch.draw(pixelBlanco, x + w, y, 2, th + 2);
        for (int i = 0; i < opcionesActuales.size(); i++) {
            float oy = y + th - 10 - (i * h);
            if (i == indiceOpcionSeleccionada) {
                batch.setColor(Color.DARK_GRAY);
                batch.draw(pixelBlanco, x + 2, oy - h + 5, w - 4, h - 5);
                batch.setColor(Color.WHITE);
            }
            juego.fuente.setColor(i == indiceOpcionSeleccionada ? Color.CYAN : Color.WHITE);
            juego.fuente.draw(batch, opcionesActuales.get(i), x + 20, oy - 5);
        }
        juego.fuente.setColor(Color.WHITE);
    }

    private void dibujarInstruccionSeleccion(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        juego.fuente.setColor(Color.YELLOW);
        juego.fuente.getData().setScale(2.0f);
        juego.fuente.draw(batch, "SELECCIONA UN POKÉMON", 700, 720 - 50);
        juego.fuente.getData().setScale(1.0f);
        juego.fuente.setColor(Color.WHITE);
    }

    private void actualizarElementosVisibles() {
        elementosVisibles.clear();
        Inventario inv = explorador.obtenerMochila();
        if (indiceCategoriaSeleccionada == 0) {
            Item p = inv.getItem("planta");
            if (p != null && p.obtenerCantidad() > 0)
                elementosVisibles.add(new DatosElemento("Planta Medicinal", "Ingrediente básico para medicinas.",
                        texturaPlanta, p.obtenerCantidad(), p));
            Item b = inv.getItem("baya");
            if (b != null && b.obtenerCantidad() > 0)
                elementosVisibles.add(new DatosElemento("Baya Aranja", "Restaura el 10% de HP del Pokémon.",
                        texturaBaya, b.obtenerCantidad(), b));
            Item g = inv.getItem("guijarro");
            if (g != null && g.obtenerCantidad() > 0)
                elementosVisibles.add(new DatosElemento("Guijarro", "Ingrediente principal para fabricar Poké Balls.",
                        texturaGuijarro, g.obtenerCantidad(), g));
        } else if (indiceCategoriaSeleccionada == 1) {
            Item pk = inv.getItem("pokeball");
            if (pk != null && pk.obtenerCantidad() > 0)
                elementosVisibles.add(new DatosElemento("Poké Ball", "Dispositivo para atrapar Pokémon.",
                        texturaPokebola, pk.obtenerCantidad(), pk));
            Item hb = inv.getItem("heavyball");
            if (hb != null && hb.obtenerCantidad() > 0)
                elementosVisibles.add(new DatosElemento("Poké Ball de Peso",
                        "Dispositivo con mejor captura en nivel bajo.", texturaHeavyBall, hb.obtenerCantidad(), hb));
        } else if (indiceCategoriaSeleccionada == 2) {
            Item po = inv.getItem("pocion");
            if (po != null && po.obtenerCantidad() > 0)
                elementosVisibles.add(new DatosElemento("Poción Herbal", "Restaura el 20% de HP del Pokémon.",
                        texturaPocionHerbal, po.obtenerCantidad(), po));
            Item el = inv.getItem("elixir");
            if (el != null && el.obtenerCantidad() > 0)
                elementosVisibles.add(new DatosElemento("Elíxir de Piel de Piedra",
                        "Aumenta la potencia del ataque (+3 por ataque).", texturaElixir, el.obtenerCantidad(), el));
            Item re = inv.getItem("revivir");
            if (re != null && re.obtenerCantidad() > 0)
                elementosVisibles.add(new DatosElemento("Revivir Casero", "Restaura el 50% de HP del Pokémon.",
                        texturaRevivir, re.obtenerCantidad(), re));
            Item mu = inv.getItem("reproductor");
            if (mu != null && mu.obtenerCantidad() > 0)
                elementosVisibles.add(new DatosElemento("Reproductor de música",
                        "Escucha música de fondo durante el viaje.", texturaReproductor, mu.obtenerCantidad(), mu));
            Item gu = inv.getItem("guante");
            if (gu != null && gu.obtenerCantidad() > 0)
                elementosVisibles.add(new DatosElemento("Guante de reflejo cuarcítico", "Recolecta doble recurso.",
                        texturaGuante, gu.obtenerCantidad(), gu));
            Item fr = inv.getItem("frijol");
            if (fr != null && fr.obtenerCantidad() > 0)
                elementosVisibles.add(new DatosElemento("Frijol mágico", "Restaura el 100% de HP del Pokémon.",
                        texturaFrijol, fr.obtenerCantidad(), fr));
        }
    }

    private void dibujarExplicacion(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        String t = "", d = "";
        if (indiceCategoriaSeleccionada == 3) {
            List<Pokemon> eq = explorador.obtenerEquipo();
            if (indiceElementoSeleccionado < eq.size()) {
                Pokemon p = eq.get(indiceElementoSeleccionado);
                t = p.obtenerNombre();
                d = "Nivel: " + p.obtenerNivel() + " | HP: " + p.obtenerHpActual() + "/" + p.obtenerHpMaximo();
            }
        } else {
            if (indiceElementoSeleccionado < elementosVisibles.size()) {
                DatosElemento de = elementosVisibles.get(indiceElementoSeleccionado);
                t = de.nombre;
                d = de.descripcion;
            }
        }
        juego.fuente.setColor(Color.WHITE);
        juego.fuente.getData().setScale(2.5f);
        juego.fuente.draw(batch, t, 100, 400);
        juego.fuente.getData().setScale(1.2f);
        juego.fuente.draw(batch, d, 100, 340, 500, com.badlogic.gdx.utils.Align.left, true);
        juego.fuente.getData().setScale(1.0f);
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }
}
