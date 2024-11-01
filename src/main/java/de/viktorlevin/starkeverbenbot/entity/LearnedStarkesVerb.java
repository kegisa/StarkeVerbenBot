package de.viktorlevin.starkeverbenbot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "learned_starke_verben")
@NoArgsConstructor
public class LearnedStarkesVerb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BotUser user;

    @OneToOne
    @JoinColumn(name = "verb_id")
    private StarkesVerb verb;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

    public LearnedStarkesVerb(BotUser user, StarkesVerb verb, Status status) {
        this.user = user;
        this.verb = verb;
        this.status = status;
    }

    public enum Status {
        IN_PROGRESS, FINISHED
    }

}
