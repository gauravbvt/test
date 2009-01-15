package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.analysis.profiling.SortableResourceSpecProvider;
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
public class DirectoryPanel extends AbstractTablePanel<ResourceSpec> {
    /**
     * A resource container or an implied set of resources
     */
    private ResourceSpec resource;

    public DirectoryPanel( String id, IModel<ResourceSpec> model ) {
        super( id, model );
        setRenderBodyOnly( true );
        resource = model.getObject();
        init();
    }

    private void init() {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        // columns
        if ( resource.isAnyActor() )
            columns.add( makeLinkColumn( "Name", "actor", "actor.name", EMPTY ) );
        if ( resource.isAnyRole() )
            columns.add( makeLinkColumn( "Role", "role", "role.name", EMPTY ) );
        if (  resource.isAnyOrganization() )
            columns.add( makeLinkColumn( "Organization", "organization", "organization.name", EMPTY ) );
        if ( resource.isAnyJurisdiction() )
            columns.add( makeLinkColumn( "Jurisdiction", "jurisdiction", "jurisdiction.name", EMPTY ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Channels" ),
                "channelsString", "channelsString" ) );                           // NON-NLS
        columns.add( makeResourceLinkColumn( "", "(view profile)" ) );
        // provider and table
        add( new AjaxFallbackDefaultDataTable<ResourceSpec>(
                "directory", columns, new SortableResourceSpecProvider( resource ), getPageSize() ) );
    }
}
