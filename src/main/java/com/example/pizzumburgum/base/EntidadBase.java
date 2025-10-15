package com.example.pizzumburgum.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@MappedSuperclass
public class EntidadBase {

    @Id
    @SequenceGenerator(name = "entity_seq",
            sequenceName = "entity_sequence",
            initialValue = 100)
    @GeneratedValue(strategy = GenerationType.SEQUENCE	, generator = "entity_seq")
    protected Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    public boolean isNew() {
        return this.id == null;
    }
}