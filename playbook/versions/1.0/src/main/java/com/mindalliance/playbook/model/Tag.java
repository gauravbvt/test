/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import org.hibernate.search.annotations.Field;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * A descriptive tag for organization purposes.
 */
@Entity
public class Tag implements Comparable<Tag>, Serializable {

    private static final long serialVersionUID = -3179076408554606968L;

    @Id
    private String name = "unnamed";

    public Tag() {
    }

    public Tag( String name ) {
        if ( name == null || name.isEmpty() )
            throw new IllegalArgumentException();

        this.name = name;
    }

    @Field
    public String getName() {
        return name;
    }

    @Override
    public boolean equals( Object obj ) {
        return this == obj 
            || obj != null && getClass() == obj.getClass() 
                           && name.equals( ( (Tag) obj ).getName() );
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo( Tag o ) {
        return name.compareTo( o.getName() );
    }
}
