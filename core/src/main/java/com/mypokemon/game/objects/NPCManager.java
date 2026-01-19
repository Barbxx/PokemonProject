package com.mypokemon.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the collection of NPCs in the game.
 * Implements Single Responsibility Principle (SRP) by handling NPC lifecycle
 * and retrieval.
 */
public class NPCManager {
    private List<NPC> npcs;

    public NPCManager() {
        this.npcs = new ArrayList<>();
    }

    public void addNPC(NPC npc) {
        npcs.add(npc);
    }

    public List<NPC> getAllNPCs() {
        return npcs;
    }

    public void render(SpriteBatch batch) {
        for (NPC npc : npcs) {
            npc.render(batch);
        }
    }

    public NPC getCloseNPC(float playerX, float playerY) {
        for (NPC npc : npcs) {
            if (npc.isClose(playerX, playerY)) {
                return npc;
            }
        }
        return null;
    }

    public boolean checkCollision(float x, float y, float w, float h) {
        for (NPC npc : npcs) {
            if (npc.overlaps(x, y, w, h)) {
                return true;
            }
        }
        return false;
    }

    public void dispose() {
        for (NPC npc : npcs) {
            npc.dispose();
        }
        npcs.clear();
    }
}
