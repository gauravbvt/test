package com.mindalliance.channels.pages.components.social.menus;

import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.orm.model.PersistentPlanObject;
import com.mindalliance.channels.db.data.messages.UserMessage;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.social.SocialPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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
    private boolean allowMessageDelete = true;
    private Updatable updatable;
    private IModel<ChannelsUserInfo> userInfoIModelModel;

    public SocialItemMenuPanel(
            String id,
            IModel<ChannelsUserInfo> userInfoIModelModel,
            String username,
            IModel<? extends PersistentPlanObject> poModel,
            boolean showProfile,
            Updatable updatable ) {
        this(id, userInfoIModelModel, username, poModel, showProfile, true, updatable );
    }

    public SocialItemMenuPanel(
            String id,
            IModel<ChannelsUserInfo> userInfoIModelModel,
            String username,
            IModel<? extends PersistentPlanObject> poModel,
            boolean showProfile,
            boolean allowMessageDelete,
            Updatable updatable ) {
        super( id, "more", userInfoIModelModel );
        this.userInfoIModelModel = userInfoIModelModel;
        this.username = username;
        this.poModel = poModel;
        this.showProfile = showProfile;
        this.allowMessageDelete = allowMessageDelete;
        this.updatable = updatable;
        doInit();
    }

    @Override
    public String getHelpTopicId() {
        return "messaging-menu";
    }

    protected void init() {
        // do nothing
    }

    private void doInit() {
        super.init();
    }

    public List<LinkMenuItem> getMenuItems() throws CommandException {
        List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>();
        ChannelsUserInfo userInfo = getUserInfo();
        final String currentUsername = getUser().getUsername();
        if ( userInfo != null ) {
            /*final Actor actor = findActor( userInfo );
            if ( actor != null && showProfile ) {
                Link link = new AjaxLink( "link" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        update( target, new Change( Change.Type.Expanded, actor ) );
                    }
                };
                menuItems.add( new LinkMenuItem(
                        "menuItem",
                        new Model<String>( "Show profile" ),
                        link ) );
            }
*/            final String participant = userInfo.getUsername();
            if ( participant != null && !participant.equals( currentUsername ) ) {
                AjaxLink link = new AjaxLink( "link" ) {
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
            if ( allowMessageDelete && message.getFromUsername().equals( currentUsername ) ) {
                AjaxLink deleteLink = new ConfirmedAjaxFallbackLink( "link", "Delete this message? (\"Unsend\" it)" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        updatable.update( target, po, SocialPanel.DELETE_MESSAGE );
                    }
                };
                menuItems.add( new LinkMenuItem(
                        "menuItem",
                        new Model<String>( "Delete message" ),
                        deleteLink ) );

                if ( getQueryService().findUserEmail( message.getFromUsername() ) != null ) {
                    AjaxLink emailLink = new AjaxLink( "link" ) {
                        public void onClick( AjaxRequestTarget target ) {
                            updatable.update( target, po, SocialPanel.EMAIL_MESSAGE );
                        }
                    };
                    menuItems.add( new LinkMenuItem(
                            "menuItem",
                            new Model<String>( message.isNotificationSent() ? "Resend email" : "Email this message" ),
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
