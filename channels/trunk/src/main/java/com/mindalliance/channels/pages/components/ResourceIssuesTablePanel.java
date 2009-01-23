package com.mindalliance.channels.pages.components;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import com.mindalliance.channels.util.SortableBeanProvider;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.analysis.DetectedIssue;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 13, 2009
 * Time: 7:26:20 PM
 */
public class ResourceIssuesTablePanel extends AbstractTablePanel {

    private ResourceSpec resourceSpec;

    public ResourceIssuesTablePanel( String id, IModel<ResourceSpec> model ) {
        super( id, model );
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
                new Model<String>("Type"), "type", "type"));
        columns.add( makeColumn("Remediation", "remediation", "remediation", EMPTY));
        // provider and table
        List<DetectedIssue> issues = Project.analyst().findAllIssuesFor( resourceSpec );
        add( new AjaxFallbackDefaultDataTable<DetectedIssue>(
                "issues",
                columns,
                new SortableBeanProvider<DetectedIssue>( issues, "about.name" ),
                getPageSize() ) );

    }

}
