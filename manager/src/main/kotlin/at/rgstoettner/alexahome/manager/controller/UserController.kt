package at.rgstoettner.alexahome.manager.controller

import at.rgstoettner.alexahome.manager.*
import java.io.File
import java.util.*

class UserController : AbstractController() {

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
    private fun addUser(account: String) {
        if (home.lambda.tls.users.dir(account).exists()) handleFatalError(CliError.USER_ALREADY_EXISTS)
        val settings = Settings()

        "Enter the password for the Certificate Authority:".println()
        val rootPass = requiredReadLine()
        "Enter a password for the user keys: (optional)".println()
        var tlsPass = safeReadLine()
        if (tlsPass.isEmpty()) tlsPass = UUID.randomUUID().toString()

        "Enter the local ip and the local port of the server: [X.X.X.X:YYYY]".println()
        val local = requiredReadLine()
        val localIP = local.split(":").getOrElse(0) { handleFatalError(CliError.UNKNOWN_ARGUMENTS);"" }
        val localPort = local.split(":").getOrElse(1) { handleFatalError(CliError.UNKNOWN_ARGUMENTS);"" }.toIntOrNull()
        if (localPort == null || localPort <= 1 || localPort > 65535) handleFatalError(CliError.ARGUMENTS_NOT_SUPPORTED)

        "Enter the remote domain and the remote port of the server: [yourdomain.com:YYYY]".println()
        val remote = requiredReadLine()
        val remoteDomain = remote.split(":").getOrElse(0) { handleFatalError(CliError.UNKNOWN_ARGUMENTS); "" }
        val remotePort = remote.split(":").getOrElse(1) { handleFatalError(CliError.UNKNOWN_ARGUMENTS); "" }.toIntOrNull()
        if (remotePort == null || remotePort <= 1 || remotePort > 65535) handleFatalError(CliError.ARGUMENTS_NOT_SUPPORTED)

        settings.user = account
        settings.role = "user"
        settings.localIp = localIP
        settings.localPort = localPort
        settings.remoteDomain = remoteDomain
        settings.remotePort = remotePort
        settings.password = tlsPass


        "Creating tls configuration...".println()
        "chmod +x ${home.tls.file("gen_user.sh")}".runCommand()

        var content = home.tls.file("openssl_nosan.cnf").readText()
        content = content.plus("\n[SAN]\nsubjectAltName=DNS:localhost,DNS:$remoteDomain,IP:127.0.0.1,IP:$localIP")
        home.tls.file("openssl.cnf").writeText(content)
        "./gen_user.sh $tlsPass $localIP $remoteDomain $rootPass $account".runCommandInside(home.tls)
        home.tls.file("openssl.cnf").delete()
        if (logContainsError()) {
            home.tls.server.deleteRecursively()
            home.tls.client.deleteRecursively()
            handleFatalError(CliError.TLS_CONFIG_FAILED)
        }

        "Building AWS lambda...".println()
        val userDir = home.lambda.tls.users.dir(account)
        home.tls.client.file("client-cert.pem").override(userDir.file("client-cert.pem"))
        home.tls.client.file("client-key.pem").override(userDir.file("client-key.pem"))
        userDir.file("settings.json").writeText(gson.toJson(settings))

        "zip -r lambda.zip index.js tls".runCommandInside(home.lambda)
        home.lambda.file("lambda.zip").override(File("lambda.zip"))


        //TODO: apply settings to executor and skill

        userTemp.mkdir()

        "Building executor...".println()
        home.tls.client.file("client-keystore.jks").override(home.executor.srcMainRes.tls.file("client-keystore.jks"))
        home.tls.client.file("client-truststore.jks").override(home.executor.srcMainRes.tls.file("client-truststore.jks"))
        home.executor.srcMainRes.file("settings.json").writeText(gson.toJson(settings))
        "gradle fatJar".runCommandInside(home.executor, false)
        "cp ${home.executor.buildLibs}/executor* ${userTemp}".runCommand() //wildcard copy

        "Building skill...".println()
        home.tls.server.file("server-keystore.jks").override(home.skill.srcMainRes.tls.file("server-keystore.jks"))
        home.tls.server.file("server-truststore.jks").override(home.skill.srcMainRes.tls.file("server-truststore.jks"))
        home.skill.srcMainRes.file("settings.json").writeText(gson.toJson(settings))
        "gradle build".runCommandInside(home.skill, false)
        "cp ${home.skill.buildLibs}/skill* ${userTemp}".runCommand() //wildcard copy

        "Building manager...".println()
        home.tls.client.file("client-keystore.jks").override(home.manager.srcMainRes.tls.file("client-keystore.jks"))
        home.tls.client.file("client-truststore.jks").override(home.manager.srcMainRes.tls.file("client-truststore.jks"))
        home.manager.srcMainRes.file("settings.json").writeText(gson.toJson(settings))
        "gradle fatJar".runCommandInside(home.manager, false)
        "cp ${home.manager.buildLibs}/manager* ${userTemp}".runCommand()


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
                users.forEach { account ->
                    val settings = home.lambda.tls.users.dir(account).file("settings.json").asClass(Settings::class.java)
                    "* Account: %-20s Remote: %-20s Local: %-20s".format(settings.user,
                            "${settings.remoteDomain}:${settings.remotePort}",
                            "${settings.localIp}:${settings.remotePort}")
                            .println()
                }
            } else {
                "No users are registered.".println()
            }
        } else {
            "No users are registered.".println()
        }
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
        val file = home.lambda.tls.users.dir(account)
        if (file.exists()) {
            file.deleteRecursively()
            val zip = root.dir(account)
            if (zip.exists()) {
                zip.delete()
            }
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