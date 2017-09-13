package at.rgstoettner.alexahome.manager.controller

import at.rgstoettner.alexahome.manager.CliError
import at.rgstoettner.alexahome.manager.handleFatalError
import at.rgstoettner.alexahome.manager.println
import at.rgstoettner.alexahome.manager.safeReadLine
import java.io.File
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
        "Enter your local ip: (required)".println()
        val tlsLocalIP = safeReadLine()
        if (tlsLocalIP.isEmpty()) reset(CliError.TLS_CONFIG_FAILED)
        "Enter your remote domain: (required)".println()
        val tlsRemoteDomain = safeReadLine()
        if (tlsRemoteDomain.isEmpty()) reset(CliError.TLS_CONFIG_FAILED)

        "Creating tls configuration...".println()
        //create temporary dir for generation of the tls files and run the generation script
        val tempDir = File("tls_gen")
        tempDir.mkdir()
        "cd tls_gen && ../AlexaHome/tls/tls.sh $tlsPass $tlsLocalIP $tlsRemoteDomain".runCommand()

        //organize the files
        File("tls_gen/client/server-cert.pem").copyTo(File("AlexaHome/lambda/tls/server-cert.pem"), true)
        File("tls_gen/client/client-cert.pem").copyTo(File("AlexaHome/lambda/tls/client-cert.pem"), true)
        File("tls_gen/client/client-key.pem").copyTo(File("AlexaHome/lambda/tls/client-key.pem"), true)
        File("tls_gen/server/server-keystore.jks").copyTo(File("AlexaHome/skill/src/main/resources/tls/server-keystore.jks"), true)
        File("tls_gen/server/server-truststore.jks").copyTo(File("AlexaHome/skill/src/main/resources/tls/server-truststore.jks"), true)
        File("tls_gen/client/client-keystore.jks").copyTo(File("AlexaHome/executor/src/main/resources/tls/client-keystore.jks"), true)
        File("tls_gen/client/client-truststore.jks").copyTo(File("AlexaHome/executor/src/main/resources/tls/client-truststore.jks"), true)
        File("AlexaHome/lambda/tls/pass.txt").writeText(tlsPass, Charsets.UTF_8);
        File("AlexaHome/skill/src/main/resources/tls/tls.properties").writeText(
                "server.ssl.trust-store-password=$tlsPass\n" +
                        "server.ssl.key-store-password=$tlsPass\n" +
                        "server.ssl.key-password=$tlsPass", Charsets.UTF_8)
        File("AlexaHome/executor/src/main/resources/tls/pass.txt").writeText(tlsPass, Charsets.UTF_8);
        tempDir.deleteRecursively()

        "Building AWS lambda...".println()
        "cd AlexaHome/lambda && zip -r lambda.zip index.js tls".runCommand()
        File("AlexaHome/lambda/lambda.zip").copyTo(File("lambda.zip"), true)

        "Building skill...".println()
        "cd AlexaHome/skill && gradle build".runCommand()
        "cp AlexaHome/skill/build/libs/skill* .".runCommand()

        "Building executor...".println()
        "cd AlexaHome/executor && gradle build".runCommand()
        "cp AlexaHome/executor/build/libs/executor* .".runCommand()

        File("AlexaHome").deleteRecursively()

        "Components successfully installed!".println()
    }

    private fun reset(error: CliError) {
        clear(true)
        handleFatalError(error)
    }

    fun clear(silent: Boolean = false) {
        "rm -rf AlexaHome".runCommand(false)
        if (!silent) "Removed project".println()
        "rm -rf lambda*".runCommand(false)
        if (!silent) "Removed AWS lambda".println()
        "rm -rf skill*".runCommand(false)
        if (!silent) "Removed skill".println()
        "rm -rf executor*".runCommand(false)
        if (!silent) "Removed executor".println()
        "rm -rf tls*".runCommand(false)
        if (!silent) "Removed temporary files".println()
    }


    private fun String.runCommand(output: Boolean = true) {
        val parts = this.split("//s".toRegex())
        val builder = ProcessBuilder("/bin/bash", "-c", *parts.toTypedArray())
        builder.redirectErrorStream(true)
        val proc = builder.start()
        proc.waitFor(60, TimeUnit.MINUTES)
        if (output) {
            println(proc.inputStream.bufferedReader().readText())
        }
    }
}