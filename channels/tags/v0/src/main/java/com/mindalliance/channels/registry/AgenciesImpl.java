// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.set.ListOrderedSet;

import com.mindalliance.channels.Agencies;
import com.mindalliance.channels.AgenciesListener;
import com.mindalliance.channels.Agency;

/**
 * Implementation of an agency registry.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class AgenciesImpl implements Agencies {

    private Set<Agency> agencies = new HashSet<Agency>();
    private Set<AgenciesListener> listeners;

    public AgenciesImpl() {
    }

    /* (non-Javadoc)
     * @see Agencies#addAgency(Agency)
     */
    public void addAgency( Agency agency ) {
        if ( !this.agencies.contains(  agency ) ) {
            this.agencies.add( agency );
            fireAgencyAdded( agency );
        }
    }

    /* (non-Javadoc)
     * @see Agencies#getAgencies()
     */
    public Collection<Agency> getAgencies() {
        return Collections.unmodifiableSet(
                new HashSet<Agency>( this.agencies ) );
    }

    /* (non-Javadoc)
     * @see Agencies#removeAgency(Agency)
     */
    public void removeAgency( Agency agency ) {
        if ( this.agencies.contains(  agency ) ) {
            this.agencies.remove( agency );
            fireAgencyRemoved( agency );
        }
    }

    private void fireAgencyRemoved( Agency agency ) {
        if ( this.listeners != null ) synchronized ( this.listeners ) {
            for ( AgenciesListener l : this.listeners )
                l.removedAgency( agency );
            }
    }

    private void fireAgencyAdded( Agency agency ) {
        if ( this.listeners != null ) synchronized ( this.listeners ) {
            for ( AgenciesListener l : this.listeners )
                l.addedAgency( agency );
            }
    }

    @SuppressWarnings( "unchecked" )
    private synchronized Set<AgenciesListener> getListeners() {
        if ( this.listeners == null )
            this.listeners = Collections.synchronizedSet(
                    (Set<AgenciesListener>) new ListOrderedSet() );
        return this.listeners;
    }

    public void addAgenciesListener( AgenciesListener agenciesListener ) {
        getListeners().add( agenciesListener );
    }

    public void removeAgenciesListener( AgenciesListener agenciesListener ) {
        if ( this.listeners != null )
            this.listeners.remove(  agenciesListener );
    }
}
