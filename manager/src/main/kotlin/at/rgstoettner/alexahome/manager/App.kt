package at.rgstoettner.alexahome.manager

import at.rgstoettner.alexahome.manager.controller.InstallController
import at.rgstoettner.alexahome.manager.controller.UserController

class App() {

    private val policy = mutableListOf<Triple<String, String, () -> Unit>>()
    private val adminPolicy = mutableListOf<Triple<String, String, () -> Unit>>()
    private val setupPolicy = mutableListOf<Triple<String, String, () -> Unit>>()

    init {
        setupPolicy.add(Triple("install", "Installs the base components (project, CA)", { InstallController().install() }))
        setupPolicy.add(Triple("uninstall", "Uninstalls the base components", { InstallController().uninstall() }))
        setupPolicy.add(Triple("update", "Installs new base components but keeps configuration", { InstallController().update() }))

        adminPolicy.add(Triple("add user", "Adds a user", { UserController().add() }))
        adminPolicy.add(Triple("list users", "Lists all configured users", { UserController().list() }))
        adminPolicy.add(Triple("remove user", "Removes a user", { UserController().remove() }))
    }

    private fun formatPolicy(k: String, v: String) = "* %-20s - %s".format(k, v)

    fun setup(showUi: Boolean = true) {
        if (showUi) {
            val b = StringBuilder()
            b.appendln("Welcome to the HomeManager.")
            b.appendln()
            b.appendln("Here you can set up the required components.")
            b.appendln("The following commands are available:")
            b.appendln()
            b.appendln("Setup")
            b.appendln("--------------------------------------------------------")
            setupPolicy.forEach { (k, v, _) -> b.appendln(formatPolicy(k, v)) }
            b.appendln()
            b.appendln("User Management")
            b.appendln("--------------------------------------------------------")
            adminPolicy.forEach { (k, v, _) -> b.appendln(formatPolicy(k, v)) }
            b.appendln()
            b.appendln("Each command starts an assistant which guides you through the process.")
            b.append("Enter a command:")
            b.toString().println()
        }
        policy.addAll(setupPolicy)
        policy.addAll(adminPolicy)
    }

    fun execute(line: String) {
        var invoked = false
        policy.forEach { (k, _, action) ->
            if (line == k) {
                action.invoke()
                invoked = true
            }
        }
        if (!invoked) {
            handleFatalError(CliError.ARGUMENTS_NOT_SUPPORTED)
        }
    }
}