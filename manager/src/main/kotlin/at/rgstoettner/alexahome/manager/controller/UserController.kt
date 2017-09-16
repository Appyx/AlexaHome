package at.rgstoettner.alexahome.manager.controller

import at.rgstoettner.alexahome.manager.*
import java.io.File
import java.util.*

class UserController : CommandController() {

    fun add() {
        if (!isInstalled) handleFatalError(CliError.NOT_INSTALLED)
        "Enter the Amazon Developer Account: [bob@something.com]".println()
        val account = requiredReadLine()
        addUser(account)
    }

    /**
     * Adds a new user:
     *
     * 1. create certificates for server and client and sign them with CA
     * 2. copy certificates/key from client folder to lambda folder and zip a new package
     * 3. copy truststore/keystore to executor and build it
     * 4. copy truststore/keystore to skill and build it
     * 5. create zip package of executor and skill
     */
    private fun addUser(account: String, settings: Settings? = null) {
        if (File("${home.lambda.tls.users}/$account").exists()) handleFatalError(CliError.USER_ALREADY_EXISTS)
        "Enter the password for the Certificate Authority:".println()
        val rootPass = requiredReadLine()
        "Enter a password for the user keys: (optional)".println()
        var tlsPass = safeReadLine()
        if (tlsPass.isEmpty()) tlsPass = UUID.randomUUID().toString()

        val oldLocal = if (settings != null) "\nPrevious was: ${settings.localIp}:${settings.localPort}" else ""
        "Enter the local ip and the local port of the server: [X.X.X.X:YYYY]$oldLocal".println()
        val local = requiredReadLine()
        val localIP = local.split(":").getOrElse(0) { handleFatalError(CliError.UNKNOWN_ARGUMENTS);"" }
        val localPort = local.split(":").getOrElse(1) { handleFatalError(CliError.UNKNOWN_ARGUMENTS);"" }.toIntOrNull()
        if (localPort == null || localPort <= 1 || localPort > 65535) handleFatalError(CliError.ARGUMENTS_NOT_SUPPORTED)

        val oldRemote = if (settings != null) "\nPrevious was: ${settings.remoteDomain}:${settings.remotePort}" else ""
        "Enter the remote domain and the remote port of the server: [yourdomain.com:YYYY]$oldRemote".println()
        val remote = requiredReadLine()
        val remoteDomain = remote.split(":").getOrElse(0) { handleFatalError(CliError.UNKNOWN_ARGUMENTS); "" }
        val remotePort = remote.split(":").getOrElse(1) { handleFatalError(CliError.UNKNOWN_ARGUMENTS); "" }.toIntOrNull()
        if (remotePort == null || remotePort <= 1 || remotePort > 65535) handleFatalError(CliError.ARGUMENTS_NOT_SUPPORTED)


        val newSettings = Settings()
        newSettings.user = account
        newSettings.role = if (settings != null) settings.role else "user"
        newSettings.localIp = localIP
        newSettings.localPort = localPort
        newSettings.remoteDomain = remoteDomain
        newSettings.remotePort = remotePort

        "Creating tls configuration...".println()
        "chmod +x ${home.tls.file("gen_user.sh")}".runCommand()
        "./gen_user.sh $tlsPass $localIP $remoteDomain $rootPass $account".runCommandInside(home.tls)
        if (logContainsError()) {
            home.tls.server.deleteRecursively()
            home.tls.client.deleteRecursively()
            handleFatalError(CliError.TLS_CONFIG_FAILED)
        }


        "Building AWS lambda...".println()
        val userDir = Directory("${home.lambda.tls.users}/$account")
        home.tls.client.file("client-cert.pem").override(userDir.file("client-cert.pem"))
        home.tls.client.file("client-key.pem").override(userDir.file("client-key.pem"))
        userDir.file("pass.txt").writeText(tlsPass, Charsets.UTF_8)
        userDir.file("settings.json").writeText(gson.toJson(newSettings))

        "zip -r lambda.zip index.js tls".runCommandInside(home.lambda)
        home.lambda.file("lambda.zip").override(File("lambda.zip"))


        userTemp.mkdir()

        "Building executor...".println()
        home.tls.client.file("client-keystore.jks").override(home.executor.srcMainRes.tls.file("client-keystore.jks"))
        home.tls.client.file("client-truststore.jks").override(home.executor.srcMainRes.tls.file("client-truststore.jks"))
        home.executor.srcMainRes.tls.file("pass.txt").writeText(tlsPass, Charsets.UTF_8)
        home.executor.srcMainRes.tls.file("host.txt").writeText(localIP, Charsets.UTF_8)
        home.executor.srcMainRes.tls.file("port.txt").writeText(localPort.toString(), Charsets.UTF_8)
        "gradle build".runCommandInside(home.executor, false)
        "cp ${home.executor.buildLibs}/executor* ${userTemp}".runCommand() //wildcard copy

        "Building skill...".println()
        home.tls.server.file("server-keystore.jks").override(home.skill.srcMainRes.tls.file("server-keystore.jks"))
        home.tls.server.file("server-truststore.jks").override(home.skill.srcMainRes.tls.file("server-truststore.jks"))
        home.skill.srcMainRes.tls.file("tls.properties").writeText(
                "server.ssl.trust-store-password=$tlsPass\n" +
                        "server.ssl.key-store-password=$tlsPass\n" +
                        "server.ssl.key-password=$tlsPass\n" +
                        "server.port=$localPort"
                , Charsets.UTF_8)
        "gradle build".runCommandInside(home.skill, false)
        "cp ${home.skill.buildLibs}/skill* ${userTemp}".runCommand() //wildcard copy

        "Building manager...".println()
        home.tls.client.file("client-keystore.jks").override(home.manager.srcMainRes.tls.file("client-keystore.jks"))
        home.tls.client.file("client-truststore.jks").override(home.manager.srcMainRes.tls.file("client-truststore.jks"))
        home.manager.srcMainRes.file("settings.json").writeText(gson.toJson(newSettings))
        "gradle fatJar".runCommandInside(home.manager, false)
        if (newSettings.role == "admin") {
            "yes | cp -f ${home.manager.buildLibs}/manager* ${root}".runCommand()
        } else {
            "cp ${home.manager.buildLibs}/manager* ${userTemp}".runCommand()
        }


        home.tls.server.deleteRecursively()
        home.tls.client.deleteRecursively()

        "zip $account.zip *".runCommandInside(userTemp)
        userTemp.file("$account.zip").override(root.file("$account.zip"))
        userTemp.deleteRecursively()

        if (logContainsError()) {
            removeUser(account, silent = true)
            handleFatalError(CliError.BUILD_FAILED)
        }
        "Successfully created user!".println()
        println()
        "Created file: lambda.zip".println()
        "Created file: $account.zip".println()
    }

    fun list() {
        if (!isInstalled) handleFatalError(CliError.NOT_INSTALLED)

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

            if (users.isNotEmpty()) {
                "The following users are currently registered:".println()
                users.forEach {
                    "* %-20s".format(it).println()
                }
            } else {
                "No users are registered.".println()
            }
        } else {
            "No users are registered.".println()
        }
    }

    fun edit() {
        if (!isInstalled) handleFatalError(CliError.NOT_INSTALLED)
        "Enter the account: [bob@example.com]".println()
        val account = requiredReadLine()
        val accountDir = Directory("${home.lambda.tls.users}/$account")
        if (!accountDir.exists()) handleFatalError(CliError.UNKNOWN_USER)
        val settings = Settings.loadFrom(accountDir.file("settings.json"))
        removeUser(account, silent = true)
        addUser(account, settings)
    }

    fun remove() {
        if (!isInstalled) handleFatalError(CliError.NOT_INSTALLED)
        "Enter the account: [bob@example.com]".println()
        val account = requiredReadLine()
        removeUser(account)
    }

    /**
     * Removes the user folder from the lambda and creates a new zip
     */
    private fun removeUser(account: String, silent: Boolean = false) {
        val file = File("${home.lambda.tls.users}/$account")
        if (file.exists()) {
            file.deleteRecursively()
            if (!silent) "Successfully removed user $account!".println()
            if (home.lambda.tls.users.walkTopDown().count() != 0) { //if directory is empty
                "zip -r lambda.zip index.js tls".runCommandInside(home.lambda, !silent)
                home.lambda.file("lambda.zip").override(File("lambda.zip"))
                if (!silent) "Created dir: lambda.zip".println()
            }
        } else {
            handleFatalError(CliError.UNKNOWN_USER)
        }
    }


}