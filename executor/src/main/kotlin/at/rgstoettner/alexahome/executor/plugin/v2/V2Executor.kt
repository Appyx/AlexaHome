package at.rgstoettner.alexahome.executor.plugin.v2

import at.rgstoettner.alexahome.plugin.v2.*
import com.google.gson.Gson
import com.google.gson.JsonObject

class V2Executor(val plugin: V2Plugin, val type: String) {
    var payload = JsonObject()
    var name: String? = null
    var error: String? = null

    private val gson = Gson()
    private lateinit var methodName: String


    fun execute(name: String, payload: JsonObject): Boolean {
        println("Executing $name for plugin: ${plugin.amazonDevice.friendlyName}")
        createNames(name)

        if (OnOff::class.java.declaredMethods.map { it.name }.contains(methodName)) {
            val response = executeOnOff(plugin.device as OnOff, payload)
            return handleResponse(response)
        }
        if (Lighting::class.java.declaredMethods.map { it.name }.contains(methodName)) {
            val response = executeLighting(plugin.device as Lighting, payload)
            return handleResponse(response)
        }
        if (LockState::class.java.declaredMethods.map { it.name }.contains(methodName)) {
            val response = executeLockState(plugin.device as LockState, payload)
            return handleResponse(response)
        }
        if (Percentage::class.java.declaredMethods.map { it.name }.contains(methodName)) {
            val response = executePercentage(plugin.device as Percentage, payload)
            return handleResponse(response)
        }
        if (Temperature::class.java.declaredMethods.map { it.name }.contains(methodName)) {
            val response = executeTemperature(plugin.device as Temperature, payload)
            return handleResponse(response)
        }
        error = "UnsupportedOperationError"
        return false //execution successful
    }

    private fun handleResponse(response: JsonObject?): Boolean {
        if (response == null) {
            error = "UnsupportedTargetSettingError"
            return false
        } else {
            payload = response
            return true
        }
    }


    private fun createNames(request: String) {
        methodName = request.decapitalize().substringBefore("Request")

        when (type) {
            "QUERY" -> {
                name = request.substringBefore("Request").plus("Response")
            }
            "CONTROL" -> {
                name = request.substringBefore("Request").plus("Confirmation")
            }
        }
    }


    private fun executeLighting(device: Lighting, payload: JsonObject): JsonObject? {
        return null
    }

    private fun executeTemperature(device: Temperature, payload: JsonObject): JsonObject? {
        return null
    }

    private fun executePercentage(device: Percentage, payload: JsonObject): JsonObject? {
        return null
    }

    private fun executeOnOff(device: OnOff, payload: JsonObject): JsonObject? {
        val response = JsonObject()
        when (methodName) {
            "turnOn" -> {
                device.turnOn()
                return response
            }
            "turnOff" -> {
                device.turnOff()
                return response
            }
            else -> return null
        }
    }

    private fun executeLockState(device: LockState, payload: JsonObject): JsonObject? {
        return null
    }


}