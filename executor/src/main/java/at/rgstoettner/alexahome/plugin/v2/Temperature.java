package at.rgstoettner.alexahome.plugin.v2;

public interface Temperature {


    /**
     * Example Utterances: <br>
     * “Alexa, what is the temperature of device name?”<br>
     * <p>
     * Example Alexa Response:<br>
     * “According to device name, it’s 70 degrees”
     *
     * @return Indicates the temperature reading from the specified appliance, defaults to Celsius.
     */
    Reading getTemperatureReading();


    /**
     * Example Utterances: <br>
     * “Alexa, set the room name to number degrees”
     * <br>
     * “Alexa, stelle Raumname auf Anzahl Grad”
     * <br>
     *
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
     *
     * Example Alexa Response: “OK”
     *
     * @return A object that indicates the previous and actual mode set by the device.
     * Contains a mode property  set to one of the following strings: AUTO, COOL, HEAT.
     * Contains a temp property in degrees Celsius.
     */
    StateInfo incrementTargetTemperature();


    /**
     * Example Utterances:<br>
     * “Alexa, decrease device name by number degrees”<br>
     * “Alexa, reduziere Gerätename um Anzahl Grad”<br>
     *
     * Example Alexa Response: “OK”
     *
     * @return A object that indicates the previous and actual mode set by the device.
     * Contains a mode property  set to one of the following strings: AUTO, COOL, HEAT.
     * Contains a temp property in degrees Celsius.
     */
    StateInfo decrementTargetTemperature();


    class Reading {
        /**
         * Floating point number that indicates the temperature in degrees.
         */
        public float value;
        /**
         * Optional value that specifies the temperature scale of the value. Valid values are CELSIUS and FAHRENHEIT. If not provided, the default, CELSIUS, is used.
         */
        public String scale;
    }

    class StateInfo {
        public State previous;
        public State actual;
    }

    class State {
        public float temp;
        public String mode;
    }


}
