package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.UserUploadService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.PersistentPlanObject;
import com.mindalliance.channels.db.data.activities.PresenceRecord;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.activities.PresenceRecordService;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.social.menus.SocialItemMenuPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2010
 * Time: 12:42:23 PM
 */
public abstract class AbstractSocialEventPanel extends AbstractUpdatablePanel {

    @SpringBean
    private PresenceRecordService presenceRecordService;

    @SpringBean
    private UserUploadService userUploadService;

    @SpringBean
    private UserRecordService userInfoService;
    
    @SpringBean
    private UserParticipationService userParticipationService;

    private PresenceRecord latestPresenceRecord = null;

    private int index;
    private IModel<? extends PersistentPlanObject> poModel;
    private boolean allowMessageDelete;
    private Updatable updatable;

    private Label nameLabel;
    private boolean showProfile;

    public AbstractSocialEventPanel( String id, int index, boolean showProfile, Updatable updatable ) {
        this( id, index, null, showProfile, updatable );
    }

    public AbstractSocialEventPanel(
            String id,
            int index,
            IModel<? extends PersistentPlanObject> poModel,
            boolean showProfile,
            Updatable updatable ) {
        this( id, index, poModel, showProfile, true, updatable );
    }


    public AbstractSocialEventPanel(
            String id,
            int index,
            IModel<? extends PersistentPlanObject> poModel,
            boolean showProfile,
            boolean allowMessageDelete,
            Updatable updatable ) {
        super( id );
        this.index = index;
        this.poModel = poModel;
        this.showProfile = showProfile;
        this.allowMessageDelete = allowMessageDelete;
        this.updatable = updatable;
    }


    public abstract Date getDate();

    protected abstract void moreInit( WebMarkupContainer socialItemContainer );

    protected String getPersistentPlanObjectUsername(  ) {
        return poModel.getObject().getUsername( );
    }

    protected String getCssClasses() {
        String cssClasses = index % 2 == 0
                ? " even"
                : " odd";
        if ( index == 0 )
            cssClasses += " first";
        PresenceRecord presenceRecord = getLatestPresenceRecord( getUsername() );
        cssClasses += presenceRecord != null && presenceRecord.isEntering()
                ? " joining"
                : " leaving";
        return cssClasses;
    }

    protected void init() {
        WebMarkupContainer socialItemContainer = new WebMarkupContainer( "socialItem" );
        String cssClasses = getCssClasses();
        if ( getUsername() != null && getUsername().equals( getUser().getUsername() ) ) {
            cssClasses += " self";
        }
        if ( !getJobTitles().isEmpty() ) {
            cssClasses += " employed";
        }
        socialItemContainer.add( new AttributeModifier( "class", new Model<String>( cssClasses ) ) );
        add( socialItemContainer );
        addMoreMenu( socialItemContainer );
        addPhoto( socialItemContainer );
        addName( socialItemContainer );
        addJobTitles( socialItemContainer );
        addIcon( socialItemContainer );
        moreInit( socialItemContainer );
    }

    private void addMoreMenu( WebMarkupContainer socialItemContainer ) {
        SocialItemMenuPanel menu = new SocialItemMenuPanel(
                "menu",
                new PropertyModel<UserRecord>( this, "userInfo" ),
                getUsername(),
                poModel,
                showProfile,
                allowMessageDelete,
                updatable );
        // menu.setVisible( !menu.isEmpty() && isPlanner() /*&& !getUsername().equals( this.getPersistentPlanObjectUsername() ) */);
        makeVisible( menu, !menu.isEmpty() && isPlanner() );
        socialItemContainer.add( menu );
    }

    private void addPhoto( WebMarkupContainer socialItemContainer ) {
        WebMarkupContainer pic = new WebMarkupContainer( "pic" );
        pic.add( new AttributeModifier( "src", new PropertyModel( this, "photoUrl" ) ) );
        socialItemContainer.add( pic );
    }

    private void addName( WebMarkupContainer socialItemContainer ) {
        nameLabel = new Label( "name", new PropertyModel<String>( this, "userFullNameAndRole" ) );
        socialItemContainer.add( nameLabel );
    }

    private void addJobTitles( WebMarkupContainer socialItemContainer ) {
        String jobTitles = getJobTitles();
        Label jobsLabel = new Label( "titles", new Model<String>( jobTitles ) );
        jobsLabel.setVisible( !jobTitles.isEmpty() );
        socialItemContainer.add( jobsLabel );
    }

    private void addIcon( WebMarkupContainer socialItemContainer ) {
        WebMarkupContainer icon = new WebMarkupContainer( "icon" );
        icon.setVisible( isPresent( getPersistentPlanObjectUsername(  ) ) );
        socialItemContainer.add( icon );
    }

    public String getTime() {
        return getShortTimeElapsedString( getDate() );
    }

    public String getLongTime() {
        return getLongTimeElapsedString( getDate() );
    }

    public String getUserFullNameAndRole() {
        if ( getUsername() == null )
            return "all users";
        else {
            String poUserName = getPersistentPlanObjectUsername(  );
            if ( poUserName == null ) {
                return "?";
            } else if ( poUserName.equals( UserRecord.PLANNERS ) ) {
                return "All modelers";
            } else if ( poUserName.equals( UserRecord.USERS ) ) {
                return "Everyone";
            } else {
                String name = getQueryService().findUserFullName( poUserName );
                String userRole = getQueryService().findUserRole( poUserName );
                if ( name == null || userRole == null )
                    return poUserName + " (removed)";
                else
                    return name
                            + (
                            userRole.equals( ChannelsUser.PLANNER )
                                    ? " (modeler)"
                                    : userRole.equals( ChannelsUser.ADMIN )
                                    ? " (admin)"
                                    : ""
                    );
            }
        }
    }

    public String getJobTitles() {
        StringBuilder sb = new StringBuilder(  );
        CommunityService communityService = getCommunityService();
        List<UserParticipation> participations = userParticipationService.getActiveUserParticipations(
                getUser(),
                communityService
        );
        for ( UserParticipation participation : participations ) {
            Agent agent = participation.getAgent( communityService );
            if ( agent != null ) {
                String s = agent.getName();
                sb.append( s );
                if ( !s.isEmpty() ) sb.append( ". " );
            }
        }
        return sb.toString();
    }
    
    public String getActorJobTitles( Actor actor ) {
        Iterator<Employment> employments = getQueryService().findAllEmploymentsForActor( actor ).iterator();
        StringBuilder sb = new StringBuilder();
        Set<String> titleSet = new HashSet<String>();
        while ( employments.hasNext() ) {
            Employment employment = employments.next();
            String title = employment.getJob().getTitle();
            if ( title.isEmpty() ) title = employment.getJob().getRoleName();
            if ( !titleSet.contains( title ) ) {
                titleSet.add( title );
                if ( sb.length() > 0 ) sb.append( ", " );
                sb.append( ( title == null || title.isEmpty() ) ? "working" : title );
                sb.append( " at " );
                sb.append( employment.getOrganization().getName() );
            }
        }
        return sb.toString();
    }


    public String getPhotoUrl() {
        String src = null;
        ChannelsUser user = getSocialEventUser();
        if ( user != null ) {
            src = userUploadService.getSquareUserIconURL( user );
        }
        return src == null ? "images/actor.user.png" : src;
    }

    public UserRecord getUserInfo() {
        ChannelsUser user = userInfoService.getUserWithIdentity( getPersistentPlanObject().getUsername() );
        return user == null ? null : user.getUserRecord();
    }

    public PresenceRecordService getPresenceRecordService() {
        return presenceRecordService;
    }

    protected Label getNameLabel() {
        return nameLabel;
    }

    public boolean isPresent( String username ) {
        PresenceRecord presenceRecord = getLatestPresenceRecord( username );
        return presenceRecord != null && presenceRecord.isEntering();
    }

    protected PresenceRecord getLatestPresenceRecord( String username ) {
        if ( latestPresenceRecord == null ) {
            latestPresenceRecord = getPresenceRecordService().findLatestPresence(
                    username,
                    planVersionUri() );
        }
        return latestPresenceRecord;
    }

    public String getShortTimeElapsedString( Date date ) {
        if ( date == null )
            return "";
        else
            return ChannelsUtils.getShortTimeIntervalString( new Date( ).getTime() - date.getTime() ) + " ago";
    }

    public String getLongTimeElapsedString( Date date ) {
        if ( date == null )
            return "";
        else
            return ChannelsUtils.getLongTimeIntervalString( new Date().getTime() - date.getTime() ) + " ago";
    }

    protected PersistentPlanObject getPersistentPlanObject() {
        return poModel.getObject();
    }

    protected ChannelsUser getSocialEventUser() {
        return userInfoService.getUserWithIdentity( getPersistentPlanObjectUsername() );
    }

}
