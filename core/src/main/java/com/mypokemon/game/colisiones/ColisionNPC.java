package com.mypokemon.game.colisiones;

import com.badlogic.gdx.math.Rectangle;
import com.mypokemon.game.objects.NPC;

// Envoltorio (Wrapper) de colisión e interacción para NPCs. Conecta la lógica del NPC con el sistema de colisiones.
public class ColisionNPC extends ColisionBase implements IInteractivo {

    private NPC npc;
    private float rangoInteraccion = 50f;

    /**
     * Constructor para crear una colisión de NPC.
     * 
     * @param npc Instancia del NPC asociado.
     */
    public ColisionNPC(NPC npc) {
        this.npc = npc;
        // Crear Rectangle desde la posición y tamaño del NPC
        this.limites = new Rectangle(npc.getX(), npc.getY(), npc.getWidth(), npc.getHeight());
        this.tipo = "NPC";
    }

    /**
     * Verifica si el jugador está dentro del rango de interacción del NPC.
     * 
     * @param x Posición X del jugador.
     * @param y Posición Y del jugador.
     * @return true si está cerca.
     */
    @Override
    public boolean estaEnRango(float x, float y) {
        return npc.isClose(x, y);
    }

    // Método para cumplir con la interfaz.
    @Override
    public void interactuar() {
        // La interacción con NPCs se maneja en GameScreen
    }

    /**
     * Obtiene el rango máximo de interacción.
     * 
     * @return Distancia en unidades del juego.
     */
    @Override
    public float obtenerRangoInteraccion() {
        return rangoInteraccion;
    }

    /**
     * Proporciona el mensaje de interacción con el NPC.
     * 
     * @return Mensaje con el nombre del NPC.
     */
    @Override
    public String obtenerMensajeInteraccion() {
        return "Presiona T para hablar con " + npc.getName();
    }

    /**
     * Obtiene el NPC asociado a esta colisión.
     * 
     * @return Instancia del NPC.
     */
    public NPC obtenerNPC() {
        return npc;
    }
}
