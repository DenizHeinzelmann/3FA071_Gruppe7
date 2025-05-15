package server;// src/main/java/com/deinegruppe/digitalehausverwaltung/server/Main.java

import utils.Server;

public class Main {
    public static void main(String[] args) {
        String serverUrl = "http://localhost:8000";
        try {
            Server.startServer(serverUrl);
            System.out.println("Drücke Enter, um den Server zu stoppen...");
            System.in.read();
            Server.stopServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
