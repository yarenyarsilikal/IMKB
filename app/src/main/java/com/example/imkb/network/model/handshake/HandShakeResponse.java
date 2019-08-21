package com.example.imkb.network.model.handshake;

import com.example.imkb.network.model.Status;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HandShakeResponse {
    @SerializedName("aesKey")
    @Expose
    public String aesKey;
    @SerializedName("aesIV")
    @Expose
    public String aesIV;
    @SerializedName("authorization")
    @Expose
    public String authorization;
    @SerializedName("lifeTime")
    @Expose
    public String lifeTime;
    @SerializedName("status")
    @Expose
    public Status status;

    public String getAesIV() {
        return aesIV;
    }

    public void setAesIV(String aesIV) {
        this.aesIV = aesIV;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(String lifeTime) {
        this.lifeTime = lifeTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }
}
