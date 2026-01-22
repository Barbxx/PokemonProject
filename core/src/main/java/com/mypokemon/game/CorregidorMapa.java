package com.mypokemon.game;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase de utilidad para corregir problemas de GID en mapas TMX.
 * Traducida de update_map.py.
 */
public class CorregidorMapa {

    public static void main(String[] args) {
        // Ruta al archivo TMX
        String tmxPath = "assets/Mapa_Hisui.tmx";

        // Si se ejecuta desde la raíz del proyecto, esta ruta debería funcionar.
        // De lo contrario, se puede usar una ruta absoluta.
        Path path = Paths.get(tmxPath);
        if (!Files.exists(path)) {
            // Fallback para entornos de IDE donde el CWD es diferente
            path = Paths.get("c:/Users/User/PokemonProject/assets/Mapa_Hisui.tmx");
        }

        try {
            fixMap(path);
            System.out.println("Mapa actualizado correctamente (versión Java).");
        } catch (IOException e) {
            System.err.println("Error al actualizar el mapa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Corrige el mapa eliminando tilesets problemáticos y remapeando GIDs.
     * 
     * @param path Ruta del archivo del mapa.
     * @throws IOException Si ocurre un error de lectura/escritura.
     */
    public static void fixMap(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("Archivo no encontrado: " + path.toAbsolutePath());
        }

        byte[] encoded = Files.readAllBytes(path);
        String content = new String(encoded, StandardCharsets.UTF_8);

        long problematicGid = 268430755L;

        // 1. Eliminar la línea del tileset problemático
        // <tileset firstgid="268430755" source="Tilesets_ambiente_2.tsx"/>
        String tilesetRegex = "\\s*<tileset firstgid=\"" + problematicGid + "\" source=\"Tilesets_ambiente_2\\.tsx\"/>";
        content = content.replaceAll(tilesetRegex, "");

        // 2. Remapear GIDs en las secciones <data encoding="csv">
        Pattern dataPattern = Pattern.compile("(<data encoding=\"csv\">)(.*?)(</data>)", Pattern.DOTALL);
        Matcher matcher = dataPattern.matcher(content);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String prefix = matcher.group(1);
            String data = matcher.group(2);
            String suffix = matcher.group(3);

            String[] gids = data.split(",");
            List<String> newGids = new ArrayList<>();

            for (String g : gids) {
                String trimmed = g.trim();
                if (trimmed.isEmpty())
                    continue;

                try {
                    // Los GID de los tiles pueden ser grandes debido a los flags de
                    // rotación/espejo, se usa long
                    long val = Long.parseLong(trimmed);

                    // Máscara para el GID real (28 bits inferiores)
                    long mask = 0x0FFFFFFFL;
                    long baseGid = val & mask;
                    long flags = val & ~mask;

                    if (baseGid >= problematicGid) {
                        // Remapear: restar el offset y sumar 1
                        long newBase = baseGid - problematicGid + 1;
                        long newVal = flags | newBase;
                        newGids.add(String.valueOf(newVal));
                    } else {
                        newGids.add(trimmed);
                    }
                } catch (NumberFormatException e) {
                    newGids.add(trimmed);
                }
            }

            String remappedData = String.join(",", newGids);
            // Reconstruir el tag de datos
            matcher.appendReplacement(sb, Matcher.quoteReplacement(prefix + remappedData + suffix));
        }
        matcher.appendTail(sb);
        content = sb.toString();

        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }
}
