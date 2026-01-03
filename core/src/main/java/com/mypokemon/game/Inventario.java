package com.mypokemon.game;

import java.io.Serializable;

public class Inventario implements Serializable {
    private int capacidadMaxima;
    // Atributos renombrados y simplificados según solicitud
    private int guijarros;
    private int plantas;
    private int bayas;
    private int pokeBalls;
    private int pociones;

    public Inventario(int capacidad) {
        this.capacidadMaxima = capacidad;
        this.guijarros = 0;
        this.plantas = 0;
        this.bayas = 0;
        this.pokeBalls = 0;
        this.pociones = 0;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public int getEspacioOcupado() {
        return guijarros + plantas + bayas + pokeBalls + pociones;
    }

    public boolean puedeAgregar(int cantidad) {
        return getEspacioOcupado() + cantidad <= capacidadMaxima;
    }

    public boolean recolectarRecurso(String tipo, int cantidad) {
        if (!puedeAgregar(cantidad)) {
            System.err.println("¡Límite de espacio superado! No se pueden agregar " + cantidad + " " + tipo);
            return false;
        }

        switch (tipo.toLowerCase()) {
            case "guijarro":
                guijarros += cantidad;
                break;
            case "planta":
                plantas += cantidad;
                break;
            case "baya":
                bayas += cantidad;
                break;
            case "pocion":
                pociones += cantidad;
                break;
            default:
                System.out.println("Tipo de recurso desconocido: " + tipo);
                return false;
        }
        System.out.println(
                "Recogiste " + cantidad + " " + tipo + "(s). Total: " + getEspacioOcupado() + "/" + capacidadMaxima);
        return true;
    }

    public boolean fabricarPokeBall() {
        // Lógica de la Misión 1: Profesor Feid
        // Receta: 2 plantas + 3 guijarros
        if (plantas >= 2 && guijarros >= 3) {
            // El crafteo consume 5 items y crea 1, liberando 4 espacios.
            // Siempre habrá espacio si tenemos los materiales.
            // Aún así, validamos si se requiere lógica estricta, pero aquí es seguro.

            plantas -= 2;
            guijarros -= 3;
            pokeBalls++;
            System.out.println("¡Poké Ball fabricada con éxito!");
            return true;
        }
        System.out.println("¡No hay materiales suficientes!");
        return false;
    }

    public void aplicarCastigo() {
        // Lógica del Dr. Brenner (Hito Final)
        // Busca y elimina dos objetos crafteados (Poké Balls o Pociones)
        int eliminados = 0;

        // Prioridad: Pociones (ejemplo) o PokeBalls. Eliminaré PokeBalls primero como
        // en el ejemplo.
        while (eliminados < 2 && pokeBalls > 0) {
            pokeBalls--;
            eliminados++;
        }

        while (eliminados < 2 && pociones > 0) {
            pociones--;
            eliminados++;
        }

        if (eliminados > 0) {
            System.out.println("El Upside Down ha reclamado tus pertenencias...");
        }
    }

    // Getters para acceso si es necesario (útil para UI/Debugging)
    public int getGuijarros() {
        return guijarros;
    }

    public int getPlantas() {
        return plantas;
    }

    public int getBayas() {
        return bayas;
    }

    public int getPokeBalls() {
        return pokeBalls;
    }

    public int getPociones() {
        return pociones;
    }
}
