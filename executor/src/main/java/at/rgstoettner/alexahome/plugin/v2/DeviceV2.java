package at.rgstoettner.alexahome.plugin.v2;

public interface DeviceV2 {
    String getAlias();

    /**
     * This method should return a unique name across all devices.
     * You use this name to control the device.
     *
     * @return a unique name
     */
    String getName();

    /**
     * CAMERA<br>
     * Indicates media devices with video or photo capabilities.
     * <p>
     * LIGHT<br>
     * Indicates light sources or fixtures.
     * <p>
     * SMARTLOCK<br>
     * Indicates door locks.
     * <p>
     * SMARTPLUG<br>
     * Indicates modules that are plugged into an existing electrical outlet.	Can control a variety of devices.
     * <p>
     * SWITCH<br>
     * Indicates in-wall switches wired to the electrical system.	Can control a variety of devices.
     * <p>
     * THERMOSTAT<br>
     * Indicates thermostats that control temperature, stand-alone air conditioners, or heaters with direct temperature control.<br>
     *
     * @return a string containing one of the types
     */
    String getType();

    String getDescription();

    String getManufacturer();

    String getModel();

    String getSoftwareVersion();
}
