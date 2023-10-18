package com.urjalabs.smartcontroller.storage;

import com.urjalabs.smartcontroller.models.UserCredential;

public interface UserDAO {
    int addUser(UserCredential userCredential);
    UserCredential getUser(String email);

}
