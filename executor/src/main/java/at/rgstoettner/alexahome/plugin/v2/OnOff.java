package at.rgstoettner.alexahome.plugin.v2;

public interface OnOff extends DeviceV2{

    /**
     * “Alexa, turn on the device name”<br>
     * “Alexa, schalte Gerätename ein”<br>
     */
    void turnOn();

    /**
     * “Alexa, turn off the device name”<br>
     * “Alexa, schalte Gerätename aus”<br>
     */
    void turnOff();
}
