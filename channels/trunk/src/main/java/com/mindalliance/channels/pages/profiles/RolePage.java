package com.mindalliance.channels.pages.profiles;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.Jurisdiction;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.analysis.profiling.Job;
import com.mindalliance.channels.analysis.profiling.Play;
import com.mindalliance.channels.analysis.profiling.SortableJobsProvider;
import com.mindalliance.channels.analysis.profiling.SortablePlaysProvider;
import com.mindalliance.channels.pages.ModelObjectLinkPanel;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.components.IssuesPanel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Role profile page
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 7, 2009
 * Time: 12:50:01 PM
 */
public class RolePage extends WebPage {

    /**
     * The role 'id' parameter in the URL.
     */
    static final String ID_PARM = "id";                                                   // NON-NLS

    static final int PAGE_SIZE = 20;

    public RolePage( PageParameters parameters ) {
        super( parameters );
        try {
            init( parameters );
        } catch ( NotFoundException e ) {
            LoggerFactory.getLogger( getClass() ).error( "Role not found", e );
        }
    }

    private void init( PageParameters parameters ) throws NotFoundException {
        // setVersioned( false );
        // setStatelessHint( true );
        Role role = findRole( parameters );
        assert role != null;
        add( new Label( "title", new Model<String>( "Role: " + role.getName() ) ) );
        IssuesPanel issuesPanel = new IssuesPanel( "issues", new Model<ModelObject>( role ) );
        add( issuesPanel );
        Form roleForm = new Form( "role-form" );
        add( roleForm );
        WebMarkupContainer roleDetailsDiv = new WebMarkupContainer( "role-details" );
        roleForm.add( roleDetailsDiv );
        roleDetailsDiv.add(
                new TextField<String>( "name",                                            // NON-NLS
                        new PropertyModel<String>( role, "name" ) ) );
        roleDetailsDiv.add(
                new TextArea<String>( "description",                                      // NON-NLS
                        new PropertyModel<String>( role, "description" ) ) );
        addPlaybook( "playbook", role );
        addDirectory( "directory", role );
    }

    private void addPlaybook( String playbookId, Role role ) {
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
                        cellItem.add( new ModelObjectLinkPanel( id,
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
                                new ModelObjectLinkPanel(
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
                                                if (channel == null) channel = "no channel" ;
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
                playbookId, columns, new SortablePlaysProvider( role ), PAGE_SIZE ) );
    }

    private void addDirectory( String directoryId, Role role ) {
        final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        // Actor column
        columns.add(
                new AbstractColumn<Job>( new Model<String>( "Name" ),
                        "actor.name" ) {               // NON-NLS

                    public void populateItem(
                            Item<ICellPopulator<Job>> cellItem, String id,
                            final IModel<Job> jobModel ) {
                        cellItem.add(
                                new ModelObjectLinkPanel(
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
                new AbstractColumn<Job>( new Model<String>( "Role" ),
                        "role.name" ) {                                  // NON-NLS

                    public void populateItem(
                            Item<ICellPopulator<Job>> cellItem, String id,
                            final IModel<Job> jobModel ) {
                        cellItem.add(
                                new ModelObjectLinkPanel(
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
                new AbstractColumn<Job>(
                        new Model<String>( "Organization" ),
                        "organization.name" ) {                                           // NON-NLS

                    public void populateItem(
                            Item<ICellPopulator<Job>> cellItem, String id,
                            final IModel<Job> jobModel ) {
                        cellItem.add(
                                new ModelObjectLinkPanel(
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
                new AbstractColumn<Job>(
                        new Model<String>( "Jurisdiction" ),
                        "jurisdiction.name" ) {                                           // NON-NLS

                    public void populateItem(
                            Item<ICellPopulator<Job>> cellItem, String id,
                            final IModel<Job> jobModel ) {
                        cellItem.add(
                                new ModelObjectLinkPanel(
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
        add( new AjaxFallbackDefaultDataTable<Job>(
                directoryId, columns, new SortableJobsProvider( role ), PAGE_SIZE ) );
    }

    private Role findRole( PageParameters parameters ) throws NotFoundException {
        Role role = null;
        if ( parameters.containsKey( ID_PARM ) ) {
            Dao dao = Project.getProject().getDao();
            role = dao.findRole( parameters.getLong( ID_PARM ) );
        }
        return role;
    }
}
