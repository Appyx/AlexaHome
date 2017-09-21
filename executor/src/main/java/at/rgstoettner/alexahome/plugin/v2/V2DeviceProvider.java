package at.rgstoettner.alexahome.plugin.v2;

import java.util.List;

public interface V2DeviceProvider {

    /**
     * Provides a list of devices.
     * Called when the class which implements this interface is loaded.
     * <p>
     * The implementing class can be used to dynamically load devices depending on something.
     * The returned devices can share a class, a base class or can have their own implementations.
     *
     * @return
     */
    List<V2Device> getDevices();
}
