package com.taskmanager;

import java.time.LocalDateTime;

public class Task {
    private int id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private LocalDateTime dueDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum TaskStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    }

    public enum TaskPriority {
        HIGH, MEDIUM, LOW
    }

    public Task(String title, String description, String category, String priority, LocalDateTime dueDate, String status) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Task(int id, String title, String description, String category, String priority, 
               LocalDateTime dueDate, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return String.format("Task #%d: %s\n" +
                           "Description: %s\n" +
                           "Category: %s\n" +
                           "Priority: %s\n" +
                           "Status: %s\n" +
                           "Due Date: %s\n" +
                           "Created: %s\n" +
                           "Last Updated: %s",
                id, title, description, category, priority, status,
                dueDate != null ? dueDate.toString() : "Not set",
                createdAt, updatedAt);
    }
} 