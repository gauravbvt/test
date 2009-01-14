package com.mindalliance.channels.pages.components;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import com.mindalliance.channels.analysis.profiling.SortableResourceIssuesProvider;
import com.mindalliance.channels.analysis.profiling.Resource;
import com.mindalliance.channels.analysis.Issue;

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

    private Resource resource;

    public ResourceIssuesTablePanel( String id, IModel<Resource> model ) {
        super( id, model );
        resource = model.getObject();
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
        add( new AjaxFallbackDefaultDataTable<Issue>(
                "issues", columns, new SortableResourceIssuesProvider( resource ), getPageSize() ) );

    }

}
