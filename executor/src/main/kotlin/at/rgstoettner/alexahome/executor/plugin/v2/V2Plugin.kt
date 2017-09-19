package at.rgstoettner.alexahome.executor.plugin.v2

class V2Plugin {

    val amazonDevice = AmazonDevice()

    class AmazonDevice() {
        var applianceId: String? = null
        var friendlyName: String? = null
        var friendlyDescription: String? = null
        var isReachable = true
        var manufacturerName: String? = null
        var modelName: String? = null
        var version: String? = null
        var additionalApplianceDetails = Any()
        var applianceTypes = mutableListOf<String>()
        var actions = listOf<String>()
    }
}