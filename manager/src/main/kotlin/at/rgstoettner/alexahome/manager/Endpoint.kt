package at.rgstoettner.alexahome.manager


import at.rgstoettner.alexahome.manager.data.Configuration

class Endpoint private constructor() {

    private object Holder {
        val INSTANCE = Endpoint()
    }

    companion object {
        val instance: Endpoint by lazy { Holder.INSTANCE }
    }

    var host: String
    var port: Int

    init {
        host = ""
        port = 443
    }

    fun configure(settings: Settings): Endpoint {
        host=settings.remoteDomain!!
        port=settings.remotePort!!
        return instance
    }


    fun writeConfig(config: Configuration) {

    }

    fun readConfig(): Configuration {
        return Configuration()
    }

}