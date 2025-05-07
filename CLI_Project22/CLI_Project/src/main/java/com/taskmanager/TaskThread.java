package com.taskmanager;

import java.util.concurrent.Callable;

public class TaskThread implements Callable<Boolean> {
    private final TaskOperation operation;
    private final Task task;
    private final int taskId;
    private final DatabaseManager dbManager;

    public enum TaskOperation {
        ADD, UPDATE, DELETE
    }

    public TaskThread(TaskOperation operation, Task task, DatabaseManager dbManager) {
        this.operation = operation;
        this.task = task;
        this.taskId = -1;
        this.dbManager = dbManager;
    }

    public TaskThread(TaskOperation operation, int taskId, DatabaseManager dbManager) {
        this.operation = operation;
        this.task = null;
        this.taskId = taskId;
        this.dbManager = dbManager;
    }

    @Override
    public Boolean call() {
        try {
            switch (operation) {
                case ADD:
                    if (task != null) {
                        dbManager.addTask(task);
                        return true;
                    }
                    break;
                case UPDATE:
                    if (task != null) {
                        dbManager.updateTask(task);
                        return true;
                    }
                    break;
                case DELETE:
                    if (taskId != -1) {
                        dbManager.deleteTask(taskId);
                        return true;
                    }
                    break;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error in task thread: " + e.getMessage());
            return false;
        }
    }
} 