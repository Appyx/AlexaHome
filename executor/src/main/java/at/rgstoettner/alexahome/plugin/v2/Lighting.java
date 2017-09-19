package at.rgstoettner.alexahome.plugin.v2;

public interface Lighting extends DeviceV2 {
    /**
     * Example Utterances:<br>
     * “Alexa, set the device name to color”<br>
     * “Alexa, set the bedroom light to red”<br>
     * “Alexa, change the kitchen to the color blue”<br>
     * “setze Wohnzimmerlicht auf rosa”<br>
     * <p>
     * Example Alexa Response: OK
     *
     * @param color Describes the color to set for the light. Specified in the Hue, Saturation, Brightness (HSB) color model.
     * @return Indicates the color of the device after the color change.
     */
    Color setColor(Color color);

    /**
     * Example Utterances:<br>
     * “Alexa, change the device name to shade of white”<br>
     * “Alexa, make the living room warm white”<br>
     * “Alexa, set the kitchen to daylight”<br>
     * “Alexa, schalte Schlafzimmerlicht auf warmes Weiß”<br>
     * <p>
     * Example Alexa Response: OK
     * <p>
     * Shades of White<br>
     * warm, warm white: 2200<br>
     * incandescent, soft white: 2700<br>
     * white: 4000<br>
     * daylight, daylight white: 5500<br>
     * cool, cool white: 7000<br>
     *
     * @param value An integer that indicates the requested color temperature in Kelvin degrees. Valid range is 1000 to 10000, inclusive
     * @return Indicates the color temperature of the device after the color change.
     */
    int setColorTemperature(int value);

    /**
     * “Alexa, set the device name warmer/softer”
     * <br>
     * “Alexa, set the dining room softer”
     * <br>
     * “Alexa, make the living room warmer”
     * <br>
     *
     * @return An integer that indicates the color temperature setting after the decrease, in Kelvin degrees.
     * Valid range is 1000 to 10000, inclusive.
     */
    public int decrementColorTemperature();

    /**
     * “Alexa, set the device name cooler/whiter”
     * <br>
     * “Alexa, set the dining room cooler”
     * <br>
     * “Alexa, make the living room light whiter”
     * <br>
     *
     * @return An integer that indicates the color temperature setting after the increase, in Kelvin degrees. Valid range is 1000 to 10000, inclusive.
     */
    int incrementColorTemperature();


    class Color {
        /**
         * A double that indicates the desired hue setting. Valid range is 0.00 to 360.00, inclusive.
         */
        public double hue;
        /**
         * A double that indicates the desired saturation setting. Valid range is 0.0000 to 1.0000, inclusive.
         */
        public double saturation;
        /**
         * A double that indicates the desired brightness setting. Valid range is 0.0000 to 1.0000, inclusive.
         */
        public double brightness;
    }
}
