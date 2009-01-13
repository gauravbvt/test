package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import com.mindalliance.channels.Resourceable;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Jurisdiction;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.analysis.profiling.Resource;
import com.mindalliance.channels.analysis.profiling.SortableResourceProvider;

import java.util.List;
import java.util.ArrayList;

/**
 * A resource directory
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 12, 2009
 * Time: 8:10:00 PM
 */
public class DirectoryPanel extends Panel {
    /**
     * A resource container or an implied set of resources
     */
    private Resourceable resourceable;
    /**
     * The default number of resources shown at a time
     */
    private int pageSize = 20;

    public DirectoryPanel( String id, IModel<? extends Resourceable> model ) {
        super( id, model );
        resourceable = model.getObject();
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
        // Actor column
        columns.add(
                new AbstractColumn<Resource>( new Model<String>( "Name" ),
                        "actor.name" ) {               // NON-NLS

                    public void populateItem(
                            Item<ICellPopulator<Resource>> cellItem, String id,
                            final IModel<Resource> jobModel ) {
                        cellItem.add(
                                new ModelObjectLink(
                                        id,
                                        new AbstractReadOnlyModel<Actor>() {
                                            @Override
                                            public Actor getObject() {
                                                return jobModel.getObject().getActor();
                                            }
                                        },
                                        new AbstractReadOnlyModel<String>() {
                                            @Override
                                            public String getObject() {
                                                return jobModel.getObject().getActorName();
                                            }
                                        } ) );
                    }
                } );
        // Role column
        columns.add(
                new AbstractColumn<Resource>( new Model<String>( "Role" ),
                        "role.name" ) {                                  // NON-NLS

                    public void populateItem(
                            Item<ICellPopulator<Resource>> cellItem, String id,
                            final IModel<Resource> jobModel ) {
                        cellItem.add(
                                new ModelObjectLink(
                                        id,
                                        new AbstractReadOnlyModel<Role>() {
                                            @Override
                                            public Role getObject() {
                                                return jobModel.getObject().getRole();
                                            }
                                        },
                                        new AbstractReadOnlyModel<String>() {
                                            @Override
                                            public String getObject() {
                                                return jobModel.getObject().getRole().getName();
                                            }
                                        } ) );
                    }
                } );
        // Organization column
        columns.add(
                new AbstractColumn<Resource>(
                        new Model<String>( "Organization" ),
                        "organization.name" ) {                                           // NON-NLS

                    public void populateItem(
                            Item<ICellPopulator<Resource>> cellItem, String id,
                            final IModel<Resource> jobModel ) {
                        cellItem.add(
                                new ModelObjectLink(
                                        id,
                                        new AbstractReadOnlyModel<Organization>() {
                                            @Override
                                            public Organization getObject() {
                                                return jobModel.getObject().getOrganization();
                                            }
                                        },
                                        new AbstractReadOnlyModel<String>() {
                                            @Override
                                            public String getObject() {
                                                return jobModel.getObject().getOrganizationName();
                                            }
                                        } ) );
                    }
                } );
        // Jurisdiction column
        columns.add(
                new AbstractColumn<Resource>(
                        new Model<String>( "Jurisdiction" ),
                        "jurisdiction.name" ) {                                           // NON-NLS

                    public void populateItem(
                            Item<ICellPopulator<Resource>> cellItem, String id,
                            final IModel<Resource> jobModel ) {
                        cellItem.add(
                                new ModelObjectLink(
                                        id,
                                        new AbstractReadOnlyModel<Jurisdiction>() {
                                            @Override
                                            public Jurisdiction getObject() {
                                                return jobModel.getObject().getJurisdiction();
                                            }
                                        },
                                        new AbstractReadOnlyModel<String>() {
                                            @Override
                                            public String getObject() {
                                                return jobModel.getObject().getJurisdictionName();
                                            }
                                        } ) );
                    }
                } );

        // Channels column
        columns.add( new PropertyColumn<String>(
                new Model<String>( "Channels" ),
                "channelsString", "channelsString" ) );                           // NON-NLS

        // provider and table
        add( new AjaxFallbackDefaultDataTable<Resource>(
                "directory", columns, new SortableResourceProvider( resourceable ), pageSize ) );
    }
}
