package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.GeoLocatable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.components.AbstractFloatingMultiAspectPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.segment.menus.SegmentActionsMenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Segment edit panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 8, 2009
 * Time: 11:14:14 AM
 */
public class SegmentEditPanel extends AbstractFloatingMultiAspectPanel {

    /**
     * Goals aspect.
     */
    public static final String GOALS = "goals";
    /**
     * Organizations aspect.
     */
    public static final String ORGANIZATIONS = "organizations";

    public static final String SCENARIO = "scenario";
    public static final String OWNERS = "owners";
    /**
     * Actionable aspects.
     */
    private static final String[] ACTIONABLE_ASPECTS = {DETAILS, SCENARIO, GOALS, OWNERS};

    private static final String[] ASPECTS = {DETAILS, SCENARIO, GOALS, ORGANIZATIONS, OWNERS };

    public SegmentEditPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    public SegmentEditPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions, String aspect ) {
        super( id, model, expansions, aspect );
    }

    @Override
    protected List<String> getAllAspects() {
        return Arrays.asList( ASPECTS );
    }

    @Override
    protected List<String> getActionableAspects() {
        return Arrays.asList( ACTIONABLE_ASPECTS );
    }

    @Override
    protected String getMapTitle() {
        return "Tasks with known locations in segment " + getSegment().getName();
    }

    @Override
    protected List<? extends GeoLocatable> getGeoLocatables() {
        List<GeoLocatable> geoLocatables = new ArrayList<GeoLocatable>();
        Iterator<Part> parts = getSegment().parts();
        while ( parts.hasNext() ) {
            geoLocatables.add( parts.next() );
        }
        return geoLocatables;
    }


    protected String getCssClass() {
        return "segment";
    }

    protected MenuPanel makeActionMenu( String menuId ) {
        return new SegmentActionsMenuPanel(
                menuId,
                new PropertyModel<ModelObject>( this, "segment" ),
                getExpansions() );
    }


    protected Component makeAspectPanel( String aspect, Change change ) {
        if ( aspect.equals( DETAILS ) ) {
            return getSegmentDetailsPanel();
        }  else if ( aspect.equals( SCENARIO ) ) {
            return getSegmentScenarioPanel();
        } else if ( aspect.equals( GOALS ) ) {
            return getSegmentGoalsPanel();
        } else if ( aspect.equals( ORGANIZATIONS ) ) {
            return getSegmentOrganizationsPanel();
        } else if ( aspect.equals( OWNERS ) ) {
            return getSegmentOwnersPanel();
        } else {
            // Should never happen
            throw new RuntimeException( "Unknown aspect " + aspect );
        }
    }

    private Component getSegmentDetailsPanel() {
        return new SegmentEditDetailsPanel( "aspect", getModel(), getExpansions() );
    }

    private Component getSegmentScenarioPanel() {
        return new ScenarioPanel(
                "aspect",
                new PropertyModel<Segment>( this, "segment" ),
                getExpansions() );
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

    private Component getSegmentOwnersPanel() {
        return new SegmentOwnersPanel(
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

}
