package com.mypokemon.game;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.Map;

/**
 * Gestiona el registro de todos los Pokémon avistados y capturados por el
 * jugador.
 * Rastrea el progreso de la investigación para cada especie y verifica
 * requisitos de misiones.
 */
public class Pokedex implements Serializable {
    private static final long serialVersionUID = 2L;
    /**
     * Mapa que asocia el nombre de un Pokémon con su información de especie y
     * progreso.
     */
    private Map<String, EspeciePokemon> registro;
    /** Orden cronológico en el que los Pokémon fueron capturados. */
    private java.util.List<String> capturedOrder;
    /** Orden cronológico en el que los Pokémon fueron avistados. */
    private java.util.List<String> encounterOrder;
    /**
     * Contador de cuántas especies han alcanzado el nivel máximo de investigación
     * (10).
     */
    private int especiesCompletas;

    public Pokedex() {
        this.registro = new TreeMap<>();
        this.capturedOrder = new java.util.ArrayList<>();
        this.encounterOrder = new java.util.ArrayList<>();
        this.especiesCompletas = 0;
    }

    public void registrarAvistamiento(String nombre) {
        registro.putIfAbsent(nombre, new EspeciePokemon(nombre));

        // Añadir a encuentro si es nuevo (para que aparezca en la Pokedex)
        if (!encounterOrder.contains(nombre)) {
            encounterOrder.add(nombre);
        }
    }

    public void registrarAccion(String nombre, boolean esCaptura) {
        // Asegurar que existe en el registro
        registrarAvistamiento(nombre);

        EspeciePokemon especie = registro.get(nombre);

        // Si es Arceus, investigación máxima inmediata al interactuar (derrotar o
        // capturar)
        if (nombre.equalsIgnoreCase("Arceus")) {
            boolean estabaCompleta = especie.isCompleta();
            especie.setInvestigacionMaxica();
            if (!estabaCompleta) {
                especiesCompletas++;
            }
            if (esCaptura && !especie.isCapturado()) {
                capturedOrder.add(nombre);
                especie.setCapturado(true);
            }
            return;
        }

        if (esCaptura) {
            if (!especie.isCapturado()) {
                capturedOrder.add(nombre);
            }
            especie.setCapturado(true);
        }

        // +2 puntos si captura, +1 punto si vence
        int puntos = esCaptura ? 2 : 1;

        // Verificar si ya estaba completo antes de añadir puntos para no contar doble
        boolean estabaCompleta = especie.isCompleta();

        especie.añadirPuntos(puntos);
        System.out.println("Progreso de " + nombre + ": " + especie.getNivelInvestigacion() + "/10");

        // Actualizar contador si se completó recién
        if (!estabaCompleta && especie.isCompleta()) {
            especiesCompletas++;
        }
    }

    public int verificarProgreso() {
        // Recorre el mapa y cuenta cuántas especies han alcanzado el nivel 10.
        // Opcionalmente podemos recalcularlo si queremos ser seguros,
        // pero mantenemos el contador especiesCompletas actualizado en registrarAccion.
        // Para adherirnos a la solicitud que dice "Recorre el mapa...":
        int completas = 0;
        for (EspeciePokemon e : registro.values()) {
            if (e.isCompleta()) {
                completas++;
            }
        }
        // Sincronizamos por si acaso
        this.especiesCompletas = completas;
        return completas;
    }

    public boolean puedeAccederAlHito() {
        // Retorna true si se cumple la condición de Arceus (5 capturados nivel 10)
        return verificarRequisitosArceus();
    }

    public void completarInstantaneamente(String nombre) {
        // Específico para el Hito Final.
        registro.putIfAbsent(nombre, new EspeciePokemon(nombre));

        if (!encounterOrder.contains(nombre)) {
            encounterOrder.add(nombre);
        }

        EspeciePokemon especie = registro.get(nombre);

        boolean estabaCompleta = especie.isCompleta();
        if (!especie.isCapturado()) {
            capturedOrder.add(nombre);
        }
        especie.setInvestigacionMaxica();

        if (!estabaCompleta) {
            especiesCompletas++;
        }
    }

    // Método de compatibilidad para Explorador.java que usaba puedeRetarArceus()
    public boolean puedeRetarArceus() {
        return puedeAccederAlHito();
    }

    public Map<String, EspeciePokemon> getRegistro() {
        return registro;
    }

    public java.util.List<String> getCapturedOrder() {
        if (capturedOrder == null)
            capturedOrder = new java.util.ArrayList<>();
        return capturedOrder;
    }

    public java.util.List<String> getEncounterOrder() {
        if (encounterOrder == null)
            encounterOrder = new java.util.ArrayList<>();
        return encounterOrder;
    }

    public boolean verificarRequisitosArceus() {
        int count = 0;
        for (EspeciePokemon esp : registro.values()) {
            if (esp.getNivelInvestigacion() >= 10 && esp.isCapturado()) {
                count++;
            }
        }
        return count >= 5;
    }
}
