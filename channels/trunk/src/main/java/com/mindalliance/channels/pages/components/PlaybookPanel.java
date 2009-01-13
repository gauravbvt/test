package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import com.mindalliance.channels.Player;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.analysis.profiling.Play;
import com.mindalliance.channels.analysis.profiling.SortablePlaysProvider;

import java.util.List;
import java.util.ArrayList;
import java.text.MessageFormat;

/**
 * Playbook page
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 12, 2009
 * Time: 8:01:49 PM
 */
public class PlaybookPanel extends Panel {
    /**
     * The player
     */
    private Player player;
    /**
     * Number of plays shown in table at a time
     */
    private int pageSize = 20;

    public PlaybookPanel( String s, IModel<? extends Player> model ) {
        super( s, model );
        player = model.getObject();
        init();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize( int pageSize ) {
        this.pageSize = pageSize;
    }

    private void init() {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        // Scenario column
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Scenario" ),
                "part.scenario.name", "part.scenario.name" ) );                  // NON-NLS
        // Part column
        columns.add(
                new AbstractColumn<Play>( new Model<String>( "Task" ),
                        "part.task" ) {                                 // NON-NLS

                    public void populateItem(
                            Item<ICellPopulator<Play>> cellItem, String id,
                            final IModel<Play> playModel ) {
                        cellItem.add( new ModelObjectLink( id,
                                new AbstractReadOnlyModel<Part>() {
                                    @Override
                                    public Part getObject() {
                                        return playModel.getObject().getPart();
                                    }
                                },
                                new AbstractReadOnlyModel<String>() {
                                    @Override
                                    public String getObject() {
                                        return playModel.getObject().getPart().getTask();
                                    }
                                } ) );
                    }
                } );
        // Info column
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Info" ),
                "flow.name", "flow.name" ) );                                     // NON-NLS
        // Sent/received column
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Sent/received" ),
                "kind", "kind" ) );                                               // NON-NLS
        // To/from colum
        columns.add(
                new AbstractColumn<Play>( new Model<String>( "To/from" ),
                        "otherPart.name" ) {                            // NON-NLS

                    public void populateItem(
                            Item<ICellPopulator<Play>> cellItem, String id,
                            final IModel<Play> playModel ) {
                        cellItem.add(
                                new ModelObjectLink(
                                        id,
                                        new AbstractReadOnlyModel<Part>() {
                                            @Override
                                            public Part getObject() {
                                                return playModel.getObject().getOtherPart();
                                            }
                                        },
                                        new AbstractReadOnlyModel<String>() {
                                            @Override
                                            public String getObject() {
                                                final Play play = playModel.getObject();
                                                String channel = play.getFlow().getChannel();
                                                if ( channel == null ) channel = "no channel";
                                                return MessageFormat.format(
                                                        "{0} ({1})",
                                                        play.getOtherPartName(),
                                                        channel );
                                            }
                                        } ) );
                    }
                } );
        // Critical column
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Priority" ),
                "criticality", "criticality" ) );                                 // NON-NLS

        // provider and table
        add( new AjaxFallbackDefaultDataTable<Play>(
                "playbook", columns, new SortablePlaysProvider( player ), pageSize ) );
    }
}
