package at.rgstoettner.alexahome.plugin.v2;

public abstract class DeviceV2Adapter implements DeviceV2 {

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public String getManufacturer() {
        return "No Maufacturer";
    }

    @Override
    public String getModel() {
        return "No Model";
    }

    @Override
    public String getSoftwareVersion() {
        return "No Software Version";
    }

    @Override
    public String getDescription() {
        return "No Description";
    }
}
