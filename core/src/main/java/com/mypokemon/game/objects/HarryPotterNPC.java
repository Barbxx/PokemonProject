package com.mypokemon.game.objects;

public class HarryPotterNPC extends NPC {
    private static final String[] DIALOG = {
            "¡Expecto Patronum!... Rayos, aquí tampoco sale el ciervo.",
            "Definitivamente creo que ya no estoy en Hogwarts. Este lugar es más extraño que el Departamento de Misterios.",
            "Escucha, no sirve de nada detenerse en los sueños y olvidarse de vivir, así que ponte a trabajar.",
            "Para registrar a estos Pokémon necesito que subas su Nivel de Investigación a 10.",
            "Recuerda: +2 puntos si logras una captura exitosa usando una Poké Ball y +1 punto si los vences en lucha.",
            "¡Ánimo!, que no tengo un Giratiempo para repetir el día."
    };

    public HarryPotterNPC(float startX, float startY) {
        super(startX, startY, 25, 35, "harryPotterSprite.png", "Entrenador Harry Potter", DIALOG);
        setPortrait("harryPotter.png");
    }
}
