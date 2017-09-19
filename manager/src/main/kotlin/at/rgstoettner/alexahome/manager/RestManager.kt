package at.rgstoettner.alexahome.manager


import at.rgstoettner.alexahome.manager.data.Configuration
import com.google.gson.Gson
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


class RestManager {
    var host: String? = null
    var port: Int? = null
    private lateinit var config: Configuration
    private lateinit var client: HttpClient
    private val gson = Gson()

    fun connect(settings: Settings, local: Boolean) {
        val timeout = 2000
        val requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .build()
        client = HttpClientBuilder.create()
                .setSSLContext(getSSLContext(settings.password!!))
                .setDefaultRequestConfig(requestConfig)
                .build()
        if (local) {
            host = settings.localIp
            port = settings.localPort
        } else {
            host = settings.remoteDomain
            port = settings.remotePort
        }
        val httpget = HttpGet("https://$host:${port}/config")
        val response = client.execute(httpget)
        when (response.statusLine.statusCode) {
            HttpStatus.SC_NOT_FOUND -> config = Configuration()
            HttpStatus.SC_OK -> config = gson.fromJson(response.entity.content.bufferedReader(), Configuration::class.java)
        }
    }


    private fun getSSLContext(password: String): SSLContext {
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
        val httpPost = HttpPost("https://$host:${port}/config")
        val entity = StringEntity(gson.toJson(config))
        httpPost.setEntity(entity)
        httpPost.setHeader("Content-type", "application/json")
        val response = client.execute(httpPost)
        when (response.statusLine.statusCode) {
            HttpStatus.SC_OK -> return
            HttpStatus.SC_BAD_REQUEST -> handleFatalError(CliError.CONFIGURATION_FAILED)
        }
    }

    fun readConfig(): Configuration {
        return config
    }

}