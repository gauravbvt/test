package com.mindalliance.channels.pages.components.entities.analytics;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/6/12
 * Time: 1:53 PM
 */
public class OrganizationAnalyticsPanel extends AbstractUpdatablePanel implements Guidable {

    public OrganizationAnalyticsPanel( String id, IModel<ModelEntity> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "analyzing";
    }

    @Override
    public String getHelpTopicId() {
        return "entity-analytics";
    }



    private void init() {
        addAssignmentsPanel();
        addCommitmentsPanel();
    }

    private void addAssignmentsPanel() {
        WebMarkupContainer assignmentsContainer = new WebMarkupContainer( "assignmentsContainer" );
        assignmentsContainer.setOutputMarkupId( true );
        makeVisible( assignmentsContainer, getOrganization().isActual() );
        addOrReplace( assignmentsContainer );
        if ( getOrganization().isActual() ) {
            assignmentsContainer.add(
                    new AssignmentsTablePanel(
                            "assignments",
                            new PropertyModel<List<Assignment>>( this, "assignments" )
                    )
            );
        } else {
            assignmentsContainer.add( new Label( "assignments", "" ) );
        }
    }

    /**
     * Find all of the actor's assignments.
     *
     * @return a list of assignments
     */
    public List<Assignment> getAssignments() {
        return getQueryService().getAssignments().with( getOrganization() ).getAssignments();
    }

    private void addCommitmentsPanel() {
        WebMarkupContainer commitmentsContainer = new WebMarkupContainer( "commitmentsContainer" );
        commitmentsContainer.setOutputMarkupId( true );
        makeVisible( commitmentsContainer, getOrganization().isActual() );
        addOrReplace( commitmentsContainer );
        if ( getOrganization().isActual() ) {
            commitmentsContainer.add(
                    new CommitmentsTablePanel(
                            "commitments",
                            new PropertyModel<List<Commitment>>( this, "commitments" )
                    )
            );
        } else {
            commitmentsContainer.add( new Label( "commitments", "" ) );
        }
    }

    /**
     * Find all of the actor's assignments.
     *
     * @return a list of assignments
     */
    public List<Commitment> getCommitments() {
        QueryService queryService = getQueryService();
        return queryService.findAllCommitmentsOf(
                getOrganization(),
                queryService.getAssignments( false ),
                queryService.findAllFlows() );
    }

    private Organization getOrganization() {
        return (Organization)getModel().getObject();
    }

    private class AssignmentsTablePanel extends AbstractFilterableTablePanel {
        /**
         * Assignments model.
         */
        private IModel<List<Assignment>> assignmentsModel;


        public AssignmentsTablePanel( String id, IModel<List<Assignment>> assignmentsModel ) {
            super( id );
            this.assignmentsModel = assignmentsModel;
            init();
        }

        /**
         * Find all employments in the plan that are not filtered out and are within selected name range.
         *
         * @return a list of employments.
         */
        @SuppressWarnings( "unchecked" )
        public List<Assignment> getFilteredAssignments() {
            return (List<Assignment>) CollectionUtils.select(
                    assignmentsModel.getObject(),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return !isFilteredOut( obj );
                        }
                    }
            );
        }

        @SuppressWarnings( "unchecked" )
        private void init() {
            List<IColumn<?,String>> columns = new ArrayList<IColumn<?,String>>();
            // columns
            columns.add( this.makeFilterableLinkColumn(
                    "Agent",
                    "actor",
                    "actor.normalizedName",
                    EMPTY,
                    AssignmentsTablePanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "Task",
                    "part",
                    "part.task",
                    EMPTY,
                    AssignmentsTablePanel.this ) );
            columns.add( makeColumn(
                    "Category",
                    "part.category.label",
                    EMPTY
            ) );
            columns.add( makeColumn(
                    "Location",
                    "part.location.displayName",
                    EMPTY ) );
            columns.add( this.makeFilterableLinkColumn(
                    "Role",
                    "employment.role",
                    "employment.role.name",
                    EMPTY,
                    AssignmentsTablePanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Jurisdiction",
                    "employment.job.jurisdiction",
                    "employment.job.jurisdiction.name",
                    EMPTY,
                    AssignmentsTablePanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Organization",
                    "employment.organization",
                    "employment.organization.name",
                    EMPTY,
                    AssignmentsTablePanel.this ) );
            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable(
                    "assignments",
                    columns,
                    new SortableBeanProvider<Assignment,String>(
                            getFilteredAssignments(),
                            "part.task" ),
                    getPageSize() ) );
        }

        /**
         * {@inheritDoc}
         */
        protected void resetTable( AjaxRequestTarget target ) {
            init();
            target.add( this );
        }
    }

    private class CommitmentsTablePanel extends AbstractFilterableTablePanel {
        /**
         * Commitments model.
         */
        private IModel<List<Commitment>> commitmentsModel;

        public CommitmentsTablePanel( String id, IModel<List<Commitment>> commitmentsModel ) {
            super( id );
            this.commitmentsModel = commitmentsModel;
            init();
        }

        /**
         * Find all employments in the plan that are not filtered out and are within selected name range.
         *
         * @return a list of employments.
         */
        @SuppressWarnings( "unchecked" )
        public List<Commitment> getFilteredCommitments() {
            return (List<Commitment>) CollectionUtils.select(
                    commitmentsModel.getObject(),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return !isFilteredOut( obj );
                        }
                    }
            );
        }

        @SuppressWarnings( "unchecked" )
        private void init() {
            List<IColumn<?,String>> columns = new ArrayList<IColumn<?,String>>();
            // columns
            columns.add( this.makeFilterableLinkColumn(
                    "By agent",
                    "committer.actor",
                    "committer.actor.normalizedName",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "from task",
                    "sharing.source",
                    "sharing.source.task",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( this.makeColumn(
                    "at location",
                    "sharing.source.location.displayName",
                    EMPTY ) );
            columns.add( makeLinkColumn(
                    "commits to share",
                    "sharing",
                    "sharing.name",
                    EMPTY ) );
            columns.add( makeColumn(
                    "with intent",
                    "sharing.intent.label",
                    EMPTY
            ) );
            columns.add( this.makeFilterableLinkColumn(
                    "with agent",
                    "beneficiary.actor",
                    "beneficiary.actor.normalizedName",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "for task",
                    "sharing.target",
                    "sharing.target.task",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( this.makeColumn(
                    "at location",
                    "sharing.target.location.displayName",
                    EMPTY ) );
            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable(
                    "commitments",
                    columns,
                    new SortableBeanProvider<Commitment,String>(
                            getFilteredCommitments(),
                            "sharing.source.task" ),
                    getPageSize() ) );
        }

        /**
         * {@inheritDoc}
         */
        protected void resetTable( AjaxRequestTarget target ) {
            init();
            target.add( this );
        }
    }

}
