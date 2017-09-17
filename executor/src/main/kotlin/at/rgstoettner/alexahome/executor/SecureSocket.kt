package at.rgstoettner.alexahome.executor

import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.*


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

        context = SSLContext.getInstance("TLS")
        context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), SecureRandom())
    }


    fun getSocket(): SSLSocket {
        val sock = context.socketFactory.createSocket() as SSLSocket
        val sslParams = SSLParameters()
        sslParams.endpointIdentificationAlgorithm = "HTTPS"
        sock.sslParameters = sslParams
        return sock
    }
}