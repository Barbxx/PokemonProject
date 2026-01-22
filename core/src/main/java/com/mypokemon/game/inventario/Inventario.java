package com.mypokemon.game.inventario;

import com.mypokemon.game.inventario.exceptions.SpaceException;
import com.mypokemon.game.inventario.exceptions.PokeballException;
import com.mypokemon.game.inventario.recursos.BayaAranja;
import com.mypokemon.game.inventario.recursos.Guijarro;
import com.mypokemon.game.inventario.recursos.PlantaMedicinal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Gestiona el almacenamiento, validación y consumo de todos los objetos en la mochila del jugador.
public class Inventario implements Serializable {
    private int capacidadMaxima;
    private List<Recurso> listaRecursos;
    private List<ItemCrafteado> listaObjetosFabricados;

    public Inventario(int capacidad) {
        this.capacidadMaxima = capacidad;
        this.listaRecursos = new ArrayList<>();
        this.listaObjetosFabricados = new ArrayList<>();

        // Inicializar recursos básicos
        listaRecursos.add(new PlantaMedicinal(0));
        listaRecursos.add(new Guijarro(0));
        listaRecursos.add(new BayaAranja(0));

        // Inicializar objetos fabricados
        listaObjetosFabricados.add(ObjectFactory.crearCrafteado("pokeball", 0));
        listaObjetosFabricados.add(ObjectFactory.crearCrafteado("heavyball", 0));
        listaObjetosFabricados.add(ObjectFactory.crearCrafteado("pocion", 0));
        listaObjetosFabricados.add(ObjectFactory.crearCrafteado("elixir", 0));
        listaObjetosFabricados.add(ObjectFactory.crearCrafteado("revivir", 0));
        listaObjetosFabricados.add(ObjectFactory.crearCrafteado("reproductor", 0));
        listaObjetosFabricados.add(ObjectFactory.crearCrafteado("guante", 0));
        listaObjetosFabricados.add(ObjectFactory.crearCrafteado("frijol", 0));
    }

    public int obtenerCapacidadMaxima() {
        return capacidadMaxima;
    }

    public int obtenerEspacioOcupado() {
        int total = 0;
        for (Recurso r : listaRecursos)
            total += r.obtenerCantidad();
        for (ItemCrafteado c : listaObjetosFabricados)
            total += c.obtenerCantidad();
        return total;
    }

    public boolean validarEspacio(int cantidad) {
        return obtenerEspacioOcupado() + cantidad <= capacidadMaxima;
    }

    public int verificarDisponibilidad(String id) {
        return obtenerCantidad(id);
    }

    public void lanzarExcepcionEspacio() throws SpaceException {
        throw new SpaceException("¡Inventario lleno! No hay espacio disponible.");
    }

    public void lanzarExcepcionPokeball() throws PokeballException {
        throw new PokeballException("¡No tienes Pokéballs disponibles!");
    }

    private Recurso buscarRecurso(String id) {
        for (Recurso r : listaRecursos)
            if (r.obtenerId().equalsIgnoreCase(id))
                return r;
        return null;
    }

    private ItemCrafteado buscarObjetoFabricado(String id) {
        for (ItemCrafteado c : listaObjetosFabricados)
            if (c.obtenerId().equalsIgnoreCase(id))
                return c;
        return null;
    }

    public void agregarItem(Item item) throws SpaceException {
        if (!validarEspacio(item.obtenerCantidad()))
            lanzarExcepcionEspacio();
        item.guardarEn(this);
    }

    public void agregarRecurso(Recurso r) {
        Recurso existente = buscarRecurso(r.obtenerId());
        if (existente != null)
            existente.agregar(r.obtenerCantidad());
    }

    public void agregarItemCrafteado(ItemCrafteado item) {
        ItemCrafteado existente = buscarObjetoFabricado(item.obtenerId());
        if (existente != null)
            existente.agregar(item.obtenerCantidad());
    }

    public boolean consumirItem(String id, int cantidad) {
        String idMin = id.toLowerCase();
        ItemCrafteado c = buscarObjetoFabricado(idMin);
        if (c != null && c.obtenerCantidad() >= cantidad) {
            c.consumir(cantidad);
            return true;
        }
        Recurso r = buscarRecurso(idMin);
        if (r != null && r.obtenerCantidad() >= cantidad) {
            r.consumir(cantidad);
            return true;
        }
        return false;
    }

    public int obtenerCantidad(String id) {
        String idMin = id.toLowerCase();
        Recurso r = buscarRecurso(idMin);
        if (r != null)
            return r.obtenerCantidad();
        ItemCrafteado c = buscarObjetoFabricado(idMin);
        if (c != null)
            return c.obtenerCantidad();
        return 0;
    }

    public String perderObjetoAleatorio() {
        for (ItemCrafteado c : listaObjetosFabricados) {
            if (c.obtenerCantidad() > 0) {
                c.consumir(1);
                return c.obtenerNombre();
            }
        }
        return null;
    }

    public List<Recurso> obtenerRecursos() {
        return listaRecursos;
    }

    public List<ItemCrafteado> obtenerObjetosFabricados() {
        return listaObjetosFabricados;
    }

    public Item getItem(String id) {
        String idMin = id.toLowerCase();
        Recurso r = buscarRecurso(idMin);
        if (r != null)
            return r;
        ItemCrafteado c = buscarObjetoFabricado(idMin);
        return c;
    }
}
