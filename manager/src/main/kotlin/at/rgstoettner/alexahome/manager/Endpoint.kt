package at.rgstoettner.alexahome.manager

import at.rgstoettner.alexahome.manager.data.Configuration

class Endpoint private constructor() {

    private object Holder {
        val INSTANCE = Endpoint()
    }

    companion object {
        val instance: Endpoint by lazy { Holder.INSTANCE }
    }

    val host: String
    val port: Int

    init {
        host = ""
        port = 443
    }

    fun configure(settings: Settings): Endpoint {

        return instance
    }


    fun writeConfig(config: Configuration) {

    }

    fun readConfig(): Configuration {
        return Configuration()
    }

}