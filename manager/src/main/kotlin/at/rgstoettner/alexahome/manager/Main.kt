package at.rgstoettner.alexahome.manager

import kotlin.system.exitProcess


val skill = RestManager()

fun main(args: Array<String>) {
    val app = App()
    val settings = Settings.load()

    var isLocal = false
    val arguments = args.toMutableList()
    args.forEach {
        if (it.startsWith("--local")) {
            arguments.remove(it)
            isLocal = true
        }
    }

    if (settings != null) { //not ready to connect devices
        when (settings.role) {
//            "admin" -> {
//                RestManager.instance.connect(settings) //blocking connect
//                app.handleAdminMode(settings)
//            }
            "user" -> {
                skill.connect(settings, isLocal) //blocking connect
                app.handleUserMode(settings)
            }
        }
    } else {
        app.handleAdminMode()
    }

    var line = ""
    if (arguments.isEmpty()) {
        line = requiredReadLine()
        line = line.trim()
    } else {
        arguments.forEach { line = line.plus(it).plus(" ") }
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