package at.rgstoettner.alexahome.manager.controller

import at.rgstoettner.alexahome.manager.*
import java.io.File
import java.util.*

class UserController : CommandController() {


    /**
     * Adds a new user:
     *
     * 1. create certificates for server and client and sign them with CA
     * 2. copy certificates/key from client folder to lambda folder and zip a new package
     * 3. copy truststore/keystore to executor and build it
     * 4. copy truststore/keystore to skill and build it
     * 5. create zip package of executor and skill
     */
    fun add() {
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
        val localIP = local.split(":").getOrElse(0) { handleFatalError(CliError.UNKNOWN_ARGUMENTS);"" }
        val localPort = local.split(":").getOrElse(0) { handleFatalError(CliError.UNKNOWN_ARGUMENTS);"" }
        "Enter the remote domain and the remote port of the server: [yourdomain.com:YYYY]".println()
        val remote = requiredReadLine()
        val remoteDomain = remote.split(":").getOrElse(1) { handleFatalError(CliError.UNKNOWN_ARGUMENTS); "" }
        val remotePort = remote.split(":").getOrElse(1) { handleFatalError(CliError.UNKNOWN_ARGUMENTS); "" }

        "Creating tls configuration...".println()
        "chmod +x ${home.tls.file("gen_user.sh")}".runCommand()
        "./gen_user.sh $tlsPass $localIP $remoteDomain $rootPass $account".runCommandInside(home.tls)
        if (logContainsError()) {
            home.tls.server.deleteRecursively()
            home.tls.client.deleteRecursively()
            handleFatalError(CliError.TLS_CONFIG_FAILED)
        }

        "Building AWS lambda...".println()
        home.tls.client.file("client-cert.pem").override(File("${home.lambda.tls.users}/$account/client-cert.pem"))
        home.tls.client.file("client-key.pem").override(File("${home.lambda.tls.users}/$account/client-key.pem"))
        File("${home.lambda.tls.users}/$account/pass.txt").writeText(tlsPass, Charsets.UTF_8)
        File("${home.lambda.tls.users}/$account/host.txt").writeText(remoteDomain, Charsets.UTF_8)
        File("${home.lambda.tls.users}/$account/port.txt").writeText(remotePort, Charsets.UTF_8)
        "zip -r lambda.zip index.js tls".runCommandInside(home.lambda)
        home.lambda.file("lambda.zip").override(File("lambda.zip"))

        userTemp.mkdir()

        "Building executor...".println()
        home.tls.client.file("client-keystore.jks").override(home.executor.srcMainRes.tls.file("client-keystore.jks"))
        home.tls.client.file("client-truststore.jks").override(home.executor.srcMainRes.tls.file("client-truststore.jks"))
        home.executor.srcMainRes.tls.file("pass.txt").writeText(tlsPass, Charsets.UTF_8)
        home.executor.srcMainRes.tls.file("host.txt").writeText(localIP, Charsets.UTF_8)
        home.executor.srcMainRes.tls.file("port.txt").writeText(localPort, Charsets.UTF_8)
        "gradle build".runCommandInside(home.executor)
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
        "gradle build".runCommandInside(home.skill)
        "cp ${home.skill.buildLibs}/skill* ${userTemp}".runCommand() //wildcard copy

        home.tls.server.deleteRecursively()
        home.tls.client.deleteRecursively()

        "zip $account.zip *".runCommandInside(userTemp)
        userTemp.file("$account.zip").override(root)
        userTemp.deleteRecursively()

        if (logContainsError()) {
            removeUser(account)
            handleFatalError(CliError.BUILD_FAILED)
        }
        "Successfully created user!".println()
        "Created dir: lambda.zip".println()
        "Created directory: $account ".println()
    }

    fun list() {
        handleFatalError(CliError.NOT_IMPLEMENTED)
    }

    fun remove() {
        "Enter the account: [bob@example.com]".println()
        val account = requiredReadLine()
        if (!isInstalled) handleFatalError(CliError.NOT_INSTALLED)
        removeUser(account)
    }

    /**
     * Removes the user folder from the lambda and creates a new zip
     */
    private fun removeUser(account: String) {
        val file = File("${home.lambda.tls.users}/$account")
        if (file.exists()) {
            file.deleteRecursively()
            "Successfully removed user $account!".println()
            if (home.lambda.tls.users.walkTopDown().count() != 0) { //if directory is empty
                "zip -r lambda.zip index.js tls".runCommandInside(home.lambda)
                home.lambda.file("lambda.zip").override(File("lambda.zip"))
                "Created dir: lambda.zip".println()
            }
        } else {
            handleFatalError(CliError.UNKNOWN_USER)
        }
    }


}