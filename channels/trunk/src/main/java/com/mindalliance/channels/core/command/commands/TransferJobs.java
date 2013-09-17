/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.MappedObject;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Transfer a list of jobs.
 */
public class TransferJobs extends AbstractCommand {

    public TransferJobs() {
        super( "daemon" );
    }

    public TransferJobs( String userName, Organization fromOrganization, Organization toOrganization, List<Job> jobs,
                         boolean copying ) {
        super( userName );
        needLockOn( fromOrganization );
        needLockOn( toOrganization );
        set( "fromOrganization", fromOrganization.getId() );
        set( "toOrganization", toOrganization.getId() );
        set( "jobs", mapJobs( jobs ) );
        set( "copying", copying );
    }

    private List<MappedObject> mapJobs( List<Job> jobs ) {
        List<MappedObject> mappedJobs = new ArrayList<MappedObject>();
        for ( Job job : jobs )
            mappedJobs.add( new MappedObject( job ) );

        return mappedJobs;
    }

    //-------------------------------
    @Override
    @SuppressWarnings( "unchecked" )
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        Organization fromOrg = commander.resolve( Organization.class, (Long) get( "fromOrganization" ) );
        Organization toOrg = commander.resolve( Organization.class, (Long) get( "toOrganization" ) );
        boolean copying = (Boolean) get( "copying" );
        List<Job> jobs = unmapJobs( (List<MappedObject>) get( "jobs" ), commander );
        MultiCommand multi = (MultiCommand) get( "subCommands" );
        if ( multi == null ) {
            multi = makeSubCommands( fromOrg, toOrg, jobs, copying );
            set( "subCommands", multi );
        }
        multi.execute( commander );
        describeTarget( toOrg );
        return new Change( Change.Type.Updated, queryService.getPlan() );
    }

    private List<Job> unmapJobs( List<MappedObject> mappedObjects, Commander commander ) throws CommandException {
        List<Job> jobs = new ArrayList<Job>();
        for ( MappedObject mappedObject : mappedObjects ) {
            Job job = (Job) mappedObject.fromMap( commander );
            jobs.add( job );
        }
        return jobs;
    }

    private MultiCommand makeSubCommands( Organization fromOrg, Organization toOrg, List<Job> jobs, boolean copying ) {
        MultiCommand subCommands = new MultiCommand( getUserName(), "transfer jobs - internal" );
        for ( Job job : jobs ) {
            if ( !copying )
                subCommands.addCommand( new UpdatePlanObject( getUserName(),
                                                              fromOrg, "jobs", job, UpdateObject.Action.Remove ) );
            subCommands.addCommand( new UpdatePlanObject( getUserName(), toOrg,
                                                          "jobs",
                                                          copying ? new Job( job ) : job,
                                                          UpdateObject.Action.AddUnique ) );
        }
        return subCommands;
    }

    @Override
    public String getName() {
        return "transfer jobs";
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        MultiCommand multi = new MultiCommand( getUserName(), "untransfer jobs" );
        MultiCommand subCommands = (MultiCommand) get( "subCommands" );
        subCommands.setMemorable( false );
        multi.addCommand( subCommands.getUndoCommand( commander ) );
        return multi;
    }
}
