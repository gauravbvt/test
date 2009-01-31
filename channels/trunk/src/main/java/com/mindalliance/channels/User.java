package com.mindalliance.channels;

import javax.persistence.Id;
import javax.persistence.Entity;

/**
 * A user of the system.
 */
@Entity
public class User {

    private String name;
    private String fullName;
    private String email;

    public User() {
    }

    public User( String name ) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName( String fullName ) {
        this.fullName = fullName;
    }

    @Id
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }
}
