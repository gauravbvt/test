package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.Collator;
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
    private Updatable updatable;
    private WebMarkupContainer presencesContainer;

    public PlannerPresenceListPanel( String id, Updatable updatable ) {
        super( id );
        this.updatable = updatable;
        init();
    }
    private void init() {
        presencesContainer = new WebMarkupContainer( "presencesContainer" );
        presencesContainer.setOutputMarkupId( true );
        add( presencesContainer );
        addPresences();
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
        List<User> planners = userService.getPlanners( getPlan().getUri() );
        final Collator collator = Collator.getInstance();
        Collections.sort( planners, new Comparator<User>() {
            public int compare( User user1, User user2 ) {
                return collator.compare( user1.getNormalizedFullName(), user2.getNormalizedFullName() );
            }
        } );
        return planners;
    }

    public void refresh( AjaxRequestTarget target, Change change ) {
        addPresences();
        target.addComponent( presencesContainer );
    }
}
