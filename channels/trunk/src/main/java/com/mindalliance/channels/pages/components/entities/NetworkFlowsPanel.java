package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A panel with a table of flows.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2009
 * Time: 8:36:26 PM
 */
public class NetworkFlowsPanel extends AbstractTablePanel<Flow> {

    /**
     * Flows model.
     */
    private IModel<ArrayList<Flow>> flowsModel;

    public NetworkFlowsPanel(
            String id,
            IModel<ArrayList<Flow>> flowsModel,
            int pageSize,
            Set<Long> expansions ) {
        super( id, null, pageSize, expansions );
        this.flowsModel = flowsModel;
        init();
    }

    private void init() {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Scenario" ),
                "scenario.name", "scenario.name" ) );
        columns.add( makeLinkColumn( "Source", "source", "source.title", EMPTY ) );
        columns.add( makeColumn( "Info", "name", "@kind", "?", "description" ) );
        columns.add( makeLinkColumn( "Target", "target", "target.title", EMPTY ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Max delay" ),
                "maxDelay", "maxDelay" ) );
        // TODO - add column for MOUs and policies
        List<Flow> flows = flowsModel.getObject();
        add( new AjaxFallbackDefaultDataTable<Flow>(
                "flows",
                columns,
                new SortableBeanProvider<Flow>( flows, "scenario.name" ),
                getPageSize() ) );

    }


}
