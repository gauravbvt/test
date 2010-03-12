package com.mindalliance.channels.pages.components.segment.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Pages menu for a part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 1:36:54 PM
 */
public class PartShowMenuPanel extends MenuPanel {

    public PartShowMenuPanel( String s, IModel<? extends Part> model, Set<Long> expansions ) {
        super( s, "Show", model, expansions );
    }

    /**
     * Get population of menu items.
     *
     * @return a list of menu items
     */
    public List<Component> getMenuItems() {
        List<Component> menuItems = new ArrayList<Component>();
        // Show/hide details
        if ( isCollapsed( getPart() ) ) {
            AjaxFallbackLink showLink = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.Expanded, getPart() ) );
                }
            };
            menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Details" ), showLink ) );
        } else {
            AjaxFallbackLink hideLink = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.Collapsed, getPart() ) );
                }
            };
            menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Hide details" ), hideLink ) );
        }
        // View part assignments
        AjaxFallbackLink assignmentsLink = new AjaxFallbackLink( "link" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.AspectViewed, getPart(), "assignments" ) );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Assignments" ),
                assignmentsLink ) );
        // View failure impacts
        AjaxFallbackLink failureImpactsLink = new AjaxFallbackLink( "link" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.AspectViewed, getPart(), "failure" ) );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Failure impacts" ),
                failureImpactsLink ) );
        // View part entities
        menuItems.addAll( getModelObjectMenuItems( "menuItem", getModelObjectWrappers() ) );
        return menuItems;
    }

    private List<ModelObjectWrapper> getModelObjectWrappers() {
        List<ModelObjectWrapper> modelObjects = new ArrayList<ModelObjectWrapper>();
        Part part = getPart();
        if ( part.getKnownActor() != null )
            modelObjects.add( new ModelObjectWrapper( "Agent", part.getKnownActor() ) );
        if ( part.getRole() != null )
            modelObjects.add( new ModelObjectWrapper( "Role", part.getRole() ) );
        if ( part.getOrganization() != null )
            modelObjects.add( new ModelObjectWrapper(
                    "Organization",
                    part.getOrganization() ) );
        if ( part.getJurisdiction() != null )
            modelObjects.add( new ModelObjectWrapper(
                    "Jurisdiction",
                    part.getJurisdiction() ) );
        if ( part.getLocation() != null )
            modelObjects.add( new ModelObjectWrapper(
                    "Location",
                    part.getLocation() ) );
        if ( part.getInitiatedEvent() != null )
            modelObjects.add( new ModelObjectWrapper(
                    "Event",
                    part.getInitiatedEvent() ) );
        return modelObjects;
    }

    private Part getPart() {
        return (Part) getModel().getObject();
    }

}
