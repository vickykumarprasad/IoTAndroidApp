package com.urjalabs.smartcontroller.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * model to handle switch
 * Created by tarun on 01-11-2017.
 */

public class Load {
    @JsonIgnore
    private int id;
    //may be removed later
    @JsonProperty("switchId")
    private int switchId;
    @JsonProperty("name")
    private String name;
    //device id or urjalabs controller id
    @JsonProperty("deviceId")
    private String deviceId;
    //we will add home info here
    @JsonProperty("description")
    private String description;
    //schedule for given switch
    @JsonProperty("schedule")
    private String schedule;
    //switch is on or off
    @JsonProperty("status")
    private String status;
    //we will add type here
    @JsonIgnore
    private String type;
    @JsonIgnore
    private int floorNo;
    @JsonIgnore
    private String roomName;
    @JsonIgnore
    private String deviceName;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    @JsonIgnore
    private String topicName;

    public Load(int switchId, String name, String deviceId, String description, String type, String schedule, String status) {
        this.switchId = switchId;
        this.name = name;
        this.deviceId = deviceId;
        this.description = description;
        this.type = type;
        this.schedule = schedule;
        this.status = status;
    }

    public Load() {

    }

    public Load(int switchId, String name, String deviceId, String description, String schedule, String status, String type, int floorNo, String roomName, String deviceName) {
        this.switchId = switchId;
        this.name = name;
        this.deviceId = deviceId;
        this.description = description;
        this.schedule = schedule;
        this.status = status;
        this.type = type;
        this.floorNo = floorNo;
        this.roomName = roomName;
        this.deviceName = deviceName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSwitchId() {
        return switchId;
    }

    public void setSwitchId(int switchId) {
        this.switchId = switchId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(int floorNo) {
        this.floorNo = floorNo;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
