package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.MappedObject;
import com.mindalliance.channels.command.MultiCommand;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Transfer a list of jobs.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 16, 2010
 * Time: 4:00:20 PM
 */
public class TransferJobs extends AbstractCommand {

    public TransferJobs() {
    }

    public TransferJobs( Organization fromOrganization,
                         Organization toOrganization,
                         List<Job> jobs,
                         boolean copying) {
        needLockOn( fromOrganization );
        needLockOn( toOrganization );
        set( "fromOrganization", fromOrganization.getId() );
        set( "toOrganization", toOrganization.getId() );
        set( "jobs", mapJobs( jobs ) );
        set( "copying", copying );
    }

    public TransferJobs( Organization fromOrganization,
                         Organization toOrganization,
                         List<Job> jobs ) {
        this( fromOrganization, toOrganization, jobs, false );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "transfer jobs";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        Organization fromOrg = commander.resolve( Organization.class, (Long) get( "fromOrganization" ) );
        Organization toOrg = commander.resolve( Organization.class, (Long) get( "toOrganization" ) );
        boolean copying = (Boolean)get( "copying" );
        List<Job> jobs = unmapJobs( (List<MappedObject>) get( "jobs" ), commander );
        MultiCommand multi = (MultiCommand)get( "subCommands");
        if (multi == null ) {
            multi = makeSubCommands( fromOrg, toOrg, jobs, copying );
            set("subCommands", multi);
        }
        multi.execute( commander );
        describeTarget( toOrg );
        return new Change( Change.Type.Updated, queryService.getPlan() );
    }

    private MultiCommand makeSubCommands(
            Organization fromOrg,
            Organization toOrg,
            List<Job> jobs,
            boolean copying ) {
        MultiCommand subCommands = new MultiCommand( "transfer jobs - internal" );
        for ( Job job : jobs ) {
            if ( !copying )
                subCommands.addCommand( new UpdatePlanObject (
                        fromOrg,
                        "jobs",
                        job,
                        UpdateObject.Action.Remove
                ));
            subCommands.addCommand( new UpdatePlanObject (
                    toOrg,
                    "jobs",
                    copying ? new Job(job) : job,
                    UpdateObject.Action.Add
            ));
        }
        return subCommands;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isUndoable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( "untransfer jobs" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }

    private List<MappedObject> mapJobs( List<Job> jobs ) {
        List<MappedObject> mappedJobs = new ArrayList<MappedObject>();
        for ( Job job : jobs )
            mappedJobs.add( new MappedObject( job ) );

        return mappedJobs;
    }

    private List<Job> unmapJobs( List<MappedObject> mappedObjects, Commander commander ) throws CommandException {
        List<Job> jobs = new ArrayList<Job>();
        for ( MappedObject mappedObject : mappedObjects ) {
            Job job = (Job) mappedObject.fromMap( commander );
            jobs.add( job );
        }
        return jobs;
    }

}
