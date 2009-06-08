package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
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
    /**
     * Maximum number of rows of issues to show at a time.
     */
    private static final int MAX_ROWS = 12;
    /**
     * Category of issues to show.
     */
    private String issueType = "All";
    /**
     * Whether to show waived issues.
     */
    private boolean includeWaived = false;
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
        addIssueTypeChoice();
        addIncludeWaived();
        addIssuesTable();
    }

    private void addIssueTypeChoice() {
        DropDownChoice<String> issueTypeChoice = new DropDownChoice<String>(
                "issueType",
                new PropertyModel<String>( this, "issueType" ),
                getIssueTypeChoices()
        );
        issueTypeChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addIssuesTable();
                target.addComponent( issuesTable );
            }
        } );
        add( issueTypeChoice );
    }

    private List<String> getIssueTypeChoices() {
        List<String> choices = new ArrayList<String>();
        choices.add( "All" );
        choices.addAll( Arrays.asList( Issue.TYPES ) );
        return choices;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType( String issueType ) {
        this.issueType = issueType;
    }

    private void addIncludeWaived() {
        CheckBox includeWaivedCheckBox = new CheckBox(
                "includeWaived",
                new PropertyModel<Boolean>( this, "includeWaived" ) );
        includeWaivedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addIssuesTable();
                target.addComponent( issuesTable );
            }
        } );
        add( includeWaivedCheckBox );
    }

    public boolean isIncludeWaived() {
        return includeWaived;
    }

    public void setIncludeWaived( boolean includeWaived ) {
        this.includeWaived = includeWaived;
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
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
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
    @SuppressWarnings( "unchecked" )
    public List<Issue> getIssues() {
        List<Issue> issues;
        if ( about != null ) {
            if ( includeWaived ) {
                issues = getAnalyst().listIssues( about, true );
            } else {
                issues = getAnalyst().listUnwaivedIssues( about, true );
            }
        } else {
            if ( includeWaived ) {
                issues = getQueryService().findAllIssues( getAnalyst() );
            } else {
                issues = getQueryService().findAllUnwaivedIssues( getAnalyst() );
            }
        }
        return (List<Issue>) CollectionUtils.select(
                issues,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( issueType.equals( "All" )
                                || ( (Issue) obj ).getType().equals( issueType ) );
                    }
                }
        );
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
