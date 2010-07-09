package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.social.menus.SocialItemMenuPanel;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.social.PlanningEventService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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

    private String involvement;
    private String username;
    private Updatable updatable;

    public AbstractSocialEventPanel( String id, String username, Updatable updatable ) {
        this( id, "", username, updatable );
    }

    public AbstractSocialEventPanel( String id, String involvement, String username, Updatable updatable ) {
        super( id );
        this.involvement = involvement;
        this.username = username;
        this.updatable = updatable;
    }

    protected abstract String getCssClass();

    public abstract String getTime();

    public abstract String getLongTime();

    protected abstract void moreInit( WebMarkupContainer socialItemContainer );

    protected void init() {
        WebMarkupContainer socialItemContainer = new WebMarkupContainer( "socialItem" );
        String cssClasses = getCssClass();
        if ( getUsername() != null && getUsername().equals( User.current().getUsername() ) ) {
            cssClasses += " self";
        }
        socialItemContainer.add( new AttributeModifier( "class", true, new Model<String>( cssClasses ) ) );
        add( socialItemContainer );
        addMoreMenu( socialItemContainer );
        addPhoto( socialItemContainer );
        addInvolvement( socialItemContainer );
        addName( socialItemContainer );
        addJobTitles( socialItemContainer );
        addTime( socialItemContainer );
        moreInit( socialItemContainer );
    }

    private void addInvolvement( WebMarkupContainer socialItemContainer ) {
        Label involvementLabel = new Label( "involvement", involvement );
        involvementLabel.setVisible( !involvement.isEmpty() );
        socialItemContainer.add( involvementLabel );
    }

    private void addMoreMenu( WebMarkupContainer socialItemContainer ) {
        SocialItemMenuPanel menu = new SocialItemMenuPanel(
                "menu",
                new PropertyModel<Participation>( this, "participation" ),
                getUsername(),
                updatable );
        socialItemContainer.add( menu );
    }

    private void addPhoto( WebMarkupContainer socialItemContainer ) {
        WebMarkupContainer pic = new WebMarkupContainer( "pic" );
        pic.add( new AttributeModifier( "src", true, new PropertyModel( this, "photoUrl" ) ) );
        socialItemContainer.add( pic );
    }

    private void addName( WebMarkupContainer socialItemContainer ) {
        Label nameLabel = new Label( "name", new PropertyModel<String>( this, "userFullName" ) );
        socialItemContainer.add( nameLabel );
    }

    private void addJobTitles( WebMarkupContainer socialItemContainer ) {
        Label jobsLabel = new Label( "titles", new PropertyModel<String>( this, "jobTitles" ) );
        socialItemContainer.add( jobsLabel );
    }

    private void addTime( WebMarkupContainer socialItemContainer ) {
        String time = getTime();
        String timeLabelString = "";
        if ( !time.isEmpty() ) {
            timeLabelString = "(" + time + ")";
        }
        Label timeLabel = new Label( "time", new Model<String>( timeLabelString ) );
        if ( !timeLabelString.isEmpty() ) {
            timeLabel.add( new AttributeModifier(
                    "title",
                    true,
                    new PropertyModel<String>( this, "longTime" ) ) );
        }
        socialItemContainer.add( timeLabel );
    }

    public String getUserFullName() {
        if ( getUsername() == null )
            return "All";
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
                : "(" + jobTitles + ")";
    }

    public String getPhotoUrl() {
        String url = null;
        Participation participation = getParticipation();
        if ( participation != null ) {
            Actor actor = participation.getActor();
            if ( actor != null )
                url = actor.getImageUrl();
        }
        if ( url == null ) {
            url = "/images/actor.png";
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


}
