package com.urjalabs.smartcontroller.storage;

import com.urjalabs.smartcontroller.models.Device;
import com.urjalabs.smartcontroller.models.Room;

/**
 * interface for home configuration
 * Created by tarun on 02-11-2017.
 */

public interface HomeConfigurationDAO {
    /**
     * save or update room configuration
     *
     * @param room     room
     */
     void saveOrUpdateRoomConfig( Room room);

    /**
     * get room configuration
     * @param floorNo  floor number
     * @param roomNo   room number
     * @return room detail
     */
    Room getRoomConfig( int floorNo, int roomNo);

    /**
     * @param floorNo floor number
     * @return number of rooms on floor
     */
    int getNumberOfRooms(int floorNo);

    /**
     * @param floorNo      floor number
     * @param numberOfRoom of rooms on floor
     */
    void saveOrUpdateNumberOfRooms(int floorNo, int numberOfRoom);

    /**
     * @param device   device details
     */
    void saveOrUpdateDeviceConfig(Device device);

    /**
     * @param floorNo  number of floor
     * @param roomNo   roomNumber
     * @param deviceNo device number
     * @return device details
     */
    Device getDeviceConfig(int floorNo, int roomNo, int deviceNo);

    void deleteDevice(Device device);

    /**
     * @param floorNo floor number
     * @param roomNo  room number
     * @return number of devices in a room
     */
    int getNumberOfDevice(int floorNo, int roomNo);

    /**
     * @param floorNo         floor number
     * @param roomNo          room number
     * @param numberOfDevices number of devices in a room
     */
    void saveOrUpdateNumberOfDevices(int floorNo, int roomNo, int numberOfDevices);

    /**
     * @return name of home
     */
    String getHomeName();

    /**
     * save home name
     *
     * @param homeName name of house
     */
    void saveOrUpdateHomeName(String homeName);

    /**
     * @return number of floor
     */
    int getNumberOfFloor();

    /**
     * save or update number of floor
     *
     * @param numberOfFloor number of floor
     */
    void saveOrUpdateNumberOfFloor(int numberOfFloor);
}
