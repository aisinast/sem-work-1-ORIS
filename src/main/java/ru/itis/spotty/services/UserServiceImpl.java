package ru.itis.spotty.services;

import ru.itis.spotty.exceptions.NotFoundException;
import ru.itis.spotty.exceptions.ValidationException;
import ru.itis.spotty.models.User;
import ru.itis.spotty.repositories.UserRepository;

import java.util.List;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void editProfile(UUID currUserId, String username, String email, String bio, String avatarUrl) {

        User user = userRepository.getUserById(currUserId);

        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        if (!username.equals(user.getUsername()) && userRepository.isUsernameExists(username)) {
            throw new ValidationException("Пользователь с ником '" + username + "' уже существует");
        }

        if (!email.equals(user.getEmail()) && userRepository.isEmailExists(email)) {
            throw new ValidationException("Пользователь с почтой '" + email + "' уже существует");
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setBio(bio);

        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }

        userRepository.updateUser(user);
    }

    @Override
    public List<User> findEligibleUsers(String query) {
        return userRepository.findEligibleUsers(query);
    }
}
