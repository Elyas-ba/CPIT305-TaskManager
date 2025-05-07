package com.taskmanager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskManager {
    private final DatabaseManager dbManager;
    private final ExecutorService executorService;
    private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    public TaskManager() {
        this.dbManager = DatabaseManager.getInstance();
        this.executorService = Executors.newFixedThreadPool(5);
    }

    public Future<Boolean> addTask(String title, String description, String category, String priority, LocalDateTime dueDate, String status) {
        Task task = new Task(title, description, category, priority, dueDate, status);
        TaskThread taskThread = new TaskThread(TaskThread.TaskOperation.ADD, task, dbManager);
        return executorService.submit(taskThread);
    }

    public Future<Boolean> updateTask(int id, String title, String description, String category, String priority, LocalDateTime dueDate, String status) {
        List<Task> tasks = dbManager.getAllTasks();
        Task taskToUpdate = tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElse(null);

        if (taskToUpdate != null) {
            taskToUpdate.setTitle(title);
            taskToUpdate.setDescription(description);
            taskToUpdate.setCategory(category);
            taskToUpdate.setPriority(priority);
            taskToUpdate.setDueDate(dueDate);
            taskToUpdate.setStatus(status);
            TaskThread taskThread = new TaskThread(TaskThread.TaskOperation.UPDATE, taskToUpdate, dbManager);
            return executorService.submit(taskThread);
        }
        return null;
    }

    public Future<Boolean> deleteTask(int id) {
        return executorService.submit(() -> {
            try {
                // Delete the task
                String deleteSql = "DELETE FROM tasks WHERE id = ?";
                try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(deleteSql)) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                }

                // Get all remaining tasks ordered by ID
                String selectSql = "SELECT id FROM tasks ORDER BY id";
                List<Integer> remainingIds = new ArrayList<>();
                try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(selectSql);
                     ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        remainingIds.add(rs.getInt("id"));
                    }
                }

                // Update IDs to be sequential
                String updateSql = "UPDATE tasks SET id = ? WHERE id = ?";
                try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(updateSql)) {
                    for (int i = 0; i < remainingIds.size(); i++) {
                        int oldId = remainingIds.get(i);
                        int newId = i + 1;
                        if (oldId != newId) {
                            pstmt.setInt(1, newId);
                            pstmt.setInt(2, oldId);
                            pstmt.executeUpdate();
                        }
                    }
                }

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public List<Task> getAllTasks() {
        return dbManager.getAllTasks();
    }

    public List<Task> getTasksByCategory(String category) {
        return dbManager.getTasksByCategory(category);
    }

    public List<Task> getTasksByPriority(String priority) {
        return dbManager.getTasksByPriority(priority);
    }

    public List<Task> getTasksByStatus(String status) {
        return dbManager.getTasksByStatus(status);
    }

    public List<Task> getUpcomingTasks() {
        return dbManager.getUpcomingTasks();
    }

    public void shutdown() {
        executorService.shutdown();
        dbManager.close();
    }

    private Connection getConnection() {
        // Implementation of getConnection method
        return null; // Placeholder return, actual implementation needed
    }
} 