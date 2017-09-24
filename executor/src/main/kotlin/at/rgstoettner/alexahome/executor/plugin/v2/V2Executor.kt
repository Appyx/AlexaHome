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
        val response = JsonObject()
        when (methodName) {
            "setColor" -> {
                val wrapper = payload.get("color") as JsonObject
                val value = gson.fromJson(wrapper, Lighting.Color::class.java)

                val result = device.setColor(value)
                if (device.isError || result == null) return null

                val achievedState = JsonObject()
                achievedState.add("color", gson.toJsonTree(result))
                response.add("achievedState", achievedState)
                return response
            }
            "setColorTemperature" -> {
                val state = payload.get("colorTemperature") as JsonObject
                val value = state.get("value").asInt

                val result = device.setColorTemperature(value)
                if (device.isError) return null

                val achievedState = JsonObject()
                val colorTemperature = JsonObject()
                colorTemperature.addProperty("value", result)
                achievedState.add("colorTemperature", colorTemperature)
                response.add("achievedState", achievedState)
                return response
            }
            "decrementColorTemperature" -> {
                val result = device.decrementColorTemperature()
                if (device.isError) return null

                val achievedState = JsonObject()
                val colorTemperature = JsonObject()
                colorTemperature.addProperty("value", result)
                achievedState.add("colorTemperature", colorTemperature)
                response.add("achievedState", achievedState)
                return response
            }
            "incrementColorTemperature" -> {
                val result = device.incrementColorTemperature()
                if (device.isError) return null

                val achievedState = JsonObject()
                val colorTemperature = JsonObject()
                colorTemperature.addProperty("value", result)
                achievedState.add("colorTemperature", colorTemperature)
                response.add("achievedState", achievedState)
                return response
            }
            else -> return null
        }
    }

    private fun executeTemperature(device: Temperature, payload: JsonObject): JsonObject? {
        val response = JsonObject()
        when (methodName) {
            "setTargetTemperature" -> {
                val state = payload.get("targetTemperature") as JsonObject
                val value = state.get("value").asFloat

                val result = device.setTargetTemperature(value)
                if (device.isError || result == null) return null

                val targetTemperature = JsonObject()
                targetTemperature.addProperty("value", result.actual.temp)
                val temperatureMode = JsonObject()
                temperatureMode.addProperty("value", result.actual.mode.name)
                response.add("targetTemperature", targetTemperature)
                response.add("temperatureMode", temperatureMode)
                val previousState = JsonObject()
                val prevTargetTemperature = JsonObject()
                prevTargetTemperature.addProperty("value", result.previous.temp)
                val prevTemperatureMode = JsonObject()
                prevTemperatureMode.addProperty("value", result.previous.mode.name)
                previousState.add("targetTemperature", prevTargetTemperature)
                previousState.add("mode", prevTemperatureMode)
                response.add("previousState", previousState)
                return response
            }
            "incrementTargetTemperature" -> {
                val state = payload.get("deltaTemperature") as JsonObject
                val value = state.get("value").asFloat

                val result = device.incrementTargetTemperature(value)
                if (device.isError || result == null) return null

                val targetTemperature = JsonObject()
                targetTemperature.addProperty("value", result.actual.temp)
                val temperatureMode = JsonObject()
                temperatureMode.addProperty("value", result.actual.mode.name)
                response.add("targetTemperature", targetTemperature)
                response.add("temperatureMode", temperatureMode)
                val previousState = JsonObject()
                val prevTargetTemperature = JsonObject()
                prevTargetTemperature.addProperty("value", result.previous.temp)
                val prevTemperatureMode = JsonObject()
                prevTemperatureMode.addProperty("value", result.previous.mode.name)
                previousState.add("targetTemperature", prevTargetTemperature)
                previousState.add("mode", prevTemperatureMode)
                response.add("previousState", previousState)
                return response
            }
            "decrementTargetTemperature" -> {
                val state = payload.get("deltaTemperature") as JsonObject
                val value = state.get("value").asFloat

                val result = device.decrementTargetTemperature(value)
                if (device.isError || result == null) return null

                val targetTemperature = JsonObject()
                targetTemperature.addProperty("value", result.actual.temp)
                val temperatureMode = JsonObject()
                temperatureMode.addProperty("value", result.actual.mode.name)
                response.add("targetTemperature", targetTemperature)
                response.add("temperatureMode", temperatureMode)
                val previousState = JsonObject()
                val prevTargetTemperature = JsonObject()
                prevTargetTemperature.addProperty("value", result.previous.temp)
                val prevTemperatureMode = JsonObject()
                prevTemperatureMode.addProperty("value", result.previous.mode.name)
                previousState.add("targetTemperature", prevTargetTemperature)
                previousState.add("mode", prevTemperatureMode)
                response.add("previousState", previousState)
                return response
            }
            else -> return null
        }
    }

    private fun executePercentage(device: Percentage, payload: JsonObject): JsonObject? {
        val response = JsonObject()
        when (methodName) {
            "decrementPercentage" -> {
                val state = payload.get("deltaPercentage") as JsonObject
                val value = state.get("value").asDouble
                device.decrementPercentage(value)
                if (device.isError) return null
                return response
            }
            "incrementPercentage" -> {
                val state = payload.get("deltaPercentage") as JsonObject
                val value = state.get("value").asDouble
                device.incrementPercentage(value)
                if (device.isError) return null
                return response
            }
            "setPercentage" -> {
                val state = payload.get("percentageState") as JsonObject
                val value = state.get("value").asDouble
                device.setPercentage(value)
                if (device.isError) return null
                return response
            }
            else -> return null
        }
    }

    private fun executeOnOff(device: OnOff, payload: JsonObject): JsonObject? {
        val response = JsonObject()
        when (methodName) {
            "turnOn" -> {
                device.turnOn()
                if (device.isError) return null
                return response
            }
            "turnOff" -> {
                device.turnOff()
                if (device.isError) return null
                return response
            }
            else -> return null
        }
    }

    private fun executeLockState(device: LockState, payload: JsonObject): JsonObject? {
        val response = JsonObject()
        when (methodName) {
            "getLockState" -> {
                val result = device.getLockState()
                if (device.isError || result == null) return null
                response.addProperty("lockState", result.name)
                return response
            }
            "setLockState" -> {
                val value = payload.get("lockState").asString
                val result = device.setLockState(LockState.Value.valueOf(value))
                if (device.isError || result == null) return null
                response.addProperty("lockState", result.name)
                return response
            }
            else -> return null
        }
    }


}