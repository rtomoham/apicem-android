package com.tomohamat.apicem.Model;

/**
 * Created by Robert on 4/13/2017.
 */

public class DeviceLicense {

    private String name;
    private String description;
    private String type;
    private String status;
    private int index;
    private String storeName;
    private String id;

    public DeviceLicense(String name,
                         String description,
                         String type,
                         String status,
                         int index,
                         String storeName,
                         String id) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.status = status;
        this.index = index;
        this.storeName = storeName;
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String toString() {
        String result = new String();

        result += "************\n";
        result += "* LICENSE " + getIndex() + " *\n";
        result += "************\n";
        result += "* id:\t" + getId() + "\n";
        result += "* name:\t" + getName() + "\n";
        result += "* description:\t" + getDescription() + "\n";
        result += "* type:\t" + getType() + "\n";
        result += "* status:\t" + getStatus() + "\n";

        return result;
    }


}
