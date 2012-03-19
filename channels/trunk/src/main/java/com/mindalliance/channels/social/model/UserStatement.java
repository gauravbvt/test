package com.mindalliance.channels.social.model;

import com.mindalliance.channels.core.model.ModelObject;

import javax.persistence.Entity;

/**
 * Abstract user statement.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/29/12
 * Time: 10:40 AM
 */
@Entity
public class UserStatement extends AbstractModelObjectReferencingPPO {
    
    private String text;

    public UserStatement() {
    }

    public UserStatement( String planUri, int planVersion, String username ) {
        super( planUri, planVersion, username);
    }


    public UserStatement( String planUri, int planVersion, String username, String text ) {
        super( planUri, planVersion, username);
        this.text = text;
    }

    public UserStatement( String planUri, int planVersion, String username, String text, ModelObject modelObject ) {
        super( planUri, planVersion, username, modelObject );
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }


}
