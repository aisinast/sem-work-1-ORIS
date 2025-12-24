package ru.itis.spotty.repositories;

import ru.itis.spotty.models.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    UUID createUser(User user);

    User getUserById(UUID id);
    User getUserByUsername(String username);
    User getUserByEmail(String email);

    boolean isEmailExists(String email);
    boolean isUsernameExists(String username);

    void updateUser(User user);

    List<User> findEligibleUsers(String query);
}
