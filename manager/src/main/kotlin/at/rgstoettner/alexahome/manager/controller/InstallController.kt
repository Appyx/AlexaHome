package at.rgstoettner.alexahome.manager.controller

import at.rgstoettner.alexahome.manager.CliError
import at.rgstoettner.alexahome.manager.handleFatalError
import at.rgstoettner.alexahome.manager.println
import at.rgstoettner.alexahome.manager.safeReadLine
import java.util.*
import java.util.concurrent.TimeUnit

class InstallController {

    private val git = "https://github.com/Appyx/AlexaHome.git"


    fun install() {
        "Fetching project...".println()
        "git clone $git".runCommand()

        "Enter a password for the TLS keys: (optional)".println()
        var tlsPass = safeReadLine()
        if (tlsPass.isEmpty()) tlsPass = UUID.randomUUID().toString()
        "Enter your domain: (required)".println()
        val tlsDomain = safeReadLine()
        if (tlsDomain.isEmpty()) reset(CliError.TLS_CONFIG_FAILED)
        "Creating tls configuration...".println()
        "cd AlexaHome/tls && ./tls.sh $tlsPass $tlsDomain".runCommand()

        "Building AWS lambda...".println()
        "cd AlexaHome/lambda && zip ../../lambda.zip tls/ index.js".runCommand()

        "Building skill...".println()
        "cd AlexaHome/skill && gradle build".runCommand()
        "cp AlexaHome/skill/build/libs/skill* .".runCommand()


    }

    private fun reset(error: CliError) {
        clear(true)
        handleFatalError(error)
    }

    fun clear(silent: Boolean = false) {
        "rm -rf AlexaHome".runCommand(false)
        if (!silent) "Removed project".println()
        "rm -rf lambda.zip".runCommand(false)
        if (!silent) "Removed AWS lambda".println()
        "rm -rf skill*".runCommand(false)
        if (!silent) "Removed skill".println()
    }


    private fun String.runCommand(output: Boolean = true) {
        val parts = this.split("//s".toRegex())
        val builder = ProcessBuilder("/bin/sh", "-c", *parts.toTypedArray())
        builder.redirectErrorStream(true)
        val proc = builder.start()
        proc.waitFor(60, TimeUnit.MINUTES)
        if (output) {
            println(proc.inputStream.bufferedReader().readText())
        }
    }
}