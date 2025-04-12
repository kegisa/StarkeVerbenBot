package de.viktorlevin.starkeverbenbot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "notification")
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BotUser user;

    @Enumerated(EnumType.ORDINAL)
    private Type type;

    @CreationTimestamp
    private OffsetDateTime sent_at;

    public Notification(BotUser user, Type type) {
        this.user = user;
        this.type = type;
    }

    public enum Type {
        RATING, ACTIVITY
    }
}
