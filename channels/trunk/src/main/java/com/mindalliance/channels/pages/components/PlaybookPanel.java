package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Player;
import com.mindalliance.channels.analysis.profiling.Play;
import com.mindalliance.channels.analysis.profiling.SortablePlaysProvider;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

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
    private Player player;

    public PlaybookPanel( String s, IModel<? extends Player> model ) {
        super( s, model );
        player = model.getObject();
        setRenderBodyOnly( true );
        init();
    }

    private void init() {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        // columns
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Scenario" ),
                "part.scenario.name", "part.scenario.name" ) );                  // NON-NLS
        columns.add( makeLinkColumn( "Task", "part", "part.task", EMPTY ) );
        columns.add( makeColumn( "info", "flow.name", EMPTY ) );
        // style class is one of: ask, notify, answer, receive
        columns.add( makeLinkColumn( "To/from", "otherPart", "otherPart.name", "@kind", EMPTY ) );
        columns.add( makeColumn( "Channel", "flow.channel", EMPTY ) );      // NON-NLS
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Max delay" ),
                "flow.maxDelay", "flow.maxDelay" ) );                                   // NON-NLS
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Priority" ),
                "criticality", "criticality" ) );                                 // NON-NLS

        // provider and table
        add( new AjaxFallbackDefaultDataTable<Play>(
                "playbook", columns, new SortablePlaysProvider( player ), getPageSize() ) );
    }
}
