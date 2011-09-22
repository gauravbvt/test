/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.segment.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

public class FlowShowMenuPanel extends MenuPanel {

    /**
     * Whether flow viewed as send.
     */
    private boolean isSend;

    /**
     * Whether flow panel is collapsed.
     */
    private boolean isCollapsed;

    public FlowShowMenuPanel( String id,
                              IModel<? extends Flow> model,
                              boolean isSend,
                              boolean isCollapsed ) {
        super( id, "Show", model );
        this.isSend = isSend;
        this.isCollapsed = isCollapsed;
        doInit();
    }

    @Override
    protected void init() {
        // do nothing
    }

    private void doInit() {
        super.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Component> getMenuItems() {

        synchronized ( getCommander() ) {
            final Flow flow = getFlow();
            List<Component> menuItems = new ArrayList<Component>();

            // Show/hide details
            menuItems.add(
                    new LinkMenuItem(
                            "menuItem",
                            new Model<String>( isCollapsed ? "Show details" : "Hide details" ),
                            new AjaxFallbackLink( "link" ) {
                                @Override
                                public void onClick( AjaxRequestTarget target ) {
                                    update(
                                            target, new Change(
                                            isCollapsed ? Change.Type.Expanded : Change.Type.Collapsed,
                                            flow ) );
                                }
                            } ) );

            // Send message
            menuItems.add( getSendMessageMenuItem( "menuItem" ) );

            // View flow eois
            if ( !isCollapsed )
                menuItems.add(
                        new LinkMenuItem(
                                "menuItem",
                                new Model<String>( "Show elements" ),
                                new AjaxFallbackLink( "link" ) {
                                    @Override
                                    public void onClick( AjaxRequestTarget target ) {
                                        update(
                                                target, new Change(
                                                Change.Type.AspectViewed, flow, "eois" ) );
                                    }
                                } ) );

            if ( flow.isSharing() ) {
                menuItems.add(
                        new LinkMenuItem(
                                "menuItem",
                                new Model<String>( "Show commitments" ),
                                new AjaxFallbackLink( "link" ) {
                                    @Override
                                    public void onClick( AjaxRequestTarget target ) {
                                        update(
                                                target, new Change(
                                                Change.Type.AspectViewed, flow, "commitments" ) );
                                    }
                                } ) );

                menuItems.add(
                        new LinkMenuItem(
                                "menuItem",
                                new Model<String>( "Show failure impacts" ),
                                new AjaxFallbackLink( "link" ) {
                                    @Override
                                    public void onClick( AjaxRequestTarget target ) {
                                        update(
                                                target,
                                                new Change( Change.Type.AspectViewed, flow, "failure" ) );
                                    }
                                } ) );

                if ( !flow.getEois().isEmpty() )
                    menuItems.add(
                            new LinkMenuItem(
                                    "menuItem",
                                    new Model<String>( "Show dissemination" ),
                                    new AjaxFallbackLink( "link" ) {
                                        @Override
                                        public void onClick( AjaxRequestTarget target ) {
                                            Change change = new Change(
                                                    Change.Type.AspectViewed, flow, "dissemination" );
                                            change.addQualifier( "show", isSend ? "targets" : "sources" );
                                            update( target, change );
                                        }
                                    } ) );
            }


            if ( getCommander().isTimedOut( User.current().getUsername() ) )
                menuItems.add( timeOutLabel( "menuItem" ) );
            else if ( !( isLockedByUser( getFlow() ) || getLockOwner( flow ) == null ) )
                menuItems.add( editedByLabel( "menuItem", flow, getLockOwner( flow ) ) );
            return menuItems;
        }
    }

    private Flow getFlow() {
        return (Flow) getModel().getObject();
    }
}
