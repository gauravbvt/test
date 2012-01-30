/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;

/**
 * A step involving some collaboration with another party.
 */
@Entity
public abstract class Collaboration extends Step {

    private static final Logger LOG = LoggerFactory.getLogger( Collaboration.class );

    private static final long serialVersionUID = -2318735159254336969L;

    private boolean confirmed;

    private boolean pending;

    @ManyToOne
    private Medium using;

    @ManyToOne
    private Contact with;
    
    @OneToMany( mappedBy = "collaboration", cascade = CascadeType.ALL )
    private List<ConfirmationReq> requests;

    //
    // Constructors
    //
    protected Collaboration() {
    }

    protected Collaboration( Step step ) {
        super( step );
        
        if ( step.isCollaboration() ) {
            Collaboration collaboration = (Collaboration) step;
            
            using = collaboration.getUsing();
            with = collaboration.getWith();
        }

    }

    public ConfirmationReq createRequest() {
        for ( ConfirmationReq request : requests )
            if ( request.isPending() )
                return request;

        pending = false;
        ConfirmationReq req = new ConfirmationReq( this );
        req.setDescription( getDefaultDescription() );        
        
        return req;
    }

    @Transient
    public String getDefaultDescription() {
        // TODO implement this

        StringBuilder sb = new StringBuilder();
        
        sb.append( "Dear " );
        sb.append( with.getGivenName() );
        sb.append( ',' );
        sb.append( "\n\n" );
        sb.append( "here would be some cleverly worded generated text " );
        sb.append( "about what I want to do..." );

        return sb.toString();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed( boolean confirmed ) {
        this.confirmed = confirmed;
    }

    /**
     * Test if this collaboration is waiting to be confirmed.
     * @return true if there are some pending confirmation requests.
     */
    public boolean isPending() {
        return pending;
    }

    public void setPending( boolean pending ) {
        this.pending = pending;
    }

    public Medium getUsing() {
        return using;
    }

    public void setUsing( Medium using ) {
        LOG.debug( "Updating 'using' to {}", using );
        this.using = using;
    }

    public Contact getWith() {
        return with;
    }

    public void setWith( Contact with ) {
        LOG.debug( "Updating 'with' to {}", with );
        this.with = with;
    }
    
    @Transient
    public boolean isCollaboration() {
        return true;
    }

    @Override
    public boolean isConfirmable() {
        return !confirmed && !pending;
    }
    
    public List<ConfirmationReq> getRequests() {
        return requests;
    }
    
    public void addRequest( ConfirmationReq request ) {
        requests.add( request );
        confirmed = false;
        pending = true;
    }
    
}
