package com.taskmanager;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new TaskManagerGUI().setVisible(true);
            } catch (Exception e) {
                System.err.println("Error starting the application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
} 