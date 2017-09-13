package at.rgstoettner.alexahome.manager

import at.rgstoettner.alexahome.manager.controller.CliController
import at.rgstoettner.alexahome.manager.data.Configuration
import com.google.gson.Gson
import java.io.File
import kotlin.system.exitProcess


fun main(args: Array<String>) {

    if (args.isNotEmpty()) {
        configPath = args[0]
    }

    val welcome =
            """
            Welcome to the HomeManager.
            Here you can manage your Alexa/HomeKit devices.
            The following commands are available:

            * install                   - Installs the necessary components
            * add device                - Adds a device
            * add scene                 - Adds a scene
            * list                      - List all scenes and devices
            * list devices              - List all scenes and devices
            * list scenes               - List all scenes and devices
            * edit device <id>          - Edit a device
            * edit scene <id>           - Edit a device
            * remove device <id>        - Removes a device
            * remove scene <id>         - Removes a scene
            * wipe                      - Removes everything

            Each command starts an assistant which guides you through the process.

            Enter a command:
            """.trimIndent()
    println(welcome)

    val line = safeReadLine()
    println()
    if (line.isEmpty()) handleFatalError(CliError.NUMBER_OF_ARGUMENTS)
    val parts = line.split(" ")
    when {
        parts[0] == "install" -> CliController().install()
        parts[0] == "add" -> CliController().add(parts)
        parts[0] == "wipe" -> CliController().wipe(parts)
        parts[0] == "list" -> CliController().list(parts)
        parts[0] == "remove" -> CliController().remove(parts)
        parts[0] == "edit" -> CliController().edit(parts)
        else -> handleFatalError(CliError.UNKNOWN_ARGUMENTS)

    }
}

fun handleFatalError(cause: CliError) {
    println("Error: ${cause.name}")
    exitProcess(1)
}

fun safeReadLine(): String {
    return readLine()!!.trim()
}


val gson = Gson()

fun loadConfiguration(): Configuration {
    var config = Configuration()
    val file = getConfigFile()
    if (file.exists()) {
        file.bufferedReader().use {
            config = gson.fromJson(it.readText(), Configuration::class.java)
        }
    }
    return config
}

fun saveConfiguration(config: Configuration) {
    val json = gson.toJson(config)
    getConfigFile().bufferedWriter().use { out ->
        out.write(json)
    }
}

fun getConfigFile(): File {
    return File(configPath)
}

var configPath = "config.json"