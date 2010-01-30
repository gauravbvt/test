package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Segments panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 21, 2009
 * Time: 12:32:23 PM
 */
public class SegmentsPanel extends AbstractTablePanel {
    /**
     * Segments shown in panel
     */
    private List<Segment> segments;

    public SegmentsPanel( String s, IModel model, Set<Long> expansions ) {
        super( s, model, expansions );
        segments = (ArrayList<Segment>)model.getObject();
        init();
    }

    @SuppressWarnings( "unchecked")
    private void init() {
        List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        // columns
        columns.add( makeLinkColumn( "Name", "", "name", "(No name)" ) );
        columns.add( makeColumn( "Description", "description", EMPTY ) );
        // table and providers of resources specified resources need to kwno how to contact
        add( new AjaxFallbackDefaultDataTable(
                "segments",
                columns,
                new SortableBeanProvider<Segment>( segments, "name" ),
                getPageSize() ) );
    }
}
