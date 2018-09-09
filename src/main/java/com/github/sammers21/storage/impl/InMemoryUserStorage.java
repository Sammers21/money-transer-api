package com.github.sammers21.storage.impl;

import com.github.sammers21.domain.User;
import com.github.sammers21.domain.impl.UserImpl;
import com.github.sammers21.storage.UserStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserStorage implements UserStorage {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public User createUserIfNotExist(String userId) {
        return users.computeIfAbsent(userId, UserImpl::new);
    }

    @Override
    public boolean isUserExist(String userId) {
        return users.get(userId) != null;
    }
}
