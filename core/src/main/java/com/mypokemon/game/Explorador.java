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
 * Representa al jugador explorador con su inventario, equipo de Pokemon y
 * progreso del juego.
 * Gestiona el guardado y carga de partidas, asi como los items crafteados
 * activos.
 */
public class Explorador implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreUsuario;
    private String nombrePartida; 
    private String genero; 
    private Inventario mochila;
    private Pokedex registro;
    private List<Pokemon> equipo;
    private int misionesCompletadas;
    private float tiempoGuanteRestante; 

    /**
     * Constructor completo del explorador.
     * 
     * @param nombreUsuario    Nombre del jugador
     * @param nombrePartida    Nombre de la partida guardada
     * @param capacidadInicial Capacidad inicial del inventario
     * @param genero           Genero del personaje CHICO o CHICA
     */
    public Explorador(String nombreUsuario, String nombrePartida, int capacidadInicial, String genero) {
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
     * Constructor con genero por defecto CHICO.
     * 
     * @param nombreUsuario    Nombre del jugador
     * @param nombrePartida    Nombre de la partida guardada
     * @param capacidadInicial Capacidad inicial del inventario
     */
    public Explorador(String nombreUsuario, String nombrePartida, int capacidadInicial) {
        this(nombreUsuario, nombrePartida, capacidadInicial, "CHICO");
    }

    /**
     * Guarda el progreso del explorador en un archivo.
     * 
     * @param filename Nombre del archivo donde guardar
     * @return true si se guardo exitosamente, false en caso contrario
     */
    public boolean guardarProgreso(String filename) {
        // Guarda el objeto Explorador completo en el archivo especificado
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
     * Guarda el progreso usando el nombre de partida y usuario por defecto.
     * 
     * @return true si se guardo exitosamente, false en caso contrario
     */
    public boolean guardarProgreso() {
        return guardarProgreso(nombrePartida + "_" + nombreUsuario + ".dat");
    }

    /**
     * Carga un explorador desde un archivo guardado.
     * 
     * @param filename Nombre del archivo a cargar
     * @return Objeto Explorador cargado o null si falla
     */
    public static Explorador cargarProgreso(String filename) {
        // Busca el archivo y reconstruye el objeto usando el nombre del archivo
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (Explorador) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("No se pudo cargar el progreso para " + filename + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifica si el explorador cumple los requisitos para enfrentar a Arceus.
     * 
     * @return true si tiene 5 especies capturadas a nivel 10, false en caso
     *         contrario
     */
    public boolean verificarRequisitosArceus() {
        // Consulta a la Pokedex para saber si ya tiene las 5 especies investigadas a
        // nivel 10
        return registro.puedeRetarArceus();
    }

    /**
     * Finaliza la investigacion legendaria guardando el progreso.
     */
    public void finalizarInvestigacionLegendaria() {
        System.out.println("Dr. Brenner: Progreso guardado. El experimento ha concluido.");
        // Incrementa contador de misiones al completar el hito
        misionesCompletadas++;
        guardarProgreso();
    }

    /**
     * Obtiene el inventario del explorador.
     * 
     * @return Objeto Inventario
     */
    public Inventario getMochila() {
        return mochila;
    }

    /**
     * Alias de getMochila.
     * 
     * @return Objeto Inventario
     */
    public Inventario getInventario() {
        return mochila; 
    }

    /**
     * Obtiene la Pokedex del explorador.
     * 
     * @return Objeto Pokedex
     */
    public Pokedex getRegistro() {
        return registro;
    }

    /**
     * Obtiene el nombre del usuario.
     * 
     * @return Nombre del usuario
     */
    public String getNombre() {
        return nombreUsuario;
    }

    /**
     * Obtiene el nombre de la partida.
     * 
     * @return Nombre de la partida
     */
    public String getNombrePartida() {
        return nombrePartida;
    }

    /**
     * Obtiene el genero del personaje.
     * 
     * @return CHICO o CHICA
     */
    public String getGenero() {
        return genero;
    }

    /**
     * Obtiene el equipo de Pokemon del explorador.
     * 
     * @return Lista de Pokemon en el equipo
     */
    public List<Pokemon> getEquipo() {
        return equipo;
    }

    /**
     * Obtiene el numero de misiones completadas.
     * 
     * @return Cantidad de misiones completadas
     */
    public int getMisionesCompletadas() {
        return misionesCompletadas;
    }

    /**
     * Agrega un Pokemon al equipo si hay espacio disponible.
     * 
     * @param pokemon Pokemon a agregar
     * @return true si se agrego exitosamente, false si el equipo esta lleno
     */
    public boolean agregarAlEquipo(Pokemon pokemon) {
        if (this.equipo.size() < 6) {
            this.equipo.add(pokemon);
            return true;
        }
        return false;
    }

    /**
     * Alias de agregarAlEquipo.
     * 
     * @param pokemon Pokemon a agregar
     */
    public void agregarPokemonEquipo(Pokemon pokemon) {
        agregarAlEquipo(pokemon);
    }

    // Sistema de Crafteo Centralizado
    private transient com.mypokemon.game.inventario.Crafteo crafteoSystem;

    /**
     * Obtiene el sistema de crafteo del explorador.
     * 
     * @return Sistema de crafteo
     */
    public com.mypokemon.game.inventario.Crafteo getCrafteoSystem() {
        if (crafteoSystem == null) {
            crafteoSystem = new com.mypokemon.game.inventario.Crafteo();
        }
        return crafteoSystem;
    }

    /**
     * Verifica si el guante de reflejo esta activo.
     * 
     * @return true si el guante esta equipado, false en caso contrario
     */
    public boolean isGuanteEquipado() {
        return tiempoGuanteRestante > 0;
    }

    /**
     * Activa el guante de reflejo por un tiempo determinado.
     * 
     * @param segundos Duracion del efecto en segundos
     */
    public void activarGuante(float segundos) {
        this.tiempoGuanteRestante = segundos;
    }

    /**
     * Obtiene el tiempo restante del guante de reflejo.
     * 
     * @return Tiempo restante en segundos
     */
    public float getTiempoGuanteRestante() {
        return tiempoGuanteRestante;
    }

    /**
     * Verifica si el reproductor de musica esta activo.
     * 
     * @return true si esta activo, false en caso contrario
     */
    public boolean isReproductorMusicaActivo() {
        com.mypokemon.game.inventario.Item item = mochila.getItem("reproductor");
        if (item instanceof com.mypokemon.game.inventario.objetoscrafteados.ReproductorMusica) {
            return ((com.mypokemon.game.inventario.objetoscrafteados.ReproductorMusica) item).isActivo();
        }
        return false;
    }

    /**
     * Activa o desactiva el reproductor de musica.
     * 
     * @param activo true para activar, false para desactivar
     */
    public void setReproductorMusicaActivo(boolean activo) {
        com.mypokemon.game.inventario.Item item = mochila.getItem("reproductor");
        if (item instanceof com.mypokemon.game.inventario.objetoscrafteados.ReproductorMusica) {
            ((com.mypokemon.game.inventario.objetoscrafteados.ReproductorMusica) item).setActivo(activo);
        }
    }

    /**
     * Actualiza los temporizadores activos (Glove, etc.)
     * 
     * @param delta Tiempo transcurrido
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
