package de.viktorlevin.starkeverbenbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "starke_verben")
@NoArgsConstructor
public class StarkesVerb {
    @Id
    private Integer id;

    @Column(name = "infinitiv")
    private String infinitiv;

    @Column(name = "präsens")
    private String präsens;

    @Column(name = "präteritum")
    private String präteritum;

    @Column(name = "partizip2")
    private String partizip2;

    @Column(name = "translation")
    private String translation;
}
