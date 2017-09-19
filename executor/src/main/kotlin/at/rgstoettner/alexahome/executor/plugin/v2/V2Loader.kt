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
        val loader = ServiceLoader.load(DeviceV2::class.java, classLoader)
        loader.forEach { device ->
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

    private fun configurePlugin(device: DeviceV2): V2Plugin? {
        if (device.name == null
                || device.description == null
                || device.manufacturer == null
                || device.model == null
                || device.softwareVersion == null
                || device.type == null) {
            return null
        }
        val plugin = V2Plugin()
        plugin.amazonDevice.applianceId = device.name.hashCode().toString()
        plugin.amazonDevice.friendlyName = device.name
        plugin.amazonDevice.friendlyDescription = device.description
        plugin.amazonDevice.manufacturerName = device.manufacturer
        plugin.amazonDevice.modelName = device.model
        plugin.amazonDevice.version = device.softwareVersion
        plugin.amazonDevice.applianceTypes = arrayListOf(device.type)
        plugin.amazonDevice.actions = getCapabilities(device)
        return plugin
    }


    private fun getCapabilities(deviceV2: DeviceV2): List<String> {
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
