package com.mypokemon.game.logic;

import com.mypokemon.game.Inventario;
import java.util.HashMap;

public class Crafteo {
    private HashMap<String, Receta> libroRecetas;

    public Crafteo() {
        libroRecetas = new HashMap<>();
        cargarRecetas();
    }

    private void cargarRecetas() {
        // Aquí defines todas las recetas que mencionaste
        libroRecetas.put("Heavy Ball", new Receta("Heavy Ball", 1, 5, 0));
        libroRecetas.put("Lure", new Receta("Lure", 1, 0, 3));
        libroRecetas.put("Ungüento Herbal", new Receta("Ungüento Herbal", 3, 0, 1));
        libroRecetas.put("Elixir", new Receta("Elixir", 0, 2, 2));
        libroRecetas.put("Revivir", new Receta("Revivir", 5, 1, 5));
        libroRecetas.put("Repelente", new Receta("Repelente", 4, 0, 0));
        libroRecetas.put("Amuleto", new Receta("Amuleto", 0, 10, 0));
    }

    public boolean intentarCraftear(String nombre, Inventario inv) {
        if (!libroRecetas.containsKey(nombre))
            return false;

        Receta r = libroRecetas.get(nombre);

        // 1. Verificar si hay materiales suficientes
        boolean tieneMateriales = inv.getPlantas() >= r.reqPlantas &&
                inv.getGuijarros() >= r.reqGuijarros &&
                inv.getBayas() >= r.reqBayas;

        // 2. Verificar si hay espacio en la mochila
        if (tieneMateriales && inv.puedeAgregar(1)) {
            // Restar materiales
            inv.recolectarRecurso("planta", -r.reqPlantas); // Using public method to modify
            inv.recolectarRecurso("guijarro", -r.reqGuijarros);
            inv.recolectarRecurso("baya", -r.reqBayas);

            // Sumar el objeto final al inventario
            inv.añadirObjetoCrafteado(nombre);
            return true;
        }

        return false;
    }
}
