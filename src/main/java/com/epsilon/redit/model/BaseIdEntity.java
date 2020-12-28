package com.epsilon.redit.model;

import javax.persistence.*;

@MappedSuperclass
public class BaseIdEntity {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    @Id
    public Long id;
}
