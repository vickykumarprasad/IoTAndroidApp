package com.urjalabs.smartcontroller.models;

/**
 * Created by tarun on 01-11-2017.
 */

public class Room {
    int floorNo;
    int roomNumber;
    private String name;
    private String type;

    public Room(int floorNo, int roomNumber, String name, String type) {
        this.floorNo = floorNo;
        this.roomNumber = roomNumber;
        this.name = name;
        this.type = type;
    }
    public Room(){

    }
    public int getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(int floorNo) {
        this.floorNo = floorNo;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
