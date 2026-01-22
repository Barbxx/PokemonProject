package com.mypokemon.game;

public class GestorMisiones {
    public enum EstadoJuego {
        EXPLORANDO,
        COMBATIENDO,
        CRAFTEANDO
    }

    private EstadoJuego estadoActual;
    private Pokedex pokedex;

    public GestorMisiones(Pokedex pokedex) {
        this.estadoActual = EstadoJuego.EXPLORANDO;
        this.pokedex = pokedex;
    }

    public void verificarHitoFinal() {
        // Si la Pokedex cumple los requisitos, dispara el diálogo del Dr. Brenner
        // y cambia la pantalla a la Cueva de Vecna.
        if (pokedex.puedeRetarArceus()) {
            System.out.println("Dr. Brenner: ¡Increíble! Has completado la investigación suficiente.");
            System.out.println("Dr. Brenner: ¡El portal a la Cueva de Vecna se ha abierto!");
            // Lógica para cambiar de pantalla iría aquí
        }
    }

    public void finalizarJuego() {
        // Al vencer a Arceus, llama a la Serialización para guardar todo el objeto
        // Explorador.
        System.out.println("¡Has vencido a Arceus!");
        System.out.println("Guardando progreso del juego...");

        // Aquí iría la lógica real de serialización
        guardarProgreso();
    }

    private void guardarProgreso() {
        // Implementación dummy de serialización
        try {
            // Ejemplo: ObjectOutputStream...
            System.out.println("Progreso guardado exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al guardar el progreso: " + e.getMessage());
        }
    }

    public void setEstadoActual(EstadoJuego estadoActual) {
        this.estadoActual = estadoActual;
    }

    public EstadoJuego getEstadoActual() {
        return estadoActual;
    }
}
