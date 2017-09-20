package at.rgstoettner.alexahome.plugin.v2;

public interface Temperature extends DeviceV2 {

    /**
     * Example Utterances: <br>
     * “Alexa, set the room name to number degrees”
     * <br>
     * “Alexa, stelle Raumname auf Anzahl Grad”
     * <br>
     * <p>
     * Example Alexa Response: <br>
     * The room name heat is set to number degrees
     *
     * @param value Specifies the target temperature, in degrees Celsius, for the device specified by applianceId. Contains a single property, value, which specifies a number.
     * @return A object that indicates the previous and actual mode set by the device.
     * Contains a mode property  set to one of the following strings: AUTO, COOL, HEAT.
     * Contains a temp property in degrees Celsius.
     */
    StateInfo setTargetTemperature(float value);


    /**
     * Example Utterances:<br>
     * “Alexa, increase the device name by number degrees”<br>
     * “Alexa, erhöhe Gerätename um Anzahl Grad”<br>
     * <p>
     * Example Alexa Response: “OK”
     *
     * @return A object that indicates the previous and actual mode set by the device.
     * Contains a mode property  set to one of the following strings: AUTO, COOL, HEAT.
     * Contains a temp property in degrees Celsius.
     */
    StateInfo incrementTargetTemperature(float delta);


    /**
     * Example Utterances:<br>
     * “Alexa, decrease device name by number degrees”<br>
     * “Alexa, reduziere Gerätename um Anzahl Grad”<br>
     * <p>
     * Example Alexa Response: “OK”
     *
     * @return A object that indicates the previous and actual mode set by the device.
     * Contains a mode property  set to one of the following strings: AUTO, COOL, HEAT.
     * Contains a temp property in degrees Celsius.
     */
    StateInfo decrementTargetTemperature(float delta);


    class StateInfo {
        public State previous;
        public State actual;

        public static class State {
            public float temp;
            public Mode mode;
        }

        public enum Mode {
            AUTO, COOL, HEAT
        }
    }


}
