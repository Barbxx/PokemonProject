package com.mypokemon.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

// Gestiona la colección de NPCs en el juego. Sigue el principio SRP.
public class NPCManager {
    private List<NPC> listaNpcs;

    public NPCManager() {
        this.listaNpcs = new ArrayList<>();
    }

    public void agregarNpc(NPC npc) {
        listaNpcs.add(npc);
    }

    public List<NPC> getTodosLosNpcs() {
        return listaNpcs;
    }

    public void renderizar(SpriteBatch batch) {
        for (NPC npc : listaNpcs) {
            npc.renderizar(batch);
        }
    }

    public NPC getNpcCercano(float playerX, float playerY) {
        for (NPC npc : listaNpcs) {
            if (npc.estaCerca(playerX, playerY)) {
                return npc;
            }
        }
        return null;
    }

    public boolean verificarColision(float x, float y, float w, float h) {
        for (NPC npc : listaNpcs) {
            if (npc.superpone(x, y, w, h)) {
                return true;
            }
        }
        return false;
    }

    public void liberar() {
        for (NPC npc : listaNpcs) {
            npc.liberar();
        }
        listaNpcs.clear();
    }
}
