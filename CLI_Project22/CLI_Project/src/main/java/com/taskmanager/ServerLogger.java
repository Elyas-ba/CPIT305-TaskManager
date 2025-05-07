package com.taskmanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerLogger {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void log(String message) {
        try (
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            out.println(message);
        } catch (IOException e) {
            System.out.println("Unable to log to server: " + e.getMessage());
        }
    }
}
