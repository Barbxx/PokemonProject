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

    // Objetos crafteados
    private int heavyBalls, lures, unguentos, elixires, revivires, repelentes, amuletos;

    public Inventario(int capacidad) {
        this.capacidadMaxima = capacidad;
        this.guijarros = 0;
        this.plantas = 0;
        this.bayas = 0;
        this.pokeBalls = 0; // Iniciar con 0 Poké Balls
        this.pociones = 0;

        // Initialize new items
        this.heavyBalls = 0;
        this.lures = 0;
        this.unguentos = 0;
        this.elixires = 0;
        this.revivires = 0;
        this.repelentes = 0;
        this.amuletos = 0;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public int getEspacioOcupado() {
        return guijarros + plantas + bayas + pokeBalls + pociones +
                heavyBalls + lures + unguentos + elixires + revivires + repelentes + amuletos;
    }

    public boolean puedeAgregar(int cantidad) {
        return getEspacioOcupado() + cantidad <= capacidadMaxima;
    }

    public boolean recolectarRecurso(String tipo, int cantidad) {
        // Allow negative quantity for crafting consumption without checking capacity
        // limit if reducing
        if (cantidad > 0 && !puedeAgregar(cantidad)) {
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
            case "pokeball": // Allow adding pokeballs generically
                pokeBalls += cantidad;
                break;
            default:
                System.out.println("Tipo de recurso desconocido (recolección base): " + tipo);
                return false;
        }

        // Normalize negatives to 0 if bug happens
        if (guijarros < 0)
            guijarros = 0;
        if (plantas < 0)
            plantas = 0;
        if (bayas < 0)
            bayas = 0;

        if (cantidad > 0)
            System.out.println("Recogiste " + cantidad + " " + tipo + "(s). Total: " + getEspacioOcupado() + "/"
                    + capacidadMaxima);
        return true;
    }

    public void añadirObjetoCrafteado(String nombre) {
        switch (nombre) {
            case "Heavy Ball":
                heavyBalls++;
                break;
            case "Lure":
                lures++;
                break;
            case "Ungüento Herbal":
                unguentos++;
                break;
            case "Elixir":
                elixires++;
                break;
            case "Revivir":
                revivires++;
                break;
            case "Repelente":
                repelentes++;
                break;
            case "Amuleto":
                amuletos++;
                break;
            default:
                System.out.println("Item desconocido: " + nombre);
                break;
        }
    }

    public boolean fabricarPokeBall() {
        // Lógica de la Misión 1: Profesor Feid (Legacy support, maybe replace with
        // generic crafting later)
        if (plantas >= 2 && guijarros >= 3) {
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
        // Lógica anterior (obsoleta)
    }

    public String perderObjetoCrafteado() {
        // Intenta eliminar 1 objeto crafteado en orden de prioridad o aleatorio
        // Crafteables: PokeBalls, HeavyBalls, Lures, Pociones/Unguentos, Elixires,
        // Revivires, Repelentes, Amuletos

        if (heavyBalls > 0) {
            heavyBalls--;
            return "Heavy Ball";
        }
        if (lures > 0) {
            lures--;
            return "Lure";
        }
        if (unguentos > 0) {
            unguentos--;
            return "Ungüento Herbal";
        }
        if (elixires > 0) {
            elixires--;
            return "Elixir";
        }
        if (revivires > 0) {
            revivires--;
            return "Revivir";
        }
        if (repelentes > 0) {
            repelentes--;
            return "Repelente";
        }
        if (amuletos > 0) {
            amuletos--;
            return "Amuleto";
        }
        // PokeBalls y Pociones base también se pueden considerar crafteables en este
        // contexto
        if (pokeBalls > 0) {
            pokeBalls--;
            return "Poké Ball";
        }
        if (pociones > 0) {
            pociones--;
            return "Poción";
        }

        return null; // No tenía nada para perder
    }

    // Getters
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

    // New Getters
    public int getHeavyBalls() {
        return heavyBalls;
    }

    public int getLures() {
        return lures;
    }

    public int getUnguentos() {
        return unguentos;
    }

    public int getElixires() {
        return elixires;
    }

    public int getRevivires() {
        return revivires;
    }

    public int getRepelentes() {
        return repelentes;
    }

    public int getAmuletos() {
        return amuletos;
    }

    /**
     * Obtiene la cantidad de un item específico por nombre
     */
    public int getCantidad(String nombre) {
        switch (nombre.toLowerCase()) {
            case "guijarro":
                return guijarros;
            case "planta":
                return plantas;
            case "baya":
                return bayas;
            case "pokeball":
                return pokeBalls;
            case "pocion":
                return pociones;
            case "heavyball":
            case "pokeballpeso":
                return heavyBalls;
            case "lure":
                return lures;
            case "unguento":
                return unguentos;
            case "elixir":
                return elixires;
            case "revivir":
                return revivires;
            case "repelente":
                return repelentes;
            case "amuleto":
                return amuletos;
            default:
                return 0;
        }
    }

    /**
     * Consume una cantidad específica de un item
     */
    public boolean consumirItem(String nombre, int cantidad) {
        if (getCantidad(nombre) < cantidad) {
            return false; // No hay suficiente cantidad
        }

        switch (nombre.toLowerCase()) {
            case "guijarro":
                guijarros -= cantidad;
                break;
            case "planta":
                plantas -= cantidad;
                break;
            case "baya":
                bayas -= cantidad;
                break;
            case "pokeball":
                pokeBalls -= cantidad;
                break;
            case "pocion":
                pociones -= cantidad;
                break;
            case "heavyball":
            case "pokeballpeso":
                heavyBalls -= cantidad;
                break;
            case "lure":
                lures -= cantidad;
                break;
            case "unguento":
                unguentos -= cantidad;
                break;
            case "elixir":
                elixires -= cantidad;
                break;
            case "revivir":
                revivires -= cantidad;
                break;
            case "repelente":
                repelentes -= cantidad;
                break;
            case "amuleto":
                amuletos -= cantidad;
                break;
            default:
                return false;
        }
        return true;
    }
}
