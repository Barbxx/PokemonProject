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
 * 
 */
public class MapFixer {

    /**
     * Punto de entrada principal para ejecutar la corrección del mapa.
     * 
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        String tmxPath = "assets/Mapa_Hisui.tmx";

        Path path = Paths.get(tmxPath);
        if (!Files.exists(path)) {
            path = Paths.get("c:/Users/User/PokemonProject/assets/Mapa_Hisui.tmx");
        }

        try {
            fixMap(path);
            System.out.println("Map updated successfully (Java version).");
        } catch (IOException e) {
            System.err.println("Error updating map: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Corrige el archivo de mapa TMX especificado.
     * Elimina referencias a tilesets problemáticos y remapea GIDs.
     * 
     * @param path Ruta al archivo TMX.
     * @throws IOException Si ocurre un error de lectura/escritura.
     */
    public static void fixMap(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + path.toAbsolutePath());
        }

        byte[] encoded = Files.readAllBytes(path);
        String content = new String(encoded, StandardCharsets.UTF_8);

        long problematicGid = 268430755L;

        // 1. Remove the problematic tileset line
        // <tileset firstgid="268430755" source="Tilesets_ambiente_2.tsx"/>
        String tilesetRegex = "\\s*<tileset firstgid=\"" + problematicGid + "\" source=\"Tilesets_ambiente_2\\.tsx\"/>";
        content = content.replaceAll(tilesetRegex, "");

        // 2. Remap GIDs in the <data encoding="csv"> sections
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
                    // Tiles GID can be large due to flip flags, so use long
                    long val = Long.parseLong(trimmed);

                    // Mask for the actual GID (lower 28 bits)
                    long mask = 0x0FFFFFFFL;
                    long baseGid = val & mask;
                    long flags = val & ~mask;

                    if (baseGid >= problematicGid) {
                        // Remap: subtract offset and add 1
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
            // Reconstruct the data tag
            matcher.appendReplacement(sb, Matcher.quoteReplacement(prefix + remappedData + suffix));
        }
        matcher.appendTail(sb);
        content = sb.toString();

        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }
}
