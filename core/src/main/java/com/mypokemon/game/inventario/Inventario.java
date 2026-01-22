package com.mypokemon.game.inventario;

import com.mypokemon.game.inventario.exceptions.EspacioException;
import com.mypokemon.game.inventario.exceptions.PokeballException;
import com.mypokemon.game.inventario.objetoscrafteados.BayaAranja;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Gestiona el almacenamiento de ítems del jugador. 
// Permite almacenar, añadir y consumir recursos y objetos crafteados.
// Valida la capacidad disponible mediante excepciones.
public class Inventario implements Serializable {
    private int capacidadMaxima;
    private List<Recurso> listRecursos;
    private List<ObjetoCrafteado> listObjCrafteados;

    public Inventario(int capacidad) {
        this.capacidadMaxima = capacidad;
        this.listRecursos = new ArrayList<>();
        this.listObjCrafteados = new ArrayList<>();

        // Inicializar recursos básicos
        listRecursos.add(new Recurso("planta", "Planta Medicinal", 0));
        listRecursos.add(new Recurso("guijarro", "Guijarro", 0));
        listRecursos.add(new BayaAranja(0)); // Baya moved to resources

        // Inicializar items crafteados
        listObjCrafteados.add(ItemFactory.crearCrafteado("pokeball", 0));
        listObjCrafteados.add(ItemFactory.crearCrafteado("heavyball", 0));
        listObjCrafteados.add(ItemFactory.crearCrafteado("pocion", 0));
        listObjCrafteados.add(ItemFactory.crearCrafteado("elixir", 0));
        listObjCrafteados.add(ItemFactory.crearCrafteado("revivir", 0));
        listObjCrafteados.add(ItemFactory.crearCrafteado("reproductor", 0));
        listObjCrafteados.add(ItemFactory.crearCrafteado("guante", 0));
        listObjCrafteados.add(ItemFactory.crearCrafteado("frijol", 0));
    }

    // Validación
    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public int getEspacioOcupado() {
        int total = 0;
        for (Recurso r : listRecursos) {
            total += r.getCantidad();
        }
        for (ObjetoCrafteado c : listObjCrafteados) {
            total += c.getCantidad();
        }
        return total;
    }

    public boolean validarEspacio(int cantidad) {
        return getEspacioOcupado() + cantidad <= capacidadMaxima;
    }

    public int verificarDisponibilidad(String id) {
        return getCantidad(id);
    }

    // Excepciones

    public void espacioException() throws EspacioException {
        throw new EspacioException("¡Inventario lleno! No hay espacio disponible.");
    }

    public void pokeballException() throws PokeballException {
        throw new PokeballException("¡No tienes Pokéballs disponibles!");
    }

    // Métodos auxiliares

    private Recurso encontrarRecurso(String id) {
        for (Recurso r : listRecursos) {
            if (r.getId().equalsIgnoreCase(id)) {
                return r;
            }
        }
        return null;
    }

    private ObjetoCrafteado encontrarCrafteado(String id) {
        for (ObjetoCrafteado c : listObjCrafteados) {
            if (c.getId().equalsIgnoreCase(id)) {
                return c;
            }
        }
        return null;
    }

    // Operaciones CRUD

    /**
     * Añade un ítem (Recurso u ObjetoCrafteado) al inventario.
     * Si ya existe, suma la cantidad.
     * 
     * @param item Ítem a agregar.
     * @throws EspacioException Si no hay espacio suficiente.
     */
    public void agregarItem(Item item) throws EspacioException {
        if (!validarEspacio(item.getCantidad())) {
            espacioException();
        }

        if (item instanceof Recurso) {
            Recurso existente = encontrarRecurso(item.getId());
            if (existente != null) {
                existente.agregar(item.getCantidad());
            }
        } else if (item instanceof ObjetoCrafteado) {
            ObjetoCrafteado existente = encontrarCrafteado(item.getId());
            if (existente != null) {
                existente.agregar(item.getCantidad());
            }
        }
    }

    /**
     * Consume una cantidad específica de un ítem por su ID.
     * 
     * @param id       ID del ítem a consumir.
     * @param cantidad Cantidad a restar.
     * @return true si se consumió exitosamente, false si no existe o no hay suficiente.
     */
    public boolean consumirItem(String id, int cantidad) {
        String idLower = id.toLowerCase();

        // Intentar consumir de crafteados
        ObjetoCrafteado c = encontrarCrafteado(idLower);
        if (c != null && c.getCantidad() >= cantidad) {
            c.consumir(cantidad);
            return true;
        }

        // Intentar consumir de recursos
        Recurso r = encontrarRecurso(idLower);
        if (r != null && r.getCantidad() >= cantidad) {
            r.consumir(cantidad);
            return true;
        }

        return false;
    }

    public int getCantidad(String id) {
        String idLower = id.toLowerCase();

        // Buscar en recursos
        Recurso r = encontrarRecurso(idLower);
        if (r != null) {
            return r.getCantidad();
        }

        // Buscar en crafteados
        ObjetoCrafteado c = encontrarCrafteado(idLower);
        if (c != null) {
            return c.getCantidad();
        }

        return 0;
    }

    // Métodos de validación simple

    /**
     * Verifica si se puede agregar una cantidad de ítems al inventario.
     * 
     * @param cantidad Cantidad a agregar.
     * @return true si hay espacio, false si no.
     */
    public boolean puedeAgregar(int cantidad) {
        return validarEspacio(cantidad);
    }

    public void agregarObjeto(Item item) {
        try {
            agregarItem(item);
        } catch (EspacioException e) {
            System.err.println("No se pudo agregar objeto: " + e.getMessage());
        }
    }

    /**
     * Penalización por perder batalla.
     * 
     * @return El nombre del objeto eliminado, o null si no se eliminó nada.
     */
    public String perderObjetoCrafteado() {
        for (ObjetoCrafteado c : listObjCrafteados) {
            if (c.getCantidad() > 0) {
                c.consumir(1);
                return c.getNombre();
            }
        }
        return null;
    }

    // Métodos para acceder a ítems reales

    /**
     * Obtiene la lista de recursos almacenados.
     * 
     * @return Lista de recursos.
     */
    public List<Recurso> getRecursos() {
        return listRecursos;
    }

    /**
     * Obtiene la lista de objetos crafteados almacenados.
     * 
     * @return Lista de objetos crafteados.
     */
    public List<ObjetoCrafteado> getObjetosCrafteados() {
        return listObjCrafteados;
    }

    /**
     * Busca un ítem específico por su ID en todo el inventario (recursos y objetos)
     * 
     * @param id ID del ítem a buscar.
     * @return El ítem encontrado, o null si no existe.
     */
    public Item getItem(String id) {
        String idLower = id.toLowerCase();

        // Buscar en recursos
        Recurso r = encontrarRecurso(idLower);
        if (r != null) {
            return r;
        }

        // Buscar en crafteados
        ObjetoCrafteado c = encontrarCrafteado(idLower);
        if (c != null) {
            return c;
        }

        return null;
    }
}
