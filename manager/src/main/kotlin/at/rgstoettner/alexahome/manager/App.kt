package at.rgstoettner.alexahome.manager

import at.rgstoettner.alexahome.manager.controller.DeviceController
import at.rgstoettner.alexahome.manager.controller.InstallController
import at.rgstoettner.alexahome.manager.controller.UserController

class App(val skill: Endpoint) {

    private val policy = mutableListOf<Triple<String, String, () -> Unit>>()

    private val devicePolicy = mutableListOf<Triple<String, String, () -> Unit>>()
    private val userPolicy = mutableListOf<Triple<String, String, () -> Unit>>()
    private val setupPolicy = mutableListOf<Triple<String, String, () -> Unit>>()
    private val otherPolicy = mutableListOf<Triple<String, String, () -> Unit>>()

    init {
        setupPolicy.add(Triple("install", "Installs the base components", { InstallController().install() }))
        setupPolicy.add(Triple("uninstall", "Uninstalls the base components", { InstallController().uninstall() }))
        setupPolicy.add(Triple("update", "Installs new base components but keeps configuration", { InstallController().update() }))

        userPolicy.add(Triple("add user", "Adds a user", { UserController().add() }))
        userPolicy.add(Triple("list users", "Lists all configured users", { UserController().list() }))
        userPolicy.add(Triple("edit user", "Edits a user", { UserController().edit() }))
        userPolicy.add(Triple("remove user", "Removes a user", { UserController().remove() }))

        devicePolicy.add(Triple("add device", "Adds a device", { DeviceController().add() }))
        devicePolicy.add(Triple("list devices", "List all devices", { DeviceController().list() }))
        devicePolicy.add(Triple("edit device", "Edit a device", { DeviceController().edit() }))
        devicePolicy.add(Triple("remove device", "Removes a device", { DeviceController().remove() }))
        devicePolicy.add(Triple("wipe", "Removes everything", { DeviceController().wipe() }))

        otherPolicy.add(Triple("login", "Endpoint to manage devices", { OtherController().login() }))
    }

    private fun formatPolicy(k: String, v: String) = "* %-20s - %s".format(k, v)

    fun handleStrangerMode() {
        val b = StringBuilder()
        b.appendln("Welcome to the HomeManager (Stranger-Mode).")
        b.appendln()
        b.appendln("Hello Stranger")
        b.appendln("You are not logged in!")
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
        userPolicy.forEach { (k, v, _) -> b.appendln(formatPolicy(k, v)) }
        b.appendln()
        b.appendln("Other")
        b.appendln("--------------------------------------------------------")
        otherPolicy.forEach { (k, v, _) -> b.appendln(formatPolicy(k, v)) }
        b.appendln()
        b.appendln("Each command starts an assistant which guides you through the process.")
        b.append("Enter a command:")
        b.toString().println()

        policy.addAll(setupPolicy)
        policy.addAll(userPolicy)
        policy.addAll(otherPolicy)
    }

    fun handleAdminMode(settings: Settings) {
        val b = StringBuilder()
        b.appendln("Welcome to the HomeManager (Admin-Mode).")
        b.appendln()
        b.appendln("Hello ${settings.user}!")
        b.appendln("You are logged in to your personal skill at ${skill.host}:${skill.port}")
        b.appendln()
        b.appendln("Here you can manage your users/devices.")
        b.appendln("The following commands are available:")
        b.appendln()
        b.appendln("Setup")
        b.appendln("--------------------------------------------------------")
        setupPolicy.forEach { (k, v, _) -> b.appendln(formatPolicy(k, v)) }
        b.appendln()
        b.appendln("User Management")
        b.appendln("--------------------------------------------------------")
        userPolicy.forEach { (k, v, _) -> b.appendln(formatPolicy(k, v)) }
        b.appendln()
        b.appendln("Device Management")
        b.appendln("--------------------------------------------------------")
        devicePolicy.forEach { (k, v, _) -> b.appendln(formatPolicy(k, v)) }
        b.appendln("Each command starts an assistant which guides you through the process.")
        b.append("Enter a command:")
        b.toString().println()

        policy.addAll(setupPolicy)
        policy.addAll(userPolicy)
        policy.addAll(devicePolicy)
        policy.addAll(otherPolicy)
    }

    fun handleUserMode(settings: Settings) {
        val b = StringBuilder()
        b.appendln("Welcome to the HomeManager (User-Mode).")
        b.appendln()
        b.appendln("Hello ${settings.user}!")
        b.appendln("You are logged in to your personal skill at ${skill.host}:${skill.port}")
        b.appendln()
        b.appendln("Here you can manage your devices.")
        b.appendln("The following commands are available:")
        b.appendln()
        b.appendln("Device Management")
        b.appendln("--------------------------------------------------------")
        devicePolicy.forEach { (k, v, _) -> b.appendln(formatPolicy(k, v)) }
        b.appendln("Each command starts an assistant which guides you through the process.")
        b.append("Enter a command:")
        b.toString().println()

        policy.addAll(devicePolicy)
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