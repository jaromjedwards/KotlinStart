import java.io.File

data class User(val username: String, val password: String, val tasks: MutableList<String> = mutableListOf())

class TaskManager {
    private val users = mutableMapOf<String, User>()
    private val dataFile = File("userData.txt")

    init {
        loadUserData()
    }

    fun register(username: String, password: String) {
        if (users.containsKey(username)) {
            println("User '$username' already exists.")
        } else {
            users[username] = User(username, password)
            println("User '$username' registered successfully.")
            saveUserData()
        }
    }

    fun login(username: String, password: String): User? {
        val user = users[username]
        if (user != null && user.password == password) {
            println("Welcome back, $username!")
            return user
        } else {
            println("Invalid username or password.")
            return null
        }
    }

    fun saveUserData() {
        dataFile.bufferedWriter().use { out ->
            users.forEach { (username, user) ->
                out.write("$username:${user.password}:${user.tasks.joinToString(";")}\n")
            }
        }
    }

    private fun loadUserData() {
        if (dataFile.exists()) {
            dataFile.bufferedReader().use { reader ->
                reader.readLines().forEach { line ->
                    val (username, password, taskString) = line.split(":")
                    val tasks = taskString.split(";").toMutableList()
                    users[username] = User(username, password, tasks)
                }
            }
        }
    }
}

fun main() {
    val taskManager = TaskManager()
    var currentUser: User? = null

    while (true) {
        if (currentUser == null) {
            println("Options: (Choose one)" +
                    "\n1. Register" +
                    "\n2. Login" +
                    "\n3. Quit")
            when (readLine()) {
                "1" -> {
                    println("Enter a username:")
                    val username = readLine() ?: ""
                    println("Enter a password:")
                    val password = readLine() ?: ""
                    taskManager.register(username, password)
                }
                "2" -> {
                    println("Enter your username:")
                    val username = readLine() ?: ""
                    println("Enter your password:")
                    val password = readLine() ?: ""
                    currentUser = taskManager.login(username, password)
                }
                "3" -> {
                    println("Exiting...")
                    break
                }
                else -> println("Invalid choice.")
            }
        } else {
            println("Options: (Choose one)" +
                    "\n1. Create task" +
                    "\n2. Complete task" +
                    "\n3. Delete task" +
                    "\n4. View tasks" +
                    "\n5. Logout")
            when (readLine()) {
                "1" -> {
                    println("What task would you like to create?")
                    val task = readLine() ?: ""
                    println("What is the reward you will give yourself for that task?")
                    val reward = readLine() ?: ""
                    currentUser.tasks.add("Task: $task Reward: $reward")
                    println("New task created!\nTask: $task Reward: $reward")
                    taskManager.saveUserData()
                }
                "2" -> {
                    if (currentUser.tasks.isNotEmpty()) {
                        println("Select the task you want to mark as completed:")
                        currentUser.tasks.forEachIndexed { index, task -> println("${index + 1}. $task") }
                        val taskIndex = readLine()?.toIntOrNull() ?: -1
                        if (taskIndex in 1..currentUser.tasks.size) {
                            val completedTask = currentUser.tasks.removeAt(taskIndex - 1)
                            println("Task \"$completedTask\" completed! Congratulations!")
                            taskManager.saveUserData()
                        } else {
                            println("Invalid task index!")
                        }
                    } else {
                        println("No tasks created yet!")
                    }
                }
                "3" -> {
                    if (currentUser.tasks.isNotEmpty()) {
                        println("Select the task you want to delete:")
                        currentUser.tasks.forEachIndexed { index, task -> println("${index + 1}. $task") }
                        val taskIndex = readLine()?.toIntOrNull() ?: -1
                        if (taskIndex in 1..currentUser.tasks.size) {
                            val deletedTask = currentUser.tasks.removeAt(taskIndex - 1)
                            println("Task \"$deletedTask\" deleted!")
                            taskManager.saveUserData()
                        } else {
                            println("Invalid task index!")
                        }
                    } else {
                        println("No tasks created yet!")
                    }
                }
                "4" -> {
                    if (currentUser.tasks.isNotEmpty()) {
                        println("Your task list:")
                        currentUser.tasks.forEachIndexed { index, task -> println("${index + 1}. $task") }
                    } else {
                        println("No tasks created yet!")
                    }
                }
                "5" -> {
                    println("Logging out...")
                    currentUser = null
                }
                else -> println("Invalid choice.")
            }
        }
    }
}