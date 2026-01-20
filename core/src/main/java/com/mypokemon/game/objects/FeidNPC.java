package com.mypokemon.game.objects;

public class FeidNPC extends NPC {
    private static final String[] DIALOG = {
            "¡Epa! ¿Qué más pues, mor? Bienvenido a la región de Hisui.",
            "Vea, Hisui está una chimba pero la vuelta está peligrosa. Usted no puede andar por ahí normal sin con qué defenderse...",
            "¡En la hierba viven Pokémon que están es locos, mor! Usted necesita su propio Pokémon pa' que lo cuide.",
            "¡Hágale pues, arranque para mi laboratorio que allá lo espero!"
    };

    public FeidNPC(float startX, float startY) {
        super(startX, startY + 5, 25, 35, "feidSprite.png", "Profesor Ferxxo", DIALOG);
        setPortrait("ferxxoCientifico.png");
        loadMusic("MusicaFeid.mp3");
    }
}
