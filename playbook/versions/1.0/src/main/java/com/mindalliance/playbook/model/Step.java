/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

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
 * A step in a play.
 */
@DiscriminatorColumn( discriminatorType = DiscriminatorType.STRING )
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@Entity
public abstract class Step implements Serializable {

    private static final long serialVersionUID = 704993355212036700L;

    public enum Type {
        SUBPLAY, TASK, RECEIVE, SEND
    }
      
    @Id @GeneratedValue
    private long id;

    @ManyToOne
    private Play play;

    private int sequence;

    private String title;

    private String description;

    private String duration;

    //
    // Constructors
    //
    protected Step() {
    }

    protected Step( Play play ) {
        this.play = play;
    }

    protected Step( Step other ) {
        this( other.getPlay() );
        
        sequence = other.getSequence();
        description = other.getDescription();
        title = other.getTitle();
        duration = other.getDuration();
    }

    public String getActionLink() {
        return null;
    }

    public String getActionText() {
        return "";
    }

    public long getId() {
        return id;
    }

    public Play getPlay() {
        return play;
    }

    /**
     * Get the owner of this step.
     * @return a contact, local to the owner
     */
    @Transient
    public Contact getOwner() {
        return play.getOwner();
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence( int sequence ) {
        this.sequence = sequence;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration( String duration ) {
        this.duration = duration;
    }
    
    @Transient
    public boolean isCollaboration() {
        return false;
    }
    
    @Transient
    public abstract Type getType();
}
