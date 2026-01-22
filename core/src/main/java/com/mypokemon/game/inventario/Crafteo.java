package com.mypokemon.game.inventario;

import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.exceptions.SpaceException;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja toda la lógica de crafteo.
 * Responsabilidades:
 * - Validar si se puede craftear
 * - Consumir materiales del inventario
 * - Crear objetos usando ItemFactory
 * - Añadir resultados al inventario
 */
public class Crafteo {
    private List<Receta> recetas;

    public Crafteo() {
        this.recetas = RecetaFactory.crearTodasLasRecetas();
    }

    /**
     * Intenta craftear un ítem.
     * 
     * @param idReceta   ID de la receta a craftear
     * @param inventario Inventario del jugador
     * @return ObjetoCrafteado creado
     * @throws IllegalArgumentException si no existe la receta o faltan materiales
     * @throws EspacioException         si no hay espacio en el inventario
     */
    public ObjetoCrafteado craftear(String idReceta, Inventario inventario) throws SpaceException {
        Receta receta = obtenerReceta(idReceta);
        if (receta == null) {
            throw new IllegalArgumentException("Receta no encontrada: " + idReceta);
        }

        // 1. Validar materiales
        if (!tieneMateriales(receta, inventario)) {
            throw new IllegalArgumentException("Materiales insuficientes para craftear " + receta.getNombreResultado());
        }

        // 2. Validar espacio
        if (!inventario.validarEspacio(1)) {
            throw new SpaceException("¡Inventario lleno! No se puede craftear " + receta.getNombreResultado());
        }

        // 3. Consumir materiales
        // 3. Consumir materiales (Direct removal without Map)
        if (receta.reqPlantas > 0) {
            if (!inventario.consumirItem("planta", receta.reqPlantas))
                throw new IllegalStateException("Error al consumir planta");
        }
        if (receta.reqGuijarros > 0) {
            if (!inventario.consumirItem("guijarro", receta.reqGuijarros))
                throw new IllegalStateException("Error al consumir guijarro");
        }
        if (receta.reqBayas > 0) {
            if (!inventario.consumirItem("baya", receta.reqBayas))
                throw new IllegalStateException("Error al consumir baya");
        }

        // 4. Crear objeto crafteado
        ObjetoCrafteado resultado = ItemFactory.crearCrafteado(receta.getIdResultado(), 1);

        // 5. Añadir al inventario
        try {
            inventario.agregarItem(resultado);
        } catch (SpaceException e) {
            // Revertir consumo de materiales si falla (aunque ya validamos espacio)
            // En producción podría implementarse un sistema de transacciones
            throw e;
        }

        System.out.println("¡Crafteado exitoso: " + resultado.getNombre() + "!");
        return resultado;
    }

    /**
     * Verifica si se puede craftear una receta.
     * 
     * @param idReceta   ID de la receta
     * @param inventario Inventario del jugador
     * @return true si se puede craftear
     */
    public boolean puedeCrear(String idReceta, Inventario inventario) {
        Receta receta = obtenerReceta(idReceta);
        if (receta == null) {
            return false;
        }

        return tieneMateriales(receta, inventario) && inventario.validarEspacio(1);
    }

    /**
     * Obtiene todas las recetas que se pueden craftear actualmente.
     * 
     * @param inventario Inventario del jugador
     * @return Lista de recetas disponibles
     */
    public List<Receta> obtenerRecetasDisponibles(Inventario inventario) {
        List<Receta> disponibles = new ArrayList<>();
        for (Receta receta : recetas) {
            if (puedeCrear(receta.getIdResultado(), inventario)) {
                disponibles.add(receta);
            }
        }
        return disponibles;
    }

    /**
     * Obtiene todas las recetas del juego.
     */
    public List<Receta> obtenerTodasLasRecetas() {
        return new ArrayList<>(recetas);
    }

    // Métodos privados auxiliares

    private Receta obtenerReceta(String idReceta) {
        for (Receta receta : recetas) {
            if (receta.getIdResultado().equalsIgnoreCase(idReceta)) {
                return receta;
            }
        }
        return null;
    }

    private boolean tieneMateriales(Receta receta, Inventario inventario) {
        return inventario.verificarDisponibilidad("planta") >= receta.reqPlantas &&
                inventario.verificarDisponibilidad("guijarro") >= receta.reqGuijarros &&
                inventario.verificarDisponibilidad("baya") >= receta.reqBayas;
    }
}
