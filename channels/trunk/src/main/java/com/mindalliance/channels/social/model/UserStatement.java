package com.mindalliance.channels.social.model;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.social.services.notification.Messageable;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract user statement.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/29/12
 * Time: 10:40 AM
 */
@Entity
abstract public class UserStatement extends AbstractModelObjectReferencingPPO implements Messageable {

    public static final String TEXT = "text";

    public static final String STATEMENT = "statement";

    @Column( length = 10000 )
    private String text;

    public UserStatement() {
    }

    public UserStatement( String username, PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
    }


    public UserStatement( String username, String text, PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
        this.text = text;
    }

    public UserStatement(
            String username,
            String text,
            ModelObject modelObject,
            PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username, modelObject );
        setText( text );
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = StringUtils.abbreviate( text, 10000 );
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


}
