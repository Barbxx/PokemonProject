package com.mypokemon.game;

public class GestorMisiones {
    /**
     * Enumeración de estados posibles del juego principal.
     */
    public enum EstadoJuego {
        EXPLORANDO,
        COMBATIENDO,
        CRAFTEANDO
    }

    private EstadoJuego estadoActual;
    private Pokedex pokedex;

    /**
     * Constructor del gestor de misiones y estados.
     * 
     * @param pokedex Referencia a la Pokédex del jugador.
     */
    public GestorMisiones(Pokedex pokedex) {
        this.estadoActual = EstadoJuego.EXPLORANDO;
        this.pokedex = pokedex;
    }

    /**
     * Verifica si se cumplen las condiciones para enfrentar al jefe final Arceus.
     * Si se cumple, podría desencadenar eventos de historia.
     */
    public void verificarHitoFinal() {
        // Si la Pokedex cumple los requisitos, dispara el diálogo del Dr. Brenner
        // y cambia la pantalla a la Cueva de Vecna.
        if (pokedex.puedeRetarArceus()) {
            System.out.println("Dr. Brenner: ¡Increíble! Has completado la investigación suficiente.");
            System.out.println("Dr. Brenner: ¡El portal a la Cueva de Vecna se ha abierto!");
            // Lógica para cambiar de pantalla iría aquí
        }
    }

    /**
     * Ejecuta la lógica de finalización del juego tras vencer a Arceus.
     * Inicia el proceso de guardado final.
     */
    public void finalizarJuego() {
        // Al vencer a Arceus, llama a la Serialización para guardar todo el objeto
        // Explorador.
        System.out.println("¡Has vencido a Arceus!");
        System.out.println("Guardando progreso del juego...");

        // Aquí iría la lógica real de serialización
        guardarProgreso();
    }

    /**
     * Guarda el progreso del juego en el almacenamiento persistente.
     */
    private void guardarProgreso() {
        // Implementación dummy de serialización
        try {
            // Ejemplo: ObjectOutputStream...
            System.out.println("Progreso guardado exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al guardar el progreso: " + e.getMessage());
        }
    }

    /**
     * Establece el estado actual del juego.
     * 
     * @param estadoActual Nuevo estado.
     */
    public void setEstadoActual(EstadoJuego estadoActual) {
        this.estadoActual = estadoActual;
    }

    /**
     * Obtiene el estado actual del juego.
     * 
     * @return Estado actual (Explorando, Combatiendo, etc).
     */
    public EstadoJuego getEstadoActual() {
        return estadoActual;
    }
}
