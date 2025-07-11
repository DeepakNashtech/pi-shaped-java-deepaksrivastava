package com.evolve.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("users")
public class Usr {
    @Id
    private Long id;
    private String name;
    private String email;
    /* Constructors
    public Usr() {}
    public Usr(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }*/
}
