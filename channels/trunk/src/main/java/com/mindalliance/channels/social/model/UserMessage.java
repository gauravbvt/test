package com.mindalliance.channels.social.model;

import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.social.services.UserMessageService;

import javax.persistence.Entity;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/12
 * Time: 11:28 AM
 */
@Entity
public class UserMessage extends AbstractPersistentPlanObject {

    private String toUsername;
    private String text = "";
    private String about;
    private String aboutString = "";
    private boolean emailed = false;
    
    public UserMessage() {} 

    public UserMessage( String planUri, String username, String text ) {
        super( planUri, username);
        this.text = text;
    }

    public UserMessage( String planUri, String username, String text, ModelObject modelObject ) {
        this( planUri, username, text );
        about = new ModelObjectRef( modelObject ).asString();
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
    
    public void setText( String text ) {
        this.text = text;
    }
    
    public ModelObjectRef getAboutRef() {
        return about == null ? null : ModelObjectRef.fromString( about );
    }

    public boolean isBroadcast( ChannelsUser currentUser ) {
        return toUsername == null // legacy - all planners
                || toUsername.equals( UserMessageService.PLANNERS ) && currentUser.isPlanner()
                || toUsername.equals( UserMessageService.USERS );
    }

    public void setAbout( ModelObject modelObject ) {
        about = new ModelObjectRef( modelObject ).asString();
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
        ModelObjectRef aboutRef = getAboutRef();
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

    public String getFromUsername() {
        return getUsername();
    }
}
