package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;
import com.mindalliance.channels.social.services.notification.Messageable;

import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

/**
 * Community planner data.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/19/13
 * Time: 10:05 AM
 */
// @Entity
public class CommunityPlanner extends AbstractPersistentChannelsObject implements Messageable {

    public static final String AUTHORIZED_AS_PLANNER = "authorized as community planner";

    @ManyToOne
    private ChannelsUserInfo userInfo;

    private boolean userNotified;

    public CommunityPlanner() {
    }

    public CommunityPlanner(
            String username,
            ChannelsUser communityPlanner,
            PlanCommunity planCommunity
    ) {
        super( planCommunity, username );
       // userInfo = communityPlanner.getUserInfo();
    }

    public ChannelsUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo( ChannelsUserInfo userInfo ) {
        this.userInfo = userInfo;
    }

    public boolean isUserNotified() {
        return userNotified;
    }

    public void setUserNotified( boolean userNotified ) {
        this.userNotified = userNotified;
    }

    @Override
    public String getLabel() {
        return "Community planner";
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof CommunityPlanner ) {
            CommunityPlanner other = (CommunityPlanner) object;
            return userInfo.equals( other.getUserInfo() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + userInfo.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "Community planner " + userInfo.getUsername() + " in " + getCommunityUri();
    }

    ////////// MESSAGEABLE

    @Override
    public String getContent( String topic, Format format, CommunityService communityService ) {
        if ( topic.equals( AUTHORIZED_AS_PLANNER ) ) {
            return "You have been promoted to planner status in community \""
                    + communityService.getPlanCommunity().getName()
                    + "\".";
        } else {
            throw new RuntimeException( "Unknown topic " + topic );
        }
    }

    @Override
    public List<String> getToUserNames( String topic, CommunityService communityService ) {
        if ( topic.equals( AUTHORIZED_AS_PLANNER ) ) {
            List<String> usernames = new ArrayList<String>();
            usernames.add( userInfo.getUsername() );
            return usernames;
        } else {
            throw new RuntimeException( "Unknown topic " + topic );
        }
    }

    @Override
    public String getFromUsername( String topic ) {
        return null;
    }

    @Override
    public String getSubject( String topic, Format format, CommunityService communityService ) {
        if ( topic.equals( AUTHORIZED_AS_PLANNER ) ) {
            return "Promotion to community planner";
        } else {
            throw new RuntimeException( "Unknown topic " + topic );
        }
    }

}
