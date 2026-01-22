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

/**
 * Representa al jugador (Explorador) en el mundo de juego.
 * Gestiona el progreso, el inventario (mochila), la Pokédex, el equipo Pokémon
 * y el guardado de datos.
 */
public class Explorador implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreUsuario;
    private String nombrePartida; // Campo para el nombre del archivo de guardado
    private com.mypokemon.game.utils.Genero genero; // CHICO o CHICA
    private Inventario mochila;
    private Pokedex registro;
    private List<Pokemon> equipo;
    private int misionesCompletadas;
    private float tiempoGuanteRestante; // Segundos restantes para el efecto del Guante de Reflejo

    /**
     * Constructor completo del explorador.
     * 
     * @param nombreUsuario    Nombre del jugador.
     * @param nombrePartida    Nombre identificador de la partida.
     * @param capacidadInicial Capacidad inicial de la mochila.
     * @param genero           Género del personaje (Enum Genero).
     */
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

    /**
     * Constructor por defecto con género masculino.
     */
    public Explorador(String nombreUsuario, String nombrePartida, int capacidadInicial) {
        this(nombreUsuario, nombrePartida, capacidadInicial, com.mypokemon.game.utils.Genero.CHICO);
    }

    /**
     * Guarda el progreso del explorador en un archivo binario (.dat).
     * 
     * @param filename Nombre del archivo.
     * @return true si se guardó con éxito.
     */
    public boolean guardarProgreso(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
            System.out.println("Progreso guardado exitosamente en " + filename);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar el progreso: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Guarda el progreso usando un nombre de archivo por defecto generado a partir
     * de la partida y el usuario.
     */
    public boolean guardarProgreso() {
        return guardarProgreso(nombrePartida + "_" + nombreUsuario + ".dat");
    }

    /**
     * Carga un objeto Explorador desde un archivo de guardado.
     * 
     * @param filename Nombre del archivo a cargar.
     * @return El objeto Explorador reconstruido o null si falla.
     */
    public static Explorador cargarProgreso(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (Explorador) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("No se pudo cargar el progreso para " + filename + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifica si se cumplen las condiciones para enfrentar a Arceus.
     */
    public boolean verificarRequisitosArceus() {
        return registro.puedeRetarArceus();
    }

    /**
     * Finaliza la investigación legendaria y guarda el progreso final.
     */
    public void finalizarInvestigacionLegendaria() {
        System.out.println("Dr. Brenner: Progreso guardado. El experimento ha concluido.");
        misionesCompletadas++;
        guardarProgreso();
    }

    // Getters y Setters con descripciones breves

    public Inventario getMochila() {
        return mochila;
    }

    public Inventario getInventario() {
        return mochila;
    }

    public Pokedex getRegistro() {
        return registro;
    }

    public String getNombre() {
        return nombreUsuario;
    }

    public String getNombrePartida() {
        return nombrePartida;
    }

    public com.mypokemon.game.utils.Genero getGenero() {
        return genero;
    }

    public List<Pokemon> getEquipo() {
        return equipo;
    }

    public int getMisionesCompletadas() {
        return misionesCompletadas;
    }

    /**
     * Intenta agregar un Pokémon al equipo (máximo 6).
     */
    public boolean agregarAlEquipo(Pokemon pokemon) {
        if (this.equipo.size() < 6) {
            this.equipo.add(pokemon);
            return true;
        }
        return false;
    }

    public void agregarPokemonEquipo(Pokemon pokemon) {
        agregarAlEquipo(pokemon);
    }

    // Sistema de Crafteo
    private transient com.mypokemon.game.inventario.Crafteo crafteoSystem;

    public com.mypokemon.game.inventario.Crafteo getCrafteoSystem() {
        if (crafteoSystem == null) {
            crafteoSystem = new com.mypokemon.game.inventario.Crafteo();
        }
        return crafteoSystem;
    }

    /**
     * Comprueba si el guante recolector está habilitado.
     */
    public boolean isGuanteEquipado() {
        return tiempoGuanteRestante > 0;
    }

    public void activarGuante(float segundos) {
        this.tiempoGuanteRestante = segundos;
    }

    public float getTiempoGuanteRestante() {
        return tiempoGuanteRestante;
    }

    /**
     * Verifica si el reproductor de música está activo en la mochila.
     */
    public boolean isReproductorMusicaActivo() {
        com.mypokemon.game.inventario.Objeto item = mochila.getItem("reproductor");
        if (item instanceof com.mypokemon.game.inventario.objetoscrafteados.ReproductorMusica) {
            return ((com.mypokemon.game.inventario.objetoscrafteados.ReproductorMusica) item).isActivo();
        }
        return false;
    }

    public void setReproductorMusicaActivo(boolean activo) {
        com.mypokemon.game.inventario.Objeto item = mochila.getItem("reproductor");
        if (item instanceof com.mypokemon.game.inventario.objetoscrafteados.ReproductorMusica) {
            ((com.mypokemon.game.inventario.objetoscrafteados.ReproductorMusica) item).setActivo(activo);
        }
    }

    /**
     * Actualiza los temporizadores de efectos activos (como el guante de
     * recolección).
     * 
     * @param delta Tiempo transcurrido por frame.
     */
    public void actualizarTemporizadores(float delta) {
        if (tiempoGuanteRestante > 0) {
            tiempoGuanteRestante -= delta;
            if (tiempoGuanteRestante < 0) {
                tiempoGuanteRestante = 0;
            }
        }
    }
}
