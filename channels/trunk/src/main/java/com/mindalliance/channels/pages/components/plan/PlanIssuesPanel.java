package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Plan issues panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 1, 2009
 * Time: 11:43:52 AM
 */
public class PlanIssuesPanel extends AbstractUpdatablePanel implements Filterable {

    private static final int MAX_ROWS = 9;
    /**
     * Model objects filtered on (show only where so and so is the actor etc.)
     */
    private ModelObject about;
    /**
     * Issues table.
     */
    private IssuesTable issuesTable;

    public PlanIssuesPanel( String id ) {
        super( id, null, null );
        init();
    }

    private void init() {
        addIssuesTable();
    }

    private void addIssuesTable() {
        issuesTable = new IssuesTable(
                "issuesTable",
                new PropertyModel<List<Issue>>( this, "issues" ) );
        issuesTable.setOutputMarkupId( true );
        addOrReplace( issuesTable );
    }

    /**
     * {@inheritDoc}
     */
    public void toggleFilter( Identifiable identifiable , String property, AjaxRequestTarget target ) {
        // only about is filtered; property is ignored
        about = ( identifiable == about ) ? null : (ModelObject) identifiable;
        addIssuesTable();
        target.addComponent( issuesTable );
    }

    /**
     * Get all issues, possibly filtered on the model object they are about.
     *
     * @return a list of issues
     */
    public List<Issue> getIssues() {
        if ( about != null ) {
            return getAnalyst().listIssues( about, true );
        } else {
            return getQueryService().findAllIssues( getAnalyst() );
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered( Identifiable identifiable, String property ) {
        return identifiable == about;
    }

    /**
     * Issues table.
     */
    public class IssuesTable extends AbstractTablePanel<Issue> {
        /**
         * Issue list model.
         */
        private IModel<List<Issue>> issuesModel;

        public IssuesTable( String id, IModel<List<Issue>> issuesModel ) {
            super( id, null, MAX_ROWS, null );
            this.issuesModel = issuesModel;
            initialize();
        }

        private void initialize() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeColumn(
                    "Kind",
                    "detectorLabel",
                    EMPTY ) );
            columns.add( makeFilterableLinkColumn(
                    "About",
                    "about",
                    "about.name",
                    EMPTY,
                    PlanIssuesPanel.this ) );
            columns.add( makeColumn(
                    "Severity",
                    "severity.label",
                    EMPTY ) );
            columns.add( makeColumn(
                    "Description",
                    "description",
                    EMPTY ) );
            columns.add( makeColumn(
                    "Reported by",
                    "reportedBy",
                    EMPTY ) );
            columns.add( makeColumn(
                    "Waived",
                    "waivedString",
                    EMPTY ) );
            // provider and table
            add( new AjaxFallbackDefaultDataTable<Issue>(
                    "issues",
                    columns,
                    new SortableBeanProvider<Issue>(
                            issuesModel.getObject(),
                            "kind" ),
                    getPageSize() ) );
        }

    }


}
