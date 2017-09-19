package at.rgstoettner.alexahome.skill.endpoints.rest

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.io.File


@RestController
class ManagerController {

    @RequestMapping(value = "/config", method = arrayOf(RequestMethod.GET))
    fun getConfig(): ResponseEntity<String> {
        println("getConfig")
        val file = File("config.json")
        if (file.exists()) {
            return ResponseEntity.status(HttpStatus.OK).body(file.readText())

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }


    @RequestMapping(value = "/config", method = arrayOf(RequestMethod.POST))
    fun writeConfig(@RequestBody json: String): ResponseEntity<String> {
        File("config.json").writeText(json)
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }
}