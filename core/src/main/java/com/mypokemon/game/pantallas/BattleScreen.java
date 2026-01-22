package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.Pokemon;
import com.mypokemon.game.Movimiento;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mypokemon.game.inventario.ObjectFactory;
import com.mypokemon.game.inventario.exceptions.SpaceException;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.List;

// Pantalla que gestiona los combates Pokémon por turnos. Maneja la lógica de ataque, captura e IA.
public class BattleScreen extends BaseScreen {

    private final com.badlogic.gdx.Screen pantallaPadre;
    private final Explorador explorador;
    private Pokemon pokemonJugador;
    private final Pokemon pokemonEnemigo;

    private OrthographicCamera camara;
    private Viewport viewport;

    private Rectangle btnAtacarRect, btnHuirRect, btnMochilaRect, btnPokemonRect;
    private Rectangle btnMov0Rect, btnMov1Rect, btnMov2Rect, btnMov3Rect;

    private String textoInfo = "...", textoDaño = "";
    private float temporizadorTextoDaño = 0, dañoX = 500, dañoY = 400;
    private int opcionSeleccionada = 0, movimientoSeleccionado = 0;
    private boolean mostrarMenuMovimientos = false, mostrarPokedex = false;
    private Texture bordeSeleccionado, texturaFondo, texturaEnemigo, texturaJugadorEspalda, fondoBoton, fondoCaja,
            fondoBorde, rellenoBarraHP, texturaCirculoBase, texturaBarraEstado;
    private com.badlogic.gdx.audio.Music musicaBatalla;

    private enum EstadoBatalla {
        TURNO_JUGADOR, TURNO_ENEMIGO, FIN_BATALLA
    }

    private EstadoBatalla estadoActual;

    public BattleScreen(PokemonMain juego, com.badlogic.gdx.Screen pantallaPadre, Explorador explorador,
            Pokemon enemigo) {
        super(juego);
        this.pantallaPadre = pantallaPadre;
        this.explorador = explorador;
        this.pokemonEnemigo = enemigo;

        if (!explorador.obtenerEquipo().isEmpty()) {
            Pokemon pOrig = explorador.obtenerEquipo().get(0);
            String n = pOrig.obtenerNombre();
            int nInv = explorador.obtenerRegistro().getRegistro().containsKey(n)
                    ? explorador.obtenerRegistro().getRegistro().get(n).obtenerNivelInvestigacion()
                    : pOrig.obtenerNivel();
            this.pokemonJugador = new Pokemon(n, nInv, 0, pOrig.esLegendario(), pOrig.obtenerTipo());
            float hpAct = pOrig.obtenerHpActual();
            if (hpAct < pokemonJugador.obtenerHpMaximo())
                pokemonJugador.recibirDaño(pokemonJugador.obtenerHpMaximo() - hpAct);
        } else
            this.pokemonJugador = new Pokemon("Piplup", 5, 20, false, "Agua");

        if (pokemonJugador != null)
            pokemonJugador.reiniciarModificadoresTemporales();
        if (pokemonEnemigo != null)
            pokemonEnemigo.reiniciarModificadoresTemporales();
        explorador.obtenerRegistro().registrarAvistamiento(enemigo.obtenerNombre());

        this.camara = new OrthographicCamera();
        this.viewport = new StretchViewport(800, 600, camara);
        this.viewport.apply();
        this.estadoActual = EstadoBatalla.TURNO_JUGADOR;
        cargarRecursos();
        actualizarDisposicion();
    }

    private void cargarRecursos() {
        texturaFondo = cargarTextura("fondoBatalla.png");
        if (texturaFondo == null)
            texturaFondo = cargarTextura("fondoBatalla.jpg");
        try {
            String n = pokemonEnemigo.obtenerNombre().toLowerCase().replace(" h.", "").replace(" jr.", "-jr")
                    .replace(" ", "-");
            texturaEnemigo = cargarTextura(n + ".png");
            if (texturaEnemigo == null)
                texturaEnemigo = cargarTextura("jigglypuff.png");
        } catch (Exception e) {
            texturaEnemigo = crearTexturaColor(Color.RED);
        }

        try {
            String n = pokemonJugador.obtenerNombre().toLowerCase().replace(" h.", "").replace(" jr.", "-jr")
                    .replace(" ", "-");
            texturaJugadorEspalda = cargarTextura(n + " atras.png");
            if (texturaJugadorEspalda == null)
                texturaJugadorEspalda = cargarTextura(n + ".png");
        } catch (Exception e) {
        }

        fondoBoton = crearTexturaColor(Color.WHITE);
        agregarTextura(fondoBoton);
        fondoCaja = crearTexturaColor(new Color(0.15f, 0.25f, 0.45f, 1));
        agregarTextura(fondoCaja);
        fondoBorde = crearTexturaColor(new Color(0.8f, 0.7f, 0.2f, 1));
        agregarTextura(fondoBorde);
        rellenoBarraHP = crearTexturaColor(Color.GREEN);
        agregarTextura(rellenoBarraHP);
        bordeSeleccionado = crearTexturaColor(Color.LIME);
        agregarTextura(bordeSeleccionado);
        texturaBarraEstado = cargarTextura("barraPokemon.png");
        if (texturaBarraEstado == null)
            texturaBarraEstado = fondoBorde;
        texturaCirculoBase = crearTexturaCirculo(new Color(0.3f, 0.6f, 0.2f, 0.8f));
        agregarTextura(texturaCirculoBase);

        if (!pokemonEnemigo.obtenerNombre().equalsIgnoreCase("Arceus")) {
            try {
                musicaBatalla = Gdx.audio.newMusic(Gdx.files.internal("batallaPokemon.mp3"));
                musicaBatalla.setLooping(true);
                musicaBatalla.setVolume(0.5f);
                musicaBatalla.play();
            } catch (Exception e) {
            }
        }
    }

    private Texture crearTexturaCirculo(Color c) {
        Pixmap p = new Pixmap(128, 128, Pixmap.Format.RGBA8888);
        p.setColor(c);
        p.fillCircle(64, 64, 60);
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }

    private Texture crearTexturaColor(Color c) {
        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(c);
        p.fill();
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }

    private void actualizarDisposicion() {
        float bw = 180, bh = 60;
        btnAtacarRect = new Rectangle(420, 110, bw, bh);
        btnMochilaRect = new Rectangle(610, 110, bw, bh);
        btnPokemonRect = new Rectangle(420, 40, bw, bh);
        btnHuirRect = new Rectangle(610, 40, bw, bh);
        btnMov0Rect = new Rectangle(420, 110, bw, bh);
        btnMov1Rect = new Rectangle(610, 110, bw, bh);
        btnMov2Rect = new Rectangle(420, 40, bw, bh);
        btnMov3Rect = new Rectangle(610, 40, bw, bh);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int sx, int sy, int p, int b) {
                com.badlogic.gdx.math.Vector3 m = new com.badlogic.gdx.math.Vector3(sx, sy, 0);
                viewport.unproject(m);
                if (estadoActual == EstadoBatalla.TURNO_JUGADOR) {
                    if (mostrarMenuMovimientos) {
                        List<Movimiento> ms = pokemonJugador.obtenerMovimientos();
                        if (btnMov0Rect.contains(m.x, m.y) && ms.size() > 0) {
                            ejecutarMovimiento(0);
                            return true;
                        }
                        if (btnMov1Rect.contains(m.x, m.y) && ms.size() > 1) {
                            ejecutarMovimiento(1);
                            return true;
                        }
                        if (btnMov2Rect.contains(m.x, m.y) && ms.size() > 2) {
                            ejecutarMovimiento(2);
                            return true;
                        }
                        if (btnMov3Rect.contains(m.x, m.y) && ms.size() > 3) {
                            ejecutarMovimiento(3);
                            return true;
                        }
                        mostrarMenuMovimientos = false;
                        return true;
                    }
                    if (btnAtacarRect.contains(m.x, m.y)) {
                        opcionSeleccionada = 0;
                        manejarAccion();
                        return true;
                    } else if (btnHuirRect.contains(m.x, m.y)) {
                        opcionSeleccionada = 3;
                        manejarHuida();
                        return true;
                    } else if (btnMochilaRect.contains(m.x, m.y)) {
                        opcionSeleccionada = 1;
                        abrirMochila();
                        return true;
                    } else if (btnPokemonRect.contains(m.x, m.y)) {
                        opcionSeleccionada = 2;
                        abrirPokemon();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean keyDown(int k) {
                if (estadoActual == EstadoBatalla.TURNO_JUGADOR) {
                    if (k == com.badlogic.gdx.Input.Keys.B || k == com.badlogic.gdx.Input.Keys.X) {
                        if (mostrarMenuMovimientos || mostrarPokedex) {
                            mostrarMenuMovimientos = false;
                            mostrarPokedex = false;
                            return true;
                        }
                    }
                    if (k == com.badlogic.gdx.Input.Keys.K) {
                        mostrarPokedex = !mostrarPokedex;
                        if (mostrarPokedex)
                            mostrarMenuMovimientos = false;
                        return true;
                    }
                    int sel = mostrarMenuMovimientos ? movimientoSeleccionado : opcionSeleccionada;
                    if (k == com.badlogic.gdx.Input.Keys.UP && sel >= 2)
                        sel -= 2;
                    else if (k == com.badlogic.gdx.Input.Keys.DOWN && sel <= 1)
                        sel += 2;
                    else if (k == com.badlogic.gdx.Input.Keys.LEFT && sel % 2 != 0)
                        sel -= 1;
                    else if (k == com.badlogic.gdx.Input.Keys.RIGHT && sel % 2 == 0)
                        sel += 1;
                    else if (k == com.badlogic.gdx.Input.Keys.ENTER || k == com.badlogic.gdx.Input.Keys.Z) {
                        if (mostrarMenuMovimientos) {
                            if (movimientoSeleccionado < pokemonJugador.obtenerMovimientos().size())
                                ejecutarMovimiento(movimientoSeleccionado);
                        } else
                            manejarAccion();
                        return true;
                    }
                    if (mostrarMenuMovimientos)
                        movimientoSeleccionado = sel;
                    else
                        opcionSeleccionada = sel;
                }
                return false;
            }
        });
        actualizarInfo("¡Un " + pokemonEnemigo.obtenerNombre() + " salvaje apareció!");
    }

    private void manejarAccion() {
        switch (opcionSeleccionada) {
            case 0:
                mostrarMenuMovimientos = true;
                movimientoSeleccionado = 0;
                break;
            case 1:
                abrirMochila();
                break;
            case 2:
                abrirPokemon();
                break;
            case 3:
                manejarHuida();
                break;
        }
    }

    private void abrirMochila() {
        juego.setScreen(new MochilaScreen(juego, this, explorador));
    }

    private void abrirPokemon() {
        MochilaScreen m = new MochilaScreen(juego, this, explorador);
        m.establecerIndiceSeleccion(3);
        juego.setScreen(m);
    }

    private void manejarHuida() {
        actualizarInfo("¡Escapaste sin problemas!");
        finalizarBatalla(false);
    }

    public void cambiarPokemon(Pokemon n) {
        this.pokemonJugador = n;
        actualizarInfo("¡Adelante " + n.obtenerNombre() + "!");
        try {
            if (texturaJugadorEspalda != null)
                removerTextura(texturaJugadorEspalda);
            String nom = n.obtenerNombre().toLowerCase().replace(" h.", "").replace(" jr.", "-jr").replace(" ", "-");
            texturaJugadorEspalda = cargarTextura(nom + " atras.png");
            if (texturaJugadorEspalda == null)
                texturaJugadorEspalda = cargarTextura(nom + ".png");
        } catch (Exception e) {
        }
    }

    private void removerTextura(Texture t) {
        t.dispose();
    }

    private void ejecutarMovimiento(int i) {
        mostrarMenuMovimientos = false;
        List<Movimiento> ms = pokemonJugador.obtenerMovimientos();
        if (i >= ms.size())
            return;
        Movimiento m = ms.get(i);
        boolean jugPriv = pokemonJugador.obtenerVelocidad() >= pokemonEnemigo.obtenerVelocidad();
        if (jugPriv) {
            realizarAtaque(pokemonJugador, pokemonEnemigo, m);
            verificarEstado();
            if (estadoActual != EstadoBatalla.FIN_BATALLA) {
                estadoActual = EstadoBatalla.TURNO_ENEMIGO;
                Gdx.app.postRunnable(() -> {
                    try {
                        Thread.sleep(1500);
                    } catch (Exception e) {
                    }
                    ejecutarTurnoEnemigo();
                });
            }
        } else {
            estadoActual = EstadoBatalla.TURNO_ENEMIGO;
            ejecutarTurnoEnemigo();
            if (estadoActual != EstadoBatalla.FIN_BATALLA) {
                Gdx.app.postRunnable(() -> {
                    try {
                        Thread.sleep(1500);
                    } catch (Exception e) {
                    }
                    realizarAtaque(pokemonJugador, pokemonEnemigo, m);
                    verificarEstado();
                    estadoActual = EstadoBatalla.TURNO_JUGADOR;
                });
            }
        }
    }

    private void realizarAtaque(Pokemon at, Pokemon df, Movimiento m) {
        int d = m.ejecutar(at, df);
        if (d == -1)
            actualizarInfo("¡" + df.obtenerNombre() + " es inmune a " + m.getTipo() + "!");
        else if (d > 0) {
            actualizarInfo(at.obtenerNombre() + " usó " + m.getNombre() + ". Daño: " + d);
            textoDaño = "-" + d;
            dañoX = at == pokemonJugador ? 500 : 160;
            dañoY = at == pokemonJugador ? 400 : 330;
            temporizadorTextoDaño = 2.0f;
        } else
            actualizarInfo(at.obtenerNombre() + " usó " + m.getNombre() + ", pero falló.");
    }

    private void ejecutarTurnoEnemigo() {
        if (estadoActual != EstadoBatalla.TURNO_ENEMIGO)
            return;
        List<Movimiento> ms = pokemonEnemigo.obtenerMovimientos();
        if (ms.isEmpty()) {
            int d = (int) (pokemonEnemigo.obtenerAtaque() * 0.3);
            pokemonJugador.recibirDaño(d);
            actualizarInfo(pokemonEnemigo.obtenerNombre() + " usó Placaje. Daño: " + d);
        } else {
            Movimiento m = ms.get((int) (Math.random() * ms.size()));
            realizarAtaque(pokemonEnemigo, pokemonJugador, m);
        }
        verificarEstado();
        if (estadoActual != EstadoBatalla.FIN_BATALLA)
            estadoActual = EstadoBatalla.TURNO_JUGADOR;
    }

    private void verificarEstado() {
        if (pokemonEnemigo.obtenerHpActual() <= 0) {
            actualizarInfo("¡" + pokemonEnemigo.obtenerNombre() + " se debilitó!");
            int p = pokemonEnemigo.obtenerNombre().equalsIgnoreCase("Arceus") ? 10 : 1;
            explorador.obtenerRegistro().registrarAccion(pokemonJugador.obtenerNombre(), false);
            String r = Math.random() < 0.5 ? "planta" : "guijarro";
            try {
                explorador.obtenerMochila().agregarItem(ObjectFactory.crearRecurso(r, 1));
                actualizarInfo("Ganaste +" + p + " Inv. y encontraste 1 " + r + ".");
            } catch (SpaceException e) {
                actualizarInfo("Ganaste +" + p + " Inv. pero tu inventario está lleno.");
            }
            finalizarBatalla(true);
        } else if (pokemonJugador.obtenerHpActual() <= 0) {
            actualizarInfo("¡Tu Pokémon se debilitó!");
            String lost = explorador.obtenerMochila().perderObjetoAleatorio();
            actualizarInfo(lost != null ? "Perdiste 1 " + lost + " en la huida." : "No tenías objetos para perder.");
            finalizarBatalla(false);
        }
    }

    private void finalizarBatalla(boolean vic) {
        if (musicaBatalla != null)
            musicaBatalla.stop();
        estadoActual = EstadoBatalla.FIN_BATALLA;
        if (pokemonJugador != null)
            pokemonJugador.reiniciarModificadoresTemporales();
        if (pokemonEnemigo != null)
            pokemonEnemigo.reiniciarModificadoresTemporales();
        Gdx.app.postRunnable(() -> {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
            }
            Gdx.app.postRunnable(() -> {
                if (vic && pokemonEnemigo.obtenerNombre().equalsIgnoreCase("Arceus"))
                    juego.setScreen(new CreditsScreen(juego, explorador.obtenerNombre()));
                else
                    juego.setScreen(pantallaPadre);
            });
        });
    }

    private void actualizarInfo(String t) {
        textoInfo = t;
    }

    @Override
    public void render(float delta) {
        if (temporizadorTextoDaño > 0) {
            temporizadorTextoDaño -= delta;
            if (temporizadorTextoDaño < 0)
                textoDaño = "";
        }
        ScreenUtils.clear(0, 0, 0, 1);
        camara.update();
        juego.batch.setProjectionMatrix(camara.combined);
        juego.batch.begin();
        if (texturaFondo != null)
            juego.batch.draw(texturaFondo, 0, 0, 800, 600);
        if (texturaEnemigo != null) {
            juego.batch.draw(texturaCirculoBase, 380, 310, 320, 100);
            juego.batch.draw(texturaEnemigo, 400, 350, 280, 280);
        }
        if (texturaJugadorEspalda != null)
            juego.batch.draw(texturaJugadorEspalda, 20, 203, 280, 280);

        juego.fuente.setColor(Color.WHITE);
        juego.fuente.getData().setScale(1.2f);
        GlyphLayout l = new GlyphLayout(juego.fuente, textoInfo);
        juego.fuente.draw(juego.batch, textoInfo, (400 - l.width) / 2, 110 + (l.height / 2));

        if (mostrarPokedex)
            dibujarPokedex();
        else if (mostrarMenuMovimientos) {
            List<Movimiento> ms = pokemonJugador.obtenerMovimientos();
            dibujarBoton(btnMov0Rect, ms.size() > 0 ? ms.get(0).getNombre() : "-", movimientoSeleccionado == 0);
            dibujarBoton(btnMov1Rect, ms.size() > 1 ? ms.get(1).getNombre() : "-", movimientoSeleccionado == 1);
            dibujarBoton(btnMov2Rect, ms.size() > 2 ? ms.get(2).getNombre() : "-", movimientoSeleccionado == 2);
            dibujarBoton(btnMov3Rect, ms.size() > 3 ? ms.get(3).getNombre() : "-", movimientoSeleccionado == 3);
        } else {
            dibujarBoton(btnAtacarRect, "Atacar", opcionSeleccionada == 0);
            dibujarBoton(btnMochilaRect, "Mochila", opcionSeleccionada == 1);
            dibujarBoton(btnPokemonRect, "Pokémon", opcionSeleccionada == 2);
            dibujarBoton(btnHuirRect, "Huir", opcionSeleccionada == 3);
        }

        if (!textoDaño.isEmpty() && temporizadorTextoDaño > 0) {
            juego.fuente.setColor(Color.RED);
            juego.fuente.getData().setScale(2.0f);
            juego.fuente.draw(juego.batch, textoDaño, dañoX, dañoY);
            juego.fuente.getData().setScale(1.0f);
        }
        dibujarInfo();
        juego.batch.end();
    }

    private void dibujarPokedex() {
        juego.batch.draw(fondoBorde, 50, 50, 700, 500);
        juego.batch.draw(fondoBoton, 60, 60, 680, 480);
        juego.fuente.setColor(Color.BLACK);
        juego.fuente.getData().setScale(1.2f);
        juego.fuente.draw(juego.batch, "POKÉDEX - Pokémon Capturados", 250, 520);
        juego.fuente.getData().setScale(1.0f);
        List<Pokemon> eq = explorador.obtenerEquipo();
        float y = 470;
        if (eq.isEmpty())
            juego.fuente.draw(juego.batch, "No has capturado ningún Pokémon.", 100, y);
        else
            for (Pokemon p : eq) {
                juego.fuente.draw(juego.batch, p.obtenerNombre() + ": " + p.obtenerDescripcion(), 100, y);
                y -= 30;
                if (y < 100)
                    break;
            }
        juego.fuente.draw(juego.batch, "Presiona B para cerrar", 300, 90);
    }

    private void dibujarInfo() {
        juego.fuente.setColor(Color.BLACK);
        juego.fuente.getData().setScale(1.1f);
        juego.fuente.draw(juego.batch, pokemonEnemigo.obtenerNombre().toUpperCase(), 20, 560);
        juego.fuente.draw(juego.batch, "Nv.Inv" + pokemonEnemigo.obtenerNivel(), 230, 560);
        juego.batch.draw(texturaBarraEstado, 10, 480, 260, 70);
        juego.batch.draw(rellenoBarraHP, 100, 508,
                120 * (pokemonEnemigo.obtenerHpActual() / pokemonEnemigo.obtenerHpMaximo()),
                8);

        juego.fuente.draw(juego.batch, pokemonJugador.obtenerNombre().toUpperCase(), 530, 300);
        juego.fuente.draw(juego.batch, "Nv.Inv" + pokemonJugador.obtenerNivel(), 740, 300);
        juego.batch.draw(texturaBarraEstado, 520, 220, 260, 70);
        juego.batch.draw(rellenoBarraHP, 610, 248,
                120 * (pokemonJugador.obtenerHpActual() / (float) pokemonJugador.obtenerHpMaximo()), 8);
    }

    private void dibujarBoton(Rectangle r, String txt, boolean sel) {
        juego.batch.draw(sel ? bordeSeleccionado : fondoBorde, r.x - 4, r.y - 4, r.width + 8, r.height + 8);
        juego.batch.draw(fondoBoton, r.x, r.y, r.width, r.height);
        juego.fuente.setColor(Color.BLACK);
        GlyphLayout l = new GlyphLayout(juego.fuente, txt);
        juego.fuente.draw(juego.batch, txt, r.x + (r.width - l.width) / 2, r.y + (r.height + l.height) / 2);
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (musicaBatalla != null)
            musicaBatalla.dispose();
    }

    public Pokemon obtenerPokemonJugador() {
        return pokemonJugador;
    }

    public void usarItemEnBatalla(String id) {
        if (id.equals("pokeball") || id.equals("heavyball")) {
            actualizarInfo("¡Lanzaste una Poké Ball!");
            if (Math.random() < 0.5) {
                explorador.agregarAlEquipo(pokemonEnemigo);
                explorador.obtenerRegistro().registrarAccion(pokemonEnemigo.obtenerNombre(), true);
                actualizarInfo("¡" + pokemonEnemigo.obtenerNombre() + " capturado!");
                finalizarBatalla(true);
            } else
                actualizarInfo("¡El Pokémon escapó!");
        } else if (id.equals("pocion")) {
            pokemonJugador.curar(20);
            actualizarInfo("¡Usaste poción!");
        }
    }
}
