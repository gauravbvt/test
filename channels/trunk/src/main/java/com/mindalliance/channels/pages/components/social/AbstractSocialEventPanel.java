package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.orm.model.PersistentPlanObject;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.social.menus.SocialItemMenuPanel;
import com.mindalliance.channels.social.model.PresenceRecord;
import com.mindalliance.channels.social.services.PresenceRecordService;
import com.mindalliance.channels.social.services.UserMessageService;
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
    private ImagingService imagingService;

    @SpringBean
    private ChannelsUserDao userDao;
    
    @SpringBean
    private PlanParticipationService planParticipationService;

    private PresenceRecord latestPresenceRecord = null;

    private int index;
    private IModel<? extends PersistentPlanObject> poModel;
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
        super( id );
        this.index = index;
        this.poModel = poModel;
        this.showProfile = showProfile;
        this.updatable = updatable;
    }


    public abstract Date getDate();

    protected abstract void moreInit( WebMarkupContainer socialItemContainer );

    protected String getPersistentPlanObjectUsername() {
        return poModel.getObject().getUsername();
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
                new PropertyModel<ChannelsUserInfo>( this, "userInfo" ),
                getUsername(),
                poModel,
                showProfile,
                updatable );
        menu.setVisible( !menu.isEmpty() && isPlanner() );
        socialItemContainer.add( menu );
    }

    private boolean isPlanner() {
        return getUser().isPlanner();
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
        icon.setVisible( isPresent( getPersistentPlanObjectUsername() ) );
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
            String poUserName = getPersistentPlanObjectUsername();
            if ( poUserName == null ) {
                return "?";
            } else if ( poUserName.equals( UserMessageService.PLANNERS ) ) {
                return "All planners";
            } else if ( poUserName.equals( UserMessageService.USERS ) ) {
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
                                    ? " (planner)"
                                    : userRole.equals( ChannelsUser.ADMIN )
                                    ? " (admin)"
                                    : ""
                    );
            }
        }
    }

    public String getJobTitles() {
        StringBuilder sb = new StringBuilder(  );
        QueryService queryService = getQueryService();
        List<PlanParticipation> participations = planParticipationService.getParticipations(
                getPlan(),
                getUserInfo(),
                queryService
        );
        for ( PlanParticipation participation : participations ) {
            Actor actor = participation.getActor( queryService );
            if ( actor != null ) {
                String s = getActorJobTitles( actor );
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
        String url = null;
        ChannelsUserInfo userInfo = getUserInfo();
        if ( userInfo != null ) {
            Actor actor = findActor( userInfo );
            if ( actor != null )
                url = imagingService.getSquareIconUrl( getPlan(), actor );
        }
        return url == null ? "images/actor.user.png" : url;
    }

    public ChannelsUserInfo getUserInfo() {
        ChannelsUser user = userDao.getUserNamed( getPersistentPlanObject().getUsername() );
        return user == null ? null : user.getUserInfo();
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
        if ( date == null ) return "";
        Date end = new Date();
        long diffInSeconds = ( end.getTime() - date.getTime() ) / 1000;
        /* sec */
        long seconds = ( diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds );
        /* min */
        long minutes = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* hours */
        long hours = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        /* days */
        long days = diffInSeconds / 24;

        StringBuilder sb = new StringBuilder();
        if ( days > 0 ) {
            sb.append( days );
            sb.append( " day" );
            sb.append( days > 1 ? "s" : "" );
        }
        if ( hours > 0 ) {
            if ( sb.length() == 0 ) {
                sb.append( hours );
                sb.append( " hour" );
                sb.append( hours > 1 ? "s" : "" );
            }
        }
        if ( minutes > 0 ) {
            if ( sb.length() == 0 ) {
                sb.append( minutes );
                sb.append( " minute" );
                sb.append( minutes > 1 ? "s" : "" );
            }
        }
        if ( sb.length() == 0 ) {
            sb.append( seconds );
            sb.append( " second" );
            sb.append( seconds > 1 ? "s" : "" );
        }
        sb.append( " ago" );
        return sb.toString();
    }

    public String getLongTimeElapsedString( Date date ) {
        if ( date == null ) return "";
        Date end = new Date();
        long diffInSeconds = ( end.getTime() - date.getTime() ) / 1000;
        /* sec */
        long seconds = ( diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds );
        /* min */
        long minutes = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* hours */
        long hours = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        /* days */
        long days = diffInSeconds / 24;

        StringBuilder sb = new StringBuilder();
        if ( days > 0 ) {
            sb.append( days );
            sb.append( " day" );
            sb.append( days > 1 ? "s" : "" );
        }
        if ( hours > 0 ) {
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( hours );
            sb.append( " hour" );
            sb.append( hours > 1 ? "s" : "" );
        }
        if ( minutes > 0 ) {
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( minutes );
            sb.append( " minute" );
            sb.append( minutes > 1 ? "s" : "" );
        }
        if ( sb.length() == 0 || seconds > 0 ) {
            if ( sb.length() > 0 ) sb.append( ", " );
            sb.append( seconds );
            sb.append( " second" );
            sb.append( seconds > 1 ? "s" : "" );
        }
        sb.append( " ago" );
        return sb.toString();
    }

    protected PersistentPlanObject getPersistentPlanObject() {
        return poModel.getObject();
    }


}
