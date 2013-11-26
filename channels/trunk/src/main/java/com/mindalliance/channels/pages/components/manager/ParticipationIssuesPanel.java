package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Participation issues panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/8/13
 * Time: 2:34 PM
 */
public class ParticipationIssuesPanel extends AbstractUpdatablePanel {

    private static final int MAX_ROWS = 8;

    @SpringBean
    private Analyst analyst;

    private Issue issueShown;

    private Component issueDetailsPanel;

    public ParticipationIssuesPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addParticipationIssuesTable();
        addIssueDetails();
    }

    private void addParticipationIssuesTable() {
        ParticipationIssuesTable participationIssuesTable;
        participationIssuesTable = new ParticipationIssuesTable(
                "issuesTable",
                new PropertyModel<List<Issue>>(this, "participationIssues")
                );
        add( participationIssuesTable );
    }

    private void addIssueDetails() {
        if ( issueShown != null ) {
            issueDetailsPanel = new ParticipationIssueDetailsPanel(
                    "issue",
                    new Model<Issue>( issueShown) );
        }
        else {
            issueDetailsPanel = new Label( "issue", "" );
            issueDetailsPanel.setOutputMarkupId( true );
        }
        makeVisible( issueDetailsPanel, issueShown != null );
        addOrReplace( issueDetailsPanel );
    }

    public List<Issue> getParticipationIssues() {
        return analyst.findAllIssues( getCommunityService() );
    }

    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( object instanceof Issue ) {
            Issue issue = (Issue) object;
            if ( issueShown != null && issueShown.equals( issue ) ) {
                issueShown = null;
            } else {
                issueShown = issue;
            }
            if ( action.equals( "showDetails" ) ) {
                addIssueDetails();
                target.add( issueDetailsPanel );
            }
        }
    }


    private class ParticipationIssuesTable extends AbstractFilterableTablePanel {

        private IModel<List<Issue>> issuesModel;

        private ParticipationIssuesTable( String id, IModel<List<Issue>> issuesModel ) {
            super( id, MAX_ROWS );
            this.issuesModel = issuesModel;
            initTable();
        }

        @SuppressWarnings( "unchecked" )
        private void initTable() {
            final List<IColumn<Issue>> columns = new ArrayList<IColumn<Issue>>();
            columns.add(  makeColumn(
                    "Issue",
                    "detectorLabel",
                    EMPTY
            ) );
            columns.add(  makeColumn(
                    "Type",
                    "type",
                    EMPTY
            ) );
            columns.add( makeFilterableColumn(
                    "About",
                    "about",
                    "about.name",
                    EMPTY,
                    null,
                    ParticipationIssuesTable.this
                    ) );
            columns.add( makeActionLinkColumn(
                    "",
                    "Details",
                    "showDetails",
                    "",
                    "more",
                    ParticipationIssuesPanel.this ) );

            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable<Issue>(
                    "participationIssuesTable",
                    columns,
                    new SortableBeanProvider<Issue>(
                            getFilteredParticipationIssues(),
                            "kind" ),
                    getPageSize() ) );

        }

        @SuppressWarnings( "unchecked" )
        private List<Issue> getFilteredParticipationIssues() {
            return (List<Issue>) CollectionUtils.select(
                    issuesModel.getObject(),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return !isFilteredOut( obj );
                        }
                    }
            );
        }


        @Override
        protected void resetTable( AjaxRequestTarget target ) {
            initTable();
            target.add( this );
        }

    }
}
