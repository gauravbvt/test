package com.mindalliance.channels.social;

import com.mindalliance.channels.command.ModelObjectRef;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.odb.PersistentObject;
import com.mindalliance.channels.query.QueryService;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2010
 * Time: 2:04:30 PM
 */
public class PlannerMessage extends PersistentObject {

    private String planId;
    private String fromUsername;
    // broadcast if null
    private String toUsername;
    private String text = "";
    private ModelObjectRef aboutRef;
    private String aboutString = "";
    private boolean emailed = false;

    public PlannerMessage( String text, String planUri ) {
        super();
        User user = User.current();
        this.text = text;
        fromUsername = user.getUsername();
        planId = planUri;
    }

    public PlannerMessage( String text, ModelObject modelObject, String planUri ) {
        this( text, planUri );
        aboutRef = new ModelObjectRef( modelObject );
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId( String planId ) {
        this.planId = planId;
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
