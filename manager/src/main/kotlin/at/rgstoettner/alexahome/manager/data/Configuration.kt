package at.rgstoettner.alexahome.manager.data

class Configuration {
    private var ids: ArrayList<String> = arrayListOf()
    private var devices: MutableMap<String, AlexaHomeDevice> = mutableMapOf()


    fun addDevice(device: AlexaHomeDevice) {
        devices.put(device.id, device)
        ids.add(device.id)
    }

}