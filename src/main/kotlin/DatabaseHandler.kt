import java.sql.Connection     // Source for dependency and imports - ChatGPT
import java.sql.DriverManager
import java.sql.Statement
import java.util.Scanner


data class Task(val id: Int, val title: String, val description: String, val due: String, val priority: Int, var status: Int)

class DatabaseHandler {

    private val url = "jdbc:sqlite:tasks.db" //source - ChatGPT

    init {

        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                Title TEXT,
                Description TEXT,
                Due TEXT,
                Priority INTEGER,
                Status INT
            );
        """.trimIndent()

        executeUpdate(createTableQuery)
    }

    fun createTask(task: Task) {
        val insertQuery = "INSERT INTO tasks (Title, Description, Due, Priority, Status) VALUES ('${task.title}', '${task.description}', '${task.due}', '${task.priority}', '${task.status}')"
        executeUpdate(insertQuery)
    }

    fun viewTasks(): List<Task> {

        val selectQuery = "SELECT * FROM tasks"
        val resultSet = executeQuery(selectQuery)

        val tasks = mutableListOf<Task>()

        while (resultSet.next()) {
            val id = resultSet.getInt("id")
            val title = resultSet.getString("Title")
            val description = resultSet.getString("Description")
            val due = resultSet.getString("Due")
            val priority = resultSet.getInt("Priority")
            val status = resultSet.getInt("Status")

            tasks.add(Task(id, title, description, due, priority, status))
        }
        return tasks
    }

    fun updateTask(taskId: Int, updatedTitle: String, updatedDescription: String, updatedDue: String, updatedPriority: Int, updatedStatus: Int) {
        val updateQuery = """
        UPDATE tasks 
        SET Title = '$updatedTitle', Description = '$updatedDescription', Due = '$updatedDue', Priority = '$updatedPriority', Status = '$updatedStatus'
        WHERE id = $taskId
    """.trimIndent()

        executeUpdate(updateQuery)
    }

    fun removeTask(taskId: Int) {
        val deleteQuery = "DELETE FROM tasks WHERE id = $taskId"
        executeUpdate(deleteQuery)
    }

    fun sortByStatus(): List<Task> {

        val selectQuery = "SELECT * FROM tasks ORDER BY Status"
        val resultSet = executeQuery(selectQuery)

        val tasks = mutableListOf<Task>()

        while (resultSet.next()) {
            val id = resultSet.getInt("id")
            val title = resultSet.getString("Title")
            val description = resultSet.getString("Description")
            val due = resultSet.getString("Due")
            val priority = resultSet.getInt("Priority")
            val status = resultSet.getInt("Status")

            tasks.add(Task(id, title, description, due, priority, status))
        }
        return tasks
    }

    fun completedTasksCount(): Int {
        val selectQuery = "SELECT COUNT(*) FROM tasks WHERE Status = 1"
        val resultSet = executeQuery(selectQuery)

        return if (resultSet.next()) {
            resultSet.getInt(1)
        } else {
            0
        }
    }

    fun uncompletedTasksCount(): Int {
        val selectQuery = "SELECT COUNT(*) FROM tasks WHERE Status = 0"
        val resultSet = executeQuery(selectQuery)

        return if (resultSet.next()) {
            resultSet.getInt(1)
        } else {
            0
        }
    }


    private fun executeUpdate(query: String) { // source for this function ChatGPT
        DriverManager.getConnection(url).use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate(query)
            }
        }
    }

    private fun executeQuery(query: String): java.sql.ResultSet { // source for this function ChatGPT
        val connection: Connection = DriverManager.getConnection(url)
        val statement: Statement = connection.createStatement()
        return statement.executeQuery(query)
    }
}

fun main() {

    val dbHandler = DatabaseHandler()
    val scanner = Scanner(System.`in`)

    while (true) {
        println("==== Task Manager ====")
        println("What would You like to do? Enter the number without dot (ex. 1)")
        println("1. Add Task")
        println("2. View Tasks")
        println("3. Update Task")
        println("4. Remove Task")
        println("5. View Completed and Uncompleted Tasks Count")
        println("6. View Tasks Sorted by Status")
        println("0. Exit")

        print("Enter your choice: ")

        val choice = scanner.nextInt()

        when (choice) {
            1 -> {
                println("Enter task details:")
                print("Title: ")
                val title = scanner.next()
                print("Description: ")
                val description = scanner.next()
                print("Due Date: ")
                val due = scanner.next()
                print("Priority (1 - 3, 1 - low importance, 3 - high importance): ")
                val priority = scanner.nextInt()
                print("Status (0 for uncompleted, 1 for completed): ")
                val status = scanner.nextInt()

                dbHandler.createTask(Task(0, title, description, due, priority, status))
                println("Task added successfully!\n")
            }
            2 -> {
                println("==== All Tasks ====")
                val tasks = dbHandler.viewTasks()
                tasks.forEach { println(it) }
                println()
            }
            3 -> {
                println("Enter task ID to update:")
                val tasks = dbHandler.viewTasks()
                tasks.forEach { println(it) }
                println()
                print("Task ID: ")
                val taskId = scanner.nextInt()

                println("Enter updated task details:")
                print("Title: ")
                val updatedTitle = scanner.next()
                print("Description: ")
                val updatedDescription = scanner.next()
                print("Due Date: ")
                val updatedDue = scanner.next()
                print("Priority (1 - 3, 1 - low importance, 3 - high importance): ")
                val updatedPriority = scanner.nextInt()
                print("Status (0 for uncompleted, 1 for completed): ")
                val updatedStatus = scanner.nextInt()

                dbHandler.updateTask(taskId, updatedTitle, updatedDescription, updatedDue, updatedPriority, updatedStatus)
                println("Task updated successfully!\n")
            }
            4 -> {
                println("Enter task ID to remove:")
                val tasks = dbHandler.viewTasks()
                tasks.forEach { println(it) }
                println()
                print("Task ID: ")
                val taskId = scanner.nextInt()

                dbHandler.removeTask(taskId)
                println("Task removed successfully!\n")
            }
            5 -> {
                val completedCount = dbHandler.completedTasksCount()
                val uncompletedCount = dbHandler.uncompletedTasksCount()

                println("Completed Tasks: $completedCount")
                println("Uncompleted Tasks: $uncompletedCount\n")
            }
            6 -> {
                println("Tasks sorted by status: ")
                val tasks = dbHandler.sortByStatus()
                tasks.forEach { println(it) }
                println()
            }
            0 -> {
                println("Exiting Task Manager. Goodbye!")
                return
            }
            else -> {
                println("Invalid choice. Please enter a valid option.\n")
            }
        }
    }
}

/// Ideas for update:
/// Class for date;
/// Pretty Print for tasks
/// Update only necessary part of the task not all fields