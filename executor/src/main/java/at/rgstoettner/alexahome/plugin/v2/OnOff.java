package at.rgstoettner.alexahome.plugin.v2;

public interface OnOff extends V2Device {

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
