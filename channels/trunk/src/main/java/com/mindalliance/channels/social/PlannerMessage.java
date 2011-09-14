package com.mindalliance.channels.social;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.engine.query.QueryService;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2010
 * Time: 2:04:30 PM
 */
public class PlannerMessage extends AbstractPersistentObject {

    private String urn;
    private String fromUsername;
    // broadcast if null
    private String toUsername;
    private String text = "";
    private ModelObjectRef aboutRef;
    private String aboutString = "";
    private boolean emailed = false;

    public PlannerMessage( String text, String urn ) {
        super();
        User user = User.current();
        this.text = text;
        fromUsername = user.getUsername();
        this.urn = urn;
    }

    public PlannerMessage( String text, ModelObject modelObject, String urn ) {
        this( text, urn );
        aboutRef = new ModelObjectRef( modelObject );
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn( String urn ) {
        this.urn = urn;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername( String toUsername ) {
        this.toUsername = toUsername;
    }

    public String getText() {
        return text;
    }

    public ModelObjectRef getAboutRef() {
        return aboutRef;
    }

    public boolean isBroadcast() {
        User current = User.current();
        return toUsername == null // legacy - all planners
                || toUsername.equals( PlannerMessagingService.PLANNERS ) && current.isPlanner()
                || toUsername.equals( PlannerMessagingService.USERS );
    }

    public void setAbout( ModelObject modelObject ) {
        aboutRef = new ModelObjectRef( modelObject );
        aboutString = aboutDescription( modelObject );
    }

    private String aboutDescription( ModelObject mo ) {
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
        return aboutRef == null ? null : (ModelObject) aboutRef.resolve( queryService );
    }

    public String getAboutString() {
        return aboutString;
    }

    public void setAboutString( String aboutString ) {
        this.aboutString = aboutString;
    }

    public boolean isEmailed() {
        return emailed;
    }

    public void setEmailed( boolean emailed ) {
        this.emailed = emailed;
    }
}
