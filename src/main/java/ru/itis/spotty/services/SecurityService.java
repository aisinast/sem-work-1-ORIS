package ru.itis.spotty.services;

import ru.itis.spotty.exceptions.AuthenticationException;
import ru.itis.spotty.models.User;

import java.util.UUID;

public interface SecurityService {
    User getUserBySessionId(UUID sessionId);
    User getUserById(UUID userId);

    UUID register(String username, String email, String password, String passwordRepeat) throws AuthenticationException;
    UUID login(String username, String password);
    void logout(UUID sessionId);
}
