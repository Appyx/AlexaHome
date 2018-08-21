package at.rgstoettner.alexahome.manager.controller

import at.rgstoettner.alexahome.manager.CliError
import at.rgstoettner.alexahome.manager.handleFatalError
import at.rgstoettner.alexahome.manager.println
import at.rgstoettner.alexahome.manager.requiredReadLine

class InstallController : AbstractController() {

    private val gitRepo = "https://github.com/Appyx/AlexaHome.git"

    fun install() {
        if (isInstalled) handleFatalError(CliError.ALREADY_INSTALLED)

        "Installing base components:".println()

        "Fetching project...".println()
        "git clone $gitRepo".runCommand()
        "chmod 700 $home".runCommand()

        "Now a Certificate Authority for signing server and client will be installed.".println()
        "Please provide a password. You will need it to add users later.".println()
        val pass = requiredReadLine {
            uninstall(true)
        }
        "Generating Certificate Authority..".println()
        "chmod +x ${home.tls.file("gen_ca.sh")}".runCommand()
        "echo export SAN=DNS:localhost,IP:127.0.0.1".runCommand()
        "./gen_ca.sh $pass".runCommandInside(home.tls)

        home.tls.certs.file("ca.crt").copyTo(home.lambda.tls.file("ca.crt"), true)
        if (logContainsError()) {
            home.tls.server.deleteRecursively()
            home.tls.client.deleteRecursively()
            handleFatalError(CliError.TLS_CONFIG_FAILED)
        }
        "Successfully installed the base components!".println()
    }

    fun uninstall(force: Boolean = false) {
        if (!isInstalled) handleFatalError(CliError.NOT_INSTALLED)
        fun remove() {
            if (home.lambda.tls.users.exists()) {
                val walk = home.lambda.tls.users.walkTopDown()
                val users = walk
                        .maxDepth(1)
                        .asSequence()
                        .filter { it.isDirectory }
                        .map { it.name }
                        .filter { it != "users" }
                        .filter { !it.startsWith(".") }
                        .toList()
                users.forEach { account ->
                    val zip = root.file("$account.zip")
                    if (zip.exists()) {
                        zip.delete()
                    }
                }
            }
            val lambdaZip = root.file("lambda.zip")
            if (lambdaZip.exists()) {
                lambdaZip.delete()
            }
            home.deleteRecursively()

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

    fun update() {
        if (!isInstalled) handleFatalError(CliError.NOT_INSTALLED)
        try {
            "Backing up base components...".println()
            home.lambda.tls.copyRecursively(updateTemp.lambda.tls, true)
            home.tls.copyRecursively(updateTemp.general.tls, true)

            home.deleteRecursively()

            "Fetching project...".println()
            "git clone $gitRepo".runCommand()
            "chmod 700 ${home}".runCommand()

            "Restoring base components...".println()
            updateTemp.lambda.tls.copyRecursively(home.lambda.tls, true)
            updateTemp.general.tls.copyRecursively(home.tls, true)
        } catch (ex: Throwable) {
            "Update failed!".println()
        } finally {
            if (updateTemp.exists()) {
                updateTemp.deleteRecursively()
            }
        }
        "Update successful!".println()
        "Note: New users will have the updated version. Old users will still use the old version.".println()
        "Note: If you want to update old users you have to remove and add them again.".println()
    }
}