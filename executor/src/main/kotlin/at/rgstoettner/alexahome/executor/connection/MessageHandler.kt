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
            println(message.toString())
            var response = "{}"
            if (message.get("type").asString == "DISCOVER") {
                loader.loadPlugins()
                val devices = loader.v2Plugins.map { it.amazonDevice }
                val responseObject = JsonObject()
                responseObject.addProperty("type", "DISCOVER")
                responseObject.addProperty("name", "DiscoverAppliancesResponse")
                responseObject.addProperty("deviceReached", true)
                responseObject.add("payload", gson.toJsonTree(devices))
                response = gson.toJson(responseObject)
            } else {
                val responseObject = JsonObject()
                responseObject.addProperty("deviceReached", true)
                responseObject.addProperty("error", "UnsupportedOperationError")
                response = gson.toJson(responseObject)
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