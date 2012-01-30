package com.mindalliance.playbook.model;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Common basis for media.
 */
@Entity
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn( name = "MEDIUM_TYPE", discriminatorType = DiscriminatorType.INTEGER )
public abstract class Medium implements Serializable {

    public enum MediumType {
        ADDRESS,
        OTHER
    }

    private static final long serialVersionUID = -754200895087374965L;

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    private Contact contact;

    private String type;

    private boolean preferred;

    protected Medium() {
    }

    protected Medium( Contact contact, String type ) {
        if ( contact == null || type == null )
            throw new IllegalArgumentException();

        this.contact = contact;
        this.type = type;
    }

    protected Medium( Contact contact, Medium medium ) {
        this( contact, medium.getType() );
    }

    @Transient
    public abstract MediumType getMediumType();
    
    @Transient
    public abstract Object getAddress();

    public Contact getContact() {
        return contact;
    }

    public String getType() {
        return type;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred( boolean preferred ) {
        this.preferred = preferred;
    }

    public long getId() {
        return id;
    }

    @Override
    public abstract boolean equals( Object obj );
    
    

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + getMediumType().hashCode();
        return result;
    }
}
