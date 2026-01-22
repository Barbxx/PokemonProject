package com.mypokemon.game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.mypokemon.game.inventario.Inventario;

// Representa al jugador (Explorador) en el mundo de juego. Gestiona progreso, inventario, Pokédex y equipo.
public class Explorador implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreUsuario;
    private String nombrePartida;
    private com.mypokemon.game.utils.Genero genero;
    private Inventario mochila;
    private Pokedex registro;
    private List<Pokemon> equipo;
    private int misionesCompletadas;
    private float tiempoGuanteRestante;
    private transient com.mypokemon.game.inventario.Crafteo sistemaCrafteo;

    public Explorador(String nombreUsuario, String nombrePartida, int capacidadInicial,
            com.mypokemon.game.utils.Genero genero) {
        this.nombreUsuario = nombreUsuario;
        this.nombrePartida = nombrePartida;
        this.genero = genero;
        this.mochila = new Inventario(capacidadInicial);
        this.registro = new Pokedex();
        this.equipo = new ArrayList<>();
        this.misionesCompletadas = 0;
        this.tiempoGuanteRestante = 0;
    }

    public Explorador(String nombreUsuario, String nombrePartida, int capacidadInicial) {
        this(nombreUsuario, nombrePartida, capacidadInicial, com.mypokemon.game.utils.Genero.CHICO);
    }

    public boolean guardarProgreso(String nombreArchivo) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
            out.writeObject(this);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean guardarProgreso() {
        return guardarProgreso(nombrePartida + "_" + nombreUsuario + ".dat");
    }

    public static Explorador cargarProgreso(String nombreArchivo) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(nombreArchivo))) {
            return (Explorador) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public boolean verificarRequisitosArceus() {
        return registro.verificarRequisitosArceus();
    }

    public void finalizarInvestigacionLegendaria() {
        misionesCompletadas++;
        guardarProgreso();
    }

    public void actualizarTemporizadores(float delta) {
        if (tiempoGuanteRestante > 0) {
            tiempoGuanteRestante -= delta;
            if (tiempoGuanteRestante < 0)
                tiempoGuanteRestante = 0;
        }
    }

    // Getters y Setters
    public Inventario obtenerMochila() {
        return mochila;
    }

    public Pokedex obtenerRegistro() {
        return registro;
    }

    public String obtenerNombre() {
        return nombreUsuario;
    }

    public String obtenerNombrePartida() {
        return nombrePartida;
    }

    public com.mypokemon.game.utils.Genero obtenerGenero() {
        return genero;
    }

    public List<Pokemon> obtenerEquipo() {
        return equipo;
    }

    public int obtenerMisionesCompletadas() {
        return misionesCompletadas;
    }

    public float obtenerTiempoGuanteRestante() {
        return tiempoGuanteRestante;
    }

    public boolean agregarAlEquipo(Pokemon pokemon) {
        if (this.equipo.size() < 6) {
            this.equipo.add(pokemon);
            return true;
        }
        return false;
    }

    public com.mypokemon.game.inventario.Crafteo obtenerSistemaCrafteo() {
        if (sistemaCrafteo == null)
            sistemaCrafteo = new com.mypokemon.game.inventario.Crafteo();
        return sistemaCrafteo;
    }

    public boolean estaGuanteEquipado() {
        return tiempoGuanteRestante > 0;
    }

    public void activarGuante(float segundos) {
        this.tiempoGuanteRestante = segundos;
    }

    public boolean estaReproductorMusicaActivo() {
        com.mypokemon.game.inventario.Item item = mochila.getItem("reproductor");
        if (item instanceof com.mypokemon.game.inventario.objetoscrafteados.ReproductorMusica) {
            return ((com.mypokemon.game.inventario.objetoscrafteados.ReproductorMusica) item).estaActivo();
        }
        return false;
    }

    public void establecerReproductorMusicaActivo(boolean activo) {
        com.mypokemon.game.inventario.Item item = mochila.getItem("reproductor");
        if (item instanceof com.mypokemon.game.inventario.objetoscrafteados.ReproductorMusica) {
            ((com.mypokemon.game.inventario.objetoscrafteados.ReproductorMusica) item).establecerActivo(activo);
        }
    }
}
