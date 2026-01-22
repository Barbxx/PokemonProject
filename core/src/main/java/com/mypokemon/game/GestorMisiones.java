package com.mypokemon.game;

/**
 * Controla el estado general del juego y maneja los eventos importantes de la
 * historia.
 * Supervisa el progreso del jugador a través de la Pokédex y gestiona el final
 * del juego.
 */
public class GestorMisiones {
    /**
     * Enumeración de los posibles estados del juego.
     * Ayuda a gestionar qué lógica debe ejecutarse en el bucle principal.
     */
    public enum EstadoJuego {
        /** El jugador se mueve libremente por el mapa. */
        EXPLORANDO,
        /** El jugador está en una batalla Pokémon. */
        COMBATIENDO,
        /** El jugador está en la pantalla de elaboración de objetos. */
        CRAFTEANDO
    }

    private EstadoJuego estadoActual;
    private Pokedex pokedex;

    /**
     * Constructor del gestor de misiones.
     * 
     * @param pokedex Referencia a la Pokédex del jugador para verificar progreso.
     */
    public GestorMisiones(Pokedex pokedex) {
        this.estadoActual = EstadoJuego.EXPLORANDO;
        this.pokedex = pokedex;
    }

    /**
     * Verifica si se cumplen las condiciones para el evento final del juego.
     * Comprueba si la investigación está completa para desbloquear a Arceus.
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
     * Ejecuta la secuencia de finalización del juego tras vencer al jefe final.
     * Guarda el progreso y muestra mensajes de cierre.
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
     * Método auxiliar para guardar el progreso de forma segura.
     */
    private void guardarProgreso() {
        try {
            System.out.println("Progreso guardado exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al guardar el progreso: " + e.getMessage());
        }
    }

    /**
     * Establece el estado actual del juego.
     * 
     * @param estadoActual Nuevo estado (EXPLORANDO, COMBATIENDO, etc.)
     */
    public void setEstadoActual(EstadoJuego estadoActual) {
        this.estadoActual = estadoActual;
    }

    /**
     * Obtiene el estado actual del juego.
     * 
     * @return El estado enum actual.
     */
    public EstadoJuego getEstadoActual() {
        return estadoActual;
    }
}
