package at.rgstoettner.alexahome.skill

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.PropertySource
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController


@SpringBootApplication
@PropertySource(value = "file:tls/pass.properties")
class SkillApplication

fun main(args: Array<String>) {
    SpringApplication.run(SkillApplication::class.java, *args)
}


@RestController
class UserController {
    @RequestMapping(value = "/", method = arrayOf(RequestMethod.POST))
    fun user(@RequestBody test: String): String {
        println(test)
        return "hello"
    }
}