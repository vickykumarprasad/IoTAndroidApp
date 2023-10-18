package com.urjalabs.smartcontroller.models;

/**
 * Created by tarun on 13-11-2017.
 */

public class MqttServerProperties {
    private String primaryServer;
    private String secondaryServer;
    private Integer portNo;
    private Boolean isAuthReq;
    private String userName;
    private String password;

    public MqttServerProperties(String primaryServer, String secondaryServer, Integer portNo) {
        this.primaryServer = primaryServer;
        this.secondaryServer = secondaryServer;
        this.portNo = portNo;
    }

    public String getPrimaryServer() {
        return primaryServer;
    }

    public void setPrimaryServer(String primaryServer) {
        this.primaryServer = primaryServer;
    }

    public String getSecondaryServer() {
        return secondaryServer;
    }

    public void setSecondaryServer(String secondaryServer) {
        this.secondaryServer = secondaryServer;
    }

    public Integer getPortNo() {
        return portNo;
    }

    public void setPortNo(Integer portNo) {
        this.portNo = portNo;
    }

    public Boolean getAuthReq() {
        return isAuthReq;
    }

    public void setAuthReq(Boolean authReq) {
        isAuthReq = authReq;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
