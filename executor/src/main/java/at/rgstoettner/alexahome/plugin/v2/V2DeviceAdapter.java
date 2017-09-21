package at.rgstoettner.alexahome.plugin.v2;

public abstract class V2DeviceAdapter implements V2Device {


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

    @Override
    public boolean isScene() {
        return false;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
