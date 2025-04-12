package de.viktorlevin.starkeverbenbot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "learned_words")
@NoArgsConstructor
public class LearnedWort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BotUser user;

    @OneToOne
    @JoinColumn(name = "word_id")
    private Wort wort;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    public LearnedWort(BotUser user, Wort wort, Status status) {
        this.user = user;
        this.wort = wort;
        this.status = status;
    }

    public enum Status {
        IN_PROGRESS, FINISHED
    }
}