package at.rgstoettner.alexahome.executor.connection

import at.rgstoettner.alexahome.executor.plugin.PluginLoader
import at.rgstoettner.alexahome.executor.plugin.v2.V2Executor
import com.google.gson.Gson
import com.google.gson.JsonObject

class MessageHandler(val socket: ReconnectingSocket, val loader: PluginLoader) {

    private val gson = Gson()

    fun handle() {
        socket.connect(onReady = {
            loader.loadPlugins()
        }, onMessage = { request ->
            val message = gson.fromJson(request, JsonObject::class.java)
            if (message.get("type").asString == Type.DISCOVER.toString()) {
                return@connect handleDiscovery()
            }
            if (message.get("type").asString == Type.CONTROL.toString()) {
                return@connect handleType(Type.CONTROL,message.get("name").asString, message.get("payload") as JsonObject)
            }
            if (message.get("type").asString == Type.QUERY.toString()) {
                return@connect handleType(Type.QUERY,message.get("name").asString, message.get("payload") as JsonObject)
            }
            return@connect "{}"
        })
    }

    private fun handleDiscovery(): String {
        loader.loadPlugins()
        val devices = loader.v2Plugins.map { it.amazonDevice }
        val responseObject = JsonObject()
        responseObject.addProperty("type", "DISCOVER")
        responseObject.addProperty("name", "DiscoverAppliancesResponse")
        responseObject.add("payload", gson.toJsonTree(devices))
        return gson.toJson(responseObject)
    }


    private fun handleType(type:Type,name: String, payload: JsonObject): String {
        val appliance = payload.get("appliance") as JsonObject
        val id = appliance.get("applianceId").asString

        val responseObject = JsonObject()
        responseObject.addProperty("type", type.toString())
        val v2 = loader.v2Plugins.find { it.amazonDevice.applianceId == id }
        if (v2 != null) {
            //plugin found
            val exec = V2Executor(v2, type.toString())
            val success = exec.execute(name, payload)
            responseObject.addProperty("executed", "true")
            if (success) {
                //plugin executed successfully
                responseObject.addProperty("name", exec.name)
                responseObject.add("payload", exec.payload)
            } else {
                //plugin execution failed
                responseObject.addProperty("error", exec.error)
            }
            return gson.toJson(responseObject)
        }
        //plugin not found
        responseObject.addProperty("executed", "false")
        return gson.toJson(responseObject)
    }

    enum class Type {
        CONTROL,
        QUERY,
        DISCOVER
    }
}