package ru.itis.spotty.services;

import ru.itis.spotty.models.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void editProfile(UUID currUserId, String username, String email, String bio, String avatarUrl);

    List<User> findEligibleUsers(String query);
}
