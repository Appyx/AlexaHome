package at.rgstoettner.alexahome.manager.controller

import at.rgstoettner.alexahome.manager.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class InstallController {

    private val git = "https://github.com/Appyx/AlexaHome.git"
    private var log = ""

    private val isInstalled = File("AlexaHome/tls/private/ca.key").exists()

    fun install() {
        if (isInstalled) handleFatalError(CliError.ALREADY_INSTALLED)

        "Installing base components:".println()

        "Fetching project...".println()
        "git clone $git".runCommand()
        "chmod 700 AlexaHome".runCommand()

        "Now a Certificate Authority for signing server and client will be installed.".println()
        "Please provide a password. You will need it to add users later.".println()
        val pass = requiredReadLine {
            uninstall(true)
        }
        "Genrating Certificate Authority..".println()
        "chmod +x AlexaHome/tls/gen_ca.sh".runCommand()
        "cd AlexaHome/tls/ && ./gen_ca.sh $pass".runCommand()

        File("AlexaHome/tls/certs/ca.crt").copyTo(File("AlexaHome/lambda/tls/ca.crt"), true)
        if (log.contains("error", true)) {
            "rm -rf AlexaHome/tls/server AlexaHome/tls/client".runCommand(false)
            handleFatalError(CliError.TLS_CONFIG_FAILED)
        }
        "Successfully installed the base components!".println()
    }

    fun uninstall(force: Boolean = false) {
        if (!isInstalled) handleFatalError(CliError.NOT_INSTALLED)
        fun remove() {
            "rm -rf AlexaHome".runCommand(false)
            "Successfully uninstalled!".println()
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

        "Enter the Amazon Developer Account: [bob@something.com]".println()
        val account = requiredReadLine()
        if (File("AlexaHome/lambda/tls/users/$account").exists()) handleFatalError(CliError.USER_ALREADY_EXISTS)
        "Enter the password for the Certificate Authority:".println()
        val rootPass = requiredReadLine()
        "Enter a password for the user keys: (optional)".println()
        var tlsPass = safeReadLine()
        if (tlsPass.isEmpty()) tlsPass = UUID.randomUUID().toString()
        "Enter the local ip and the local port of the server: [X.X.X.X:YYYY]".println()
        val local = requiredReadLine()
        val localIP = local.split(":")[0]
        val localPort = local.split(":")[1]
        "Enter the remote domain and the remote port of the server: [yourdomain.com:YYYY]".println()
        val remote = requiredReadLine()
        val remoteDomain = remote.split(":")[0]
        val remotePort = remote.split(":")[1]

        "Creating tls configuration...".println()
        "chmod +x AlexaHome/tls/gen_user.sh".runCommand()
        "cd AlexaHome/tls && ./gen_user.sh $tlsPass $localIP $remoteDomain $rootPass $account".runCommand()
        if (log.contains("exception", true) || log.contains("error", true)) {
            "rm -rf AlexaHome/tls/server AlexaHome/tls/client".runCommand(false)
            handleFatalError(CliError.TLS_CONFIG_FAILED)
        }

        "Building AWS lambda...".println()
        File("AlexaHome/tls/client/client-cert.pem").copyTo(File("AlexaHome/lambda/tls/users/$account/client-cert.pem"), true)
        File("AlexaHome/tls/client/client-key.pem").copyTo(File("AlexaHome/lambda/tls/users/$account/client-key.pem"), true)
        File("AlexaHome/lambda/tls/users/$account/pass.txt").writeText(tlsPass, Charsets.UTF_8)
        File("AlexaHome/lambda/tls/users/$account/host.txt").writeText(remoteDomain, Charsets.UTF_8)
        File("AlexaHome/lambda/tls/users/$account/port.txt").writeText(remotePort, Charsets.UTF_8)
        "cd AlexaHome/lambda && zip -r lambda.zip index.js tls".runCommand()
        File("AlexaHome/lambda/lambda.zip").copyTo(File("lambda.zip"), true)

        "mkdir $account".runCommand()

        "Building executor...".println()
        File("AlexaHome/tls/client/client-keystore.jks").copyTo(File("AlexaHome/executor/src/main/resources/tls/client-keystore.jks"), true)
        File("AlexaHome/tls/client/client-truststore.jks").copyTo(File("AlexaHome/executor/src/main/resources/tls/client-truststore.jks"), true)
        File("AlexaHome/executor/src/main/resources/tls/pass.txt").writeText(tlsPass, Charsets.UTF_8)
        File("AlexaHome/executor/src/main/resources/tls/host.txt").writeText(localIP, Charsets.UTF_8)
        File("AlexaHome/executor/src/main/resources/tls/port.txt").writeText(localPort, Charsets.UTF_8)
        "cd AlexaHome/executor && gradle build".runCommand()
        "cp AlexaHome/executor/build/libs/executor* $account".runCommand()

        "Building skill...".println()
        File("AlexaHome/tls/server/server-keystore.jks").copyTo(File("AlexaHome/skill/src/main/resources/tls/server-keystore.jks"), true)
        File("AlexaHome/tls/server/server-truststore.jks").copyTo(File("AlexaHome/skill/src/main/resources/tls/server-truststore.jks"), true)
        File("AlexaHome/skill/src/main/resources/tls/tls.properties").writeText(
                "server.ssl.trust-store-password=$tlsPass\n" +
                        "server.ssl.key-store-password=$tlsPass\n" +
                        "server.ssl.key-password=$tlsPass\n" +
                        "server.port=$localPort"
                , Charsets.UTF_8)
        "cd AlexaHome/skill && gradle build".runCommand()
        "cp AlexaHome/skill/build/libs/skill* $account".runCommand()

        "rm -rf AlexaHome/tls/server AlexaHome/tls/client".runCommand(false)
        "Successfully created user!".println()
        "Created file: lambda.zip".println()
        "Created directory: $account ".println()
    }

    fun removeUser(account: String) {
        if (!isInstalled) handleFatalError(CliError.NOT_INSTALLED)
        val file = File("AlexaHome/lambda/tls/users/$account")
        if (file.exists()) {
            file.deleteRecursively()
            "Successfully removed user $account!".println()
            val parent = File("AlexaHome/lambda/tls/users")
            if (parent.walkTopDown().count() != 0) {
                "cd AlexaHome/lambda && zip -r lambda.zip index.js tls".runCommand()
                File("AlexaHome/lambda/lambda.zip").copyTo(File("lambda.zip"), true)
                "Created file: lambda.zip".println()
            }
        } else {
            handleFatalError(CliError.UNKNOWN_USER)
        }
    }


    private fun String.runCommand(output: Boolean = true) {
        val parts = this.split("//s".toRegex())
        val builder = ProcessBuilder("/bin/bash", "-c", *parts.toTypedArray())
        builder.redirectErrorStream(true)
        val proc = builder.start()
        proc.waitFor(60, TimeUnit.MINUTES)
        if (output) {
            val text = proc.inputStream.bufferedReader().readText()
            log = log.plus(text)
            println(text)
        }
    }

    fun update() {
        if (!isInstalled) handleFatalError(CliError.NOT_INSTALLED)
        try {
            "Backing up base components...".println()
            File("AlexaHome/lambda/tls").copyRecursively(File("update_temp/lambda/tls"), true)
            File("AlexaHome/skill/src/main/resources/tls").copyRecursively(File("update_temp/skill/tls"), true)
            File("AlexaHome/tls").copyRecursively(File("update_temp/general/tls"), true)
            File("AlexaHome/executor/src/main/resources/tls").copyRecursively(File("update_temp/executor/tls"), true)
            File("AlexaHome").deleteRecursively()

            "Fetching project...".println()
            "git clone $git".runCommand()
            "chmod 700 AlexaHome".runCommand()

            "Restoring base components...".println()
            File("update_temp/lambda/tls").copyRecursively(File("AlexaHome/lambda/tls"), true)
            File("update_temp/skill/tls").copyRecursively(File("AlexaHome/skill/src/main/resources/tls"), true)
            File("update_temp/general/tls").copyRecursively(File("AlexaHome/tls"), true)
            File("update_temp/executor/tls").copyRecursively(File("AlexaHome/executor/src/main/resources/tls"), true)
        } catch (ex: Throwable) {
            "Update failed!".println()
        } finally {
            val temp = File("update_temp")
            if (temp.exists()) {
                temp.deleteRecursively()
            }
        }
        "Update successful!".println()
    }
}