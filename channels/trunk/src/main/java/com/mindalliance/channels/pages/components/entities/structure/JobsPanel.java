package com.mindalliance.channels.pages.components.entities.structure;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.TransferJobs;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.query.Play;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.core.util.NameRange;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.NameRangePanel;
import com.mindalliance.channels.pages.components.NameRangeable;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
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
     * Unconfirmed job.
     */
    private Job unconfirmedJob;
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

    /**
     * Whether job transfer UI activated.
     */
    private boolean transferring;
    /**
     * Job transfer markup container.
     */
    private WebMarkupContainer jobTransferDiv;
    /**
     * Organization jobs are being transferred from.
     */
    private Organization transferFrom;
    /**
     * Jobs transfer panel.
     */
    private Component jobTransferPanel;
    /**
     * Whether copying the jobs.
     */
     private boolean copying = false;
    /**
     * Do transfer button.
     */
    private AjaxLink<String> doTransfer;


    public JobsPanel( String id, IModel<Organization> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        addJobs();
        addJobTransfers();
        flowsDiv = new WebMarkupContainer( "flows" );
        flowsDiv.setOutputMarkupId( true );
        addOrReplace( flowsDiv );
        addJobPlaybook( null );
    }

    public void setNameRange( AjaxRequestTarget target, NameRange range ) {
        jobActorRange = range;
        jobsDiv.addOrReplace( makeJobsTable() );
        rangePanel.setSelected( target, range );
        target.add( jobsDiv );
    }

    private void addJobs() {
        jobsDiv = new WebMarkupContainer( "jobsDiv" );
        jobsDiv.setOutputMarkupId( true );
        addOrReplace( jobsDiv );
        rangePanel = new NameRangePanel(
                "ranges",
                new PropertyModel<List<String>>( this, "jobActorNames" ),
                MAX_JOB_ROWS,
                this,
                "All last names" );
        jobsDiv.add( rangePanel );
        Label linkedLabel = new Label( "linkedHeader", "Is linked" );
        addTipTitle( linkedLabel, "A job is linked (to another, primary job for the same agent) when one automatically gets the linked job when hired in the primary job" );
        jobsDiv.add( linkedLabel );
        jobsDiv.addOrReplace( makeJobsTable() );
    }

    private ListView<JobWrapper> makeJobsTable() {
        List<JobWrapper> jobWrappers = getJobWrappers();
        final int count = jobWrappers.size();
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
                addEntityCell( item, "supervisor" );
                addBooleanCell( item, "linked", "linked" );
                addShowFlowsCell( item );
                item.add( new AttributeModifier(
                        "class",
                        new Model<String>( cssClasses( item, count ) ) ) );
            }
        };
    }

    private String cssClasses( ListItem<JobWrapper> item, int count ) {
        int index = item.getIndex();
        String cssClasses = index % 2 == 0 ? "even" : "odd";
        if ( index == count - 1 ) cssClasses += " last";
        return cssClasses;
    }

    private void addJobTransfers() {
        jobTransferDiv = new WebMarkupContainer( "transferDiv" );
        jobTransferDiv.setOutputMarkupId( true );
        makeVisible( jobTransferDiv, isTransferring() );
        addOrReplace( jobTransferDiv );
        DropDownChoice transferFromChoice = new DropDownChoice<Organization>(
                "fromOrganization",
                new PropertyModel<Organization>( this, "transferFrom" ),
                new PropertyModel<List<? extends Organization>>( this, "transferFromOrganizations" ),
                new IChoiceRenderer<Organization>() {
                    public Object getDisplayValue( Organization org ) {
                        if ( !JobsPanel.this.isLockedByOtherUser( org ) ) {
                            return org.getLabel();
                        } else {
                            return org.getLabel()
                                    + " (edited by "
                                    + getQueryService().findUserFullName( JobsPanel.this.getLockOwner( org ) ) + ")";
                        }
                    }

                    public String getIdValue( Organization org, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        transferFromChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addJobTransferPanel( jobTransferDiv );
                target.add( jobTransferPanel );
            }
        } );
        jobTransferDiv.add( transferFromChoice );
        addJobTransferPanel( jobTransferDiv );
        // take copies option
        CheckBox copyingCheckBox = new CheckBox(
                "copying",
                new PropertyModel<Boolean>( this, "copying" )
                );
        copyingCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        });
        jobTransferDiv.add( copyingCheckBox );
        // do transfer button
        doTransfer = new AjaxLink<String>(
                "doTransfer",
                new PropertyModel<String>( this, "transferButtonLabel" ) ) {
            public void onClick( AjaxRequestTarget target ) {
                if ( executeJobTransfers() ) {
                    addJobs();
                    addJobTransfers();
                    target.add( jobsDiv );
                    target.add( jobTransferDiv );
                    update( target, new Change(
                            Change.Type.Updated,
                            getOrganization(),
                            "jobs"
                    ) );
                }
            }
        };
        doTransfer.setOutputMarkupId( true );
        makeVisible( doTransfer, isTransferring() );
        jobTransferDiv.add( doTransfer );

        WebMarkupContainer transferContainer = new WebMarkupContainer( "transferContainer" );
        transferContainer.setOutputMarkupId( true );
        transferContainer.setVisible( getPlan().isDevelopment() );
        addOrReplace( transferContainer );
        CheckBox transferCheckBox = new CheckBox(
                "transfer",
                new PropertyModel<Boolean>( this, "transferring" ) );
        transferCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addJobTransferPanel( jobTransferDiv );
                makeVisible( jobTransferDiv, isTransferring() );
                target.add( jobTransferDiv );
            }
        } );
        transferCheckBox.setEnabled( isLockedByUser( getOrganization() ) );
        transferContainer.add( transferCheckBox );
    }

    private boolean executeJobTransfers() {
        List<Job> transferredJobs = getTransferredJobs();
        if ( transferredJobs.isEmpty() ) {
            return false;
        } else {
            doCommand( new TransferJobs( getUser().getUsername(), getTransferFrom(),
                    getOrganization(),
                    transferredJobs,
                    copying ) );
            return true;
        }
    }

    public boolean isCopying() {
        return copying;
    }

    public void setCopying( boolean copying ) {
        this.copying = copying;
    }

    private void addJobTransferPanel( WebMarkupContainer transferringDiv ) {
        if ( getTransferFrom() == null ) {
            jobTransferPanel = new Label( "jobTransfers", "" );
        } else {
            jobTransferPanel = new JobTransferTable(
                    "jobTransfers",
                    new PropertyModel<Organization>( this, "transferFrom" )
            );
        }
        jobTransferPanel.setOutputMarkupId( true );
        makeVisible( jobTransferPanel, getTransferFrom() != null );
        transferringDiv.addOrReplace( jobTransferPanel );
    }

    public boolean isTransferring() {
        return transferring;
    }

    public void setTransferring( boolean transferring ) {
        this.transferring = transferring;
        copying = false;
    }

    public Organization getTransferFrom() {
        return transferFrom;
    }

    public void setTransferFrom( Organization transferFrom ) {
        this.transferFrom = transferFrom;
    }

    public List<Organization> getTransferFromOrganizations() {
        List<Organization> orgs = new ArrayList<Organization>(
                getQueryService().listActualEntities( Organization.class ) );
        orgs.remove( getOrganization() );
        orgs.remove( Organization.UNKNOWN );
        Collections.sort( orgs, new Comparator<Organization>() {
            public int compare( Organization o1, Organization o2 ) {
                return Collator.getInstance().compare( o1.getName(), o2.getName() );
            }
        } );
        return orgs;
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
                    new Model<Boolean>( false ),
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
    public Specable getJobResourceSpec() {
        return getJobResourceSpec( selectedJob );
    }

    private Specable getJobResourceSpec( Job job ) {
        ResourceSpec spec = job.resourceSpec( getOrganization() );
        return new ResourceSpec(
                null, spec.getRole(), spec.getOrganization(), spec.getJurisdiction() );
    }

    private void addConfirmedCell( ListItem<JobWrapper> item ) {
        final JobWrapper jobWrapper = item.getModel().getObject();
        final CheckBox confirmedCheckBox = new CheckBox(
                "confirmed",
                new PropertyModel<Boolean>( jobWrapper, "confirmed" ) );
        makeVisible( confirmedCheckBox, jobWrapper.canBeConfirmed() );
        item.add( confirmedCheckBox );
        confirmedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                if ( !getOrganization().getJobs().contains( jobWrapper.getJob() ) ) {
                    addJobPlaybook( null );
                    target.add( flowsDiv );
                }
                jobsDiv.addOrReplace( makeJobsTable() );
                target.add( jobsDiv );
                update( target, new Change(
                        Change.Type.Updated,
                        getOrganization(),
                        "jobs"
                ) );
            }
        } );
        confirmedCheckBox.setEnabled( isLockedByUser( getOrganization() ) );
    }

    private void addBooleanCell( ListItem<JobWrapper> item, String id, String property ) {
        final JobWrapper jobWrapper = item.getModel().getObject();
        final CheckBox aCheckBox = new CheckBox(
                id,
                new PropertyModel<Boolean>( jobWrapper, property ) );
        item.add( aCheckBox );
        aCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                jobsDiv.addOrReplace( makeJobsTable() );
                target.add( jobsDiv );
                update( target, new Change(
                        Change.Type.Updated,
                        getOrganization(),
                        "jobs"
                ) );
            }
        } );
        Organization org = getOrganization();
        aCheckBox.setEnabled( isLockedByUser( org ) );
    }

    private void addEntityCell( ListItem<JobWrapper> item, final String property ) {
        item.setOutputMarkupId( true );
        JobEntityPanel entityPanel = new JobEntityPanel( property, item );
        item.add( entityPanel );
    }

    private void addTitleCell( final ListItem<JobWrapper> item ) {
        final JobWrapper jobWrapper = item.getModel().getObject();
        final List<String> choices = getQueryService().findAllJobTitles();
        AutoCompleteTextField<String> titleField = new AutoCompleteTextField<String>(
                "title",
                new PropertyModel<String>( jobWrapper, "title" ),
                getAutoCompleteSettings() ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( getQueryService().likelyRelated( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        titleField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addConfirmedCell( item );
                target.add( item );
                if ( !jobWrapper.isMarkedForCreation() && jobWrapper.isConfirmed() ) {
                    update( target, new Change(
                            Change.Type.Updated,
                            getOrganization(),
                            "jobs"
                    ) );
                }
            }
        } );
        titleField.setOutputMarkupId( true );
        titleField.setEnabled( isLockedByUser( getOrganization() ) );
        addInputHint( titleField, "A job title" );
        item.add( titleField );
    }

    private void addShowFlowsCell( ListItem<JobWrapper> item ) {
        final JobWrapper jobWrapper = item.getModel().getObject();
        AjaxLink<String> flowsLink = new AjaxLink<String>(
                "flows-link",
                new Model<String>( "Show flows" ) ) {
            public void onClick( AjaxRequestTarget target ) {
                addJobPlaybook( jobWrapper.getJob() );
                target.add( flowsDiv );
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
    public List<String> getJobActorNames() {
        List<String> names = new ArrayList<String>();
        for ( Job job : getOrganization().getJobs() ) {
            names.add( job.getActorName() );
        }
        for ( Job job : getQueryService().findUnconfirmedJobs( getOrganization() ) ) {
            names.add( job.getActorName() );
        }
        return names;
    }


    // Used by PropertyModel
    private List<JobWrapper> getJobWrappers() {
        List<JobWrapper> jobWrappers = new ArrayList<JobWrapper>();
        List<Job> confirmedJobs = getOrganization().getJobs();
        // Confirmed jobs
        for ( Job job : confirmedJobs ) {
            if ( jobActorRange == null
                    || jobActorRange.contains( job.getActorName().trim().toLowerCase() ) )
                jobWrappers.add( new JobWrapper( job, true ) );
        }
        // Unconfirmed jobs
        List<Job> unconfirmedJobs = getQueryService().findUnconfirmedJobs( getOrganization() );
        for ( Job job : unconfirmedJobs ) {
            if ( jobActorRange == null
                    || jobActorRange.contains( job.getActorName().trim().toLowerCase() ) )
                jobWrappers.add( new JobWrapper( job, false ) );
        }
        Collections.sort( jobWrappers, new Comparator<JobWrapper>() {
            public int compare( JobWrapper jw1, JobWrapper jw2 ) {
                return collator.compare( jw1.getNormalizedActorName(), jw2.getNormalizedActorName() );
            }
        } );
        if ( getPlan().isDevelopment() ) {
            // New job
            JobWrapper creationJobWrapper;
            // Use previously unconfirmed job if not null and not already implied
            if ( unconfirmedJob != null && !unconfirmedJobs.contains( unconfirmedJob ) ) {
                creationJobWrapper = new JobWrapper( unconfirmedJob, false );
                unconfirmedJob = null;
            } else {
                creationJobWrapper = new JobWrapper( new Job(), false );
            }
            creationJobWrapper.setMarkedForCreation( true );
            jobWrappers.add( creationJobWrapper );
        }
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
        return getQueryService().findAllPlays( getJobResourceSpec( job ), false );

    }

    /**
     * {@inheritDoc}
     */
    public void update( AjaxRequestTarget target, Object object, String action ) {
        makeVisible( doTransfer, !getTransferredJobs().isEmpty() );
        target.add( doTransfer );
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isUpdated() ) {
            if ( selectedJob != null )
                setNameRange( target, rangePanel.getRangeFor( selectedJob.getActorName() ) );
        }
        super.updateWith( target, change, updated );
    }

    private List<Job> getTransferredJobs() {
        if ( jobTransferPanel instanceof JobTransferTable ) {
            return ( (JobTransferTable) jobTransferPanel ).getTransferredJobs();
        } else {
            return new ArrayList<Job>();
        }
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
                doCommand( new UpdatePlanObject( getUser().getUsername(), getOrganization(),
                        "jobs",
                        job,
                        UpdateObject.Action.AddUnique ) );
                selectedJob = job;
            } else if ( !markedForCreation ) {
                unconfirmedJob = job;
                doCommand( new UpdatePlanObject( getUser().getUsername(), getOrganization(),
                        "jobs",
                        job,
                        UpdateObject.Action.Remove ) );
                Specable resourceSpec = job.resourceSpec( getOrganization() );
                if ( resourceSpec.getActor() != null )
                    getCommander().cleanup( Actor.class, resourceSpec.getActor().getName() );
                if ( resourceSpec.getRole() != null )
                    getCommander().cleanup( Role.class, resourceSpec.getRole().getName() );
                if ( resourceSpec.getJurisdiction() != null )
                    getCommander().cleanup( Place.class, resourceSpec.getJurisdiction().getName() );
                if ( job.getSupervisor() != null )
                    getCommander().cleanup( Actor.class, job.getSupervisor().getName() );

            }
        }

        public boolean isLinked() {
            return job.isLinked();
        }

        public void setLinked( boolean val ) {
            if ( markedForCreation || !confirmed ) {
                job.setLinked( val );
            } else {
                int index = getOrganization().getJobs().indexOf( job );
                    if ( index >= 0 ) {
                        doCommand( new UpdatePlanObject(
                                getUser().getUsername(),
                                getOrganization(),
                                "jobs[" + index + "].linked",
                                val,
                                UpdateObject.Action.Set ) );
                    }
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
                        doCommand( new UpdatePlanObject( getUser().getUsername(), getOrganization(),
                                "jobs[" + index + "].title",
                                title,
                                UpdateObject.Action.Set ) );
                    }
                }
            }
        }

        private String getNormalizedActorName() {
            return getQueryService().safeFindOrCreate( Actor.class, getActorName() ).getNormalizedName();
        }

        public String getActorName() {
            return job.getActorName();
        }

        public void setActorName( String name ) {
            String oldName = getActorName();
            if ( name != null && !isSame( name, oldName ) ) {
                if ( markedForCreation ) {
                    job.setActor( getQueryService().safeFindOrCreate( Actor.class, name ) );
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
                    job.setRole( getQueryService().safeFindOrCreateType( Role.class, name ) );
                }
                getCommander().cleanup( Role.class, oldName );
            }
        }

        public String getJurisdictionName() {
            return job.getJurisdictionName();
        }

        public void setJurisdictionName( String name ) {
            String oldName = getJurisdictionName();
            if ( !isSame( name, oldName ) ) {
                if ( markedForCreation ) {
                    if ( name != null )
                        job.setJurisdiction( getQueryService().safeFindOrCreate( Place.class, name ) );
                    else
                        job.setJurisdiction( null );
                }
                getCommander().cleanup( Place.class, oldName );
            }
        }

        public String getSupervisorName() {
            return job.getSupervisorName();
        }

        public void setSupervisorName( String name ) {
            String oldName = getSupervisorName();
            if ( !isSame( name, oldName ) ) {
                if ( markedForCreation ) {
                    if ( name != null )
                        job.setSupervisor( getQueryService().safeFindOrCreate( Actor.class, name ) );
                    else
                        job.setSupervisor( null );
                }
                getCommander().cleanup( Actor.class, oldName );
            }
        }


        /**
         * Wrapped job can be confirmed?
         *
         * @return a boolean
         */
        public boolean canBeConfirmed() {
            return !job.getActorName().isEmpty()
                    && !job.getRoleName().isEmpty()
                    && !job.getTitle().isEmpty();
        }

        /**
         * Whether the job has flows.
         *
         * @return a boolean
         */
        public boolean hasFlows() {
            return !findAllPlays( job ).isEmpty();
        }

        /**
         * Get actor from its name.
         * Returns null if name is empty.
         *
         * @return an actor
         */
        public Actor getActor() {
            if ( !getActorName().isEmpty() )
                return getQueryService().safeFindOrCreate( Actor.class, getActorName() );
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
                return getQueryService().safeFindOrCreate( Role.class, getRoleName() );
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
                return getQueryService().safeFindOrCreate( Place.class, getJurisdictionName() );
            else
                return null;
        }

         /**
         * Get jurisdiction place from its name.
         * Returns null if name is empty.
         *
         * @return a place
         */
        public Actor getSupervisor() {
            if ( !getSupervisorName().isEmpty() )
                return getQueryService().safeFindOrCreate( Actor.class, getSupervisorName() );
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
         * Job wrapper.
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
            AjaxLink link = new AjaxLink( "entity-link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    ModelEntity mo = (ModelEntity) ChannelsUtils.getProperty( jobWrapper, property, null );
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
            Class<? extends ModelEntity> moClass =
                    property.equals( "actor" )
                            ? Actor.class
                            : property.equals( "role" )
                            ? Role.class
                            : property.equals( "jurisdiction" )
                            ? Place.class
                            // supervisor
                            : Actor.class;
            final List<String> choices = getQueryService().findAllEntityNames(
                    moClass,
                    moClass == Role.class
                            ? ModelEntity.Kind.Type
                            : ModelEntity.Kind.Actual );
            // text field
            AutoCompleteTextField<String> entityField = new AutoCompleteTextField<String>(
                    "entity-field",
                    new PropertyModel<String>( jobWrapper, property + "Name" ),
                    getAutoCompleteSettings() ) {
                protected Iterator<String> getChoices( String s ) {
                    List<String> candidates = new ArrayList<String>();
                    for ( String choice : choices ) {
                        if ( property.equals( "role" ) ) {
                             if ( getQueryService().likelyRelated( s, choice ) )
                            candidates.add( choice );
                        }
                        else if ( Matcher.matches( s, choice ) )
                            candidates.add( choice );
                    }
                    return candidates.iterator();

                }
            };
            entityField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                protected void onUpdate( AjaxRequestTarget target ) {
                    addConfirmedCell( item );
                    target.add( item );
                    if ( !jobWrapper.isMarkedForCreation() ) {
                        update( target, new Change(
                                Change.Type.Updated,
                                getOrganization(),
                                "jobs" ) );
                    }
                }
            } );
            entityField.setOutputMarkupId( true );
            entityField.setEnabled( isLockedByUser( getOrganization() ) );
            addInputHint(
                    entityField,
                    ( property.equals( "role" )
                            ? "A role"
                            : property.equals( "actor" )
                            ? "An actual agent"
                            : property.equals( "jurisdiction")
                            ? "An actual place"
                            : "An actual agent" // supervisor
                    )
            );
            add( entityField );
            makeVisible( entityField, jobWrapper.isMarkedForCreation() );
        }

    }

    /**
     * Job transfer wrapper.
     */
    public class JobTransfer implements Serializable {
        /**
         * Job.
         */
        private Job job;
        /**
         * Whether transferred.
         */
        private boolean confirmed;

        public JobTransfer( Job job ) {
            this.job = job;
            confirmed = false;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public void setConfirmed( boolean confirmed ) {
            this.confirmed = confirmed;
        }

        public Job getJob() {
            return job;
        }

        public Actor getActor() {
            return job.getActor();
        }

        public Role getRole() {
            return job.getRole();
        }

        public Place getJurisdiction() {
            return job.getJurisdiction();
        }

        public Actor getSupervisor() {
            return job.getSupervisor();
        }

        public String getTitle() {
            return job.getTitle();
        }

    }

    public class JobTransferTable extends AbstractFilterableTablePanel {

        /**
         * Job transfer origin.
         */
        private IModel<Organization> fromOrgModel;
        /**
         * Jobs to be transferred.
         */
        private List<JobTransfer> jobTransfers;

        public JobTransferTable(
                String id,
                IModel<Organization> fromOrgModel ) {
            super( id );
            this.fromOrgModel = fromOrgModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            jobTransfers = (List<JobTransfer>) CollectionUtils.collect(
                    getFromOrganization().getJobs(),
                    new Transformer() {
                        public Object transform( Object input ) {
                            return new JobTransfer( (Job) input );
                        }
                    }
            );
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // Columns
            columns.add( makeCheckBoxColumn(
                    "Transfer",
                    "confirmed",
                    isLockedByUser( getOrganization() ) && !isLockedByOtherUser( getFromOrganization() ),
                    JobsPanel.this
            ) );
            columns.add( makeFilterableLinkColumn(
                    "Agent",
                    "actor",
                    "actor.normalizedName",
                    EMPTY,
                    JobTransferTable.this ) );
            columns.add( makeColumn(
                    "Title",
                    "title",
                    ""
            ) );
            columns.add( makeFilterableLinkColumn(
                    "Role",
                    "role",
                    "role.name",
                    EMPTY,
                    JobTransferTable.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Jurisdiction",
                    "jurisdiction",
                    "jurisdiction.name",
                    EMPTY,
                    JobTransferTable.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Supervisor",
                    "supervisor",
                    "supervisor.name",
                    EMPTY,
                    JobTransferTable.this ) );
            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable(
                    "transferableJobs",
                    columns,
                    new SortableBeanProvider<JobTransfer>(
                            getFilteredTransfers(),
                            "actor.normalizedName" ),
                    getPageSize() ) );
        }

        private Organization getFromOrganization() {
            return fromOrgModel.getObject();
        }

        /**
         * Find all agreements implied or confirmed by this organization.
         *
         * @return a list of agreement wrappers.
         */
        @SuppressWarnings( "unchecked" )
        public List<JobTransfer> getFilteredTransfers() {
            return (List<JobTransfer>) CollectionUtils.select(
                    jobTransfers,
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return !isFilteredOut( obj );
                        }
                    }
            );
        }


        protected void resetTable( AjaxRequestTarget target ) {
            initialize();
            target.add( this );
        }

        /**
         * Get jobs to be transferred.
         *
         * @return a list of jobs
         */
        public List<Job> getTransferredJobs() {
            List<Job> jobs = new ArrayList<Job>();
            for ( JobTransfer jobTransfer : jobTransfers ) {
                if ( jobTransfer.isConfirmed() ) {
                    jobs.add( jobTransfer.getJob() );
                }
            }
            return jobs;
        }
    }


}
