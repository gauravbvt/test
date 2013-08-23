package com.mindalliance.channels.pages.components.entities.structure;

import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.query.Play;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
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
public class PlaysTablePanel extends AbstractTablePanel<Play> {
    /**
     * The player
     */
    private Specable player;
    /**
     * Whether the plays are specific to resourceSpec.
     */
    private boolean specific = true;
    /**
     * Pre-computed plays.
     */
    private IModel<List<Play>> playsModel;

    public PlaysTablePanel( String s, IModel<ResourceSpec> model, Set<Long> expansions ) {
        super( s, model, expansions );
        player = model.getObject();
        init();
    }

    public PlaysTablePanel(
            String s,
            IModel<ResourceSpec> model,
            IModel<Boolean> specificModel,
            int pageSize,
            Set<Long> expansions ) {
        super( s, model, pageSize, expansions );
        player = model.getObject();
        specific = specificModel.getObject();
        init();
    }

    public PlaysTablePanel( String s, IModel<ResourceSpec> model, int pageSize, Set<Long> expansions ) {
        super( s, model, pageSize, expansions );
        player = model.getObject();
        init();
    }

    public PlaysTablePanel(
            String s,
            IModel<ModelEntity> entityModel,
            IModel<List<Play>> playsModel,
            Set<Long> expansions,
            int pageSize
    ) {
        super( s, entityModel, pageSize, expansions );
        this.playsModel = playsModel;
        init();
    }

    @SuppressWarnings( "unchecked" )
    private void init() {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        // columns
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Segment" ),
                "part.segment.name", "part.segment.name" ) );                  // NON-NLS
        columns.add( makeLinkColumn( "Role", "part.role", "part.role.name", EMPTY ) );
        columns.add( makeLinkColumn( "Task", "part", "part.task", EMPTY ) );
        columns.add( makeColumn(
                "Category",
                "part.category.label",
                EMPTY
        ) );
        columns.add( makeColumn( "Info", "flow.name", "@kind", "?", "flow.description" ) );
        // style class is one of: ask, notify, answer, receive
        // columns.add( makeColumn( "Channels", "flow.channelsString", EMPTY ) );      // NON-NLS
        columns.add( makeColumn(
                "Intent",
                "flow.intent.label",
                EMPTY
        ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Max delay" ),
                "flow.maxDelay", "flow.maxDelay" ) );                                   // NON-NLS
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Importance" ),
                "requiredness", "requiredness" ) );                                 // NON-NLS
        // provider and table
        List<Play> plays = getPlays();
        add( new AjaxFallbackDefaultDataTable(
                "plays",
                columns,
                new SortableBeanProvider<Play>( plays, "part.segment.name" ),
                getPageSize() ) );
    }

    private List<Play> getPlays() {
        if ( playsModel != null ) {
            return playsModel.getObject();
        } else {
            return getQueryService().findAllPlays( player, specific );
        }
    }

}
