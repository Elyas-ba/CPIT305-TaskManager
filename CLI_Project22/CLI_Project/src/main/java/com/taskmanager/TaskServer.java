package com.taskmanager;

import java.io.*;
import java.net.*;

public class TaskServer {
    public static void main(String[] args) {
        int port = 12345;
        System.out.println("Server is running on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected!");

                // Create a new thread to handle client messages
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Action received: " + line);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected.");
        }
    }
}
