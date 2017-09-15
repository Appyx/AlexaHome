package at.rgstoettner.alexahome.skill

import at.rgstoettner.alexahome.skill.endpoints.websocket.ExecutorController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

fun main(args: Array<String>) {
    SpringApplication.run(SkillApplication::class.java, *args)
}

@SpringBootApplication
@PropertySource("classpath:tls/tls.properties")
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


