package at.rgstoettner.alexahome.manager.controller

import at.rgstoettner.alexahome.manager.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class InstallController {

    private val git = "https://github.com/Appyx/AlexaHome.git"

    private val projectDir = File("AlexaHome")
    private val tlsDir = File("tls_gen")

    private val isInstalled = File("AlexaHome/tls/private/ca.key").exists()

    fun install() {
        if (isInstalled) handleFatalError(CliError.ALREADY_INSTALLED)
        try {
            "Installing base components:".println()

            "Fetching project...".println()
            "git clone $git".runCommand()
            "chmod 600 AlexaHome".runCommand()

            "Now a Certificate Authority for signing server and client will be installed.".println()
            "Please provide a password. You will need it to add users later.".println()
            val pass = requiredReadLine {
                uninstall(true)
            }
            "Genrating Certificate Authority..".println()
            "chmod +x AlexaHome/tls/gen_ca.sh".runCommand()
            "cd AlexaHome/tls/ && ./gen_ca.sh $pass".runCommand()

            File("AlexaHome/tls/certs/ca.crt").copyTo(File("AlexaHome/lambda/tls/ca.crt"), true)
            File("AlexaHome/tls/users").mkdir()
            "Successfully installed the base components!".println()
        } catch (ex: Throwable) {
            uninstall(true)
        }
    }

    fun uninstall(force: Boolean = false) {
        fun remove() {
            "rm -rf AlexaHome".runCommand(false)
            "Removed base components".println()
        }

        if (force) {
            remove()
        } else {
            "You will no longer be able to add users.".println()
            "Are you sure to remove the Certificate Authority? [yes/no]".println()
            val answer = requiredReadLine()
            if (answer == "yes") {
                remove()
            } else {
                "Canceled.".println()
            }
        }
    }

    fun addUser() {
        if (!isInstalled) handleFatalError(CliError.NOT_INSTALLED)

        "Enter the Amazon Developer Account".println()
        val account = requiredReadLine()
        "Enter the password for the Certificate Authority:".println()
        val rootPass = requiredReadLine()
        "Enter a password for the user keys: (optional)".println()
        var tlsPass = safeReadLine()
        if (tlsPass.isEmpty()) tlsPass = UUID.randomUUID().toString()
        "Enter the local ip of the server:".println()
        val tlsLocalIP = requiredReadLine()
        "Enter the remote domain:".println()
        val tlsRemoteDomain = requiredReadLine()

        "Creating tls configuration...".println()

        "rm -r server client".runCommand(false)
        if (File("AlexaHome/tls/users/$account").exists()) handleFatalError(CliError.USER_ALREADY_EXISTS)
        "cd AlexaHome/tls && ./gen_user.sh $tlsPass $tlsLocalIP $tlsRemoteDomain $rootPass $account".runCommand()

        "Building AWS lambda...".println()
        File("AlexaHome/tls/client/client-cert.pem").copyTo(File("AlexaHome/lambda/tls/users/$account/client-cert.pem"), true)
        File("AlexaHome/tls/client/client-key.pem").copyTo(File("AlexaHome/lambda/tls/users/$account/client-key.pem"), true)
        File("AlexaHome/lambda/tls/pass.txt").writeText(tlsPass, Charsets.UTF_8);
        "cd AlexaHome/lambda && zip -r lambda.zip index.js tls".runCommand()
        File("AlexaHome/lambda/lambda.zip").copyTo(File("lambda.zip"), true)

        "mkdir $account".runCommand()

        "Building executor...".println()
        File("AlexaHome/tls/client/client-keystore.jks").copyTo(File("AlexaHome/executor/src/main/resources/tls/client-keystore.jks"), true)
        File("AlexaHome/tls/client/client-truststore.jks").copyTo(File("AlexaHome/executor/src/main/resources/tls/client-truststore.jks"), true)
        File("AlexaHome/executor/src/main/resources/tls/pass.txt").writeText(tlsPass, Charsets.UTF_8);
        "cd AlexaHome/executor && gradle build".runCommand()
        "cp AlexaHome/executor/build/libs/executor* $account".runCommand()

        "Building skill...".println()
        File("AlexaHome/tls/server/server-keystore.jks").copyTo(File("AlexaHome/skill/src/main/resources/tls/server-keystore.jks"), true)
        File("AlexaHome/tls/server/server-truststore.jks").copyTo(File("AlexaHome/skill/src/main/resources/tls/server-truststore.jks"), true)
        File("AlexaHome/skill/src/main/resources/tls/tls.properties").writeText(
                "server.ssl.trust-store-password=$tlsPass\n" +
                        "server.ssl.key-store-password=$tlsPass\n" +
                        "server.ssl.key-password=$tlsPass", Charsets.UTF_8)
        "cd AlexaHome/skill && gradle build".runCommand()
        "cp AlexaHome/skill/build/libs/skill* $account".runCommand()
    }


    fun clear(silent: Boolean = false) {
        "rm -rf AlexaHome".runCommand(false)
        if (!silent) "Removed git".println()
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