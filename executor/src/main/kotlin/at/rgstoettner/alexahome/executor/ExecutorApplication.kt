package at.rgstoettner.alexahome.executor

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.web.socket.client.WebSocketConnectionManager
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


@SpringBootApplication
class ExecutorApplication

fun main(args: Array<String>) {
    SpringApplication.run(ExecutorApplication::class.java, *args)
}

@Component
class Runner : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    lateinit var handler: WebSocketController
    @Value("\${skill.location}") lateinit var host: String
    @Value("\${skill.port}") lateinit var port: String

    override fun run(args: ApplicationArguments) {
        val password = ClassPathResource("tls/pass.txt").inputStream.bufferedReader(Charsets.UTF_8).readText()
        val keyStore = KeyStore.getInstance("jks")
        keyStore.load(ClassPathResource("tls/client-keystore.jks").inputStream, password.toCharArray())
        val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
        keyManagerFactory.init(keyStore, password.toCharArray())

        val trustStore = KeyStore.getInstance("jks")
        trustStore.load(ClassPathResource("tls/client-truststore.jks").inputStream, password.toCharArray())
        val trustManagerFactory = TrustManagerFactory.getInstance("SunX509")
        trustManagerFactory.init(trustStore)

        val context = SSLContext.getInstance("SSL")
        context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), SecureRandom())

        val client = StandardWebSocketClient()
        client.userProperties = mapOf(Pair(org.apache.tomcat.websocket.Constants.SSL_CONTEXT_PROPERTY, context))


        val manager = WebSocketConnectionManager(client, handler, "wss://$host:$port/wss")
        manager.start()

        val lock = Object()
        synchronized(lock) {
            lock.wait()
        }
    }
}


