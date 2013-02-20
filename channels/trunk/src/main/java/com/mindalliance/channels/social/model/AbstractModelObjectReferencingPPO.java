package com.mindalliance.channels.social.model;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/7/12
 * Time: 12:57 PM
 */
@Entity
public abstract class AbstractModelObjectReferencingPPO extends AbstractPersistentChannelsObject {

    /**
     * Model object reference as string.
     */
    @Column(length=2000)
    private String moRef;
    /**
     * Model object label.
     */
    @Column(length=1000)
    private String moLabel;
    
    private String moTypeName;

    public AbstractModelObjectReferencingPPO() {
    }

    public AbstractModelObjectReferencingPPO( String communityUri, String planUri, int planVersion, String username ) {
        super( communityUri, planUri, planVersion, username);
    }

    public AbstractModelObjectReferencingPPO(
            String communityUri,
            String planUri,
            int planVersion,
            String username,
            ModelObject modelObject ) {
        this( communityUri, planUri, planVersion, username );
        moRef = new ModelObjectRef( modelObject ).asString();
        moLabel = aboutLabel( modelObject );
        moTypeName = modelObject.getTypeName();
    }

    public String getMoRef() {
        return moRef;
    }

    public void setMoRef( ModelObject modelObject ) {
        moRef = new ModelObjectRef( modelObject ).asString();
        moLabel = aboutLabel( modelObject );
        moTypeName = modelObject.getTypeName();
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

    public String getMoTypeName() {
        return moTypeName;
    }

    public void setMoTypeName( String moTypeName ) {
        this.moTypeName = moTypeName;
    }

    private String aboutLabel( ModelObject mo ) {
        String description = "";
        if ( mo != null ) {
            description = mo.getKindLabel() + " \"" + mo.getLabel() + "\"";
            if ( mo instanceof SegmentObject ) {
                description += " in scenario \"" + ( (SegmentObject) mo ).getSegment().getLabel() + "\"";
            }        }
        return description;
    }

    public ModelObject getAbout( CommunityService communityService ) {
        ModelObjectRef aboutRef = getAboutRef();
        return aboutRef == null ? null : (ModelObject) aboutRef.resolve( communityService );
    }

    public ModelObjectRef getAboutRef() {
        return moRef == null ? null : ModelObjectRef.fromString( moRef );
    }
    
    public ModelObject getModelObject( CommunityService communityService ) {
        ModelObject mo = null;
        ModelObjectRef modelObjectRef = getAboutRef();
        if ( modelObjectRef != null ) {
            mo = (ModelObject)modelObjectRef.resolve( communityService );
        }
        return mo;
    }

    public Segment getSegment( CommunityService communityService ) {
        Segment segment = null;
        ModelObject mo = getModelObject( communityService );
        if ( mo != null && mo instanceof SegmentObject ){
            segment = ((SegmentObject)mo).getSegment();
        }
        return segment;
    }


}
