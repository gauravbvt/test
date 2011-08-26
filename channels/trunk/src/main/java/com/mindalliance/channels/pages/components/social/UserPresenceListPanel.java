package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.engine.command.Change;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserService;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.social.PlanningEventService;
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
public class UserPresenceListPanel extends AbstractSocialListPanel {

    @SpringBean
    UserService userService;

    @SpringBean
    private PlanningEventService planningEventService;

    private Updatable updatable;
    private WebMarkupContainer presencesContainer;
    private boolean showHereOnly = true;
    private AjaxFallbackLink showHideLink;
    private Label showHideLabel;

    public UserPresenceListPanel( String id, Updatable updatable, boolean collapsible ) {
        super( id, collapsible );
        this.updatable = updatable;
        init();
    }

    protected void init() {
        super.init();
        addShowHideLink();
        addShowHideLabel();
        presencesContainer = new WebMarkupContainer( "presencesContainer" );
        presencesContainer.setOutputMarkupId( true );
        add( presencesContainer );
        addPresences();
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
                showHereOnly ? "show all users" : "hide inactive users" );
        showHideLabel.setOutputMarkupId( true );
        showHideLink.addOrReplace( showHideLabel );
    }

    private void addPresences() {
        ListView<User> presenceList = new ListView<User>(
                "presences",
                getUsersPresent() ) {
            protected void populateItem( ListItem<User> item ) {
                User planner = item.getModelObject();
                UserPresencePanel presencePanel = new UserPresencePanel(
                        "presence",
                        planner.getUsername(),
                        item.getIndex(),
                        updatable );
                item.add( presencePanel );
            }
        };
        presencesContainer.addOrReplace( presenceList );
    }

    public List<User> getUsersPresent() {
        List<User> usersPresent = new ArrayList<User>();
        for ( User user : userService.getUsers( getPlan().getUri() ) ) {
            if ( !showHereOnly || isHere( user.getUsername() ) ) {
                usersPresent.add( user );
            }
        }
        final Collator collator = Collator.getInstance();
        Collections.sort( usersPresent, new Comparator<User>() {
            public int compare( User user1, User user2 ) {
                return collator.compare( user1.getNormalizedFullName(), user2.getNormalizedFullName() );
            }
        } );
        return usersPresent;
    }

    private boolean isHere( String username ) {
        return planningEventService.isActive( username, getPlan() );
    }

    public void refresh( AjaxRequestTarget target, Change change ) {
        addPresences();
        target.addComponent( presencesContainer );
    }
}
