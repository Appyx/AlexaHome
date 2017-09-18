package at.rgstoettner.alexahome.manager

import com.google.gson.Gson
import java.io.File

class Settings {

    var user: String? = null
    var role: String? = null
    var remoteDomain: String? = null
    var remotePort: Int? = null
    var localIp: String? = null
    var localPort: Int? = null
    var password: String? = null


    companion object {

        private val gson = Gson()

        fun load(): Settings? {
            val reader = this::class.java
                    .classLoader
                    .getResourceAsStream("settings.json")
                    ?.bufferedReader()

            if (reader != null) {
                return gson.fromJson(reader, Settings::class.java)
            } else {
                return null
            }
        }
    }


}