package at.rgstoettner.alexahome.manager

import com.google.gson.Gson
import java.io.File

class Settings {

    var user: String? = null
    var role: String? = null
    var remoteDomain: String? = null
    var remotePort: Int? = null
    var localIp: String? = null
    var localPort: String? = null


    companion object {

        private val gson = Gson()

        fun load(): Settings {
            val reader = this::class.java
                    .classLoader
                    .getResourceAsStream("settings.json")
                    ?.bufferedReader()

            if (reader != null) {
                return gson.fromJson(reader, Settings::class.java)
            } else {
                return Settings()
            }
        }

        fun save(settings: Settings) {
            val writer = File("AlexaHome/manager/src/main/resources/settings.json").writer()
            gson.toJson(settings, writer)
        }
    }
}