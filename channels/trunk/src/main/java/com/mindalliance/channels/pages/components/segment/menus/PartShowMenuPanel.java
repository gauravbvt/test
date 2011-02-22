package com.mindalliance.channels.pages.components.segment.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.query.QueryService;
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
        // View part overrides
        if ( getQueryService().isOverridden( getPart() )
                || getQueryService().isOverriding( getPart() ) ) {
            AjaxFallbackLink overridesLink = new AjaxFallbackLink( "link" ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.AspectViewed, getPart(), "overrides" ) );
                }
            };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Overrides" ),
                    overridesLink ) );
        }
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
        // Dissemination
        AjaxFallbackLink disseminationLink = new AjaxFallbackLink( "link" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.AspectViewed, getPart(), "dissemination" );
                change.addQualifier( "show", "targets" );
                update( target, change );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Dissemination" ),
                disseminationLink ) );
        // View part entities
        menuItems.addAll( getModelObjectMenuItems( "menuItem", getModelObjectWrappers() ) );
        return menuItems;
    }

    private List<ModelObjectWrapper> getModelObjectWrappers() {
        List<ModelObjectWrapper> modelObjects = new ArrayList<ModelObjectWrapper>();
        Part part = getPart();
        QueryService queryService = getQueryService();
        if ( part.getKnownActor( queryService ) != null )
            modelObjects.add( new ModelObjectWrapper( "About agent", part.getKnownActor( queryService ) ) );
        if ( part.getRole() != null )
            modelObjects.add( new ModelObjectWrapper( "About role", part.getRole() ) );
        if ( part.getOrganization() != null )
            modelObjects.add( new ModelObjectWrapper(
                    "About organization",
                    part.getOrganization() ) );
        if ( part.getJurisdiction() != null )
            modelObjects.add( new ModelObjectWrapper(
                    "About jurisdiction",
                    part.getJurisdiction() ) );
        if ( part.getLocation() != null )
            modelObjects.add( new ModelObjectWrapper(
                    "About location",
                    part.getLocation() ) );
        if ( part.getInitiatedEvent() != null )
            modelObjects.add( new ModelObjectWrapper(
                    "About event",
                    part.getInitiatedEvent() ) );
        return modelObjects;
    }

    private Part getPart() {
        return (Part) getModel().getObject();
    }

}
