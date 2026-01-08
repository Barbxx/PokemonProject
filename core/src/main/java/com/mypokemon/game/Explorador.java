package com.mypokemon.game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Explorador implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreUsuario;
    private Inventario mochila;
    private Pokedex registro;
    private List<Pokemon> equipo;
    private int misionesCompletadas;

    public Explorador(String nombre, int capacidadInicial) {
        this.nombreUsuario = nombre;
        this.mochila = new Inventario(capacidadInicial);
        this.registro = new Pokedex();
        this.equipo = new ArrayList<>();
        this.misionesCompletadas = 0;
    }

    public void guardarProgreso() {
        // Guarda el objeto Explorador completo en un archivo .dat
        String filename = nombreUsuario + "_save.dat";
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
            System.out.println("Progreso guardado exitosamente en " + filename);
        } catch (IOException e) {
            System.err.println("Error al guardar el progreso: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Explorador cargarProgreso(String nombre) {
        // Busca el archivo y reconstruye el objeto
        String filename = nombre + "_save.dat";
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (Explorador) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("No se pudo cargar el progreso para " + nombre + ": " + e.getMessage());
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
}
