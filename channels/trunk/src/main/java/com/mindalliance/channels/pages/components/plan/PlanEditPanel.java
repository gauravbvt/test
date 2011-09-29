package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractMultiAspectPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.PlanEditActionsMenuPanel;
import com.mindalliance.channels.pages.components.plan.menus.PlanEditShowMenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.Arrays;
import java.util.List;
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
     * Participations aspect.
     */
    public static final String PARTICIPATIONS = "Participations";
    /**
     * Incidents aspect.
     */
    public static final String EVENTS = "All events";

    /**
     * Classifications aspect.
     */
    public static final String CLASSIFICATIONS = "Secrecy classifications";

    /**
     * Map aspect.
     */
    public static final String MAP = "All segments";

    /**
     * Who's who aspect.
     */
    public static final String WHOSWHO = "Who's who";

    /**
     * All issues aspect.
     */
    public static final String ISSUES = "All issues";

    /**
     * Index aspect.
     */
    public static final String INDEX = "Index";
    /**
     * Evaluation aspect.
     */
    public static final String EVAL = "Evaluation";
    /**
     * Versions aspect.
     */
    public static final String VERSIONS = "Versions";

    /**
     * Bibliography aspect.
     */
    public static final String BIBLIOGRAPHY = "Bibliography";
    /**
     * Organizations in scope.
     */
    public static final String ORGANIZATIONS = "All organizations";
    /**
     * The plan's tag domain.
     */
    public static final String TAGS = "All tags";
    /**
     * Procedures diagrammed.
     */
    public static final String PROCEDURES = "Assignments and commitments";
    /**
     * All types
     */
    public static final String TYPOLOGIES = "All types";

    /**
     * Actionable aspects.
     */
    private static final String[] ACTIONABLE_ASPECTS = {DETAILS, CLASSIFICATIONS, EVENTS, ORGANIZATIONS, PARTICIPATIONS};


    public PlanEditPanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
    }

    public PlanEditPanel( String id, IModel<? extends Identifiable> iModel,
                          Set<Long> expansions, String aspect ) {
        super( id, iModel, expansions, aspect );
    }

/*
    protected void annotateHeaderTitle( ModelObject object, Analyst analyst ) {
        // Show no issue indicator - too costly! --todo : review
    }
*/

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultAspect() {
        return DETAILS;
    }

    protected int getWidth() {
        return 930;
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

    @Override
    protected boolean isAspectShownEditable() {
        return Arrays.asList( ACTIONABLE_ASPECTS ).contains( getAspectShown() );
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
                new PropertyModel<ModelObject>( this, "object" ));
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
        } else if ( aspect.equals( PARTICIPATIONS ) ) {
            return getPlanParticipationsPanel();
        } else if ( aspect.equals( EVENTS ) ) {
            return getPlanIncidentsPanel();
        } else if ( aspect.equals( CLASSIFICATIONS ) ) {
            return getPlanClassificationsPanel();
        } else if ( aspect.equals( ORGANIZATIONS ) ) {
            return getPlanOrganizationsPanel();
        } else if ( aspect.equals( WHOSWHO ) ) {
            return getPlanWhoswhoPanel();
        } else if ( aspect.equals( ISSUES ) ) {
            return getPlanIssuesPanel();
        } else if ( aspect.equals( BIBLIOGRAPHY ) ) {
            return getPlanBibliographyPanel();
        } else if ( aspect.equals( INDEX ) ) {
            return getPlanIndexPanel();
        } else if ( aspect.equals( EVAL ) ) {
            return getPlanEvaluationPanel();
        } else if ( aspect.equals( VERSIONS ) ) {
            return getPlanVersionsPanel();
        } else if ( aspect.equals( TAGS ) ) {
            return getPlanTagsPanel();
        }  else if ( aspect.equals( TYPOLOGIES ) ) {
            return getPlanTypologiesPanel();
        }else if ( aspect.equals( PROCEDURES ) ) {
            return getPlanProcedureMapPanel();
        } else {
            // Should never happen
            throw new RuntimeException( "Unknown aspect " + aspect );
        }
    }

    private Component getPlanDetailsPanel() {
        return new PlanEditDetailsPanel( "aspect", getModel(), getExpansions() );
    }

    private Component getPlanMapPanel() {
        return new PlanSegmentsMapPanel( "aspect", getModel(), getExpansions() );
    }

    private Component getPlanParticipationsPanel() {
        return new PlanPartipationsPanel( "aspect", getModel(), getExpansions() );
    }

    private Component getPlanIncidentsPanel() {
        return new PlanEventsPanel( "aspect", getModel(), getExpansions() );
    }

    private Component getPlanOrganizationsPanel() {
        return new PlanOrganizationsPanel( "aspect", getModel(), getExpansions() );
    }

    private Component getPlanIndexPanel() {
        return new PlanIndexPanel( "aspect", getModel(), getExpansions() );
    }

    private Component getPlanVersionsPanel() {
        return new PlanVersionsPanel( "aspect", getModel(), getExpansions() );
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

    private Component getPlanEvaluationPanel() {
        return new PlanEvaluationPanel( "aspect", getModel(), getExpansions() );
    }

    private Component getPlanClassificationsPanel() {
        return new PlanClassificationSystemsPanel( "aspect" );
    }

    private Component getPlanTagsPanel() {
        return new PlanTagsPanel( "aspect" );
    }

    private Component getPlanTypologiesPanel() {
        return new PlanTypologiesPanel( "aspect" );
    }

    private Component getPlanProcedureMapPanel() {
        return new PlanProcedureMapPanel( "aspect" );
    }

    public Plan getPlan() {
        return (Plan) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( Issue.class ) )
            setAspectShown( target, DETAILS );
        if ( !( change.isSelected() && ( change.isForInstanceOf( Plan.class ) ) ) ) {
            super.updateWith( target, change, updated );
        }
    }

}
