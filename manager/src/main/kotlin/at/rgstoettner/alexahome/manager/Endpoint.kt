package at.rgstoettner.alexahome.manager


import at.rgstoettner.alexahome.manager.data.Configuration
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


class Endpoint private constructor() {

    private object Holder {
        val INSTANCE = Endpoint()
    }

    companion object {
        val instance: Endpoint by lazy { Holder.INSTANCE }
    }

    var host: String
    var port: Int

    init {
        host = ""
        port = 443
    }

    fun configure(settings: Settings): Endpoint {

        host = settings.remoteDomain!!
        port = settings.remotePort!!

        val client = HttpClientBuilder.create()
                .setSSLContext(getSSLContext(settings.password!!))
                .build()

        val httpget = HttpGet("https://localhost:${settings.localPort}/test")
        val response = client.execute(httpget)

        println(response.entity.content.bufferedReader().readText())

        return this
    }

    fun getSSLContext(password: String): SSLContext {
        val keyStore = KeyStore.getInstance("jks")
        keyStore.load(this::class.java.classLoader.getResourceAsStream("tls/client-keystore.jks"), password.toCharArray())
        val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
        keyManagerFactory.init(keyStore, password.toCharArray())

        val trustStore = KeyStore.getInstance("jks")
        trustStore.load(this::class.java.classLoader.getResourceAsStream("tls/client-truststore.jks"), password.toCharArray())
        val trustManagerFactory = TrustManagerFactory.getInstance("SunX509")
        trustManagerFactory.init(trustStore)

        val context = SSLContext.getInstance("TLS")
        context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), SecureRandom())
        return context
    }


    fun writeConfig(config: Configuration) {

    }

    fun readConfig(): Configuration {
        return Configuration()
    }

}