package at.rgstoettner.alexahome.manager.controller

import at.rgstoettner.alexahome.manager.*

class OtherController : CommandController() {
    fun login() {
        if (!isInstalled) handleFatalError(CliError.NOT_INSTALLED)
        "Enter your user: [alice@example.com]".println()
        val account = requiredReadLine()
        val settingsFile = Directory("${home.lambda.tls.users}/$account").file("settings.json")

        if (settingsFile.exists()) {
            val settings = gson.fromJson(settingsFile.reader(), Settings::class.java)
            settings.role = "admin"
            settingsFile.writeText(gson.toJson(settings))

            "Logging in ...".println()
            home.manager.srcMainRes.file("settings.json").writeText(gson.toJson(settings))
            "gradle fatJar".runCommandInside(home.manager, false)
            "yes | cp -f ${home.manager.buildLibs}/manager* ${root}".runCommand(false)
            "The manager is now in Admin-Mode. You can now manage devices".println()
        } else {
            handleFatalError(CliError.UNKNOWN_USER)
        }
    }
}