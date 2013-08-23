package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A panel with a table of external flows.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2009
 * Time: 7:38:33 PM
 */
public class ExternalFlowsPanel extends AbstractTablePanel<ExternalFlow> {

    private IModel<ArrayList<ExternalFlow>> externalFlowsModel;

    public ExternalFlowsPanel(
            String id,
            IModel<ArrayList<ExternalFlow>> externalFlowsModel,
            int pageSize,
            Set<Long> expansions ) {
        super( id, null, pageSize, expansions );
        this.externalFlowsModel = externalFlowsModel;
        init();
    }

    @SuppressWarnings( "unchecked" )
    private void init() {
        List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        columns.add( makeLinkColumn( "Task", "externalPart", "externalPart.title", EMPTY ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( " in segment" ),
                "externalPart.segment.name", "externalPart.segment.name" ) );
        columns.add( makeColumn( "produces info", "name", "@kind", "?", "description" ) );
        columns.add( makeLinkColumn( "consumed by task", "part", "part.title", EMPTY ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "in segment" ),
                "segment.name", "segment.name" ) );
        columns.add( makeGeomapLinkColumn(
                "",
                "name",
                Arrays.asList( "externalPart", "part" ),
                new Model<String>( "Show both tasks in map" ) ) );
        List<ExternalFlow> externalFlows = externalFlowsModel.getObject();
        add( new AjaxFallbackDefaultDataTable(
                "flows",
                columns,
                new SortableBeanProvider<ExternalFlow>( externalFlows, "segment.name" ),
                getPageSize() ) );

    }


}
