package com.taskmanager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:taskmanager.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "description TEXT," +
                "status TEXT DEFAULT 'PENDING'," +
                "category TEXT," +
                "priority TEXT," +
                "due_date TIMESTAMP," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    public synchronized void addTask(Task task) {
        // First, get the next available ID
        int nextId = 1;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(id) as max_id FROM tasks")) {
            if (rs.next()) {
                nextId = rs.getInt("max_id") + 1;
            }
        } catch (SQLException e) {
            System.err.println("Error getting next ID: " + e.getMessage());
            return;
        }

        String sql = "INSERT INTO tasks (id, title, description, status, category, priority, due_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, nextId);
            pstmt.setString(2, task.getTitle());
            pstmt.setString(3, task.getDescription());
            pstmt.setString(4, task.getStatus());
            pstmt.setString(5, task.getCategory());
            pstmt.setString(6, task.getPriority());
            pstmt.setTimestamp(7, task.getDueDate() != null ? Timestamp.valueOf(task.getDueDate()) : null);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding task: " + e.getMessage());
        }
    }

    public synchronized List<Task> getAllTasks() {
        return getTasksWithQuery("SELECT * FROM tasks ORDER BY id ASC");
    }

    public synchronized List<Task> getTasksByCategory(String category) {
        return getTasksWithQuery("SELECT * FROM tasks WHERE category = ? ORDER BY id ASC", category);
    }

    public synchronized List<Task> getTasksByPriority(String priority) {
        return getTasksWithQuery("SELECT * FROM tasks WHERE priority = ? ORDER BY id ASC", priority);
    }

    public synchronized List<Task> getTasksByStatus(String status) {
        return getTasksWithQuery("SELECT * FROM tasks WHERE status = ? ORDER BY id ASC", status);
    }

    public synchronized List<Task> getUpcomingTasks() {
        return getTasksWithQuery("SELECT * FROM tasks WHERE due_date IS NOT NULL AND due_date > CURRENT_TIMESTAMP " +
                               "ORDER BY id ASC");
    }

    private List<Task> getTasksWithQuery(String query, String... params) {
        List<Task> tasks = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setString(i + 1, params[i]);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getString("priority"),
                        rs.getTimestamp("due_date") != null ? rs.getTimestamp("due_date").toLocalDateTime() : null,
                        rs.getString("status"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                    );
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving tasks: " + e.getMessage());
        }
        return tasks;
    }

    public synchronized void updateTask(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, status = ?, " +
                    "category = ?, priority = ?, due_date = ?, " +
                    "updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getStatus());
            pstmt.setString(4, task.getCategory());
            pstmt.setString(5, task.getPriority());
            pstmt.setTimestamp(6, task.getDueDate() != null ? Timestamp.valueOf(task.getDueDate()) : null);
            pstmt.setInt(7, task.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating task: " + e.getMessage());
        }
    }

    public synchronized void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting task: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
} 