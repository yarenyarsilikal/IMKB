package com.example.imkb.network.model.handshake;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HandShakeRequest {
    @SerializedName("deviceId")
    @Expose
    public String deviceId;
    @SerializedName("systemVersion")
    @Expose
    public String systemVersion;
    @SerializedName("platformName")
    @Expose
    public String platformName;
    @SerializedName("deviceModel")
    @Expose
    public String deviceModel;
    @SerializedName("manifacturer")
    @Expose
    public String manifacturer;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getManifacturer() {
        return manifacturer;
    }

    public void setManifacturer(String manifacturer) {
        this.manifacturer = manifacturer;
    }
}
