package com.mypokemon.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mypokemon.game.PokemonMain;

/** Launches the desktop (LWJGL3) application. */
public class LanzadorLwjgl3 {
    /**
     * Punto de entrada principal para la versión de escritorio de la aplicación.
     * 
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired())
            return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    /**
     * Crea e inicia la instancia de Lwjgl3Application.
     * 
     * @return Nueva instancia de la aplicación.
     */
    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new PokemonMain(), getDefaultConfiguration());
    }

    /**
     * Configura los parámetros por defecto de la ventana y el renderizado (FPS,
     * VSync, iconos).
     * 
     * @return Configuración de la aplicación configurada.
     */
    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("PokemonGame");

        configuration.useVsync(true);

        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);

        configuration.setWindowedMode(1280, 720);
        configuration.setResizable(true);

        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0);

        return configuration;
    }
}
