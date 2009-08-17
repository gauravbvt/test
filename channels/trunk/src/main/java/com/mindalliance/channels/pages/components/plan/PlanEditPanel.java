package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.components.AbstractMultiAspectPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.PlanEditActionsMenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.PlanEditShowMenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

/**
 * Plan edit panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 6, 2009
 * Time: 5:08:07 PM
 */
public class PlanEditPanel extends AbstractMultiAspectPanel {

    /**
     * Incidents aspect.
     */
    public static final String EVENTS = "events";

    /**
     * Map aspect.
     */
    public static final String MAP = "map";

    /**
     * Who's who aspect.
     */
    public static final String WHOSWHO = "who's who";

    /**
     * All issues aspect.
     */
    public static final String ISSUES = "all issues";

    /**
     * Index aspect.
     */
    public static final String INDEX = "index";

    /**
     * Bibliography aspect.
     */
    public static final String BIBLIOGRAPHY = "bibliography";

    public PlanEditPanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
    }

    public PlanEditPanel( String id, IModel<? extends Identifiable> iModel,
                          Set<Long> expansions, String aspect ) {
        super( id, iModel, expansions, aspect );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultAspect() {
        return DETAILS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getCssClass() {
        return "plan";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getObjectClassName() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MenuPanel makeShowMenu( String menuId ) {
        PlanEditShowMenuPanel showMenu = new PlanEditShowMenuPanel(
                menuId,
                new PropertyModel<ModelObject>( this, "object" ) );
        showMenu.setPlanEditPanel( this );
        return showMenu;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean objectNeedsLocking() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getMaxTitleNameLength() {
        return 25;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MenuPanel makeActionMenu( String menuId ) {
        return new PlanEditActionsMenuPanel(
                menuId,
                new PropertyModel<ModelObject>( this, "object" ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Component makeAspectPanel( String aspect ) {
        if ( aspect.equals( DETAILS ) ) {
            return getPlanDetailsPanel();
        } else if ( aspect.equals( MAP ) ) {
            return getPlanMapPanel();
        } else if ( aspect.equals( EVENTS ) ) {
            return getPlanIncidentsPanel();
        } else if ( aspect.equals( WHOSWHO ) ) {
            return getPlanWhoswhoPanel();
        } else if ( aspect.equals( ISSUES ) ) {
            return getPlanIssuesPanel();
        } else if ( aspect.equals( BIBLIOGRAPHY ) ) {
            return getPlanBibliographyPanel();
        } else if ( aspect.equals( INDEX ) ) {
            return getPlanIndexPanel();
        } else {
            // Should never happen
            throw new RuntimeException( "Unknown aspect " + aspect );
        }
    }

    private Component getPlanDetailsPanel() {
        return new PlanEditDetailsPanel( "aspect", getModel(), getExpansions() );
    }

    private Component getPlanMapPanel() {
        return new PlanMapPanel( "aspect", getModel(), getExpansions() );
    }

    private Component getPlanIncidentsPanel() {
        return new PlanEventsPanel( "aspect", getModel(), getExpansions() );
    }

    private Component getPlanIndexPanel() {
        return new PlanIndexPanel( "aspect", getModel(), getExpansions() );
    }

    private Component getPlanIssuesPanel() {
        return new PlanIssuesPanel( "aspect" );
    }

    private Component getPlanBibliographyPanel() {
        return new PlanBibliographyPanel( "aspect" );
    }

    private Component getPlanWhoswhoPanel() {
        return new PlanWhosWhoPanel( "aspect", getModel(), getExpansions() );
    }

    public Plan getPlan() {
        return (Plan) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWith( AjaxRequestTarget target, Change change ) {
        if ( change.getSubject() instanceof Issue )
            setAspectShown( target, DETAILS );

        super.updateWith( target, change );
    }

}
