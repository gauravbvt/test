package com.mindalliance.channels.pages.components.community.requirements;

import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/10/13
 * Time: 9:01 PM
 */
public class CommunityCommitmentsTablePanel  extends AbstractFilterableTablePanel {

    private PropertyModel<List<CommunityCommitment>> commitmentsModel;

    public CommunityCommitmentsTablePanel( String id, PropertyModel<List<CommunityCommitment>> commitmentsModel ) {
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
    public List<CommunityCommitment> getFilteredCommitments() {
        return (List<CommunityCommitment>) CollectionUtils.select(
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
        columns.add( this.makeFilterableColumn(
                "Agent",
                "committer.agent",
                "committer.agent.name",
                EMPTY,
                "committer.agent.description",
                CommunityCommitmentsTablePanel.this ) );
        columns.add( this.makeFilterableColumn(
                "for",
                "committer.jurisdiction",
                "committer.jurisdiction.name",
                EMPTY,
                "committer.jurisdiction.description",
                CommunityCommitmentsTablePanel.this ) );
        columns.add( makeFilterableColumn(
                "at agency",
                "committer.agency",
                "committer.agency.name",
                EMPTY,
                "committer.agency.description",
                CommunityCommitmentsTablePanel.this ) );
        columns.add( makeColumn(
                "commits to share",
                "sharing.name",
                null,
                EMPTY ) );
        columns.add( this.makeFilterableColumn(
                "with agent",
                "beneficiary.agent",
                "beneficiary.agent.name",
                EMPTY,
                "beneficiary.agent.description",
                CommunityCommitmentsTablePanel.this ) );
        columns.add( this.makeFilterableColumn(
                "for",
                "beneficiary.jurisdiction",
                "beneficiary.jurisdiction.name",
                EMPTY,
                "beneficiary.jurisdiction.description",
                CommunityCommitmentsTablePanel.this ) );
        columns.add( makeFilterableColumn(
                "at agency",
                "beneficiary.agency",
                "beneficiary.agency.name",
                EMPTY,
                "beneficiary.agency.description",
                CommunityCommitmentsTablePanel.this ) );
        columns.add( makeParticipationAnalystColumn(
                "Can be fulfilled?",
                null,
                "realizability",
                "?",
                null
        ) );
        // provider and table
        addOrReplace( new AjaxFallbackDefaultDataTable(
                "commitments",
                columns,
                new SortableBeanProvider<CommunityCommitment>(
                        getFilteredCommitments(),
                        "committer.agent.name" ),
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
