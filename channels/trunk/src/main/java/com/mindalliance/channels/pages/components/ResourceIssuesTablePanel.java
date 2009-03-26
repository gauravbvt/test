package com.mindalliance.channels.pages.components;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import com.mindalliance.channels.util.SortableBeanProvider;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.pages.Project;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 13, 2009
 * Time: 7:26:20 PM
 */
public class ResourceIssuesTablePanel extends AbstractTablePanel {
    /**
     * Resource specification shown in panel
     */
    private ResourceSpec resourceSpec;

    public ResourceIssuesTablePanel( String id, IModel<ResourceSpec> model, Set<Long> expansions ) {
        super( id, model, expansions );
        resourceSpec = model.getObject();
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
        List<Issue> issues = Project.analyst().findAllIssuesFor( resourceSpec );
        add( new AjaxFallbackDefaultDataTable<Issue>(
                "issues",
                columns,
                new SortableBeanProvider<Issue>( issues, "about.name" ),
                getPageSize() ) );

    }

}
