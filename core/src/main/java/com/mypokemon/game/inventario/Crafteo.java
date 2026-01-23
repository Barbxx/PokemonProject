package com.mypokemon.game.inventario;

import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.exceptions.EspacioException;

import java.util.ArrayList;
import java.util.List;

// Maneja la lógica de crafteo - Valida requisitos, consume materiales y produce nuevos ítems.

public class Crafteo {
    private List<Receta> recetas;

    public Crafteo() {
        this.recetas = RecetaFactory.crearTodasLasRecetas();
    }

    /**
     * Intenta craftear un ítem especificado por el ID de la receta.
     * Consume los materiales del inventario si es posible.
     *
     * @param idReceta   ID de la receta a utilizar.
     * @param inventario Inventario del jugador donde se consumirán recursos y guardará el resultado.
     * @return El objeto crafteado exitosamente.
     * @throws IllegalArgumentException Si la receta no existe o faltan materiales.
     * @throws EspacioException         Si no hay espacio suficiente en el inventario.
     */

    public ObjetoCrafteado craftear(String idReceta, Inventario inventario) throws EspacioException {
        Receta receta = obtenerReceta(idReceta);
        if (receta == null) {
            throw new IllegalArgumentException("Receta no encontrada: " + idReceta);
        }

        // 1) Valida materiales
        if (!tieneMateriales(receta, inventario)) {
            throw new IllegalArgumentException("Materiales insuficientes para craftear " + receta.getNombreResultado());
        }

        // 2) Valida espacio
        if (!inventario.validarEspacio(1)) {
            throw new EspacioException("¡Inventario lleno! No se puede craftear " + receta.getNombreResultado());
        }

        // 3) Consume materiales
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

        // 4) Crea objeto crafteado
        ObjetoCrafteado resultado = ItemFactory.crearCrafteado(receta.getIdResultado(), 1);

        // 5) Agrega al inventario
        try {
            inventario.agregarItem(resultado);
        } catch (EspacioException e) {
            // Revertir consumo de materiales si falla
            throw e;
        }

        System.out.println("¡Crafteado exitoso: " + resultado.getNombre() + "!");
        return resultado;
    }

    /**
     * Verifica si es posible craftear una receta específica.
     *
     * @param idReceta   ID de la receta.
     * @param inventario Inventario para verificar materiales.
     * @return true si hay suficientes materiales y espacio.
     */
    public boolean puedeCrear(String idReceta, Inventario inventario) {
        Receta receta = obtenerReceta(idReceta);
        if (receta == null) {
            return false;
        }

        return tieneMateriales(receta, inventario) && inventario.validarEspacio(1);
    }

    /**
     * Obtiene una lista de recetas que el jugador puede craftear con su inventario
     * actual.
     *
     * @param inventario Inventario del jugador.
     * @return Lista de recetas realizables.
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
     * Retorna la lista completa de todas las recetas del juego.
     *
     * @return Lista de recetas.
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
