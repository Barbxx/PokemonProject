package com.mypokemon.game.network;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private String saveName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            // Read Save Name (Partida Name)
            saveName = in.readUTF();
            System.out.println("Processing save file validation for: " + saveName);

            // Check/Create File
            // We use a relative path "saves" in the project root
            File saveDir = new File("saves");
            if (!saveDir.exists()) {
                if (saveDir.mkdir()) {
                    System.out.println("Created saves directory.");
                }
            }

            File saveFile = new File(saveDir, saveName + ".json");
            if (!saveFile.exists()) {
                if (saveFile.createNewFile()) {
                    System.out.println("Created new save file: " + saveFile.getAbsolutePath());
                    // Optionally write initial JSON structure here if needed
                    // e.g., FileUtils.writeStringToFile(saveFile, "{}", StandardCharsets.UTF_8);
                }
            } else {
                System.out.println("Found existing save file: " + saveFile.getAbsolutePath());
            }

            // Send Confirmation
            out.writeUTF("OK");

            // Loop for updates (Asynchronous Pokedex saving)
            while (!socket.isClosed()) {
                try {
                    String msgType = in.readUTF();
                    if ("POKEDEX_UPDATE".equals(msgType)) {
                        String data = in.readUTF();
                        // Here you would parse the JSON and update the file
                        // For now, we simulate the 'Thread' processing
                        System.out.println("[" + getName() + "] Saving Pokedex for " + saveName + " async: " + data);
                        // TODO: Write 'data' to saveFile safely
                    }
                } catch (EOFException e) {
                    // Client disconnected
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("ClientHandler Error: " + e.getMessage());
        } finally {
            try {
                if (socket != null && !socket.isClosed())
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("ClientHandler finished for " + saveName);
        }
    }
}
