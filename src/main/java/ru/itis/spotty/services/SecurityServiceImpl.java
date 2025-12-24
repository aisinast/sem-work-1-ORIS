package ru.itis.spotty.services;

import ru.itis.spotty.exceptions.AuthenticationException;
import ru.itis.spotty.models.User;
import ru.itis.spotty.repositories.SessionRepository;
import ru.itis.spotty.repositories.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

public class SecurityServiceImpl implements SecurityService {
    private Duration sessionDuration;
    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private final Base64.Encoder base64Encoder;

    public SecurityServiceImpl(UserRepository userRepository,
                               SessionRepository sessionRepository,
                               Properties properties) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;

        this.base64Encoder = Base64.getEncoder();

        long minutes = Long.parseLong(properties.getProperty("session.duration.minutes"));
        this.sessionDuration = Duration.ofMinutes(minutes);
    }


    @Override
    public User getUserBySessionId(UUID sessionId) {
        try {
            UUID userId = sessionRepository.getUserIdBySessionId(sessionId);
            if (userId == null) {
                throw new AuthenticationException("User not found");
            }
            return userRepository.getUserById(userId);
        } catch (Exception e) {
            throw new AuthenticationException("Ошибка аутентификации: " + e.getMessage());
        }
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.getUserById(userId);
    }

    @Override
    public UUID register(String username, String email, String password, String passwordRepeat) throws AuthenticationException {
        if (userRepository.isUsernameExists(username)) {
            throw new AuthenticationException("Пользователь с логином '" + username + "' уже существует");
        }

        if (userRepository.isEmailExists(email)) {
            throw new AuthenticationException("Пользователь с почтой '" + email + "' уже существует");
        }

        if (!password.equals(passwordRepeat)) {
            throw new AuthenticationException("Пароли не совпадают");
        }

        String salt = UUID.randomUUID().toString();
        String saltedPassword = password + salt;
        String hashedPassword;
        hashedPassword = getHashedPassword(saltedPassword);


        User user = new User(username, email, null, null, hashedPassword, salt, LocalDateTime.now());
        UUID userId = userRepository.createUser(user);
        UUID sessionId = UUID.randomUUID();
        sessionRepository.addSession(sessionId, userId, LocalDateTime.now().plus(sessionDuration));

        return sessionId;
    }

    @Override
    public UUID login(String usernameOrEmail, String password) {

        User user;

        boolean looksLikeEmail = usernameOrEmail.contains("@");

        if (looksLikeEmail) {
            Pattern emailPattern = Pattern.compile("^[A-Z0-9_]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            if (!emailPattern.matcher(usernameOrEmail).matches()) {
                throw new AuthenticationException("Некорректный ввод почты");
            }

            try {
                user = userRepository.getUserByEmail(usernameOrEmail);
            } catch (IllegalArgumentException e) {
                throw new AuthenticationException("Пользователь не найден");
            }
        } else {
            Pattern usernamePattern = Pattern.compile("^[A-Z0-9_]{3,32}$",  Pattern.CASE_INSENSITIVE);
            if (!usernamePattern.matcher(usernameOrEmail).matches()) {
                throw new AuthenticationException("Некорректный ввод логина");
            }

            try {
                user = userRepository.getUserByUsername(usernameOrEmail);
            } catch (IllegalArgumentException e) {
                throw new AuthenticationException("Пользователь не найден");
            }
        }

        String salt = user.getSalt();
        String saltedPassword = password + salt;
        String hashedPassword = getHashedPassword(saltedPassword);

        if (!user.getPasswordHash().equals(hashedPassword)) {
            throw new AuthenticationException("Неправильный логин или пароль");
        }

        UUID sessionId = UUID.randomUUID();
        sessionRepository.addSession(sessionId, user.getUserId(), LocalDateTime.now().plus(sessionDuration));
        return sessionId;
    }

    @Override
    public void logout(UUID sessionId) {
        sessionRepository.removeSession(sessionId);
    }

    private String getHashedPassword(String saltedPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] passwordHashBytes = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return base64Encoder.encodeToString(passwordHashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
