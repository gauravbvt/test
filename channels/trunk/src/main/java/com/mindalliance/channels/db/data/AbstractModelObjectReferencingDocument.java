package com.mindalliance.channels.db.data;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/25/13
 * Time: 2:21 PM
 */
public class AbstractModelObjectReferencingDocument extends AbstractChannelsDocument {
    /**
     * Model object reference as string.
     */
    private String moRef;
    /**
     * Model object label.
     */
    private String moLabel;

    private String moTypeName;

    public AbstractModelObjectReferencingDocument() {
    }

    public AbstractModelObjectReferencingDocument( String communityUri,
                                                   String planUri,
                                                   int planVersion,
                                                   String username ) {
        super( communityUri, planUri, planVersion, username );
    }

    public AbstractModelObjectReferencingDocument(
            String communityUri,
            String planUri,
            int planVersion,
            String username,
            ModelObject modelObject ) {
        this( communityUri, planUri, planVersion, username );
        setMoRef( new ModelObjectRef( modelObject ).asString() );
        setMoLabel( aboutLabel( modelObject ) );
        moTypeName = modelObject.getTypeName();
    }

    public String getMoRef() {
        return moRef;
    }

    public void setMoRef( ModelObject modelObject ) {
        String aMoRef = new ModelObjectRef( modelObject ).asString();
        moRef = aMoRef;
        setMoLabel( aboutLabel( modelObject ) );
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
            }
        }
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
            mo = (ModelObject) modelObjectRef.resolve( communityService );
        }
        return mo;
    }

    public Segment getSegment( CommunityService communityService ) {
        Segment segment = null;
        ModelObject mo = getModelObject( communityService );
        if ( mo != null && mo instanceof SegmentObject ) {
            segment = ( (SegmentObject) mo ).getSegment();
        }
        return segment;
    }

    @Override
    public String getKindLabel() {
        return getTypeName();
    }


}
