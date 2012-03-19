package com.mindalliance.channels.pages.components.social.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.orm.model.PersistentPlanObject;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.social.SocialPanel;
import com.mindalliance.channels.social.model.UserMessage;
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
    private IModel<? extends PersistentPlanObject> poModel;
    private boolean showProfile;
    private Updatable updatable;
    private IModel<ChannelsUserInfo> userInfoIModelModel;

    public SocialItemMenuPanel(
            String id,
            IModel<ChannelsUserInfo> userInfoIModelModel,
            String username,
            IModel<? extends PersistentPlanObject> poModel,
            boolean showProfile,
            Updatable updatable ) {
        super( id, "more", userInfoIModelModel );
        this.userInfoIModelModel = userInfoIModelModel;
        this.username = username;
        this.poModel = poModel;
        this.showProfile = showProfile;
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
        ChannelsUserInfo userInfo = getUserInfo();
        final String currentUsername = getUser().getUsername();
        if ( userInfo != null ) {
            final Actor actor = findActor( userInfo );
            if ( actor != null && showProfile ) {
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
            final String participant = userInfo.getUsername();
            if ( participant != null && !participant.equals( currentUsername ) ) {
                Link link = new AjaxFallbackLink( "link" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        updatable.update( target, participant, SocialPanel.SEND_MESSAGE );
                    }
                };
                menuItems.add( new LinkMenuItem(
                        "menuItem",
                        new Model<String>( "Send a message" ),
                        link ) );
            }
        }
        final PersistentPlanObject po = getPersistentObject();
        if ( po != null && po instanceof UserMessage ) {
            UserMessage message = (UserMessage) po;
            if ( message.getFromUsername().equals( currentUsername ) ) {
                Link deleteLink = new ConfirmedAjaxFallbackLink( "link", "Delete this message? (\"Unsend\" it)" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        updatable.update( target, po, SocialPanel.DELETE_MESSAGE );
                    }
                };
                menuItems.add( new LinkMenuItem(
                        "menuItem",
                        new Model<String>( "Delete message" ),
                        deleteLink ) );

                if ( getQueryService().findUserEmail( message.getFromUsername() ) != null ) {
                    Link emailLink = new AjaxFallbackLink( "link" ) {
                        public void onClick( AjaxRequestTarget target ) {
                            updatable.update( target, po, SocialPanel.EMAIL_MESSAGE );
                        }
                    };
                    menuItems.add( new LinkMenuItem(
                            "menuItem",
                            new Model<String>( message.isEmailed() ? "Resend email" : "Email this message" ),
                            emailLink ) );
                }
            }
        }
        return menuItems;
    }


    private PersistentPlanObject getPersistentObject() {
        return poModel == null ? null : poModel.getObject();
    }

    private ChannelsUserInfo getUserInfo() {
        return userInfoIModelModel.getObject();
    }

}
