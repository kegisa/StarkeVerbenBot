package de.viktorlevin.starkeverbenbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "worte")
@NoArgsConstructor
public class Wort {
    @Id
    private Integer id;

    @Column(name = "word")
    private String word;

    @Column(name = "translation")
    private String translation;
}