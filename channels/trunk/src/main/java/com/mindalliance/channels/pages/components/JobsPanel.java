package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Job;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.util.SemMatch;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdateProjectObject;
import com.mindalliance.channels.pages.ModelObjectLink;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.text.Collator;

/**
 * Jobs panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 18, 2009
 * Time: 7:45:01 PM
 */
public class JobsPanel extends AbstractCommandablePanel {

    private static Collator collator = Collator.getInstance();
    private WebMarkupContainer jobsDiv;
    private Job selectedJob;
    private WebMarkupContainer flowsDiv;

    public JobsPanel( String id, IModel<Organization> model ) {
        super( id, model );
        init();
    }

    private void init() {
        addJobsTable();
        flowsDiv = new WebMarkupContainer( "flows" );
        flowsDiv.setOutputMarkupId( true );
        add( flowsDiv );
        addJobPlaybook( null );
        setOutputMarkupId( true );
    }

    private void addJobsTable() {
        jobsDiv = new WebMarkupContainer( "jobsDiv" );
        jobsDiv.setOutputMarkupId( true );
        addOrReplace( jobsDiv );
        jobsDiv.addOrReplace( makeJobsTable() );
    }

    private ListView<JobWrapper> makeJobsTable() {
        List<JobWrapper> jobWrappers = getJobWrappers();
        return new ListView<JobWrapper>(
                "jobs",
                jobWrappers
        ) {
            protected void populateItem( ListItem<JobWrapper> item ) {
                addConfirmedCell( item );
                addEntityCell( item, "actor" );
                addTitleCell( item );
                addEntityCell( item, "role" );
                addEntityCell( item, "jurisdiction" );
                addShowFlowsCell( item );
            }
        };
    }

    private void addJobPlaybook( Job job ) {
        selectedJob = job;
        Label jobLabel;
        Component jobPlaybook;
        if ( job == null ) {
            jobLabel = new Label( "job", "?" );
            jobPlaybook = new Label( "playbook", "No playbook" );
        } else {
            jobLabel = new Label( "job", selectedJob.toString() );
            jobPlaybook = new PlaybookPanel(
                    "playbook",
                    new PropertyModel<ResourceSpec>( this, "jobResourceSpec" ) );
        }
        flowsDiv.addOrReplace( jobLabel );
        flowsDiv.addOrReplace( jobPlaybook );
        makeVisible( flowsDiv, job != null );
    }

    public ResourceSpec getJobResourceSpec() {
        return selectedJob.resourceSpec( getOrganization() );
    }

    private void addConfirmedCell( ListItem<JobWrapper> item ) {
        final JobWrapper jobWrapper = item.getModel().getObject();
        CheckBox confirmedCheckBox = new CheckBox(
                "confirmed",
                new PropertyModel<Boolean>( jobWrapper, "confirmed" ) );
        makeVisible( confirmedCheckBox, jobWrapper.canBeConfirmed() );
        item.addOrReplace( confirmedCheckBox );
        confirmedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                if ( !getOrganization().getJobs().contains( jobWrapper.getJob() ) ) {
                    addJobPlaybook( null );
                    target.addComponent( flowsDiv );
                }
                jobsDiv.addOrReplace( makeJobsTable() );
                target.addComponent( jobsDiv );
                update( target, new Change(
                        Change.Type.Updated,
                        getOrganization(),
                        "jobs"
                ) );
            }
        } );
    }

    private void addEntityCell( ListItem<JobWrapper> item, final String property ) {
        item.setOutputMarkupId( true );
        JobEntityPanel entityPanel = new JobEntityPanel( property, item );
        item.add( entityPanel );
    }

    private void addTitleCell( final ListItem<JobWrapper> item ) {
        final JobWrapper jobWrapper = item.getModel().getObject();
        final List<String> choices = getService().findAllJobTitles();
        TextField<String> titleField = new AutoCompleteTextField<String>(
                "title",
                new PropertyModel<String>( jobWrapper, "title" ) ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( SemMatch.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        titleField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addConfirmedCell( item );
                target.addComponent( item );
                if ( !jobWrapper.isMarkedForCreation() ) {
                    update( target, new Change(
                            Change.Type.Updated,
                            getOrganization(),
                            "jobs"
                    ) );
                }
            }
        } );
        item.add( titleField );
    }

    private void addShowFlowsCell( ListItem<JobWrapper> item ) {
        final JobWrapper jobWrapper = item.getModel().getObject();
        AjaxFallbackLink<String> flowsLink = new AjaxFallbackLink<String>(
                "flows-link",
                new Model<String>( "Show flows" ) ) {
            public void onClick( AjaxRequestTarget target ) {
                addJobPlaybook( jobWrapper.getJob() );
                target.addComponent( flowsDiv );
            }
        };
        makeVisible( flowsLink, !jobWrapper.isMarkedForCreation() && jobWrapper.hasFlows() );
        item.add( flowsLink );

    }

    // Used by PropertyModel
    private List<JobWrapper> getJobWrappers() {
        List<JobWrapper> jobWrappers = new ArrayList<JobWrapper>();
        // Confirmed jobs
        for ( Job job : getOrganization().getJobs() ) {
            jobWrappers.add( new JobWrapper( job, true ) );
        }
        // Unconfirmed jobs
        for ( Job job : getService().findUnconfirmedJobs( getOrganization() ) ) {
            jobWrappers.add( new JobWrapper( job, false ) );
        }
        Collections.sort( jobWrappers, new Comparator<JobWrapper>() {
            public int compare( JobWrapper jw1, JobWrapper jw2 ) {
                return collator.compare( jw1.getActor().normalize(), jw2.getActor().normalize() );
            }
        } );
        // New job
        JobWrapper creationJobWrapper = new JobWrapper( new Job(), false );
        creationJobWrapper.setMarkedForCreation( true );
        jobWrappers.add( creationJobWrapper );
        return jobWrappers;
    }

    private Organization getOrganization() {
        return (Organization) getModel().getObject();
    }

    public class JobWrapper implements Serializable {

        private Job job;
        private boolean markedForCreation;
        private boolean confirmed;

        protected JobWrapper( Job job, boolean confirmed ) {
            this.job = job;
            markedForCreation = false;
            this.confirmed = confirmed;
        }

        public Job getJob() {
            return job;
        }

        public void setJob( Job job ) {
            this.job = job;
        }

        public boolean isMarkedForCreation() {
            return markedForCreation;
        }

        public void setMarkedForCreation( boolean markedForCreation ) {
            this.markedForCreation = markedForCreation;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public void setConfirmed( boolean confirmed ) {
            this.confirmed = confirmed;
            if ( confirmed ) {
                doCommand( new UpdateProjectObject(
                        getOrganization(),
                        "jobs",
                        job,
                        UpdateObject.Action.Add
                ) );

            } else if ( !markedForCreation ) {
                doCommand( new UpdateProjectObject(
                        getOrganization(),
                        "jobs",
                        job,
                        UpdateObject.Action.Remove
                ) );
                if ( job.getActor() != null ) getCommander().cleanup( Actor.class, job.getActor().getName() );
                if ( job.getRole() != null ) getCommander().cleanup( Role.class, job.getRole().getName() );
                if ( job.getJurisdiction() != null )
                    getCommander().cleanup( Place.class, job.getJurisdiction().getName() );
            }
        }

        public String getTitle() {
            return job.getTitle();
        }

        public void setTitle( String title ) {
            String oldTitle = getTitle();
            if ( markedForCreation ) {
                job.setTitle( title );
            } else {
                if ( !isSame( title, oldTitle ) ) {
                    int index = getOrganization().getJobs().indexOf( job );
                    if ( index >= 0 ) {
                        doCommand( new UpdateProjectObject(
                                getOrganization(),
                                "jobs[" + index + "].title",
                                title,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
            }
        }

        public Actor getActor() {
            return job.getActor();
        }

        public String getActorName() {
            Actor actor = job.getActor();
            return actor != null ? actor.getName() : "";
        }

        public void setActorName( String name ) {
            String oldName = getActorName();
            if ( name != null && !isSame( name, oldName ) ) {
                Actor actor = getService().findOrCreate( Actor.class, name );
                if ( markedForCreation ) {
                    job.setActor( actor );
                } else {
                    int index = getOrganization().getJobs().indexOf( job );
                    if ( index >= 0 ) {
                        doCommand( new UpdateProjectObject(
                                getOrganization(),
                                "jobs[" + index + "].actor",
                                actor,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
                getCommander().cleanup( Actor.class, oldName );
            }
        }

        public Role getRole() {
            return job.getRole();
        }

        public String getRoleName() {
            Role role = job.getRole();
            return role != null ? role.getName() : "";
        }

        public void setRoleName( String name ) {
            String oldName = getRoleName();
            if ( name != null && !isSame( name, oldName ) ) {
                Role role = getService().findOrCreate( Role.class, name );
                if ( markedForCreation ) {
                    job.setRole( role );
                } else {
                    int index = getOrganization().getJobs().indexOf( job );
                    if ( index >= 0 ) {
                        doCommand( new UpdateProjectObject(
                                getOrganization(),
                                "jobs[" + index + "].role",
                                role,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
                getCommander().cleanup( Role.class, oldName );
            }
        }

        public Place getJurisdiction() {
            return job.getJurisdiction();
        }

        public String getJurisdictionName() {
            Place jurisdiction = job.getJurisdiction();
            return jurisdiction != null ? jurisdiction.getName() : "";
        }

        public void setJurisdictionName( String name ) {
            String oldName = getJurisdictionName();
            if ( name != null && !isSame( name, oldName ) ) {
                Place jurisdiction = getService().findOrCreate( Place.class, name );
                if ( markedForCreation ) {
                    job.setJurisdiction( jurisdiction );
                } else {
                    int index = getOrganization().getJobs().indexOf( job );
                    if ( index >= 0 ) {
                        doCommand( new UpdateProjectObject(
                                getOrganization(),
                                "jobs[" + index + "].jurisdiction",
                                jurisdiction,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
                getCommander().cleanup( Place.class, oldName );
            }
        }

        public boolean canBeConfirmed() {
            return job.getActor() != null
                    && job.getRole() != null
                    && !job.getTitle().isEmpty();
        }

        public boolean hasFlows() {
            return !getService().findAllPlays( job.resourceSpec( getOrganization() ) ).isEmpty();
        }

    }

    public class JobEntityPanel extends Panel {

        private String property;
        private JobWrapper jobWrapper;
        private ListItem<JobWrapper> item;

        public JobEntityPanel( String property, ListItem<JobWrapper> item ) {
            super( property );
            this.property = property;
            this.item = item;
            jobWrapper = item.getModel().getObject();
            init();
        }

        private void init() {
            // link
            AjaxFallbackLink link = new AjaxFallbackLink( "entity-link" ) {
                public void onClick( AjaxRequestTarget ajaxRequestTarget ) {
                    ModelObject mo = (ModelObject) CommandUtils.getProperty( jobWrapper, property, null );
                    if ( mo != null ) setResponsePage(
                            new RedirectPage( ModelObjectLink.linkForEntity( mo ) ) );
                }
            };
            add( link );
            Label label = new Label( "entity", new PropertyModel<String>( jobWrapper, property + "Name" ) );
            link.add( label );
            makeVisible( link, !jobWrapper.isMarkedForCreation() );
            Class<? extends ModelObject> moClass =
                    property.equals( "actor" )
                            ? Actor.class
                            : ( property.equals( "role" )
                            ? Role.class
                            : Place.class );
            final List<String> choices = getService().findAllNames( moClass );
            // text field
            TextField<String> entityField = new AutoCompleteTextField<String>(
                    "entity-field",
                    new PropertyModel<String>( jobWrapper, property + "Name" ) ) {
                protected Iterator<String> getChoices( String s ) {
                    List<String> candidates = new ArrayList<String>();
                    for ( String choice : choices ) {
                        if ( SemMatch.matches( s, choice ) ) candidates.add( choice );
                    }
                    return candidates.iterator();

                }
            };
            entityField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                protected void onUpdate( AjaxRequestTarget target ) {
                    addConfirmedCell( item );
                    target.addComponent( item );
                    if ( !jobWrapper.isMarkedForCreation() ) {
                        update( target, new Change(
                                Change.Type.Updated,
                                getOrganization(),
                                "jobs" ) );
                    }
                }
            } );
            add( entityField );
            makeVisible( entityField, jobWrapper.isMarkedForCreation() );
        }

    }


}
