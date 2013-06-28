package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.activities.PresenceRecordService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.Updatable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
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
    UserRecordService userInfoService;

    @SpringBean
    private PresenceRecordService presenceRecordService;

    private Updatable updatable;
    private boolean showProfile;
    private WebMarkupContainer presencesContainer;
    private boolean showHereOnly = true;
    private AjaxLink showHideLink;
    private Label showHideLabel;

    public UserPresenceListPanel( String id, Updatable updatable, boolean collapsible, boolean showProfile ) {
        super( id, collapsible );
        this.updatable = updatable;
        this.showProfile = showProfile;
        init();
    }

    @Override
    public String getHelpSectionId() {
        return null;  // Todo
    }

    @Override
    public String getHelpTopicId() {
        return null;  // Todo
    }

    protected void init() {
        super.init();
        add( makeHelpIcon( "helpPresence", "planners", "presence", "images/help_guide_gray.png" ) );
        addShowHideLink();
        addShowHideLabel();
        presencesContainer = new WebMarkupContainer( "presencesContainer" );
        presencesContainer.setOutputMarkupId( true );
        add( presencesContainer );
        addPresences();
    }

    private void addShowHideLink() {
        showHideLink = new AjaxLink( "hideShowLink" ) {
            public void onClick( AjaxRequestTarget target ) {
                showHereOnly = !showHereOnly;
                addShowHideLabel();
                target.add( showHideLabel );
                addPresences();
                target.add( presencesContainer );
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
        ListView<ChannelsUser> presenceList = new ListView<ChannelsUser>(
                "presences",
                getUsersPresent() ) {
            protected void populateItem( ListItem<ChannelsUser> item ) {
                ChannelsUser planner = item.getModelObject();
                UserPresencePanel presencePanel = new UserPresencePanel(
                        "presence",
                        new Model<UserRecord>( planner.getUserRecord() ),
                        item.getIndex(),
                        showProfile,
                        updatable );
                item.add( presencePanel );
            }
        };
        presencesContainer.addOrReplace( presenceList );
    }

    public List<ChannelsUser> getUsersPresent() {
        List<ChannelsUser> usersPresent = new ArrayList<ChannelsUser>();
        for ( ChannelsUser user : userInfoService.getPlanners( getPlan().getUri() ) ) {
            if ( !showHereOnly || isHere( user.getUsername() ) ) {
                usersPresent.add( user );
            }
        }
        final Collator collator = Collator.getInstance();
        Collections.sort( usersPresent, new Comparator<ChannelsUser>() {
            public int compare( ChannelsUser user1, ChannelsUser user2 ) {
                return collator.compare( user1.getNormalizedFullName(), user2.getNormalizedFullName() );
            }
        } );
        return usersPresent;
    }

    private boolean isHere( String username ) {
        return presenceRecordService.isAlive( username, getPlanCommunity().getUri() );
    }

    public void refresh( AjaxRequestTarget target, Change change ) {
        addPresences();
        target.add( presencesContainer );
    }
}
