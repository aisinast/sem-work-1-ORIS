package ru.itis.spotty.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import ru.itis.spotty.repositories.JdbcSessionRepository;
import ru.itis.spotty.repositories.SessionRepository;

import java.util.UUID;

public class CookieUtils {
    public static String extractSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("sessionId")) {
                    try {
                        return cookie.getValue();
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public static UUID extractUserIdFromSession(HttpServletRequest request) {
        String sessionId = CookieUtils.extractSessionId(request);
        if (sessionId == null) {
            return null;
        }

        SessionRepository sessionRepository = new JdbcSessionRepository();

        return sessionRepository.getUserIdBySessionId(UUID.fromString(sessionId));
    }
}
