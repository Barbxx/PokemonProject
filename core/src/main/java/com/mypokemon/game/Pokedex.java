package com.mypokemon.game;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.Map;

/**
 * Registro de Pokemon encontrados y capturados por el explorador.
 * Gestiona el nivel de investigacion de cada especie y verifica requisitos para
 * enfrentar a Arceus.
 */
public class Pokedex implements Serializable {
    private static final long serialVersionUID = 2L;
    private Map<String, EspeciePokemon> registro;
    private java.util.List<String> capturedOrder;
    private java.util.List<String> encounterOrder;
    private int especiesCompletas;

    /**
     * Constructor de la Pokedex que inicializa los registros vacios.
     */
    public Pokedex() {
        this.registro = new TreeMap<>();
        this.capturedOrder = new java.util.ArrayList<>();
        this.encounterOrder = new java.util.ArrayList<>();
        this.especiesCompletas = 0;
    }

    /**
     * Registra el avistamiento de un Pokemon sin modificar su nivel de
     * investigacion.
     * 
     * @param nombre Nombre del Pokemon avistado
     */
    public void registrarAvistamiento(String nombre) {
        registro.putIfAbsent(nombre, new EspeciePokemon(nombre));

        // Añadir a encuentro si es nuevo (para que aparezca en la Pokedex)
        if (!encounterOrder.contains(nombre)) {
            encounterOrder.add(nombre);
        }
    }

    /**
     * Registra una accion con un Pokemon y actualiza su nivel de investigacion.
     * 
     * @param nombre    Nombre del Pokemon
     * @param esCaptura true si fue capturado, false si solo fue derrotado
     */
    public void registrarAccion(String nombre, boolean esCaptura) {
        // Asegurar que existe en el registro
        registrarAvistamiento(nombre);

        EspeciePokemon especie = registro.get(nombre);

        // Si es Arceus, investigación máxima inmediata al interactuar (derrotar )
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

    /**
     * Verifica cuantas especies han alcanzado el nivel 10 de investigacion.
     * 
     * @return Cantidad de especies con investigacion completa
     */
    public int verificarProgreso() {
        // Recorre el mapa y cuenta cuántas especies han alcanzado el nivel 10.
        int completas = 0;
        for (EspeciePokemon e : registro.values()) {
            if (e.isCompleta()) {
                completas++;
            }
        }
        this.especiesCompletas = completas;
        return completas;
    }

    /**
     * Verifica si el jugador puede acceder al hito final con Arceus.
     * 
     * @return true si cumple los requisitos, false en caso contrario
     */
    public boolean puedeAccederAlHito() {
        // Retorna true si se cumple la condición de Arceus (5 capturados nivel 10)
        return verificarRequisitosArceus();
    }

    /**
     * Completa instantaneamente la investigacion de un Pokemon a nivel maximo.
     * 
     * @param nombre Nombre del Pokemon a completar
     */
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

    /**
     * Verifica si el jugador puede retar a Arceus.
     * 
     * @return true si tiene 5 especies capturadas a nivel 10, false en caso
     *         contrario
     */
    public boolean puedeRetarArceus() {
        return puedeAccederAlHito();
    }

    /**
     * Obtiene el mapa de registro de todas las especies.
     * 
     * @return Mapa con todas las especies registradas
     */
    public Map<String, EspeciePokemon> getRegistro() {
        return registro;
    }

    /**
     * Obtiene la lista de Pokemon capturados en orden de captura.
     * 
     * @return Lista de nombres de Pokemon capturados
     */
    public java.util.List<String> getCapturedOrder() {
        if (capturedOrder == null)
            capturedOrder = new java.util.ArrayList<>();
        return capturedOrder;
    }

    /**
     * Obtiene la lista de Pokemon encontrados en orden de encuentro.
     * 
     * @return Lista de nombres de Pokemon encontrados
     */
    public java.util.List<String> getEncounterOrder() {
        if (encounterOrder == null)
            encounterOrder = new java.util.ArrayList<>();
        return encounterOrder;
    }

    /**
     * Verifica si el jugador cumple los requisitos para enfrentar a Arceus.
     * 
     * @return true si tiene al menos 5 Pokemon capturados con nivel 10, false en
     *         caso contrario
     */
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
