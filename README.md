# Task Manager Application

A Java-based task management application with a graphical user interface (GUI) that allows users to create, manage, and track tasks efficiently.

## Features

- **Task Management**
  - Create new tasks with title, description, category, priority, and due date
  - Edit existing tasks
  - Delete tasks
  - View all tasks in a table format
  - Automatic ID management (sequential IDs maintained after deletion)

- **Task Organization**
  - Categorize tasks
  - Set priority levels (HIGH, MEDIUM, LOW)
  - Track task status (PENDING, IN_PROGRESS, COMPLETED)
  - Set due dates for tasks

- **Filtering and Sorting**
  - Filter tasks by:
    - Category
    - Priority
    - Status
    - Upcoming tasks (with due dates)
  - Sort tasks by:
    - ID (default)
    - Category
    - Priority (HIGH > MEDIUM > LOW)
    - Status
    - Due Date

- **User Interface**
  - Modern and intuitive GUI
  - Table view for task display
  - Form-based task creation and editing
  - Real-time updates
  - Responsive design

## Technical Details

- **Database**
  - SQLite database for persistent storage
  - Automatic table creation
  - Efficient querying and data management

- **Architecture**
  - Model-View-Controller (MVC) pattern
  - Multithreaded task operations
  - Singleton database manager
  - Asynchronous task processing

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── taskmanager/
│   │           ├── Task.java              # Task model class
│   │           ├── TaskManager.java       # Task management logic
│   │           ├── TaskManagerGUI.java    # GUI implementation
│   │           ├── DatabaseManager.java   # Database operations
│   │           └── Main.java              # Application entry point
│   └── resources/
└── test/
    └── java/
        └── com/
            └── taskmanager/
                └── tests/                  # Test classes
```

## Requirements

- Java 17 or higher
- Maven for building
- SQLite JDBC driver (automatically managed by Maven)

## Setup and Installation

1. Clone the repository:
   ```bash
   git clone (https://github.com/Elyas-ba/CPIT305-TaskManager)
   cd task-manager
   ```

2. Build the project using Maven:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn exec:java -Dexec.mainClass="com.taskmanager.TaskManagerGUI"
   ```

## Usage

1. **Creating a Task**
   - Click the "Add Task" button
   - Fill in the task details
   - Click "Add" to save

2. **Editing a Task**
   - Select a task from the table
   - Click the "Edit Task" button
   - Modify the task details
   - Click "Update" to save changes

3. **Deleting a Task**
   - Select a task from the table
   - Click the "Delete Task" button
   - Confirm deletion

4. **Filtering Tasks**
   - Use the filter dropdown to select a filter type
   - Choose the filter value
   - Click "Apply Filter" to view filtered tasks

5. **Sorting Tasks**
   - Use the sort dropdown to select a sort type
   - Click "Apply Sort" to sort tasks

## Database Schema

The application uses a SQLite database with the following schema:

```sql
CREATE TABLE IF NOT EXISTS tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    status TEXT DEFAULT 'PENDING',
    category TEXT,
    priority TEXT,
    due_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details. 
