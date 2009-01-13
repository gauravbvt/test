package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Resourceable;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Jurisdiction;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.analysis.profiling.Resource;
import com.mindalliance.channels.analysis.profiling.SortableResourceProvider;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

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
public class DirectoryPanel extends AbstractTablePanel<Resource> {
    /**
     * A resource container or an implied set of resources
     */
    private Resourceable resourceable;

    public DirectoryPanel( String id, IModel<? extends Resourceable> model ) {
        super( id, model );
        setRenderBodyOnly( true );
        resourceable = model.getObject();
        init();
    }

    private void init() {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        // columns
        if ( ! (resourceable instanceof Actor ) )
        columns.add( makeLinkColumn( "Name", "actor", "actor.name", EMPTY ) );
        if ( ! (resourceable instanceof Role) )
            columns.add( makeLinkColumn( "Role", "role", "role.name", EMPTY ) );
        if ( ! (resourceable instanceof Organization ) )
            columns.add( makeLinkColumn( "Organization", "organization", "organization.name", EMPTY ) );
        if ( ! (resourceable instanceof Jurisdiction ) )
            columns.add( makeLinkColumn( "Jurisdiction", "jurisdiction", "jurisdiction.name", EMPTY ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Channels" ),
                "channelsString", "channelsString" ) );                           // NON-NLS
        // provider and table
        add( new AjaxFallbackDefaultDataTable<Resource>(
                "directory", columns, new SortableResourceProvider( resourceable ), getPageSize() ) );
    }
}
