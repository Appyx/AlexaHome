package at.rgstoettner.alexahome.executor.plugin.v2

import at.rgstoettner.alexahome.plugin.v2.*
import java.io.File
import java.net.URLClassLoader
import java.util.*

class V2Loader(val pluginDir: File) {

    private val plugins = mutableListOf<V2Plugin>()

    fun load(): List<V2Plugin> {
        val jars = pluginDir.walkTopDown()
                .maxDepth(1)
                .filter { it.name.endsWith(".jar") }
                .map { it.toURI().toURL() }
                .toList()
        jars.forEach {
            println("Adding: ${it.path}")
        }
        val classLoader = URLClassLoader.newInstance(jars.toTypedArray())
        val deviceLoader = ServiceLoader.load(V2Device::class.java, classLoader)
        val providerLoader = ServiceLoader.load(V2DeviceProvider::class.java, classLoader)

        val devices = deviceLoader.toMutableList()
        devices.addAll(providerLoader.toList().flatMap { it.devices })

        devices.forEach { device ->
            val plugin = configurePlugin(device)
            if (plugin != null) {
                plugins.add(plugin)
                println("Loaded plugin: ${device.name}")
            } else {
                println("Plugin is missing required information")
            }
        }

        return plugins
    }

    private fun configurePlugin(device: V2Device): V2Plugin? {
        if (device.name == null
                || device.description == null
                || device.manufacturer == null
                || device.model == null
                || device.softwareVersion == null) {
            return null
        }
        val plugin = V2Plugin(device)
        plugin.amazonDevice.applianceId = device.name.hashCode().toString()
        plugin.amazonDevice.friendlyName = device.name
        plugin.amazonDevice.friendlyDescription = device.description
        plugin.amazonDevice.manufacturerName = device.manufacturer
        plugin.amazonDevice.modelName = device.model
        plugin.amazonDevice.version = device.softwareVersion
        val type = if (device.isScene) "ACTIVITY_TRIGGER" else "SMARTPLUG"
        plugin.amazonDevice.applianceTypes = arrayListOf(type)
        plugin.amazonDevice.actions = getCapabilities(device)
        return plugin
    }


    private fun getCapabilities(deviceV2: V2Device): List<String> {
        val capabilities = mutableListOf<String>()
        val clazz = deviceV2::class.java
        if (clazz.interfaces.contains(Lighting::class.java)) {
            capabilities.addAll(Lighting::class.java.declaredMethods.map { it.name }.toList())
        }
        if (clazz.interfaces.contains(LockState::class.java)) {
            capabilities.addAll(LockState::class.java.declaredMethods.map { it.name }.toList())
        }
        if (clazz.interfaces.contains(OnOff::class.java)) {
            capabilities.addAll(OnOff::class.java.declaredMethods.map { it.name }.toList())
        }
        if (clazz.interfaces.contains(Percentage::class.java)) {
            capabilities.addAll(Percentage::class.java.declaredMethods.map { it.name }.toList())
        }
        if (clazz.interfaces.contains(Temperature::class.java)) {
            capabilities.addAll(Temperature::class.java.declaredMethods.map { it.name }.toList())
        }
        return capabilities
    }
}
