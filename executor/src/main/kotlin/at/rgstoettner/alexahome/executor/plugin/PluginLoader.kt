package at.rgstoettner.alexahome.executor.plugin

import at.rgstoettner.alexahome.executor.plugin.v2.V2Loader
import at.rgstoettner.alexahome.executor.plugin.v2.V2Plugin
import java.io.File

class PluginLoader(val path: String?, loadDefault: Boolean = true) {
    private val pluginDir: File
    var v2Plugins = listOf<V2Plugin>()

    init {
        if (path != null) {
            pluginDir = File(path)
            if (!pluginDir.exists()) {
                println("plugin folder not found")
            }
        } else {
            pluginDir = File("plugins")
            pluginDir.mkdirs()
        }

//        if (loadDefault) {
//            addDefaultPlugins("AlexaHomePlugins.jar")
//        }
    }

    private fun addDefaultPlugins(vararg names: String) {
        names.forEach { jarFile ->
            val stream = this::class.java.classLoader
                    .getResourceAsStream("plugins/$jarFile")
            if (stream != null) {
                stream.use { input ->
                    File(pluginDir, jarFile).outputStream().use { input.copyTo(it) }
                }
            } else {
                println("Default plugin not found: $jarFile")
            }
        }
    }

    fun loadPlugins() {
        v2Plugins = V2Loader(pluginDir).load()

        if (v2Plugins.isEmpty()) {
            println("No plugins found inside ${pluginDir.absolutePath}")
        }
    }


}