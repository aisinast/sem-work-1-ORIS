package ru.itis.spotty.repositories;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SessionRepository {
    void addSession(UUID sessionId, UUID userId, LocalDateTime expiresAt);
    UUID getUserIdBySessionId(UUID sessionId);
    void removeSession(UUID sessionId);
}

