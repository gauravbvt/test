package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Commitments panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 29, 2009
 * Time: 7:40:43 PM
 */
public class CommitmentsPanel extends AbstractUpdatablePanel implements Filterable {
    /**
     * Commitments model.
     */
    private IModel<List<Commitment>> commitmentsModel;
    private Map<String, Identifiable> filters;
    private CommitmentsTablePanel commitmentsTablePanel;

    public CommitmentsPanel( String id, IModel<List<Commitment>> commitmentsModel ) {
        super( id );
        this.commitmentsModel = commitmentsModel;
        init();
    }

    private void init() {
        filters = new HashMap<String,Identifiable>();
        setOutputMarkupId( true );
        addCommitmentsTablePanel();
    }

    private void addCommitmentsTablePanel() {
        commitmentsTablePanel = new CommitmentsTablePanel(
                "commitmentsTable",
                new PropertyModel<List<Commitment>>( this, "commitments" ) );
        commitmentsTablePanel.setOutputMarkupId( true );
        addOrReplace( commitmentsTablePanel );
    }

    /**
     * {@inheritDoc}
     */
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        // Property ignored since no two properties filtered are ambiguous on type.
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( property );
        } else {
            filters.put( property, identifiable );
        }
        addCommitmentsTablePanel();
        target.addComponent( commitmentsTablePanel );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered( Identifiable identifiable, String property ) {
        Identifiable i = filters.get( property );
        return i != null && i.equals( identifiable );
    }

    private boolean isFilteredOut( Commitment commitment ) {
        for ( String property : filters.keySet() ) {
            if ( !ModelObject.areEqualOrNull(
                    (ModelObject) filters.get( property ),
                    (ModelObject) CommandUtils.getProperty( commitment, property, null ) ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find all employments in the plan that are not filtered out and are within selected name range.
     *
     * @return a list of employments.
     */
    @SuppressWarnings( "unchecked" )
    public List<Commitment> getCommitments() {
        return (List<Commitment>) CollectionUtils.select(
                commitmentsModel.getObject(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return !isFilteredOut( (Commitment) obj );
                    }
                }
        );
    }

    private class CommitmentsTablePanel extends AbstractTablePanel {

        private IModel<List<Commitment>> commitmentsModel;

        private CommitmentsTablePanel( String id, IModel<List<Commitment>> commitmentsModel ) {
            super( id );
            this.commitmentsModel = commitmentsModel;
            initialize();
        }
        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( this.makeFilterableLinkColumn(
                    "Actor",
                    "committer.actor",
                    "committer.actor.normalizedName",
                    EMPTY,
                    CommitmentsPanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "in role",
                    "committer.role",
                    "committer.role.name",
                    EMPTY,
                    CommitmentsPanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "for",
                    "committer.jurisdiction",
                    "committer.jurisdiction.name",
                    EMPTY,
                    CommitmentsPanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "at organization",
                    "committer.organization",
                    "committer.organization.name",
                    EMPTY,
                    CommitmentsPanel.this ) );
             columns.add( makeLinkColumn(
                    "commits to share",
                    "sharing",
                    "sharing.name",
                    EMPTY ) );
            columns.add( this.makeFilterableLinkColumn(
                     "with actor",
                     "beneficiary.actor",
                     "beneficiary.actor.normalizedName",
                     EMPTY,
                     CommitmentsPanel.this ) );
             columns.add( this.makeFilterableLinkColumn(
                     "in role",
                     "beneficiary.role",
                     "beneficiary.role.name",
                     EMPTY,
                     CommitmentsPanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "for",
                    "beneficiary.jurisdiction",
                    "beneficiary.jurisdiction.name",
                    EMPTY,
                    CommitmentsPanel.this ) );
             columns.add( makeFilterableLinkColumn(
                     "at organization",
                     "beneficiary.organization",
                     "beneficiary.organization.name",
                     EMPTY,
                     CommitmentsPanel.this ) );
           // provider and table
            add( new AjaxFallbackDefaultDataTable(
                    "commitments",
                    columns,
                    new SortableBeanProvider<Commitment>(
                            commitmentsModel.getObject(),
                            "committer.actor.normalizedName" ),
                    getPageSize() ) );
        }

    }


}
