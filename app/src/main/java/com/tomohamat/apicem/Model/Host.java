package com.tomohamat.apicem.Model;

/**
 * Created by Robert on 3/13/2017.
 */

public class Host {

    private String id;
    private String hostIp;
    private String hostMac;
    private String hostType;
    private String connectedNetworkDeviceId;
    private String connectedNetworkDeviceIpAddress;

    public Host(String id, String hostIp, String hostMac) {
        this.id = id;
        this.hostIp = hostIp;
        this.hostMac = hostMac;
    }

    public String getHostIp() {
        return hostIp;
    }

    public String getHostMac() {
        return hostMac;
    }

    public String getId() {
        return id;
    }
}
