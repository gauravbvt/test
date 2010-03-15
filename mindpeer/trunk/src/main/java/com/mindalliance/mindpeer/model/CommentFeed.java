// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import javax.persistence.Entity;

/**
 * A standard comment feed.
 */
@Entity
public class CommentFeed extends Product {

    /**
     * Create a new ModelObject instance.
     */
    protected CommentFeed() {
        this( "feed" );
    }

    public CommentFeed( String name ) {
        super( name );
    }
}
