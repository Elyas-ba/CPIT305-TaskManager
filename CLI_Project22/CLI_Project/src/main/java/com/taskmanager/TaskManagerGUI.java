package com.taskmanager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.concurrent.Future;

public class TaskManagerGUI extends JFrame {
    private final TaskManager taskManager;
    private final DefaultTableModel tableModel;
    private final JTable taskTable;
    private final JComboBox<String> categoryComboBox;
    private final JComboBox<String> priorityComboBox;
    private final JComboBox<String> statusComboBox;
    private final JSpinner dateSpinner;
    private final JSpinner timeSpinner;
    private final JTextField titleField;
    private final JTextField descriptionField;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");

    public TaskManagerGUI() {
        
        
        taskManager = new TaskManager();
        
        // Set up the main frame
        setTitle("Task Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        // Create the main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // Create the task table
        String[] columnNames = {"ID", "Title", "Description", "Category", "Priority", "Status", "Due Date", "Created", "Updated"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        taskTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(taskTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Create the control panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title field
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(15);
        controlPanel.add(titleField, gbc);

        // Description field
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionField = new JTextField(15);
        controlPanel.add(descriptionField, gbc);

        // Category field
        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryComboBox = new JComboBox<>(new String[]{"WORK", "PERSONAL", "OTHER", "SHOPPING"});
        controlPanel.add(categoryComboBox, gbc);

        // Priority field
        gbc.gridx = 0; gbc.gridy = 3;
        controlPanel.add(new JLabel("Priority:"), gbc);
        gbc.gridx = 1;
        priorityComboBox = new JComboBox<>(new String[]{"HIGH", "MEDIUM", "LOW"});
        controlPanel.add(priorityComboBox, gbc);

        // Status field
        gbc.gridx = 0; gbc.gridy = 4;
        controlPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(new String[]{"PENDING", "IN_PROCESS", "COMPLETE", "CANCELLED"});
        controlPanel.add(statusComboBox, gbc);

        // Due date field with date and time spinners
        gbc.gridx = 0; gbc.gridy = 5;
        controlPanel.add(new JLabel("Due Date:"), gbc);
        gbc.gridx = 1;
        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "MM/dd");
        dateSpinner.setEditor(dateEditor);
        timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        dateTimePanel.add(dateSpinner);
        dateTimePanel.add(timeSpinner);
        controlPanel.add(dateTimePanel, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        // Add button
        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(e -> {
            try {
                String title = titleField.getText();
                String description = descriptionField.getText();
                String category = (String) categoryComboBox.getSelectedItem();
                String priority = (String) priorityComboBox.getSelectedItem();
                String status = (String) statusComboBox.getSelectedItem();
                LocalDateTime dueDate = LocalDateTime.of(
                    ((java.util.Date) dateSpinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                    ((java.util.Date) timeSpinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime()
                );

                Future<Boolean> future = taskManager.addTask(title, description, category, priority, dueDate, status);
                if (future != null) {
                    future.get(); // Wait for the task to be added
                }
                refreshTaskTable();
                clearFields();
                ServerLogger.log("Added task: " + title);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding task: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(addButton);

        // Update button
        JButton updateButton = new JButton("Update Task");
        updateButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a task to update",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String title = titleField.getText();
                String description = descriptionField.getText();
                String category = (String) categoryComboBox.getSelectedItem();
                String priority = (String) priorityComboBox.getSelectedItem();
                String status = (String) statusComboBox.getSelectedItem();
                LocalDateTime dueDate = LocalDateTime.of(
                    ((java.util.Date) dateSpinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                    ((java.util.Date) timeSpinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime()
                );

                taskManager.updateTask(id, title, description, category, priority, dueDate, status);
                refreshTaskTable();
                clearFields();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating task: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(updateButton);

        // Delete button
        JButton deleteButton = new JButton("Delete Task");
        deleteButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a task to delete",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                taskManager.deleteTask(id);
                refreshTaskTable();
                clearFields();
                ServerLogger.log("Deleted task: " + titleField.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting task: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(deleteButton);

        // Sort panel
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sortPanel.add(new JLabel("Sort by:"));
        
        JComboBox<String> sortComboBox = new JComboBox<>(new String[]{"ID", "Category", "Priority", "Status", "Due Date"});
        JButton sortButton = new JButton("Apply Sort");
        
        sortButton.addActionListener(e -> {
            String sortType = (String) sortComboBox.getSelectedItem();
            List<Task> tasks = taskManager.getAllTasks();
            
            switch (sortType) {
                case "Category":
                    tasks.sort((t1, t2) -> t1.getCategory().compareToIgnoreCase(t2.getCategory()));
                    break;
                case "Priority":
                    // Custom sorting for priority (HIGH > MEDIUM > LOW)
                    tasks.sort((t1, t2) -> {
                        String p1 = t1.getPriority();
                        String p2 = t2.getPriority();
                        if (p1.equals(p2)) return 0;
                        if (p1.equals("HIGH")) return -1;
                        if (p1.equals("LOW")) return 1;
                        if (p2.equals("HIGH")) return 1;
                        if (p2.equals("LOW")) return -1;
                        return 0;
                    });
                    break;
                case "Status":
                    tasks.sort((t1, t2) -> t1.getStatus().compareToIgnoreCase(t2.getStatus()));
                    break;
                case "Due Date":
                    tasks.sort((t1, t2) -> {
                        if (t1.getDueDate() == null && t2.getDueDate() == null) return 0;
                        if (t1.getDueDate() == null) return 1;
                        if (t2.getDueDate() == null) return -1;
                        return t1.getDueDate().compareTo(t2.getDueDate());
                    });
                    break;
                default:
                    // Default sort by ID
                    tasks.sort((t1, t2) -> Integer.compare(t1.getId(), t2.getId()));
            }
            
            updateTaskTable(tasks);
        });
        
        sortPanel.add(sortComboBox);
        sortPanel.add(sortButton);

        // Add panels to main panel
        mainPanel.add(controlPanel, BorderLayout.WEST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(sortPanel, BorderLayout.NORTH);

        // Add table selection listener
        taskTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = taskTable.getSelectedRow();
                if (selectedRow != -1) {
                    loadTaskDetails(selectedRow);
                }
            }
        });

        // Set column widths
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(30); // ID column
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Title
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Description
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Category
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(80); // Priority
        taskTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        taskTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Due Date
        taskTable.getColumnModel().getColumn(7).setPreferredWidth(120); // Created
        taskTable.getColumnModel().getColumn(8).setPreferredWidth(120); // Updated

        // Initial refresh
        refreshTaskTable();
    }

    private void refreshTaskTable() {
        List<Task> tasks = taskManager.getAllTasks();
        updateTaskTable(tasks);
    }

    private void updateTaskTable(List<Task> tasks) {
        tableModel.setRowCount(0);
        for (Task task : tasks) {
            Object[] row = {
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCategory(),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate() != null ? task.getDueDate().format(dateFormatter) : "Not set",
                task.getCreatedAt().format(dateFormatter),
                task.getUpdatedAt().format(dateFormatter)
            };
            tableModel.addRow(row);
        }
    }

    private void loadTaskDetails(int row) {
        String title = (String) tableModel.getValueAt(row, 1);
        String description = (String) tableModel.getValueAt(row, 2);
        String category = (String) tableModel.getValueAt(row, 3);
        String priority = (String) tableModel.getValueAt(row, 4);
        String status = (String) tableModel.getValueAt(row, 5);
        String dueDateStr = (String) tableModel.getValueAt(row, 6);

        // Update fields
        titleField.setText(title);
        descriptionField.setText(description);
        categoryComboBox.setSelectedItem(category);
        priorityComboBox.setSelectedItem(priority);
        statusComboBox.setSelectedItem(status);
        
        if (!"Not set".equals(dueDateStr)) {
            try {
                LocalDateTime dueDate = LocalDateTime.parse(dueDateStr, dateFormatter);
                dateSpinner.setValue(java.sql.Timestamp.valueOf(dueDate));
                timeSpinner.setValue(java.sql.Timestamp.valueOf(dueDate));
            } catch (Exception e) {
                // Handle parsing error
            }
        }
    }

    private void clearFields() {
        titleField.setText("");
        descriptionField.setText("");
        categoryComboBox.setSelectedIndex(0);
        priorityComboBox.setSelectedIndex(0);
        statusComboBox.setSelectedIndex(0);
        dateSpinner.setValue(new java.util.Date());
        timeSpinner.setValue(new java.util.Date());
    }
   
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new TaskManagerGUI().setVisible(true);
        });
    }
} 