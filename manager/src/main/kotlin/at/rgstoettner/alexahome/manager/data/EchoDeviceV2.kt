package at.rgstoettner.alexahome.manager.data

open class EchoDeviceV2(var friendlyName: String, var applianceTypes: List<String>, var actions: List<String>) {

    var applianceId = System.currentTimeMillis().toString()
    var friendlyDescription = "no description available"
    var isReachable = true
    var manufacturerName = "Simulated Manufacturer"
    var modelName = "Simulated Model"
    var version = "1.0"
    var additionalApplianceDetails = Any()

}