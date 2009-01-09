package com.mindalliance.channels.pages.profiles;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.IModel;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.pages.Project;

import java.util.List;
import java.util.ArrayList;


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
    static final String ID_PARM = "id";                                       // NON-NLS
    static final int PAGE_SIZE = 20;

    public RolePage( PageParameters parameters ) {
        super( parameters );
        try {
            init( parameters );
        } catch ( NotFoundException e ) {
            setResponsePage( new ProfileNotFoundPage( e.getMessage() ) );
        }
    }

    private void init( PageParameters parameters ) throws NotFoundException {
        // setVersioned( false );
        // setStatelessHint( true );
        Role role = findRole( parameters );
        IssuesPanel issuesPanel = new IssuesPanel( "issues", new Model<ModelObject>( role ) );
        add( issuesPanel );
        Form roleForm = new Form( "role-form" );
        add( roleForm );
        WebMarkupContainer roleDetailsDiv = new WebMarkupContainer( "role-details" );
        roleForm.add( roleDetailsDiv );
        roleDetailsDiv.add( new TextField<String>( "name",                                     // NON-NLS
                new PropertyModel<String>( role, "name" ) ) );

        roleDetailsDiv.add( new TextArea<String>( "description",                              // NON-NLS
                new PropertyModel<String>( role, "description" ) ) );
        addPlaybook("playbook", role);
        addDirectory("directory", role);
    }

    private void addPlaybook( String id, final Role role ) {
        List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        // Scenario column
        columns.add(new AbstractColumn<Play> (new Model<String>("Scenario"), "part.scenario.name" ) {
            public void populateItem( Item<ICellPopulator<Play>> cellItem, String id, IModel<Play> playModel ) {
                cellItem.add(new ModelObjectLinkPanel(id, playModel.getObject().getPart().getScenario()));
            }
            
        });
        // Part column
        columns.add(new AbstractColumn<Play> (new Model<String>("Task"), "part.task" ) {
            public void populateItem( Item<ICellPopulator<Play>> cellItem, String id, IModel<Play> playModel ) {
                Part part = playModel.getObject().getPart();
                cellItem.add(new ModelObjectLinkPanel(id, part, part.getTask()));
            }
        });
        // Info column
        columns.add(new PropertyColumn<String>(new Model<String>("Info"), "part.info", "part.info" ));
        // Sent/received column
        columns.add(new PropertyColumn<String>(new Model<String>("Sent/received"), "kind", "kind" ));
        // To/from colum
        columns.add(new AbstractColumn<Play> (new Model<String>("To/from"), "otherPart.name" ) {
            public void populateItem( Item<ICellPopulator<Play>> cellItem, String id, IModel<Play> playModel ) {
                Play play = playModel.getObject();
                Part otherPart = play.getOtherPart();
                cellItem.add(new ModelObjectLinkPanel(id, otherPart, otherPart.getName()
                        + " (" + play.getFlow().getChannel() + ")"));
            }
        });
        // Critical column
        columns.add(new PropertyColumn<String>(new Model<String>("Priority"), "criticality", "criticality" ));
        // provider and table
        SortablePlaysProvider data = new SortablePlaysProvider( role );
        AjaxFallbackDefaultDataTable<Play> table = new AjaxFallbackDefaultDataTable<Play>(id, columns, data, PAGE_SIZE);
        add(table);
    }

    private void addDirectory( String id, Role role ) {
        List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        // Actor column
        columns.add(new AbstractColumn<Job> (new Model<String>("Name"), "actor.name" ) {
            public void populateItem( Item<ICellPopulator<Job>> cellItem, String id, IModel<Job> jobModel ) {
                Job job = jobModel.getObject();
                cellItem.add(new ModelObjectLinkPanel(id, job.getActor(), job.getActor().getName()));
            }
        });
        // Role column
        columns.add(new AbstractColumn<Job> (new Model<String>("Role"), "role.name" ) {
            public void populateItem( Item<ICellPopulator<Job>> cellItem, String id, IModel<Job> jobModel ) {
                Job job = jobModel.getObject();
                cellItem.add(new ModelObjectLinkPanel(id, job.getRole(), job.getRole().getName()));
            }
        });
        // Organization column
        columns.add(new AbstractColumn<Job> (new Model<String>("Organization"), "organization.name" ) {
            public void populateItem( Item<ICellPopulator<Job>> cellItem, String id, IModel<Job> jobModel ) {
                Job job = jobModel.getObject();
                cellItem.add(new ModelObjectLinkPanel(id, job.getOrganization(), job.getOrganization().getName()));
            }
        });
        // Jurisdiction column
        columns.add(new AbstractColumn<Job> (new Model<String>("Jurisdiction"), "jurisdiction.name" ) {
            public void populateItem( Item<ICellPopulator<Job>> cellItem, String id, IModel<Job> jobModel ) {
                Job job = jobModel.getObject();
                cellItem.add(new ModelObjectLinkPanel(id, job.getJurisdiction(), job.getJurisdiction().getName()));
            }
        });
        // Channels column
        columns.add(new PropertyColumn<String>(new Model<String>("Channels"), "channelsString", "channelsString" ));
        // provider and table
        SortableJobsProvider data = new SortableJobsProvider( role );
        AjaxFallbackDefaultDataTable<Job> table = new AjaxFallbackDefaultDataTable<Job>(id, columns, data, PAGE_SIZE);
        add(table);
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
