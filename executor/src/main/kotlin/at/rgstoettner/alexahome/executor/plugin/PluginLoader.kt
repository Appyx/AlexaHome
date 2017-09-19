package at.rgstoettner.alexahome.executor.plugin

import java.io.File
import java.net.URLClassLoader
import javax.tools.ToolProvider

class PluginLoader(path: String?) {
    private val javaFiles: File
    private val classFiles: File
    private val objects = mutableMapOf<String, Class<*>>()

    init {
        if (path != null) {
            javaFiles = File(path)
        } else {
            javaFiles = File("plugins")
        }
        classFiles = File(javaFiles, "classes")
        javaFiles.mkdirs()
        classFiles.mkdirs()

        addDefaultPlugins()

        javaFiles.walkTopDown()
                .maxDepth(1)
                .filter { !it.name.startsWith(".") }
                .filter { it.name != javaFiles.name }
                .filter { it.name != "classes" }
                .forEach {
                    val compiler = ToolProvider.getSystemJavaCompiler()
                    compiler.run(null, null, null, "-d", classFiles.path, it.path)
                }

        classFiles.walkTopDown()
                .maxDepth(1)
                .filter { it.name.endsWith(".class") }
                .filter { !it.name.startsWith(".") }
                .filter { it.name != classFiles.name }
                .forEach {
                    val classLoader = URLClassLoader.newInstance(arrayOf(classFiles.toURI().toURL()))
                    val name = it.name.split(".class").getOrNull(0)
                    name?.let {
                        println(name)
                        val cls = Class.forName(name, true, classLoader)

                        objects.put(name, cls)
                    }
                }
    }


    private fun addDefaultPlugins() {
        val content = this::class.java.classLoader
                .getResourceAsStream("plugins/Command.java")
                .bufferedReader().readText()
        File(javaFiles, "Command.java").writeText(content)
    }


    fun runPlugin(name: String, argument: String?): String? {
        val clazz = objects.get(name)
        var result: String? = null
        if (clazz != null) {
            val obj = clazz.newInstance()
            val resultObj = clazz.getMethod("run", String::class.java).invoke(obj, argument)
            if (resultObj != null) {
                result = resultObj as String
            } else {
                result = null
            }
        } else {
            println("Plugin not found")
        }
        return result
    }
}