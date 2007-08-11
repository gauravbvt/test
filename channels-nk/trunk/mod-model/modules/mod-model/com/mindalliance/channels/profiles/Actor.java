// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.profiles;

import java.util.List;

import com.mindalliance.channels.definitions.Information;
import com.mindalliance.channels.definitions.Category.Taxonomy;
import com.mindalliance.channels.support.CollectionType;
import com.mindalliance.channels.support.GUID;

/**
 * A resource who can also execute tasks (actors are
 * specified by agents in scenarios).
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 * @composed - expertise * Information
 */
public abstract class Actor extends Resource {

    private List<Information> expertise;

    /**
     * Default constructor.
     */
    public Actor() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     * @param taxonomy the taxonomy
     */
    public Actor( GUID guid, Taxonomy taxonomy ) {
        super( guid, taxonomy );
    }

    /**
     * What the agent knows (general knowledge plus possibly
     * situational knowledge).
     * @param information the information
     */
    public boolean knows( Information information ) {
        // TODO
        return false;
    }

    /**
     * Return the value of expertise.
     */
    @CollectionType( type = Information.class )
    public List<Information> getExpertise() {
        return expertise;
    }

    /**
     * Set the value of expertise.
     * @param expertise The new value of expertise
     */
    public void setExpertise( List<Information> expertise ) {
        this.expertise = expertise;
    }

    /**
     * Add an expertise.
     * @param information the information one is expert about
     */
    public void addExpertise( Information information ) {
        expertise.add( information );
    }

    /**
     * Remove an expertise.
     * @param information the information
     */
    public void removeExpertise( Information information ) {
        expertise.remove( information );
    }

}
