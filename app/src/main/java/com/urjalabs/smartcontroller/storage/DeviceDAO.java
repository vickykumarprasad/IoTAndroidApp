package com.urjalabs.smartcontroller.storage;

import com.urjalabs.smartcontroller.models.Device;

import java.util.List;

/**
 * Created by tarun on 08-11-2017.
 */

public interface DeviceDAO {
    /**
     * @param device device details
     */
    int createDevice(Device device);

    /**
     * @param id
     * @return
     */
    Device getDevice(int id);

    boolean deleteDevice(Device device);

    /**
     * @param floorNo floor number
     * @param roomNo  room number
     * @return list of devices in a room
     */
    List<Device> getDevicesInRoom(int floorNo, int roomNo);

}
