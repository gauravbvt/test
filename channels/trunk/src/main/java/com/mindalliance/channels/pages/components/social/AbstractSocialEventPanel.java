package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.imaging.ImagingService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.odb.PersistentObject;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.social.menus.SocialItemMenuPanel;
import com.mindalliance.channels.social.PlannerMessagingService;
import com.mindalliance.channels.social.PlanningEventService;
import com.mindalliance.channels.social.PresenceEvent;
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
    private PlanningEventService planningEventService;

    @SpringBean
    private ImagingService imagingService;

    private PresenceEvent latestPresenceEvent = null;

    private String username;
    private int index;
    private IModel<? extends PersistentObject> poModel;
    private Updatable updatable;

    private Label nameLabel;

    public AbstractSocialEventPanel( String id, String username, int index, Updatable updatable ) {
        this( id, username, index, null, updatable );
    }

    public AbstractSocialEventPanel(
            String id,
            String username,
            int index,
            IModel<? extends PersistentObject> poModel,
            Updatable updatable ) {
        super( id );
        this.username = username;
        this.index = index;
        this.poModel = poModel;
        this.updatable = updatable;
    }


    public abstract Date getDate();

    protected abstract void moreInit( WebMarkupContainer socialItemContainer );

    protected String getCssClasses() {
        String cssClasses = index % 2 == 0
                ? " even"
                : " odd";
        if ( index == 0 )
            cssClasses += " first";
        PresenceEvent presenceEvent = getLatestPresenceEvent();
        cssClasses += presenceEvent != null && presenceEvent.isEntering()
                ? " joining"
                : " leaving";
        return cssClasses;
    }

    protected void init() {
        WebMarkupContainer socialItemContainer = new WebMarkupContainer( "socialItem" );
        String cssClasses = getCssClasses();
        if ( getUsername() != null && getUsername().equals( User.current().getUsername() ) ) {
            cssClasses += " self";
        }
        if ( !getJobTitles().isEmpty() ) {
            cssClasses += " employed";
        }
        socialItemContainer.add( new AttributeModifier( "class", true, new Model<String>( cssClasses ) ) );
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
                new PropertyModel<Participation>( this, "participation" ),
                getUsername(),
                poModel,
                updatable );
        menu.setVisible( !menu.isEmpty() && isPlanner() );
        socialItemContainer.add( menu );
    }

    private boolean isPlanner() {
        return User.current().isPlanner();
    }

    private void addPhoto( WebMarkupContainer socialItemContainer ) {
        WebMarkupContainer pic = new WebMarkupContainer( "pic" );
        pic.add( new AttributeModifier( "src", true, new PropertyModel( this, "photoUrl" ) ) );
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
        icon.setVisible( isPresent() );
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
            String userName = getUsername();
            if ( username == null ) {
                return "?";
            } else if ( username.equals( PlannerMessagingService.PLANNERS ) ) {
                return "All planners";
            } else if ( username.equals( PlannerMessagingService.USERS ) ) {
                return "Everyone";
            } else {
                String name = getQueryService().findUserFullName( userName );
                String userRole = getQueryService().findUserRole( userName );
                if ( name == null || userRole == null )
                    return getUsername() + " (removed)";
                else
                    return name
                            + (
                            userRole.equals( User.PLANNER )
                                    ? " (planner)"
                                    : userRole.equals( User.ADMIN )
                                    ? " (admin)"
                                    : ""
                    );
            }
        }
    }

    public String getJobTitles() {
        String jobTitles = "";
        Participation participation = getParticipation();
        if ( participation != null ) {
            Actor actor = participation.getActor();
            if ( actor != null ) {
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
                jobTitles = sb.toString();
            }
        }
        return jobTitles.isEmpty()
                ? ""
                : jobTitles;
    }

/*
    public String getPhotoUrl() {
        String url = null;
        Participation participation = getParticipation();
        if ( participation != null ) {
            Actor actor = participation.getActor();
            if ( actor != null )
                url = actor.getImageUrl();
        }
        if ( url == null ) {
            url = "images/actor.user.png";
        }
        return url;
    }
*/

    public String getPhotoUrl() {
        String url = null;
        Participation participation = getParticipation();
        if ( participation != null ) {
            Actor actor = participation.getActor();
            if ( actor != null )
                url = imagingService.getSquareIconUrl( getPlan(), actor );
        }
        return url == null ? "images/actor.user.png" : url;
    }


    protected String getUsername() {
        return username;
    }

    public Participation getParticipation() {
        return getQueryService().findParticipation( getUsername() );
    }

    public PlanningEventService getPlanningEventService() {
        return planningEventService;
    }

    protected Label getNameLabel() {
        return nameLabel;
    }

    public boolean isPresent() {
        PresenceEvent presenceEvent = getLatestPresenceEvent();
        return presenceEvent != null && presenceEvent.isEntering();
    }

    protected PresenceEvent getLatestPresenceEvent() {
        if ( latestPresenceEvent == null ) {
            latestPresenceEvent = getPlanningEventService().findLatestPresence( getUsername(), getPlan() );
        }
        return latestPresenceEvent;
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


}
