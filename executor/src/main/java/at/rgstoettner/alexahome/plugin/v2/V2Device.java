package at.rgstoettner.alexahome.plugin.v2;

public interface V2Device {

    /**
     * This method should return a unique name across all devices.
     * You use this name to control the device.
     *
     * @return a unique name
     */
    String getName();

    /**
     * Determines whether this device should be shown under scenes in the Alexa-App
     *
     * @return True if this device should be treated as a scene, False otherwise.
     */
    boolean isScene();


    /**
     * A boolean indicating whether the current execution failed.
     * This will be called only right after the execution.
     *
     * @return True if an error occurred, False otherwise.
     */
    boolean isError();

    /**
     * The description is visible in the Alexa-App below the name of the device.
     *
     * @return A String describing the device.
     */
    String getDescription();

    String getManufacturer();

    String getModel();

    String getSoftwareVersion();
}
