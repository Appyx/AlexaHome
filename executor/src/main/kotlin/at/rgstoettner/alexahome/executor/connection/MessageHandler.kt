package at.rgstoettner.alexahome.executor.connection

import at.rgstoettner.alexahome.executor.plugin.PluginLoader
import com.google.gson.Gson
import com.google.gson.JsonObject

class MessageHandler(val socket: ReconnectingSocket, val loader: PluginLoader) {

    private var state = States.UNDEFINED
    private val gson = Gson()

    fun connect() {
        socket.connect(onReady = {

        }, onMessage = { request ->
            val message = gson.fromJson(request, JsonObject::class.java)
            var response = "{}"
            if (message.get("type").asString == "discover") {
                loader.loadPlugins()
                val devices = loader.v2Plugins.map { it.amazonDevice }
                response = gson.toJson(devices)
            }
            response
        })
    }


    private enum class States {
        UNDEFINED,
        DISCOVERY,
        CONTROL
    }
}