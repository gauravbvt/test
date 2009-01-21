package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Deletable;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.pages.ProfileLink;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 21, 2009
 * Time: 10:46:12 AM
 */
public class ResourceSpecsPanel extends AbstractTablePanel {

    private ArrayList<ResourceSpec> resourceSpecs;

    public ResourceSpecsPanel( String id, IModel<ArrayList<ResourceSpec>> model ) {
        super( id, model );
        resourceSpecs = model.getObject();
        init();
    }

    private void init() {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        // columns
        columns.add( makeLinkColumn( "Actor", "actor", "actor.name", EMPTY ) );
        columns.add( makeLinkColumn( "Role", "role", "role.name", EMPTY ) );
        columns.add( makeLinkColumn( "Organization", "organization", "organization.name", EMPTY ) );
        columns.add( makeLinkColumn( "Jurisdiction", "jurisdiction", "jurisdiction.name", EMPTY ) );
/*
        // view profile column
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
                cellItem.add( new AttributeModifier( "class", true, new Model<String>( "link" ) ) );
            }
        } );
*/
        // delete column
        columns.add( new AbstractColumn<ResourceSpec>( new Model<String>( "Delete" ) ) {
            public void populateItem( Item<ICellPopulator<ResourceSpec>> cellItem,
                                      final String id,
                                      final IModel<ResourceSpec> model ) {
                cellItem.add( new DeletePanel( id, new Model<Deletable>( model.getObject() ) ) );
            }
        } );
        // table and providers of resources specified resources need to kwno how to contact
        add( new AjaxFallbackDefaultDataTable<ResourceSpec>(
                "resourceSpecs",
                columns,
                new SortableBeanProvider<ResourceSpec>( resourceSpecs, "name" ),
                getPageSize() ) );

    }
}
