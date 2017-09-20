package at.rgstoettner.alexahome.manager

import kotlin.system.exitProcess


fun main(args: Array<String>) {
    val app = App()
    val arguments = args.toMutableList()
    app.handleAdminMode()

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