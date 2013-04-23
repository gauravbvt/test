package com.mindalliance.channels.pages.components.segment.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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

    @Override
    public String getHelpTopicId() {
        return "show-task";
    }

    /**
     * Find explicit or implicit, single, actual actor, if any.
     *
     * @param part         a part
     * @param queryService a query service
     * @return an actor or null
     */
    private static Actor getKnownActor( Part part, QueryService queryService ) {
        Actor actor = part.getActor();
        return actor == null ? queryService.getKnownActualActor( part ) : actor;
    }

    /**
     * Get population of menu items.
     *
     * @return a list of menu items
     */
    public List<LinkMenuItem> getMenuItems() {
        List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>();
        // Show/hide details
        if ( isCollapsed( getPart() ) ) {
            AjaxLink showLink = new AjaxLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.Expanded, getPart() ) );
                }
            };
            menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Details" ), showLink ) );
        } else {
            AjaxLink hideLink = new AjaxLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.Collapsed, getPart() ) );
                }
            };
            menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Hide details" ), hideLink ) );
        }
        // View checklist
/*
        AjaxLink checklistLink = new AjaxLink( "link" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, getPart() ) );
                update( target, new Change( Change.Type.AspectViewed, getPart(), "checklist" ) );
            }
        };
        addTipTitle( checklistLink, "Open the checklist editor (and show the details of the task if hidden)" );


        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Checklist" ),
                checklistLink ) );
*/
        // View part assignments
        AjaxLink assignmentsLink = new AjaxLink( "link" ) {
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
            AjaxLink overridesLink = new AjaxLink( "link" ) {
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
        AjaxLink failureImpactsLink = new AjaxLink( "link" ) {
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
        AjaxLink disseminationLink = new AjaxLink( "link" ) {
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
        // Surveys
        AjaxLink surveysLink = new AjaxLink( "link" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.AspectViewed, getPart(), "surveys" );
                update( target, change );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Surveys" ),
                surveysLink ) );

        // View part entities
        menuItems.addAll( getModelObjectMenuItems( "menuItem", getModelObjectWrappers() ) );
        return menuItems;
    }

    private List<ModelObjectWrapper> getModelObjectWrappers() {
        List<ModelObjectWrapper> modelObjects = new ArrayList<ModelObjectWrapper>();
        Part part = getPart();
        QueryService queryService = getQueryService();
        if ( getKnownActor( part, queryService ) != null )
            modelObjects.add( new ModelObjectWrapper( "Agent", getKnownActor( part, queryService ) ) );
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
        if ( part.getKnownLocation() != null )
            modelObjects.add( new ModelObjectWrapper(
                    "Location",
                    part.getKnownLocation() ) );
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
