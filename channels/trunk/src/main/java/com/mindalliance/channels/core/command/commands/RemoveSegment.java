/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Change.Type;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.dao.Exporter;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Remove plan segment command.
 */
public class RemoveSegment extends AbstractCommand {

    public RemoveSegment() {
        super( "daemon" );
    }

    public RemoveSegment( String userName, Segment segment ) {
        super( userName );
        List<Part> parts = segment.listParts();
        needLockOn( segment );
        needLocksOn( parts );
        needLocksOn( segment.listFlows() );
        needLocksOn( segment.listExternalParts() );
        set( "segment", segment.getId() );
    }

    @Override
    public String getName() {
        return "delete segment";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            describeTarget( segment );
            segment.setBeingDeleted( true );
            Exporter exporter = commander.getExporter( getUserName() );
            exporter.export( segment, bos );
            set( "xml", bos.toString() );
            Plan plan = commander.getPlan();
            if ( plan.getSegmentCount() == 1 ) {
                // first create a new, replacement segment
                Segment defaultSegment = queryService.createSegment();
                plan.addSegment( defaultSegment );
                set( "defaultSegment", defaultSegment.getId() );
            }
            queryService.remove( segment );
            releaseAnyLockOn( commander, segment );
            return new Change( Type.Removed, segment );

        } catch ( IOException e ) {
            throw new CommandException( "Failed to remove segment.", e );
        }
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        String xml = (String) get( "xml" );
        if ( xml != null ) {
            MultiCommand multi = new MultiCommand( getUserName(), "restore segment" );
            RestoreSegment restoreSegment = new RestoreSegment( getUserName() );
            restoreSegment.set( "xml", xml );
            Long defaultSegmentId = (Long) get( "defaultSegment" );
            if ( defaultSegmentId != null ) {
                restoreSegment.set( "defaultSegment", defaultSegmentId );
            }
            multi.addCommand( restoreSegment );
            return multi;
        } else {
            throw new CommandException( "Can not restore segment." );
        }
    }

    @Override
    public boolean isSegmentSpecific() {
        return false;
    }
}
