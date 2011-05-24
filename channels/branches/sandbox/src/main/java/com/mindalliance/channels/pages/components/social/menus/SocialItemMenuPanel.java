package com.mindalliance.channels.pages.components.social.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.odb.PersistentObject;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.social.SocialPanel;
import com.mindalliance.channels.social.PlannerMessage;
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
    private IModel<? extends PersistentObject> poModel;
    private Updatable updatable;
    private IModel<Participation> participationModel;

    public SocialItemMenuPanel(
            String id,
            IModel<Participation> participationModel,
            String username,
            IModel<? extends PersistentObject> poModel,
            Updatable updatable ) {
        super( id, "more", participationModel );
        this.participationModel = participationModel;
        this.username = username;
        this.poModel = poModel;
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
        final String currentUsername = User.current().getUsername();
        if ( participation != null ) {
            final Actor actor = participation.getActor();
            if ( actor != null && canShowAgentProfile() ) {
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
            if ( participant != null && !participant.equals( currentUsername ) ) {
                Link link = new AjaxFallbackLink( "link" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        updatable.update( target, currentUsername, SocialPanel.SEND_MESSAGE );
                    }
                };
                menuItems.add( new LinkMenuItem(
                        "menuItem",
                        new Model<String>( "Send a message" ),
                        link ) );
            }
        }
        final PersistentObject po = getPersistentObject();
        if ( po != null && po instanceof PlannerMessage ) {
            PlannerMessage message = (PlannerMessage) po;
            if ( message.getFromUsername().equals( currentUsername ) ) {
                Link link = new ConfirmedAjaxFallbackLink( "link", "Delete this message? (\"Unsend\" it)" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        updatable.update( target, po, SocialPanel.DELETE_MESSAGE );
                    }
                };
                menuItems.add( new LinkMenuItem(
                        "menuItem",
                        new Model<String>( "Delete message" ),
                        link ) );
            }
            Link link = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    updatable.update( target, po, SocialPanel.EMAIL_MESSAGE );
                }
            };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( message.isEmailed() ? "Resend email" : "Email this message" ),
                    link ) );
        }
        return menuItems;
    }

    private boolean canShowAgentProfile() {
        return ( (Component) updatable ).getPage().getClass().isAssignableFrom( PlanPage.class );
    }

    private PersistentObject getPersistentObject() {
        return poModel == null ? null : poModel.getObject();
    }

    private Participation getParticipation() {
        return participationModel.getObject();
    }

}
