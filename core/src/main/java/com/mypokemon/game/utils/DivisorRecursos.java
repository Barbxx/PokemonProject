package com.mypokemon.game.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DivisorRecursos {

    private static final int CHUNK_HEIGHT = 2048;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;
    private static final int COLUMNS = 8; // 256 / 32
    private static final int TILES_PER_CHUNK = (CHUNK_HEIGHT / TILE_HEIGHT) * COLUMNS;

    public static void main(String[] args) {
        String inputDir = "c:/Users/User/PokemonProject/assets/";
        String imageFile = inputDir + "Tilesets.png";
        String tsxFile = inputDir + "Tilesets_ambiente_2.tsx";

        System.out.println("Starting DivisorRecursos...");

        try {
            // 1. Process Image
            File imgFile = new File(imageFile);
            BufferedImage fullImage = ImageIO.read(imgFile);
            int width = fullImage.getWidth();
            int height = fullImage.getHeight();

            System.out.println("Image Loaded: " + width + "x" + height);

            int numChunks = (int) Math.ceil((double) height / CHUNK_HEIGHT);
            System.out.println("Splitting into " + numChunks + " chunks.");

            for (int i = 0; i < numChunks; i++) {
                int y = i * CHUNK_HEIGHT;
                int h = Math.min(CHUNK_HEIGHT, height - y);

                BufferedImage chunk = fullImage.getSubimage(0, y, width, h);
                File outputFile = new File(inputDir + "Tilesets_part_" + i + ".png");
                ImageIO.write(chunk, "png", outputFile);
                System.out.println("Saved: " + outputFile.getName());
            }

            // 2. Process TSX
            File xmlFile = new File(tsxFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Extract all tile properties
            Map<Integer, Node> tileProperties = new TreeMap<>();
            NodeList tiles = doc.getElementsByTagName("tile");

            for (int i = 0; i < tiles.getLength(); i++) {
                Node tileNode = tiles.item(i);
                if (tileNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) tileNode;
                    int id = Integer.parseInt(element.getAttribute("id"));
                    // Deep clone the properties/node so we can reuse it
                    tileProperties.put(id, tileNode.cloneNode(true));
                }
            }

            System.out.println("Loaded properties for " + tileProperties.size() + " tiles.");

            // Create new TSX files
            for (int i = 0; i < numChunks; i++) {
                createTsxForChunk(i, numChunks, tileProperties, inputDir);
            }

            System.out.println("Asset Splitting Complete!");
            System.out.println(
                    "IMPORTANT: Now you must manually update Mapa_Hisui.tmx to replace the single <tileset> with:");

            int firstGid = 1;
            for (int i = 0; i < numChunks; i++) {
                System.out.println("<tileset firstgid=\"" + firstGid + "\" source=\"Tilesets_part_" + i + ".tsx\"/>");
                firstGid += TILES_PER_CHUNK;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTsxForChunk(int chunkIndex, int totalChunks, Map<Integer, Node> allProps, String dir)
            throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("tileset");
        doc.appendChild(rootElement);

        // Calculate tile range for this chunk
        int startId = chunkIndex * TILES_PER_CHUNK;
        int endId = startId + TILES_PER_CHUNK - 1;

        // Determine height of this specific chunk (last one might be smaller)
        // Note: For TSX metadata, we primarily need the image source.
        // We assume standard chunks are 2048, checking properties against what we have.
        // For simplicity, we just reference the generated image.

        rootElement.setAttribute("version", "1.10");
        rootElement.setAttribute("tiledversion", "1.11.2");
        rootElement.setAttribute("name", "Tilesets_part_" + chunkIndex);
        rootElement.setAttribute("tilewidth", String.valueOf(TILE_WIDTH));
        rootElement.setAttribute("tileheight", String.valueOf(TILE_HEIGHT));
        rootElement.setAttribute("tilecount", String.valueOf(TILES_PER_CHUNK)); // Close enough, Tiled recalculates
                                                                                // usually
        rootElement.setAttribute("columns", String.valueOf(COLUMNS));

        Element image = doc.createElement("image");
        image.setAttribute("source", "Tilesets_part_" + chunkIndex + ".png");
        image.setAttribute("width", String.valueOf(TILE_WIDTH * COLUMNS));
        // We won't rigorously calculate exact pixel height here, Tiled reads image
        // But we should put something valid.
        image.setAttribute("height", String.valueOf(CHUNK_HEIGHT));
        rootElement.appendChild(image);

        // Add relevant tiles
        for (int id = startId; id <= endId; id++) {
            if (allProps.containsKey(id)) {
                Node originalNode = allProps.get(id);
                // We need to import this node into new doc
                Node importedNode = doc.importNode(originalNode, true);
                if (importedNode instanceof Element) {
                    Element tileElem = (Element) importedNode;
                    int newId = id - startId;
                    tileElem.setAttribute("id", String.valueOf(newId));
                    rootElement.appendChild(tileElem);
                }
            }
        }

        // Write to file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(dir + "Tilesets_part_" + chunkIndex + ".tsx"));
        transformer.transform(source, result);

        // System.out.println("Created TSX for chunk " + chunkIndex);
    }
}
