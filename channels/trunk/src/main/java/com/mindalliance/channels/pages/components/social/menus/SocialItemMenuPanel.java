package com.mindalliance.channels.pages.components.social.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.social.SocialPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Planner presence menu panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 5, 2010
 * Time: 7:50:03 PM
 */
public class SocialItemMenuPanel extends MenuPanel {

    private String username;
    private Updatable updatable;
    private IModel<Participation> participationModel;

    public SocialItemMenuPanel(
            String id,
            IModel<Participation> participationModel,
            String username,
            Updatable updatable ) {
        super( id, "more", participationModel );
        this.participationModel = participationModel;
        this.username = username;
        this.updatable = updatable;
        doInit();
    }

    protected void init() {
        // do nothing
    }

    private void doInit() {
        super.init();
    }

    public List<Component> getMenuItems() throws CommandException {
        List<Component> menuItems = new ArrayList<Component>();
        Participation participation = getParticipation();
        if ( participation != null ) {
            final Actor actor = participation.getActor();
            if ( actor != null ) {
                Link link = new AjaxFallbackLink( "link" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        update( target, new Change( Change.Type.Expanded, actor ) );
                    }
                };
                menuItems.add( new LinkMenuItem(
                        "menuItem",
                        new Model<String>( "Show profile" ),
                        link ) );
            }
            String participant = participation.getUsername();
            if ( participant != null && !participant.equals( User.current().getUsername() ) ) {
                Link link = new AjaxFallbackLink( "link" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        updatable.update( target, username, SocialPanel.SEND_MESSAGE );
                    }
                };
                menuItems.add( new LinkMenuItem(
                        "menuItem",
                        new Model<String>( "Send message" ),
                        link ) );
            }
        }
        return menuItems;
    }

    private Participation getParticipation() {
        return participationModel.getObject();
    }

}
