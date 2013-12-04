/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.entities.issues;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Issues table.
 */
public class IssuesTablePanel extends AbstractTablePanel {

    @SpringBean
    private Analyst analyst;

    /**
     * Resource spec.
     */
    private final ResourceSpec resourceSpec;

    /**
     * Whether the plays are specific to resourceSpec.
     */
    private final boolean specific;

    public IssuesTablePanel( String id, IModel<ResourceSpec> model, IModel<Boolean> specificModel, int pageSize,
                             Set<Long> expansions ) {
        super( id, model, pageSize, expansions );
        resourceSpec = model.getObject();
        specific = specificModel.getObject();
        init();
    }

    @SuppressWarnings( "unchecked" )
    private void init() {
        List<IColumn<?>> columns = new ArrayList<IColumn<?>>();

        // columns
        columns.add( new PropertyColumn<String>( new Model<String>( "Description" ), "description", "description" ) );
        columns.add( makeLinkColumn( "About", "about", "about.label", "(no name)" ) );
        columns.add( new PropertyColumn<String>( new Model<String>( "Type" ), "type", "type" ) );
        columns.add( new PropertyColumn<String>( new Model<String>( "Waived?" ), "waivedString", "waivedString" ) );
        columns.add( new PropertyColumn<String>( new Model<String>( "Severity" ),
                                                 "severity.ordinal",
                                                 "severity.negativeLabel" ) );
        columns.add( makeColumn( "Remediation", "remediation", "remediation", EMPTY ) );
        columns.add( makeColumn( "Reported by", "reportedBy", "reportedBy", EMPTY ) );

        // provider and table
        List<Issue> issues = getCommunityService(  ).getDoctor().findAllIssuesFor( getCommunityService(), resourceSpec, specific );
        add( new AjaxFallbackDefaultDataTable( "issues-table",
                                               columns,
                                               new SortableBeanProvider<Issue>( issues, "about.name" ),
                                               getPageSize() ) );
    }
}
