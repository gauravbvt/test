package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.components.AbstractMultiAspectPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.segment.menus.SegmentActionsMenuPanel;
import com.mindalliance.channels.pages.components.segment.menus.SegmentShowMenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.Arrays;
import java.util.Set;

/**
 * Segment edit panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 8, 2009
 * Time: 11:14:14 AM
 */
public class SegmentEditPanel extends AbstractMultiAspectPanel {

    /**
     * Goals aspect.
     */
    public static final String GOALS = "goals";
    /**
     * Organizations aspect.
     */
    public static final String ORGANIZATIONS = "organizations";
    /**
     * Task mover aspect.
     */
    public static final String MOVER = "task mover";
    /**
     * Actionable aspects.
     */
    private static final String[] ACTIONABLE_ASPECTS = {DETAILS, GOALS, MOVER};

    public SegmentEditPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    public SegmentEditPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions, String aspect ) {
        super( id, model, expansions, aspect );
    }

    protected String getDefaultAspect() {
        return DETAILS;
    }

    protected int getMaxTitleNameLength() {
        return 20;
    }

    protected String getObjectClassName() {
        return null;
    }

    protected String getCssClass() {
        return "segment";
    }

    protected MenuPanel makeShowMenu( String menuId ) {
        SegmentShowMenuPanel showMenu = new SegmentShowMenuPanel(
                menuId,
                new PropertyModel<Segment>( this, "segment" ),
                getExpansions() );
        showMenu.setSegmentEditPanel( this );
        return showMenu;
    }

    @Override
    protected boolean isAspectShownEditable() {
        return Arrays.asList( ACTIONABLE_ASPECTS ).contains( getAspectShown() );
    }

    protected MenuPanel makeActionMenu( String menuId ) {
        return new SegmentActionsMenuPanel(
                menuId,
                new PropertyModel<ModelObject>( this, "segment" ),
                getExpansions() );
    }


    protected Component makeAspectPanel( String aspect ) {
        if ( aspect.equals( DETAILS ) ) {
            return getSegmentDetailsPanel();
        } else if ( aspect.equals( GOALS ) ) {
            return getSegmentGoalsPanel();
        } else if ( aspect.equals( ORGANIZATIONS ) ) {
            return getSegmentOrganizationsPanel();
        } else if ( aspect.equals( MOVER ) ) {
            return getSegmentTaskMoverPanel();
        } else {
            // Should never happen
            throw new RuntimeException( "Unknown aspect " + aspect );
        }
    }

    private Component getSegmentDetailsPanel() {
        return new SegmentEditDetailsPanel( "aspect", getModel(), getExpansions() );
    }

    private Component getSegmentGoalsPanel() {
        return new GoalListPanel(
                "aspect",
                new PropertyModel<Segment>( this, "segment" ),
                getExpansions() );
    }

    private Component getSegmentOrganizationsPanel() {
        return new SegmentOrganizationsPanel(
                "aspect",
                new PropertyModel<Segment>( this, "segment" ),
                getExpansions() );
    }

    private Component getSegmentTaskMoverPanel() {
        return new SegmentPartMoverPanel(
                "aspect",
                new PropertyModel<Segment>( this, "segment" ),
                getExpansions() );
    }

    /**
     * Get segment from model.
     *
     * @return a segment
     */
    public Segment getSegment() {
        return (Segment) getModel().getObject();
    }

    /**
     * Change visibility.
     *
     * @param target  an ajax request target
     * @param visible a boolean
     */
    public void setVisibility( AjaxRequestTarget target, boolean visible ) {
        makeVisible( target, this, visible );
    }

}
