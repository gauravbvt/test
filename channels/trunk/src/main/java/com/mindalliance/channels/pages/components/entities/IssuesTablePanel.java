package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * Issues table.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 27, 2009
 * Time: 1:43:13 PM
 */
public class IssuesTablePanel extends AbstractTablePanel {
    /**
     * Resource spec.
     */
    private ResourceSpec resourceSpec;
    /**
     * Whether the plays are specific to resourceSpec.
     */
    private boolean specific = false;

    public IssuesTablePanel(
            String id,
            IModel<ResourceSpec> model,
            IModel<Boolean> specificModel,
            int pageSize,
            Set<Long> expansions ) {
        super( id, model, pageSize, expansions );
        resourceSpec = model.getObject();
        specific = specificModel.getObject();
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
                new Model<String>( "Waived?" ), "waivedString", "waivedString" ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Severity" ), "severity.ordinal", "severity.label" ) );
        columns.add( makeColumn( "Remediation", "remediation", "remediation", EMPTY ) );
        columns.add( makeColumn( "Reported by", "reportedBy", "reportedBy", EMPTY ) );
        // provider and table
        List<Issue> issues = getDqo().findAllIssuesFor( resourceSpec, specific );
        add( new AjaxFallbackDefaultDataTable<Issue>(
                "issues-table",
                columns,
                new SortableBeanProvider<Issue>( issues, "about.name" ),
                getPageSize() ) );
    }

}
