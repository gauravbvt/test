// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.profiles.InferableObject;
import com.mindalliance.channels.data.support.GUID;

/**
 * An element in a storyline.
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class StorylineElement extends InferableObject
    implements ScenarioElement {

    /** Backpointer... */
    private Storyline storyline;

    /**
     * Default constructor.
     */
    public StorylineElement() {
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public StorylineElement( GUID guid ) {
        super( guid );
    }

    /**
     * Return the storyline.
     */
    @PropertyOptions( ignore = true )
    public Storyline getStoryline() {
        return storyline;
    }

    /**
     * Set the storyline.
     * @param storyline the storyline to set
     */
    public void setStoryline( Storyline storyline ) {
        this.storyline = storyline;
    }
}
