package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 20, 2009
 * Time: 2:03:29 PM
 */
    public class CommitmentsTablePanel extends AbstractFilterableTablePanel {
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
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( this.makeFilterableLinkColumn(
                    "Agent",
                    "committer.actor",
                    "committer.actor.normalizedName",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "in role",
                    "committer.role",
                    "committer.role.name",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "for",
                    "committer.jurisdiction",
                    "committer.jurisdiction.name",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "at organization",
                    "committer.organization",
                    "committer.organization.name",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( makeLinkColumn(
                    "commits to share",
                    "sharing",
                    "sharing.name",
                    EMPTY ) );
            columns.add( this.makeFilterableLinkColumn(
                    "with agent",
                    "beneficiary.actor",
                    "beneficiary.actor.normalizedName",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "in role",
                    "beneficiary.role",
                    "beneficiary.role.name",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "for",
                    "beneficiary.jurisdiction",
                    "beneficiary.jurisdiction.name",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "at organization",
                    "beneficiary.organization",
                    "beneficiary.organization.name",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( makeAnalystColumn(
                    "Can be fulfilled?",
                    null,
                    "realizability",
                    "?"
            ) );
            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable(
                    "commitments",
                    columns,
                    new SortableBeanProvider<Commitment>(
                            getFilteredCommitments(),
                            "committer.actor.normalizedName" ),
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
