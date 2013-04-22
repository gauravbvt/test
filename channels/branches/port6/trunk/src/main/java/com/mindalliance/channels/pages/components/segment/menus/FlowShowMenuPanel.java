/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.segment.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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
    public String getHelpTopicId() {
        return "show-flow";
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
    public List<LinkMenuItem> getMenuItems() {

        synchronized ( getCommander() ) {
            final Flow flow = getFlow();
            List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>();
            // Show/hide details
            menuItems.add(
                    new LinkMenuItem(
                            "menuItem",
                            new Model<String>( isCollapsed ? "Details" : "Hide details" ),
                            new AjaxLink( "link" ) {
                                @Override
                                public void onClick( AjaxRequestTarget target ) {
                                    update(
                                            target, new Change(
                                            isCollapsed ? Change.Type.Expanded : Change.Type.Collapsed,
                                            flow ) );
                                }
                            } ) );
            menuItems.addAll( getModelObjectMenuItems( "menuItem", getModelObjectWrappers() ) );
            // View flow eois
            if ( !isCollapsed )
                menuItems.add(
                        new LinkMenuItem(
                                "menuItem",
                                new Model<String>( "Elements" ),
                                new AjaxLink( "link" ) {
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
                                new Model<String>( "Commitments" ),
                                new AjaxLink( "link" ) {
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
                                new Model<String>( "Failure" ),
                                new AjaxLink( "link" ) {
                                    @Override
                                    public void onClick( AjaxRequestTarget target ) {
                                        update(
                                                target,
                                                new Change( Change.Type.AspectViewed, flow, "failure" ) );
                                    }
                                } ) );

                if ( !flow.getEffectiveEois().isEmpty() )
                    menuItems.add(
                            new LinkMenuItem(
                                    "menuItem",
                                    new Model<String>( "Dissemination" ),
                                    new AjaxLink( "link" ) {
                                        @Override
                                        public void onClick( AjaxRequestTarget target ) {
                                            Change change = new Change(
                                                    Change.Type.AspectViewed, flow, "dissemination" );
                                            change.addQualifier( "show", isSend ? "targets" : "sources" );
                                            update( target, change );
                                        }
                                    } ) );
            }

            // Surveys
            AjaxLink surveysLink = new AjaxLink( "link" ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    Change change = new Change( Change.Type.AspectViewed, getFlow(), "surveys" );
                    update( target, change );
                }
            };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Surveys" ),
                    surveysLink ) );

            if ( getCommander().isTimedOut( getUser().getUsername() ) )
                menuItems.add( timeOutLinkMenuItem( "menuItem" ) );
            else if ( !( isLockedByUser( getFlow() ) || getLockOwner( flow ) == null ) )
                menuItems.add( editedByLinkMenuItem( "menuItem", flow, getLockOwner( flow ) ) );
            // Send message
            menuItems.add( getSendMessageMenuItem( "menuItem" ) );
            return menuItems;
        }
    }

    private List<ModelObjectWrapper> getModelObjectWrappers() {
        List<ModelObjectWrapper> modelObjects = new ArrayList<ModelObjectWrapper>();
        Flow flow = getFlow();
        if ( flow.getInfoProduct() != null ) {
            modelObjects.add( new ModelObjectWrapper( "Info product", flow.getInfoProduct() ) );
        }
        return modelObjects;
    }

    private Flow getFlow() {
        return (Flow) getModel().getObject();
    }
}
