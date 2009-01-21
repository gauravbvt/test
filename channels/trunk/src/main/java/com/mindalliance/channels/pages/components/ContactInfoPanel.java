package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.analysis.profiling.SortableResourceSpecProvider;
import com.mindalliance.channels.pages.ProfileLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.AttributeModifier;

import java.util.List;
import java.util.ArrayList;

/**
 * A directory of all resources specified
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 1:43:01 PM
 */
public class ContactInfoPanel extends AbstractTablePanel<ResourceSpec> {
    /**
     * A resource specification
     */
    private ResourceSpec resourceSpec;

    public ContactInfoPanel( String id, IModel<ResourceSpec> model ) {
        super( id, model );
        setRenderBodyOnly( true );
        resourceSpec = model.getObject();
        init();
    }

    private void init() {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        // columns
        if ( resourceSpec.isAnyActor() )
            columns.add( makeLinkColumn( "Name", "actor", "actor.name", EMPTY ) );
        if ( resourceSpec.isAnyRole() )
            columns.add( makeLinkColumn( "Role", "role", "role.name", EMPTY ) );
        if ( resourceSpec.isAnyOrganization() )
            columns.add( makeLinkColumn( "Organization", "organization", "organization.name", EMPTY ) );
        if ( resourceSpec.isAnyJurisdiction() )
            columns.add( makeLinkColumn( "Jurisdiction", "jurisdiction", "jurisdiction.name", EMPTY ) );
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Channels" ),
                "channelsString", "channelsString" ) );                           // NON-NLS
        // Column with link to profile for row's resourceSpec
        columns.add( new AbstractColumn<ResourceSpec>( new Model<String>( "" ) ) {
            public void populateItem( Item<ICellPopulator<ResourceSpec>> cellItem,
                                      final String id,
                                      final IModel<ResourceSpec> model ) {
                if ( !model.getObject().equals( resourceSpec ) ) {
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
                    cellItem.add( new AttributeModifier( "class", true, new Model<String>( "link" ) ) );
                } else {
                    cellItem.add( new Label( id, "" ) );
                }
            }
        } );
        // table with provider of resources here specified
        add( new AjaxFallbackDefaultDataTable<ResourceSpec>(
                "contactInfo", columns, new SortableResourceSpecProvider( resourceSpec, true ), getPageSize() ) );
    }

}
