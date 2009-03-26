package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 25, 2009
 * Time: 5:39:31 PM
 */
public class EntityIssuesPanel extends AbstractTablePanel {

    private IssuesPanel issuesPanel;

    public EntityIssuesPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        // columns
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Description" ),
                "description", "description" ) );
        columns.add( makeLinkColumn( "About", "about", "about.label", "(no name)" ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Type" ), "type", "type" ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Severity" ), "severity.ordinal", "severity.label" ) );
        columns.add( makeColumn( "Remediation", "remediation", "remediation", EMPTY ) );
        columns.add( makeColumn( "Reported by", "reportedBy", "reportedBy", EMPTY ) );
        // provider and table
        List<Issue> issues = Project.analyst().findAllIssuesFor( ResourceSpec.with( getEntity() ) );
        add( new AjaxFallbackDefaultDataTable<Issue>(
                "issues-table",
                columns,
                new SortableBeanProvider<Issue>( issues, "about.name" ),
                getPageSize() ) );
        issuesPanel = new IssuesPanel(
                "issues",
                new Model<ModelObject>( getEntity() ),
                getExpansions() );
        issuesPanel.setOutputMarkupId( true );
        add( issuesPanel );
        makeVisible( issuesPanel, Project.analyst().hasIssues( getEntity(), false ) );
    }

    private ModelObject getEntity() {
        return (ModelObject) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        makeVisible( target, issuesPanel, Project.analyst().hasIssues( getEntity(), false ) );
        super.updateWith( target, change );
    }



}
