package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.PlanningEventService;
import com.mindalliance.channels.social.PresenceEvent;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Planner presence list panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 5, 2010
 * Time: 1:29:02 PM
 */
public class PlannerPresenceListPanel extends AbstractUpdatablePanel {

    @SpringBean
    UserService userService;

    @SpringBean
    PlanningEventService planningEventService;

    private Updatable updatable;
    private WebMarkupContainer presencesContainer;
    private boolean showHereOnly = false;
    private AjaxFallbackLink showHideLink;
    private Label showHideLabel;

    public PlannerPresenceListPanel( String id, Updatable updatable ) {
        super( id );
        this.updatable = updatable;
        init();
    }

    private void init() {
        addHideSocial();
        addShowHideLink();
        addShowHideLabel();
        presencesContainer = new WebMarkupContainer( "presencesContainer" );
        presencesContainer.setOutputMarkupId( true );
        add( presencesContainer );
        addPresences();
    }

    private void addHideSocial() {
        AjaxFallbackLink hideSocialLink = new AjaxFallbackLink( "hideAll" ) {
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Collapsed, Channels.SOCIAL_ID ) );
            }
        };
        add( hideSocialLink );
    }

    private void addShowHideLink() {
        showHideLink = new AjaxFallbackLink( "hideShowLink" ) {
            public void onClick( AjaxRequestTarget target ) {
                showHereOnly = !showHereOnly;
                addShowHideLabel();
                target.addComponent( showHideLabel );
                addPresences();
                target.addComponent( presencesContainer );
            }
        };
        add( showHideLink );
    }

    private void addShowHideLabel() {
        showHideLabel = new Label(
                "hideShow",
                showHereOnly ? "show all users" : "hide inactive users"  );
        showHideLabel.setOutputMarkupId( true );
        showHideLink.addOrReplace( showHideLabel );
    }

    private void addPresences() {
        ListView<User> presenceList = new ListView<User>(
                "presences",
                getPlanners() ) {
            protected void populateItem( ListItem<User> item ) {
                User planner = item.getModelObject();
                PlannerPresencePanel presencePanel = new PlannerPresencePanel(
                        "presence",
                        planner.getUsername(),
                        updatable );
                item.add( presencePanel );
            }
        };
        presencesContainer.addOrReplace( presenceList );
    }

    public List<User> getPlanners() {
        List<User> planners = new ArrayList<User>();
        for ( User user : userService.getPlanners( getPlan().getUri() ) ) {
            if ( !showHereOnly || isHere( user.getUsername() ) ) {
                planners.add( user );
            }
        }
        final Collator collator = Collator.getInstance();
        Collections.sort( planners, new Comparator<User>() {
            public int compare( User user1, User user2 ) {
                return collator.compare( user1.getNormalizedFullName(), user2.getNormalizedFullName() );
            }
        } );
        return planners;
    }

    private boolean isHere( String username ) {
        PresenceEvent presenceEvent = planningEventService.findLatestPresence( username );
        return presenceEvent != null && presenceEvent.isLogin();
    }

    public void refresh( AjaxRequestTarget target, Change change ) {
        addPresences();
        target.addComponent( presencesContainer );
    }
}
