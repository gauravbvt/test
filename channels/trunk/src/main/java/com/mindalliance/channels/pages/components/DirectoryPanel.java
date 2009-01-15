package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.pages.ProfileLink;
import com.mindalliance.channels.analysis.profiling.SortableContactProvider;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.AttributeModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * A resource directory
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 12, 2009
 * Time: 8:10:00 PM
 */
public class DirectoryPanel extends AbstractTablePanel<ResourceSpec> {
    /**
     * A resource container or an implied set of resources
     */
    private ResourceSpec resourceSpec;

    public DirectoryPanel( String id, IModel<ResourceSpec> model ) {
        super( id, model );
        setRenderBodyOnly( true );
        resourceSpec = model.getObject();
        init();
    }

    private void init() {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        // columns
        columns.add( makeLinkColumn( "Name", "actor", "actor.name", EMPTY ) );
        columns.add( makeLinkColumn( "Role", "role", "role.name", EMPTY ) );
        columns.add( makeLinkColumn( "Organization", "organization", "organization.name", EMPTY ) );
        columns.add( makeLinkColumn( "Jurisdiction", "jurisdiction", "jurisdiction.name", EMPTY ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Channels" ),
                "channelsString", "channelsString" ) );                           // NON-NLS
        // Column with link to profile for row's resourceSpec
        columns.add( new AbstractColumn<ResourceSpec>( new Model<String>( "" ) ) {
            public void populateItem( Item<ICellPopulator<ResourceSpec>> cellItem,
                                      final String id,
                                      final IModel<ResourceSpec> model ) {
                cellItem.add( new ProfileLink( id,
                        new AbstractReadOnlyModel<ResourceSpec>() {
                            public ResourceSpec getObject() {
                                return model.getObject();
                            }
                        },
                        new AbstractReadOnlyModel<String>() {
                            public String getObject() {
                                return "(view " + model.getObject().toString() + ")";
                            }
                        }
                ) );
                cellItem.add( ( new AttributeModifier( "class", true, new Model<String>( "link" ) ) ) );
            }
        } );
        // provider and table
        add( new AjaxFallbackDefaultDataTable<ResourceSpec>(
                "directory", columns, new SortableContactProvider( resourceSpec ), getPageSize() ) );
    }
}
