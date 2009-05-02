package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Plan;
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
            IModel<Plan> model,
            IModel<ArrayList<ExternalFlow>> externalFlowsModel,
            int pageSize,
            Set<Long> expansions ) {
        super( id, null, pageSize, expansions );
        this.externalFlowsModel =  externalFlowsModel;
        init();
    }

    private void init() {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Scenario" ),
                "scenario.name", "scenario.name" ) );
        columns.add( makeLinkColumn( "Part", "part", "part.title", EMPTY ) );
        columns.add( makeColumn( "Info", "name", "@kind", "?", "description" ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Connected scenario" ),
                "externalPart.scenario.name", "externalPart.scenario.name" ) );
        columns.add( makeLinkColumn( "Connected part", "externalPart", "externalPart.title", EMPTY ) );
        List<ExternalFlow> externalFlows = externalFlowsModel.getObject();
        add( new AjaxFallbackDefaultDataTable<ExternalFlow>(
                "flows",
                columns,
                new SortableBeanProvider<ExternalFlow>( externalFlows, "scenario.name" ),
                getPageSize() ) );

    }
}
