package util;

/**
 * Created by Merlin on 07.03.2016.
 */
public class DeviceItem {
    private String name;
    private int device;

    public DeviceItem(String name, int device) {
        this.name = name;
        this.device = device;
    }
    @Override
    public String toString() { return name; }

    public int getDeviceInt(){
        return device;
    }
}
