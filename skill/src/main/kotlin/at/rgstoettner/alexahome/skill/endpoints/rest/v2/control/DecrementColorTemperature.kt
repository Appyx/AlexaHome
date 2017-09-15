package at.rgstoettner.alexahome.skill.endpoints.rest.v2.control


class DecrementColorTemperature(id: String) : ControlResponse(id) {

    init {


        //ValueOutOfRangeError - value out of range
        //BridgeOfflineError  - Networking issues hub
        //TargetOfflineError  - Networking issues device only
        //NoSuchTargetError - the device was not found in the profile
        //UnsupportedTargetError - not supported anymore
        //UnsupportedOperationError - operation is unsupported
        //UnsupportedTargetSettingError - command not working
        //DriverInternalError - device not responding

        try {
            val result = executor.getValueForCommand(command!!)
            val value = result!!.toInt()

            val achievedState = putObject("achievedState")
            val colorTemperature = achievedState.putObject("colorTemperature")
            colorTemperature.put("value", value)
        } catch (ex: Throwable) {
            this.toError("NoSuchTargetError")
        }
    }

}