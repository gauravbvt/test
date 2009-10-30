package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Assignments table panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 29, 2009
 * Time: 4:33:07 PM
 */
public class AssignmentsPanel extends AbstractUpdatablePanel implements Filterable {
    /**
     * Assignments model.
     */
    private IModel<List<Assignment>> assignmentsModel;
    /**
     * Model objects filtered on (show only where so and so is the actor etc.)
     */
    private List<Identifiable> filters;
    /**
     * Filtered assignments table.
     */
    private AssignmentsTablePanel assignmentsTablePanel;


    public AssignmentsPanel( String id, IModel<List<Assignment>> assignmentsModel ) {
        super( id );
        this.assignmentsModel = assignmentsModel;
        init();
    }

    private void init() {
        filters = new ArrayList<Identifiable>();
        setOutputMarkupId( true );
        addAssignmentsTablePanel();
    }

    private void addAssignmentsTablePanel() {
        assignmentsTablePanel = new AssignmentsTablePanel(
                "assignmentsTable",
                new PropertyModel<List<Assignment>>( this, "assignments" ) );
        assignmentsTablePanel.setOutputMarkupId( true );
        addOrReplace( assignmentsTablePanel );
    }

    /**
     * {@inheritDoc}
     */
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        // Property ignored since no two properties filtered are ambiguous on type.
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( identifiable );
        } else {
            filters.add( identifiable );
        }
        addAssignmentsTablePanel();
        target.addComponent( assignmentsTablePanel );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered( Identifiable identifiable, String property ) {
        return filters.contains( identifiable );
    }

    private boolean isFilteredOut( Assignment assignment ) {
        boolean filteredOut = false;
        Employment employment = assignment.getEmployment();
        for ( Identifiable filter : filters ) {
            filteredOut = filteredOut ||
                    ( filter instanceof Actor && employment.getActor() != filter )
                    || ( filter instanceof Role && employment.getRole() != filter )
                    || ( filter instanceof Organization && employment.getOrganization() != filter )
                    || ( filter instanceof Place && employment.getJurisdiction() != filter );
        }
        return filteredOut;
    }


    /**
     * Find all employments in the plan that are not filtered out and are within selected name range.
     *
     * @return a list of employments.
     */
    @SuppressWarnings( "unchecked" )
    public List<Assignment> getAssignments() {
        return (List<Assignment>) CollectionUtils.select(
                assignmentsModel.getObject(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return !isFilteredOut( (Assignment) obj );
                    }
                }
        );
    }

    private class AssignmentsTablePanel extends AbstractTablePanel {

        private IModel<List<Assignment>> assignmentsModel;

        private AssignmentsTablePanel( String s, IModel<List<Assignment>> assignmentsModel ) {
            super( s );
            this.assignmentsModel = assignmentsModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( this.makeFilterableLinkColumn(
                    "Actor",
                    "employment.actor",
                    "employment.actor.normalizedName",
                    EMPTY,
                    AssignmentsPanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "Role",
                    "employment.role",
                    "employment.role.name",
                    EMPTY,
                    AssignmentsPanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Jurisdiction",
                    "employment.job.jurisdiction",
                    "employment.job.jurisdiction.name",
                    EMPTY,
                    AssignmentsPanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Organization",
                    "employment.organization",
                    "employment.organization.name",
                    EMPTY,
                    AssignmentsPanel.this ) );
            // provider and table
            add( new AjaxFallbackDefaultDataTable(
                    "assignments",
                    columns,
                    new SortableBeanProvider<Assignment>(
                            assignmentsModel.getObject(),
                            "employment.actor.normalizedName" ),
                    getPageSize() ) );
        }
    }
}
