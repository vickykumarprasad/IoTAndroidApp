package com.urjalabs.smartcontroller.storage;

import com.urjalabs.smartcontroller.models.Load;

import java.util.List;

/**
 * dao for load switch
 * Created by tarun on 03-11-2017.
 */

public interface LoadSwitchDAO {
    // Adding new load

    /**
     * @param load
     * @return -1 if exist 1
     */
    int addLoad(Load load);

    // Getting single contact

    /**
     * @param id load id
     * @return load
     */
    Load getLoad(int id);

    // Getting All load
    List<Load> getLoadsByType(String type);

    List<Load> getLoadsByDeviceID(String deviceID);

    /**
     * @return total get load count
     */
    int getTotalLoadCount();

    /**
     * @param type type of load e.g.
     * @return total number of load by type
     */
    int getLoadCountByType(String type);

    /**
     * @param deviceId device id of load e.g.
     * @return total number of load by type
     */
    int getLoadCountByDeviceID(String deviceId);

    /**
     * @param load load object
     * @return 1 if update true
     */
    int updateLoad(Load load);

    // Deleting single contact

    boolean deleteLoad(Load load);

    int deleteLoadByDeviceID(String deviceId);

    /**
     * @param columnName  name of field
     * @param columnValue value of field
     * @return true if exist
     */
    boolean isValueExistInLoad(String columnName, String columnValue);

    /**
     * @return topics list for mqtt
     */
    List<String> getAllTopics();
}
