package at.rgstoettner.alexahome.executor

import java.net.Socket
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

class SecureSocket(password: String) {

    private val context: SSLContext

    init {
        val keyStore = KeyStore.getInstance("jks")
        keyStore.load(this::class.java.classLoader.getResourceAsStream("tls/client-keystore.jks"), password.toCharArray())
        val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
        keyManagerFactory.init(keyStore, password.toCharArray())

        val trustStore = KeyStore.getInstance("jks")
        trustStore.load(this::class.java.classLoader.getResourceAsStream("tls/client-truststore.jks"), password.toCharArray())
        val trustManagerFactory = TrustManagerFactory.getInstance("SunX509")
        trustManagerFactory.init(trustStore)

        context = SSLContext.getInstance("SSL")
        context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), SecureRandom())
    }


    fun getSocket(): Socket {
        return context.socketFactory.createSocket()
    }
}