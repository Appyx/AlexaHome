package at.rgstoettner.alexahome.skill

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler


@SpringBootApplication
@PropertySource("classpath:tls/tls.properties")
class SkillApplication {
    @Bean
    fun propertySourcesPlaceholderConfigurer(): PropertySourcesPlaceholderConfigurer {
        return PropertySourcesPlaceholderConfigurer()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(SkillApplication::class.java, *args)
}


@RestController
class HttpsController {
    @RequestMapping(value = "/", method = arrayOf(RequestMethod.POST))
    fun user(@RequestBody test: String): String {
        println(test)
        return "hello"
    }
}


@Configuration
@EnableWebSocket
class WebsocketConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(myMessageHandler(), "/wss");
    }

    @Bean
    fun myMessageHandler(): WebSocketHandler {
        return MyMessageHandler()
    }


}


@Configuration
class MyMessageHandler : TextWebSocketHandler() {

    override fun afterConnectionClosed(session: WebSocketSession?, status: CloseStatus?) {
        // The WebSocket has been closed
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        session.sendMessage(TextMessage("hello client"))
    }

    override fun handleTextMessage(session: WebSocketSession, textMessage: TextMessage) {
        // A message has been received
        println("Message received: " + textMessage.payload)
    }
}