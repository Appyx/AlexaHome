package at.rgstoettner.alexahome.skill

import at.rgstoettner.alexahome.skill.endpoints.websocket.ExecutorController
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import java.util.*

fun main(args: Array<String>) {
    val app = SpringApplication(SkillApplication::class.java)
    val mapper = ObjectMapper()
    val node = mapper.readValue(ClassPathResource("tls/settings.json").inputStream.bufferedReader(Charsets.UTF_8), JsonNode::class.java)
    val props = Properties()
    props.put("server.ssl.trust-store-password", node.get("password").textValue())
    props.put("server.ssl.key-store-password", node.get("password").textValue())
    props.put("server.ssl.key-password", node.get("password").textValue())
    props.put("server.port", node.get("localPort").intValue())
    app.setDefaultProperties(props)
    app.run(*args)
}

@SpringBootApplication
class SkillApplication : ApplicationContextAware {
    companion object {
        lateinit var context: ApplicationContext
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }
}


@Configuration
@EnableWebSocket
class WebsocketConfig : WebSocketConfigurer {

    @Autowired
    lateinit var executorController: ExecutorController

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(executorController, "/wss");
    }
}


