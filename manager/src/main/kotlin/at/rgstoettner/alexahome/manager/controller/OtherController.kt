package at.rgstoettner.alexahome.manager.controller

import at.rgstoettner.alexahome.manager.*
import java.io.File

class OtherController : CommandController() {
    fun login() {
        if (!isInstalled) handleFatalError(CliError.NOT_INSTALLED)
        "Enter your user: [alice@example.com]".println()
        val account = requiredReadLine()
        val file = File("${home.lambda.tls.users}/$account/settings.json")
        if (file.exists()) {
            "Building manager...".println()
            val settings = gson.fromJson(file.reader(), Settings::class.java)
            settings.role = "admin"
            home.manager.srcMainRes.file("settings.json").writeText(gson.toJson(settings))
            "gradle fatJar".runCommandInside(home.manager)
            "yes | cp -f ${home.manager.buildLibs}/manager* ${root}".runCommand()
        } else {
            handleFatalError(CliError.UNKNOWN_USER)
        }
    }
}