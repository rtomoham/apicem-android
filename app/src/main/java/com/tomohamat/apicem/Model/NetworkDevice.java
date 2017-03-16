package com.tomohamat.apicem.Model;

/**
 * Created by Robert on 3/13/2017.
 */

public class NetworkDevice {

    public static final String FAMILY_SWITCHES = "Switches and Hubs";
    public static final String FAMILY_APS = "Access Points";

    private String id;
    private String serialNumber;
    private String hostname;
    private String family;
    private String type;


    public NetworkDevice(String id, String serialNumber, String hostname, String family, String type) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.hostname = hostname;
        this.family = family;
        this.type = type;
    }

    public String getHostname() {
        return hostname;
    }

    public String getFamily() {
        return family;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public boolean isAp() {
        return FAMILY_APS.equals(family);
    }

    public boolean isSwitch() {
        return FAMILY_SWITCHES.equals(family);
    }

    public String toString() {
        return id + "\n" +
                serialNumber + "\n" +
                hostname + "\n" +
                family + "\n" +
                type + "\n";
    }
}
