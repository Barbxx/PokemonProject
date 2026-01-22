package com.mypokemon.game.inventario;

import com.mypokemon.game.inventario.exceptions.SpaceException;
import java.util.ArrayList;
import java.util.List;

// Maneja la lógica de fabricación (crafteo) de objetos: validación, consumo de recursos y creación.
public class Crafteo {
    private List<Receta> recetas;

    public Crafteo() {
        this.recetas = RecetaFactory.crearTodasLasRecetas();
    }

    // Intenta fabricar un objeto consumiendo materiales y validando el espacio en
    // el inventario.
    public ItemCrafteado craftear(String idReceta, Inventario inventario) throws SpaceException {
        Receta receta = buscarRecetaPorId(idReceta);
        if (receta == null) {
            throw new IllegalArgumentException("Receta no encontrada: " + idReceta);
        }

        if (!verificarMateriales(receta, inventario)) {
            throw new IllegalArgumentException("Materiales insuficientes para fabricar " + receta.getNombreResultado());
        }

        if (!inventario.validarEspacio(1)) {
            throw new SpaceException("¡Inventario lleno! No se puede fabricar " + receta.getNombreResultado());
        }

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

        ItemCrafteado resultado = ObjectFactory.crearCrafteado(receta.getIdResultado(), 1);
        try {
            inventario.agregarItem(resultado);
        } catch (SpaceException e) {
            throw e;
        }

        System.out.println("¡Fabricación exitosa: " + resultado.obtenerNombre() + "!");
        return resultado;
    }

    // Verifica si es posible fabricar un objeto (materiales y espacio).
    public boolean puedeFabricar(String idReceta, Inventario inventario) {
        Receta receta = buscarRecetaPorId(idReceta);
        return receta != null && verificarMateriales(receta, inventario) && inventario.validarEspacio(1);
    }

    // Obtiene las recetas que se pueden fabricar con los materiales actuales.
    public List<Receta> obtenerRecetasDisponibles(Inventario inventario) {
        List<Receta> disponibles = new ArrayList<>();
        for (Receta r : recetas) {
            if (puedeFabricar(r.getIdResultado(), inventario))
                disponibles.add(r);
        }
        return disponibles;
    }

    public List<Receta> obtenerTodasLasRecetas() {
        return new ArrayList<>(recetas);
    }

    private Receta buscarRecetaPorId(String id) {
        for (Receta r : recetas) {
            if (r.getIdResultado().equalsIgnoreCase(id))
                return r;
        }
        return null;
    }

    private boolean verificarMateriales(Receta r, Inventario inv) {
        return inv.verificarDisponibilidad("planta") >= r.reqPlantas &&
                inv.verificarDisponibilidad("guijarro") >= r.reqGuijarros &&
                inv.verificarDisponibilidad("baya") >= r.reqBayas;
    }
}
