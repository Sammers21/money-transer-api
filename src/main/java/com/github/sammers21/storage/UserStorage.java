package com.github.sammers21.storage;

import com.github.sammers21.domain.User;

/**
 * Stores users and accounts.
 */
public interface UserStorage {

    /**
     * @param userId id of the user
     * @return created or existed user
     */
    User createUserIfNotExist(String userId);

    /**
     * @param userId id of the user
     * @return whether the user with given id exist or not
     */
    boolean isUserExist(String userId);
}
