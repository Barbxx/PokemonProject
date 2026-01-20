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

public class Explorador implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreUsuario;
    private String nombrePartida; // New field for filename
    private String genero; // CHICO or CHICA
    private Inventario mochila;
    private Pokedex registro;
    private List<Pokemon> equipo;
    private int misionesCompletadas;

    public Explorador(String nombreUsuario, String nombrePartida, int capacidadInicial, String genero) {
        this.nombreUsuario = nombreUsuario;
        this.nombrePartida = nombrePartida;
        this.genero = genero;
        this.mochila = new Inventario(capacidadInicial);
        this.registro = new Pokedex();
        this.equipo = new ArrayList<>();
        this.misionesCompletadas = 0;
    }

    public Explorador(String nombreUsuario, String nombrePartida, int capacidadInicial) {
        this(nombreUsuario, nombrePartida, capacidadInicial, "CHICO");
    }

    public void guardarProgreso(String filename) {
        // Guarda el objeto Explorador completo en el archivo especificado
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
            System.out.println("Progreso guardado exitosamente en " + filename);
        } catch (IOException e) {
            System.err.println("Error al guardar el progreso: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void guardarProgreso() {
        // Fallback for default behavior if needed, generally shouldn't be used with new
        // logic
        guardarProgreso(nombrePartida + "_" + nombreUsuario + ".dat");
    }

    public static Explorador cargarProgreso(String filename) {
        // Busca el archivo y reconstruye el objeto usando el nombre del archivo
        // Ensure extension is handled if not provided (helper logic could go here, but
        // let's assume valid filenames)
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (Explorador) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("No se pudo cargar el progreso para " + filename + ": " + e.getMessage());
            return null;
        }
    }

    public boolean verificarRequisitosArceus() {
        // Consulta a la Pokedex para saber si ya tiene las 5 especies investigadas a
        // nivel 10
        return registro.puedeRetarArceus();
    }

    public void finalizarInvestigacionLegendaria() {
        System.out.println("Dr. Brenner: Progreso guardado. El experimento ha concluido.");
        // Incrementa contador de misiones al completar el hito (opcional, pero buena
        // práctica)
        misionesCompletadas++;
        guardarProgreso();
    }

    // Getters
    public Inventario getMochila() {
        return mochila;
    }

    public Inventario getInventario() {
        return mochila; // Alias para getMochila()
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

    public String getGenero() {
        return genero;
    }

    public List<Pokemon> getEquipo() {
        return equipo;
    }

    public int getMisionesCompletadas() {
        return misionesCompletadas;
    }

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

    // Sistema de Crafteo Centralizado
    private com.mypokemon.game.inventario.Crafteo crafteoSystem;

    public com.mypokemon.game.inventario.Crafteo getCrafteoSystem() {
        if (crafteoSystem == null) {
            crafteoSystem = new com.mypokemon.game.inventario.Crafteo();
        }
        return crafteoSystem;
    }
}
