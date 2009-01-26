package com.mindalliance.channels.pages.components;

import org.apache.wicket.model.IModel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.util.SortableBeanProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 21, 2009
 * Time: 12:32:23 PM
 */
public class ScenariosPanel extends AbstractTablePanel {
    /**
     * Scenarios shown in panel
     */
    private List<Scenario> scenarios;

    public ScenariosPanel( String s, IModel<ArrayList<Scenario>> model ) {
        super( s, model );
        scenarios = model.getObject();
        init();
    }

    private void init() {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        // columns
        columns.add( makeLinkColumn( "Name", "", "name", "(No name)" ) );
        columns.add( makeColumn( "Description", "description", EMPTY ) );
        // table and providers of resources specified resources need to kwno how to contact
        add( new AjaxFallbackDefaultDataTable<Scenario>(
                "scenarios",
                columns,
                new SortableBeanProvider<Scenario>( scenarios, "name" ),
                getPageSize() ) );
    }
}
