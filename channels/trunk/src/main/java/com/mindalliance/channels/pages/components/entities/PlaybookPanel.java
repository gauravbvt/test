package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.util.Play;
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
 * Playbook page
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 12, 2009
 * Time: 8:01:49 PM
 */
public class PlaybookPanel extends AbstractTablePanel<Play> {
    /**
     * The player
     */
    private ResourceSpec player;

    public PlaybookPanel( String s, IModel<ResourceSpec> model, Set<Long> expansions ) {
        super( s, model, expansions );
        player = model.getObject();
        init();
    }

    public PlaybookPanel( String s, IModel<ResourceSpec> model, int pageSize, Set<Long> expansions ) {
        super( s, model, pageSize, expansions );
        player = model.getObject();
        init();
    }


    private void init() {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        // columns
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Scenario" ),
                "part.scenario.name", "part.scenario.name" ) );                  // NON-NLS
        // columns.add( makeLinkColumn( "Role", "part.role", "part.role.name", EMPTY ) );
        columns.add( makeLinkColumn( "Task", "part", "part.task", EMPTY ) );
        columns.add( makeColumn( "Info", "flow.name", "kind", "?", "flow.description" ) );
        // style class is one of: ask, notify, answer, receive
        // columns.add( makeColumn( "Channels", "flow.channelsString", EMPTY ) );      // NON-NLS
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Max delay" ),
                "flow.maxDelay", "flow.maxDelay" ) );                                   // NON-NLS
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Importance" ),
                "requiredness", "requiredness" ) );                                 // NON-NLS
        // provider and table
        List<Play> plays = getService().findAllPlays( player );
        add( new AjaxFallbackDefaultDataTable<Play>(
                "playbook",
                columns,
                new SortableBeanProvider<Play>( plays, "part.scenario.name" ),
                getPageSize() ) );
    }



}
