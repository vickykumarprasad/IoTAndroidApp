package com.urjalabs.smartcontroller.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * device model
 * Created by tarun on 01-11-2017.
 */

public class Device {
    @JsonIgnore
    private int floorNo;
    @JsonIgnore
    private int roomNo;
    @JsonIgnore
    private int id;
    @JsonProperty("deviceId")
    private String deviceId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;

    public Device(int floorNo, int roomNo, String deviceId, String name, String description) {
        this.floorNo = floorNo;
        this.roomNo = roomNo;
        this.deviceId = deviceId;
        this.name = name;
        this.description = description;
    }
    public Device(int floorNo, int roomNo, String deviceId) {
        this.floorNo = floorNo;
        this.roomNo = roomNo;
        this.deviceId = deviceId;
    }
    public Device(String deviceId, String name, String description) {
        this.deviceId = deviceId;
        this.name = name;
        this.description = description;
    }

    public Device() {
    }

    public int getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(int floorNo) {
        this.floorNo = floorNo;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(int roomNo) {
        this.roomNo = roomNo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeviceId() {

        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
