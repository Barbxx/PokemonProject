package com.mypokemon.game.colisiones;

import com.badlogic.gdx.math.Rectangle;
import com.mypokemon.game.objects.NPC;

/**
 * Wrapper de colisión para NPCs.
 */
public class ColisionNPC extends ColisionBase implements IInteractivo {

    private NPC npc;
    private float rangoInteraccion = 50f;

    public ColisionNPC(NPC npc) {
        this.npc = npc;
        // Crear Rectangle desde la posición y tamaño del NPC
        this.limites = new Rectangle(npc.getX(), npc.getY(), npc.getWidth(), npc.getHeight());
        this.tipo = "NPC";
    }

    @Override
    public boolean estaEnRango(float x, float y) {
        return npc.isClose(x, y);
    }

    @Override
    public void interactuar() {
        // La interacción con NPCs se maneja en GameScreen
        // Este método está aquí para cumplir con la interfaz
    }

    @Override
    public float obtenerRangoInteraccion() {
        return rangoInteraccion;
    }

    @Override
    public String obtenerMensajeInteraccion() {
        return "Presiona T para hablar con " + npc.getName();
    }

    /**
     * Obtiene el NPC asociado a esta colisión.
     */
    public NPC obtenerNPC() {
        return npc;
    }
}
