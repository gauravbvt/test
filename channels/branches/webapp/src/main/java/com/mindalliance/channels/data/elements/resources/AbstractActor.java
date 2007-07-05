// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.resources;

import java.util.ArrayList;
import java.util.List;

import com.beanview.annotation.PropertyOptions;
import com.mindalliance.channels.data.components.Contactable;
import com.mindalliance.channels.data.elements.assertions.Assertion;
import com.mindalliance.channels.data.elements.assertions.CanAccess;
import com.mindalliance.channels.data.elements.assertions.NeedsToKnow;
import com.mindalliance.channels.data.reference.Information;
import com.mindalliance.channels.util.CollectionType;
import com.mindalliance.channels.util.GUID;

/**
 * An accessible resource who can also execute tasks (actors are
 * specified by agents in scenarios).
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public abstract class AbstractActor extends AccessibleResource
    implements Actor {

    private List<Information> expertise;

    /**
     * Default constructor.
     */
    public AbstractActor() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public AbstractActor( GUID guid ) {
        super( guid );
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
     * What situational information (i.e. in scenario) the actor
     * needs.
     * @param information the information
     */
    public boolean needsToKnow( Information information ) {
        // TODO
        return false;
    }

    /**
     * Return the needsToKnow assertions.
     */
    @PropertyOptions( ignore = true )
    public List<NeedsToKnow> getNeedsToKnowAssertions() {
        List<NeedsToKnow> list = new ArrayList<NeedsToKnow>();
        for ( Assertion assertion : getAssertions() )
            if ( assertion instanceof NeedsToKnow )
                list.add( (NeedsToKnow) assertion );
        return list;
    }

    /**
     * Return the canAccess assertions.
     */
    @PropertyOptions( ignore = true )
    public List<CanAccess> getCanAccessAssertions() {
        List<CanAccess> list = new ArrayList<CanAccess>();
        for ( Assertion assertion : getAssertions() )
            if ( assertion instanceof CanAccess )
                list.add( (CanAccess) assertion );
        return list;
    }

    /**
     * Return if this actor has access to a resource.
     * @param contactable the contactable resource
     */
    public boolean hasAccess( Contactable contactable ) {
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
