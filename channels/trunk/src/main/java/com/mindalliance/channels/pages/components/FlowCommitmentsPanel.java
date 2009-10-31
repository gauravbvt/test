package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 28, 2009
 * Time: 1:29:01 PM
 */
public class FlowCommitmentsPanel extends FloatingCommandablePanel {

    /**
     * Pad top on move.
     */
    private static final int PAD_TOP = 68;
    /**
     * Pad left on move.
     */
    private static final int PAD_LEFT = 5;
    /**
     * Pad bottom on move and resize.
     */
    private static final int PAD_BOTTOM = 5;
    /**
     * Pad right on move and resize.
     */
    private static final int PAD_RIGHT = 6;
    /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 300;
    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;

    public FlowCommitmentsPanel( String id, IModel<Flow> flowModel, Set<Long> expansions ) {
        super( id, flowModel, expansions );
        init();
    }

    private void init() {
        addAbout();
        addCommitmentsTable();
    }

    private void addAbout() {
        Label infoLabel = new Label( "info", new Model<String>( getFlow().getName() ) );
        add( infoLabel );
        Label fromTask = new Label( "fromTask", new Model<String>( ( (Part) getFlow().getSource() ).getTask() ) );
        add( fromTask );
        Label toTask = new Label( "toTask", new Model<String>( ( (Part) getFlow().getTarget() ).getTask() ) );
        add( toTask );
        Label anyOrAllLabel = new Label(
                "anyOrAll",
                new Model<String>( getFlow().isAll() ? "all" : "any" ) );
        add( anyOrAllLabel );
    }

    private void addCommitmentsTable() {
        CommitmentsTablePanel commitmentsTablePanel = new CommitmentsTablePanel(
                "commitments",
                new PropertyModel<List<Commitment>>( this, "commitments" )
        );
        add( commitmentsTablePanel );
    }

    public List<Commitment> getCommitments() {
        return getQueryService().findAllCommitments( getFlow() );
    }


    /**
     * {@inheritDoc}
     */
    protected void close( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.AspectViewed, getFlow(), null );
        update( target, change );
    }

    private Flow getFlow() {
        return (Flow) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadTop() {
        return PAD_TOP;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadLeft() {
        return PAD_LEFT;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadBottom() {
        return PAD_BOTTOM;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadRight() {
        return PAD_RIGHT;
    }

    /**
     * {@inheritDoc}
     */
    protected int getMinWidth() {
        return MIN_WIDTH;
    }

    /**
     * {@inheritDoc}
     */
    protected int getMinHeight() {
        return MIN_HEIGHT;
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
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( this.makeFilterableLinkColumn(
                    "Actor",
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
                    "with actor",
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
            target.addComponent( this );
        }
    }

}
