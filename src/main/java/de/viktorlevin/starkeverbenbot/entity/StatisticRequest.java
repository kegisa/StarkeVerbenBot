package de.viktorlevin.starkeverbenbot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "requests_statistic")
@NoArgsConstructor
public class StatisticRequest {

    @Id
    @Column(name = "id")
    private Integer id;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "user_id")
    private BotUser user;

    @JoinColumn(name = "request")
    private String request;

    @Column(name = "requested_at")
    @CreationTimestamp
    private OffsetDateTime requestedAt;
}
