/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * A step involving some collaboration with another party.
 */
@Entity
public abstract class Collaboration extends Step {

    private static final long serialVersionUID = -2318735159254336969L;

    @ManyToOne
    private Medium using;

    @ManyToOne
    private Contact with;
    
    @OneToOne( mappedBy = "collaboration", cascade = CascadeType.REMOVE, optional = true )
    private ConfirmationReq request;
    
    @OneToOne( mappedBy = "step", cascade = CascadeType.REMOVE, optional = true )
    private Ack agreement;

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

    protected Collaboration( Play play ) {
        super( play );
    }

    public ConfirmationReq createRequest() {

        if ( request != null )
            return request;

        ConfirmationReq req = new ConfirmationReq( this );
        req.setDescription( getDefaultDescription() );        
        
        return req;
    }

    @Override
    public String getActionLink() {
        return using == null ? null : using.getActionUrl();
    }

    @Override
    public String getActionText() {
        return  using == null ? null : using.getMediumType().getVerb();
    }

    @Transient
    public String getDefaultDescription() {
        // TODO implement this

        StringBuilder sb = new StringBuilder();
        
        sb.append( "Dear " );
        sb.append( with.getGivenName() );
        sb.append( ',' );
        sb.append( "\n\n" );
        
        if ( isSend() )
            sb.append( "I would like to contact you at " +  getUsing() );
        else
            sb.append( "I need you to contact me at " + getUsing() );

        sb.append( " in the context of my task \"" + getTitle() + "\". " );
        
        sb.append( "Is that OK?" );

        return sb.toString();
    }

    public Medium getUsing() {
        return using;
    }

    public void setUsing( Medium using ) {
        this.using = using;
    }

    public Contact getWith() {
        return with;
    }

    public void setWith( Contact with ) {
        this.with = with;
    }
    
    @Override
    @Transient
    public boolean isCollaboration() {
        return true;
    }

    public ConfirmationReq getRequest() {
        return request;
    }

    /**
     * Get the agreement that originated this step.
     * @return a 'yes' answer to a collaboration request.
     */
    public Ack getAgreement() {
        return agreement;
    }

    public void setAgreement( Ack agreement ) {
        this.agreement = agreement;
    }

    @Transient
    public boolean isAgreed() {
        return agreement != null;
    }

    /**
     * @return true if this collaboration implies calling someone.
     */
    public abstract boolean isSend();

    @Transient
    public String getMediumString() {
        StringBuilder sb = new StringBuilder();        

        if ( !isSend() )
            sb.append( "contacting me " );
        
        sb.append( using == null ? "somehow" : using.getMediumString() );
        
        return sb.toString();
    }
}
