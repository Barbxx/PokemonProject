package com.mypokemon.game.objects;

// Representa al NPC Profesor Ferxxo (Feid). Proporciona diálogos de introducción y guía.
public class FeidNPC extends NPC {
    private static final String[] DIALOG = {
            "¡Epaaa! ¿Qué más pues, mor? ¡Bienvenido a la región OneFerxxo, el lugar más chimba del mundo!",
            "Vea pues, le cuento, OneFerxxo está dividido en cinco zonas que están una re-chimba, pero la vuelta está caliente, ¿sabía? Usted no puede andar por ahí normal, sin con qué defenderse...",
            "La vuelta es que necesitamos que se ponga las pilas, mor. Le toca jalar hasta Tundra Alba y darle por la jeta al dios ese... al Arceus, ¡ese mismo!",
            "Ese que nos mandó a toditos para acá... ¡y yo que estaba re-rela comiéndome un chicharrón! Imagínese esa vuelta...",
            "En el camino se va a encontrar con una mano de bichos; para que no se pierda, chequee la Pokédex, que ahí es donde guardamos todos los encuentros con esas criaturas.",
            "¡Ojo pues! Que en la hierba viven unos Pokémon que están es locos, mor, ¡re-tostados de la cabeza! Y ni hablemos de ese tal Arceus, ese man es otro nivel. Por eso usted necesita su propio Pokémon, pa’ que le cuide la espalda y no me lo dejen solo.",
            "¡Hágale pues! Arranque de una pa’ mi laboratorio que allá le uno. ¡Lo espero, no se me demore pues!"
    };

    // Constructor para el Profesor Ferxxo.
    public FeidNPC(float startX, float startY) {
        super(startX, startY + 5, 25, 35, "feidSprite.png", "Profesor Ferxxo", DIALOG);
        setRetrato("ferxxoCientifico.png");
        cargarMusica("MusicaFeid.mp3");
    }
}
