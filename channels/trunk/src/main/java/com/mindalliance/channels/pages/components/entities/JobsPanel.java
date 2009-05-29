package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.NameRangePanel;
import com.mindalliance.channels.pages.components.NameRangeable;
import com.mindalliance.channels.util.NameRange;
import com.mindalliance.channels.util.Play;
import com.mindalliance.channels.util.SemMatch;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Jobs panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 18, 2009
 * Time: 7:45:01 PM
 */
public class JobsPanel extends AbstractCommandablePanel implements NameRangeable {
    /**
     * Maximum number of jobs to show at a time.
     */
    private static final int MAX_JOB_ROWS = 5;
    /**
     * Collator.
     */
    private static Collator collator = Collator.getInstance();
    /**
     * Jobs container.
     */
    private WebMarkupContainer jobsDiv;
    /**
     * Selected job.
     */
    private Job selectedJob;
    /**
     * Flows container.
     */
    private WebMarkupContainer flowsDiv;
    /**
     * The range of names of actors which jobs to display.
     */
    private NameRange jobActorRange;
    /**
     * A name range panel.
     */
    private NameRangePanel rangePanel;

    public JobsPanel( String id, IModel<Organization> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        addJobs();
        flowsDiv = new WebMarkupContainer( "flows" );
        flowsDiv.setOutputMarkupId( true );
        add( flowsDiv );
        addJobPlaybook( null );
        setOutputMarkupId( true );
    }

    public void setNameRange( AjaxRequestTarget target, NameRange range ) {
        jobActorRange = range;
        jobsDiv.addOrReplace( makeJobsTable() );
        rangePanel.setSelected( target, range );
        target.addComponent( jobsDiv );
    }

    private void addJobs() {
        jobsDiv = new WebMarkupContainer( "jobsDiv" );
        jobsDiv.setOutputMarkupId( true );
        add( jobsDiv );
        rangePanel = new NameRangePanel(
                "ranges",
                new PropertyModel<List<String>>( this, "jobActorLastNames" ),
                MAX_JOB_ROWS,
                this,
                "All last names" );
        jobsDiv.add( rangePanel );
        jobsDiv.addOrReplace( makeJobsTable() );
    }

    private ListView<JobWrapper> makeJobsTable() {
        List<JobWrapper> jobWrappers = getJobWrappers();
        return new ListView<JobWrapper>(
                "jobs",
                jobWrappers
        ) {
            /** {@inheritDoc} */
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
            jobPlaybook = new PlaysTablePanel(
                    "playbook",
                    new PropertyModel<ResourceSpec>( this, "jobResourceSpec" ),
                    new Model<Boolean>(false),
                    5,
                    getExpansions() );
        }
        flowsDiv.addOrReplace( jobLabel );
        flowsDiv.addOrReplace( jobPlaybook );
        makeVisible( flowsDiv, job != null );
    }

    /**
     * Get resource spec of selected job.
     *
     * @return a resource spec
     */
    public ResourceSpec getJobResourceSpec() {
        ResourceSpec spec = selectedJob.resourceSpec( getOrganization() );
        spec.setActor( null );
        return spec;
    }

    private void addConfirmedCell( ListItem<JobWrapper> item ) {
        final JobWrapper jobWrapper = item.getModel().getObject();
        final CheckBox confirmedCheckBox = new CheckBox(
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
        final List<String> choices = getQueryService().findAllJobTitles();
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
                if ( !jobWrapper.isMarkedForCreation() && jobWrapper.isConfirmed() ) {
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

    /**
     * Get the list of all job actor names.
     *
     * @return a list of strings
     */
    public List<String> getJobActorLastNames() {
        List<String> names = new ArrayList<String>();
        for ( Job job : getOrganization().getJobs() ) {
            names.add( job.getActorLastName() );
        }
        for ( Job job : getQueryService().findUnconfirmedJobs( getOrganization() ) ) {
            names.add( job.getActorLastName() );
        }
        return names;
    }


    // Used by PropertyModel
    private List<JobWrapper> getJobWrappers() {
        List<JobWrapper> jobWrappers = new ArrayList<JobWrapper>();
        // Confirmed jobs
        for ( Job job : getOrganization().getJobs() ) {
            if ( jobActorRange == null
                    || jobActorRange.contains( job.getActorLastName().trim().toLowerCase() ) )
                jobWrappers.add( new JobWrapper( job, true ) );
        }
        // Unconfirmed jobs
        for ( Job job : getQueryService().findUnconfirmedJobs( getOrganization() ) ) {
            if ( jobActorRange == null
                    || jobActorRange.contains( job.getActorLastName().trim().toLowerCase() ) )
                jobWrappers.add( new JobWrapper( job, false ) );
        }
        Collections.sort( jobWrappers, new Comparator<JobWrapper>() {
            public int compare( JobWrapper jw1, JobWrapper jw2 ) {
                return collator.compare( jw1.getNormalizedActorName(), jw2.getNormalizedActorName() );
            }
        } );
        // New job
        JobWrapper creationJobWrapper = new JobWrapper( new Job(), false );
        creationJobWrapper.setMarkedForCreation( true );
        jobWrappers.add( creationJobWrapper );
        return jobWrappers;
    }

    /**
     * Get edited organization.
     *
     * @return an organization
     */
    public Organization getOrganization() {
        return (Organization) getModel().getObject();
    }

    private List<Play> findAllPlays( Job job ) {
        ResourceSpec spec = job.resourceSpec( getOrganization() );
        spec.setActor( null );
        return getQueryService().findAllPlays( spec, false );

    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        if ( change.isUpdated() ) {
            setNameRange( target, rangePanel.getRangeFor( selectedJob.getActorLastName() ) );
        }
        super.updateWith( target, change );
    }

    /**
     * Job wrapper.
     */
    public class JobWrapper implements Serializable {
        /**
         * Job.
         */
        private Job job;
        /**
         * Whether to be created.
         */
        private boolean markedForCreation;
        /**
         * Whether confirmed.
         */
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
                assert markedForCreation;
                doCommand( new UpdatePlanObject(
                        getOrganization(),
                        "jobs",
                        job,
                        UpdateObject.Action.Add
                ) );
                selectedJob = job;
            } else if ( !markedForCreation ) {
                doCommand( new UpdatePlanObject(
                        getOrganization(),
                        "jobs",
                        job,
                        UpdateObject.Action.Remove
                ) );
                ResourceSpec resourceSpec = job.resourceSpec( getOrganization() );
                if ( resourceSpec.getActor() != null )
                    getCommander().cleanup( Actor.class, resourceSpec.getActor().getName() );
                if ( resourceSpec.getRole() != null )
                    getCommander().cleanup( Role.class, resourceSpec.getRole().getName() );
                if ( resourceSpec.getJurisdiction() != null )
                    getCommander().cleanup( Place.class, resourceSpec.getJurisdiction().getName() );
            }
        }

        public String getTitle() {
            return job.getTitle();
        }

        public void setTitle( String title ) {
            String oldTitle = getTitle();
            if ( markedForCreation || !confirmed ) {
                job.setTitle( title );
            } else {
                if ( !isSame( title, oldTitle ) ) {
                    int index = getOrganization().getJobs().indexOf( job );
                    if ( index >= 0 ) {
                        doCommand( new UpdatePlanObject(
                                getOrganization(),
                                "jobs[" + index + "].title",
                                title,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
            }
        }

        private String getNormalizedActorName() {
            return getQueryService().findOrCreate( Actor.class, getActorName() ).normalize();
        }

        public String getActorName() {
            return job.getActorName();
        }

        public void setActorName( String name ) {
            String oldName = getActorName();
            if ( name != null && !isSame( name, oldName ) ) {
                if ( markedForCreation ) {
                    job.setActor( getQueryService().findOrCreate( Actor.class, name ) );
                } else {
                    int index = getOrganization().getJobs().indexOf( job );
                    if ( index >= 0 ) {
                        doCommand( new UpdatePlanObject(
                                getOrganization(),
                                "jobs[" + index + "].actorName",
                                name,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
                getCommander().cleanup( Actor.class, oldName );
            }
        }

        public String getRoleName() {
            return job.getRoleName();
        }


        public void setRoleName( String name ) {
            String oldName = getRoleName();
            if ( name != null && !isSame( name, oldName ) ) {
                if ( markedForCreation ) {
                    job.setRole( getQueryService().findOrCreate( Role.class, name ) );
                } else {
                    int index = getOrganization().getJobs().indexOf( job );
                    if ( index >= 0 ) {
                        doCommand( new UpdatePlanObject(
                                getOrganization(),
                                "jobs[" + index + "].roleName",
                                name,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
                getCommander().cleanup( Role.class, oldName );
            }
        }

        public String getJurisdictionName() {
            return job.getJurisdictionName();
        }

        public void setJurisdictionName( String name ) {
            String oldName = getJurisdictionName();
            if ( name != null && !isSame( name, oldName ) ) {
                if ( markedForCreation ) {
                    job.setJurisdiction( getQueryService().findOrCreate( Place.class, name ) );
                } else {
                    int index = getOrganization().getJobs().indexOf( job );
                    if ( index >= 0 ) {
                        doCommand( new UpdatePlanObject(
                                getOrganization(),
                                "jobs[" + index + "].jurisdictionName",
                                name,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
                getCommander().cleanup( Place.class, oldName );
            }
        }

        /**
         * Wrapped job can be confirmed?
         *
         * @return a boolean
         */
        public boolean canBeConfirmed() {
            return !job.getActorName().isEmpty()
                    && !job.getRoleName().isEmpty();
            // && !job.getTitle().isEmpty();
        }

        /**
         * Whether the job has flows.
         * @return a boolean
         */
        public boolean hasFlows() {
            return !findAllPlays(job).isEmpty();
        }

        /**
         * Get actor from its name.
         * Returns null if name is empty.
         *
         * @return an actor
         */
        public Actor getActor() {
            if ( !getActorName().isEmpty() )
                return getQueryService().findOrCreate( Actor.class, getActorName() );
            else
                return null;
        }

        /**
         * Get role from its name.
         * Returns null if name is empty.
         *
         * @return a role
         */
        public Role getRole() {
            if ( !getRoleName().isEmpty() )
                return getQueryService().findOrCreate( Role.class, getRoleName() );
            else
                return null;
        }

        /**
         * Get jurisdiction place from its name.
         * Returns null if name is empty.
         *
         * @return a place
         */
        public Place getJurisdiction() {
            if ( !getJurisdictionName().isEmpty() )
                return getQueryService().findOrCreate( Place.class, getJurisdictionName() );
            else
                return null;
        }
    }

    /**
     * Job entity panel.
     */
    public class JobEntityPanel extends Panel {
        /**
         * The property of the value edited.
         */
        private String property;
        /**
         *  Job wrapper.
         */
        private JobWrapper jobWrapper;
        /**
         * Item in which the property value appears.
         */
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
                public void onClick( AjaxRequestTarget target ) {
                    ModelObject mo = (ModelObject) CommandUtils.getProperty( jobWrapper, property, null );
                    if ( mo != null ) {
                        // setResponsePage(  new RedirectPage( ModelObjectLink.linkForEntity( mo ) ) );
                        update( target, new Change( Change.Type.Expanded, mo ) );
                    }
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
            final List<String> choices = getQueryService().findAllNames( moClass );
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
