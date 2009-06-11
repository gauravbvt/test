package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract issue table panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 10, 2009
 * Time: 1:20:17 PM
 */
public abstract class AbstractIssueTablePanel extends AbstractUpdatablePanel implements Filterable {

    protected static final String ALL = "All";
    /**
     * Category of issues to show.
     */
    private String issueType = ALL;
    /**
     * Model objects filtered on (show only where so and so is the actor etc.)
     */
    private ModelObject about;
    /**
     * Issues table.
     */
    private IssuesTable issuesTable;
    /**
     * Maximum number of rows in table.
     */
    private int maxRows;

    public AbstractIssueTablePanel( String id, IModel<ModelObject> model, int maxRows ) {
        super( id, model );
        this.maxRows = maxRows;
        init();
    }

    private void init() {
        addIssueTypeChoice();
        addIncluded();
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
        choices.add( ALL );
        choices.addAll( Arrays.asList( Issue.TYPES ) );
        return choices;
    }

    protected ModelObject getAbout() {
        return about;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType( String issueType ) {
        this.issueType = issueType;
    }

    /**
     * Add fields that augment the scope for issues.
     */
    abstract protected void addIncluded();

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
    public abstract List<Issue> getIssues();

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered( Identifiable identifiable, String property ) {
        return identifiable == about;
    }

    /**
     * Update issues table.
     *
     * @param target an ajax request target
     */
    protected void updateIssuesTable( AjaxRequestTarget target ) {
        addIssuesTable();
        target.addComponent( issuesTable );
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
            super( id, null, maxRows, null );
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
                    AbstractIssueTablePanel.this ) );
            columns.add( makeColumn(
                    "Severity",
                    "severity.label",
                    EMPTY ) );
            columns.add( makeColumn(
                    "Description",
                    "description",
                    EMPTY ) );
            columns.add( makeColumn(
                    "Remediation",
                    "remediation",
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
