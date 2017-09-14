package at.rgstoettner.alexahome.manager

import at.rgstoettner.alexahome.manager.controller.CliParser
import at.rgstoettner.alexahome.manager.data.Configuration
import com.google.gson.Gson
import java.io.File
import kotlin.system.exitProcess


fun main(args: Array<String>) {

    val parts: List<String>
    if (args.isEmpty()) {
        val welcome =
                """
            Welcome to the HomeManager.
            Here you can manage your Alexa/HomeKit devices.
            The following commands are available:

            * install                   - Installs the base components to work with other commands
            * uninstall                 - Uninstalls the base components
            * clear                     - Removes all generated content but the base components
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
        if (line.isEmpty()) handleFatalError(CliError.NUMBER_OF_ARGUMENTS)
        parts = line.split(" ")
    } else {
        parts = args.toList()
    }

    when {
        parts[0] == "clear" -> CliParser().clear()
        parts[0] == "install" -> CliParser().install()
        parts[0] == "uninstall" -> CliParser().uninstall()
        parts[0] == "add" -> CliParser().add(parts)
        parts[0] == "wipe" -> CliParser().wipe(parts)
        parts[0] == "list" -> CliParser().list(parts)
        parts[0] == "remove" -> CliParser().remove(parts)
        parts[0] == "edit" -> CliParser().edit(parts)
        else -> handleFatalError(CliError.UNKNOWN_ARGUMENTS)
    }
}

fun handleFatalError(cause: CliError) {
    println("Error: ${cause.name}")
    exitProcess(1)
}

fun safeReadLine(): String {
    val str = readLine()!!.trim()
    println()
    return str
}

fun requiredReadLine(onFatal: (() -> Unit) = {}): String {
    val str = readLine()!!.trim()
    if (str.isEmpty()) {
        onFatal()
        handleFatalError(CliError.INPUT_REQUIRED)
    }
    println()
    return str
}

fun String.println() {
    println(this)
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