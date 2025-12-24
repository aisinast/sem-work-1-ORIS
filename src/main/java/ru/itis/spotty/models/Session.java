package ru.itis.spotty.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Session {
    private final UUID sessionId;
    private final UUID userId;
    private LocalDateTime expiresAt;

    public Session(UUID sessionId, UUID userId, LocalDateTime expiresAt) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
