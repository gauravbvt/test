package com.mindalliance.channels.db.data.messages;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.db.data.AbstractModelObjectReferencingDocument;
import com.mindalliance.channels.social.services.notification.Messageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/25/13
 * Time: 2:18 PM
 */
public abstract class UserStatement extends AbstractModelObjectReferencingDocument implements Messageable {

    public static final String TEXT = "text";

    public static final String STATEMENT = "statement";

    private String text;

    public UserStatement() {
    }

    public UserStatement( String username, PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getModelUri(), planCommunity.getModelVersion(), username );
    }


    public UserStatement( String username, String text, PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getModelUri(), planCommunity.getModelVersion(), username );
        this.text = text;
    }

    public UserStatement(
            String username,
            String text,
            ModelObject modelObject,
            PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getModelUri(), planCommunity.getModelVersion(), username, modelObject );
        setText( text );
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }


    /// Messageable

    abstract public String getToUsername( String topic );

    @Override
    public List<String> getToUserNames( String topic, CommunityService communityService ) {
        List<String> usernames = new ArrayList<String>();
        usernames.add(  getToUsername(  topic  ) );
        return usernames;
    }

    @Override
    public String getFromUsername( String topic ) {
        return getUsername();
    }

    @Override
    public String getContent(
            String topic,
            Format format,
            CommunityService communityService ) {
        if ( topic.equals( TEXT ) ) return getTextContent( format, communityService );
        else throw new RuntimeException( "invalid content" );
    }

    @Override
    public String getSubject(
            String topic,
            Format format,
            CommunityService communityService ) {
        if ( topic.equals( TEXT ) ) return getTextSubject( format, communityService  );
        else throw new RuntimeException( "invalid content" );
    }

    @Override
    public String getLabel() {
        return "Message";
    }


    abstract protected String getTextContent( Format format, CommunityService communityService );


    abstract protected String getTextSubject( Format format, CommunityService communityService );


    public String messageContent() {
        return getText();
    }
}
