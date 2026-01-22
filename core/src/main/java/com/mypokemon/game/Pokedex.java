package com.mypokemon.game;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el registro de todos los Pokémon avistados y capturados por el
 * jugador.
 * Rastrea el progreso de la investigación para cada especie y verifica
 * requisitos de misiones.
 */
public class Pokedex implements Serializable {
    private static final long serialVersionUID = 2L;

    private Map<String, EspeciePokemon> registro;
    private List<String> capturedOrder;
    private List<String> encounterOrder;

    public Pokedex() {
        this.registro = new TreeMap<>();
        this.capturedOrder = new ArrayList<>();
        this.encounterOrder = new ArrayList<>();
    }

    public void registrarAvistamiento(String nombre) {
        registro.putIfAbsent(nombre, new EspeciePokemon(nombre));
        if (!encounterOrder.contains(nombre)) {
            encounterOrder.add(nombre);
        }
    }

    public void registrarAccion(String nombre, boolean esCaptura) {
        registrarAvistamiento(nombre);
        EspeciePokemon especie = registro.get(nombre);

        if (nombre.equalsIgnoreCase("Arceus")) {
            especie.establecerInvestigacionMaxima();
            if (esCaptura && !especie.estaCapturado()) {
                capturedOrder.add(nombre);
                especie.establecerCapturado(true);
            }
            return;
        }

        if (esCaptura) {
            if (!especie.estaCapturado()) {
                capturedOrder.add(nombre);
            }
            especie.establecerCapturado(true);
        }

        int puntos = esCaptura ? 2 : 1;
        especie.añadirPuntos(puntos);
        System.out.println("Progreso de " + nombre + ": " + especie.obtenerNivelInvestigacion() + "/10");

    }

    public int verificarProgreso() {
        int completas = 0;
        for (EspeciePokemon e : registro.values()) {
            if (e.estaCompleta()) {
                completas++;
            }
        }
        return completas;
    }

    public boolean puedeAccederAlHito() {
        return verificarRequisitosArceus();
    }

    public void completarInstantaneamente(String nombre) {
        registro.putIfAbsent(nombre, new EspeciePokemon(nombre));
        if (!encounterOrder.contains(nombre)) {
            encounterOrder.add(nombre);
        }
        EspeciePokemon especie = registro.get(nombre);
        if (!especie.estaCapturado()) {
            capturedOrder.add(nombre);
        }
        especie.establecerInvestigacionMaxima();
    }

    public boolean puedeRetarArceus() {
        return puedeAccederAlHito();
    }

    public Map<String, EspeciePokemon> getRegistro() {
        return registro;
    }

    public List<String> getCapturedOrder() {
        if (capturedOrder == null)
            capturedOrder = new ArrayList<>();
        return capturedOrder;
    }

    public List<String> getEncounterOrder() {
        if (encounterOrder == null)
            encounterOrder = new ArrayList<>();
        return encounterOrder;
    }

    public boolean verificarRequisitosArceus() {
        int count = 0;
        for (EspeciePokemon esp : registro.values()) {
            if (esp.obtenerNivelInvestigacion() >= 10 && esp.estaCapturado()) {
                count++;
            }
        }
        return count >= 5;
    }
}
