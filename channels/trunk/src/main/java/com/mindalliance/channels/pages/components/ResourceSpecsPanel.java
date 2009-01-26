package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Deletable;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.pages.ProfileLink;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.html.basic.Label;
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
    /**
     * Resource specifications shown in panel
     */
    private List<ResourceSpec> resourceSpecs;

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
        columns.add( makeColumn( "", "kind", "italic", EMPTY ) );
        // view profile column
        columns.add( new AbstractColumn<ResourceSpec>( new Model<String>( "" ) ) {
            public void populateItem( Item<ICellPopulator<ResourceSpec>> cellItem,
                                      final String id,
                                      final IModel<ResourceSpec> model ) {
                final ResourceSpec resourceSpec = model.getObject();
                if ( !resourceSpec.isEntityOnly() ) {
                    cellItem.add( new ProfileLink( id,
                            new AbstractReadOnlyModel<ResourceSpec>() {
                                public ResourceSpec getObject() {
                                    return resourceSpec;
                                }
                            },
                            new AbstractReadOnlyModel<String>() {
                                public String getObject() {
                                    return "(view " + resourceSpec.toString() + ")";
                                }
                            }
                    ) );
                    cellItem.add( new AttributeModifier( "class",
                                                            true,
                                                            new Model<String>( "link" ) ) );
                } else {
                    cellItem.add( new Label( id, new Model<String>( "" ) ) );
                }
            }
        } );
        // delete column
        columns.add( new AbstractColumn<ResourceSpec>( new Model<String>( "Delete" ) ) {
            public void populateItem( Item<ICellPopulator<ResourceSpec>> cellItem,
                                      final String id,
                                      final IModel<ResourceSpec> model ) {
                cellItem.add(
                        new DeletePanel( id,
                                         new Model<Deletable>(
                                                 new DeletableWrapper( model.getObject() ) ) ) );
            }
        } );
        // table and providers of resources specified resources need to kwno how to contact
        add( new AjaxFallbackDefaultDataTable<ResourceSpec>(
                "resourceSpecs",
                columns,
                new SortableBeanProvider<ResourceSpec>( resourceSpecs, "role.name" ),
                getPageSize() ) );

    }

    //==================================================
    /**
     * A wrapper to keep track of the deletion state of an attachment.
     */
    public static class DeletableWrapper implements Deletable {

        /**
         * The underlying attachment.
         */
        private ResourceSpec resourceSpec;

        /**
         * True if user marked item for deletion.
         */
        private boolean markedForDeletion;

        public DeletableWrapper( ResourceSpec resourceSpec ) {
            this.resourceSpec = resourceSpec;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isMarkedForDeletion() {
            return markedForDeletion;
        }

        /**
         * {@inheritDoc}
         */
        public void setMarkedForDeletion( boolean delete ) {
            markedForDeletion = delete;
            if ( delete ) {
                Project.dao().removeResourceSpec( resourceSpec );
            }
        }

        public ResourceSpec getResourceSpec() {
            return resourceSpec;
        }
    }
}
