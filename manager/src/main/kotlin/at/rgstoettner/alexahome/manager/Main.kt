package at.rgstoettner.alexahome.manager

import kotlin.system.exitProcess


fun main(args: Array<String>) {


    val settings = Settings.load()
    val skill = Endpoint.instance.configure(settings) //blocking connect


    val app = App(skill)

    if (settings.user != null) { //not ready to configure devices
        when (settings.role) {
            "admin" -> {
                app.handleAdminMode(settings)
            }
            "user" -> {
                app.handleUserMode(settings)
            }
        }
    } else {
        app.handleStrangerMode()
    }

    var line = ""
    if (args.isEmpty()) {
        line = requiredReadLine()
        line = line.trim()
    } else {
        args.forEach { line = line.plus(it).plus(" ") }
        line = line.trim()
        println("Argument was: $line")
    }
    app.execute(line)
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