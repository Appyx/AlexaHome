package at.rgstoettner.alexahome.manager.data

class ExtendedDevice(friendlyName: String, type: String, actions: List<String>) : EchoDeviceV2(friendlyName, listOf(type), actions) {

    var commands = mutableMapOf<String, String>()
        set(value) {
            field = mutableMapOf()
            value.forEach { k, v ->
                val key = k.plus("Request").capitalize()
                field.put(key, v)
            }

        }


}