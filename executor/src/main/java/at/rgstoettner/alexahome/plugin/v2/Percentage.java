package at.rgstoettner.alexahome.plugin.v2;

public interface Percentage extends DeviceV2 {

    /**
     * “Alexa, decrease device name by number percent”
     * <br>
     * “Alexa, reduziere Gerätename um Anzahl Prozent”
     * <br>
     *
     * @param delta The percent decrease to apply to the device specified as a 64-bit double. For this directive, deltaPercentage is subtracted from the current percent setting. For example, if the device is currently set to 40%, a deltaPercentage value of 15 means the device will be set at 25% after the request completes. Range is between 0.00 and 100.00, inclusive.
     */
    void decrementPercentage(double delta);

    /**
     * “Alexa, increase device name by number percent”
     * <br>
     * “Alexa, erhöhe Gerätename um Anzahl Prozent”
     * <br>
     *
     * @param delta The percent increase to apply to the device specified as a 64-bit double. For this directive, deltaPercentage is added to the current percentage setting. For example, if the device is currently set to 40%, a deltaPercentage value of 15 means the device will be set at 55% after the request completes. Range is between 0.00 and 100.00, inclusive.
     */
    void incrementPercentage(double delta);


    /**
     * “Alexa, set name to number percent”<br>
     * “Alexa, stelle Geräteame auf Anzahl Prozent”<br>
     *
     * @param value The percent change to apply to the device specified as a 64-bit double value with precision of up to two decimal places. Range is from 0.00 to 100.00, inclusive.
     */
    void setPercentage(double value);

}
