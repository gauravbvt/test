package com.mindalliance.channels.social.model;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;
import com.mindalliance.channels.core.query.QueryService;

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
public class UserStatement extends AbstractPersistentPlanObject {
    
    private String text;
    /**
     * Model object reference as string.
     */
    private String moRef;
    /**
     * Model object label.
     */
    private String moLabel;

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
        this( planUri, planVersion, username, text );
        moRef = new ModelObjectRef( modelObject ).asString();
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }

    public String getMoRef() {
        return moRef;
    }

    public void setMoRef( ModelObject modelObject ) {
        moRef = new ModelObjectRef( modelObject ).asString();
        moLabel = aboutLabel( modelObject );
    }

    public void setMoRef( String moRef ) {
        this.moRef = moRef;
    }

    public String getMoLabel() {
        return moLabel == null ? "" : moLabel;
    }

    public void setMoLabel( String moLabel ) {
        this.moLabel = moLabel;
    }

    private String aboutLabel( ModelObject mo ) {
        String description = "";
        if ( mo != null ) {
            description = mo.getKindLabel() + " \"" + mo.getLabel() + "\"";
            if ( mo instanceof SegmentObject ) {
                description += " in segment \"" + ( (SegmentObject) mo ).getSegment().getLabel() + "\"";
            }
        }
        return description;
    }

    public ModelObject getAbout( QueryService queryService ) {
        ModelObjectRef aboutRef = getAboutRef();
        return aboutRef == null ? null : (ModelObject) aboutRef.resolve( queryService );
    }

    public ModelObjectRef getAboutRef() {
        return moRef == null ? null : ModelObjectRef.fromString( moRef );
    }

}
