package com.mypokemon.game.objects;

/**
 * Clase que representa al NPC Harry Potter.
 * Ofrece diálogos con referencias temáticas y explica el sistema de
 * investigación.
 */
public class HarryPotterNPC extends NPC {
    private static final String[] DIALOG = {
            "Expecto Patronum!... ¡Rayos! Ni una chispa, aquí tampoco sale el ciervo.",
            "Definitivamente creo que ya no estoy en Hogwarts. Este lugar es más extraño que el Departamento de Misterios... las leyes de la magia parecen no funcionar igual aquí. Pero escucha, necesitamos tu ayuda.",
            "¿Ya te han hablado de Arceus, verdad?",
            "Es una fuerza antigua, me atrevería a decir que es aún más poderoso que el que no puede ser nombrado...",
            "Bueno, te lo explico rápido, como si fuera una clase de Pociones… el nivel de investigación es el progreso de conocimiento sobre cada criatura. Obtendrás +2 puntos si logras una captura limpia usando una Poké Ball y +1 punto si los vences en lucha.",
            "¡Ánimo! Ponle el mismo empeño que a un partido de Quidditch, que no tengo un Giratiempo para repetir el día. ¡Date prisa!",
    };

    /**
     * Constructor para Harry Potter.
     * 
     * @param startX Posición inicial en X.
     * @param startY Posición inicial en Y.
     */
    public HarryPotterNPC(float startX, float startY) {
        super(startX, startY, 25, 35, "harryPotterSprite.png", "Entrenador Harry Potter", DIALOG);
        setPortrait("harryPotter.png");
        loadMusic("MusicaHarryPotter.mp3");
    }
}
