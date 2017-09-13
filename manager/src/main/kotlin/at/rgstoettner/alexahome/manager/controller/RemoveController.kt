package at.rgstoettner.alexahome.manager.controller

import at.rgstoettner.alexahome.manager.configPath
import at.rgstoettner.alexahome.manager.getConfigFile
import at.rgstoettner.alexahome.manager.safeReadLine

class RemoveController {

    fun removeDevice(id: String) {

    }

    fun removeScene(id: String) {

    }

    fun wipeAll() {
        println("This will permanently remove all data. Are you Sure? [yes/no]")
        val answer = safeReadLine()
        println()
        if (answer == "yes") {
            if (getConfigFile().exists()) {
                getConfigFile().delete()
                println("Completely deleted all data.")
            } else {
                println("There is no data at: ${configPath}")
            }
        } else {
            println("Cancelling...")
        }
    }
}