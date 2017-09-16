package at.rgstoettner.alexahome.manager.controller

import at.rgstoettner.alexahome.manager.*
import java.io.File

class OtherController : CommandController() {
    fun login() {
        if (!isInstalled) handleFatalError(CliError.NOT_INSTALLED)
        "Enter your user: [alice@example.com]".println()
        val account = requiredReadLine()
        val userDir = Directory("${home.lambda.tls.users}/$account")

        if (userDir.file("settings.json").exists()) {
            val settings = gson.fromJson(userDir.file("settings.json").reader(), Settings::class.java)
            settings.role = "admin"

            "Building AWS lambda...".println()
            userDir.file("settings.json").writeText(gson.toJson(settings))
            "zip -r lambda.zip index.js tls".runCommandInside(home.lambda)
            home.lambda.file("lambda.zip").override(File("lambda.zip"))

            "Building manager...".println()
            home.manager.srcMainRes.file("settings.json").writeText(gson.toJson(settings))
            "gradle fatJar".runCommandInside(home.manager, false)
            "yes | cp -f ${home.manager.buildLibs}/manager* ${root}".runCommand()
        } else {
            handleFatalError(CliError.UNKNOWN_USER)
        }
    }
}