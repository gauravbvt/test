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
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.social.PlanningEventService;
import com.mindalliance.channels.social.PresenceEvent;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

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
    private QueryService queryService;

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

    public abstract String getTime();

    public abstract String getLongTime();

    protected abstract void moreInit( WebMarkupContainer socialItemContainer );

    protected String getCssClasses() {
        String cssClasses = index % 2 == 0
                ? " even"
                : " odd";
        if ( index == 0 )
            cssClasses += " first";
        PresenceEvent presenceEvent = getLatestPresenceEvent();
        cssClasses += presenceEvent != null && presenceEvent.isLogin()
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
        menu.setVisible( !menu.isEmpty() );
        socialItemContainer.add( menu );
    }

    private void addPhoto( WebMarkupContainer socialItemContainer ) {
        WebMarkupContainer pic = new WebMarkupContainer( "pic" );
        pic.add( new AttributeModifier( "src", true, new PropertyModel( this, "photoUrl" ) ) );
        socialItemContainer.add( pic );
    }

    private void addName( WebMarkupContainer socialItemContainer ) {
        nameLabel = new Label( "name", new PropertyModel<String>( this, "userFullName" ) );
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

    public String getUserFullName() {
        if ( getUsername() == null )
            return "all planners";
        else
            return queryService.findUserFullName( getUsername() );
    }

    public String getJobTitles() {
        String jobTitles = "";
        Participation participation = getParticipation();
        if ( participation != null ) {
            Actor actor = participation.getActor();
            if ( actor != null ) {
                Iterator<Employment> employments = queryService.findAllEmploymentsForActor( actor ).iterator();
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
            url = "/images/actor.user.png";
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
                url = imagingService.getSquareIconUrl( actor );
        }
        if ( url == null ) {
            url = "/images/actor.user.png";
        }
        return url;
    }


    protected String getUsername() {
        return username;
    }

    public Participation getParticipation() {
        return queryService.findParticipation( getUsername() );
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public PlanningEventService getPlanningEventService() {
        return planningEventService;
    }

    protected Label getNameLabel() {
        return nameLabel;
    }

    public boolean isPresent() {
        PresenceEvent presenceEvent = getLatestPresenceEvent();
        return presenceEvent != null && presenceEvent.isLogin();
    }

    protected PresenceEvent getLatestPresenceEvent() {
        if ( latestPresenceEvent == null ) {
            latestPresenceEvent = getPlanningEventService().findLatestPresence( getUsername() );
        }
        return latestPresenceEvent;
    }


}
