package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.dao.Exporter;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.query.QueryService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Remove plan segment command.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 6, 2009
 * Time: 8:59:52 AM
 */
public class RemoveSegment extends AbstractCommand {

    public RemoveSegment() {
    }

    public RemoveSegment( Segment segment ) {
        List<Part> parts = segment.listParts();
        needLockOn( segment );
        needLocksOn( parts );
        needLocksOn( segment.listFlows() );
        needLocksOn( segment.listExternalParts() );
        set( "segment", segment.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "delete plan segment";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            describeTarget( segment );
            segment.setBeingDeleted( true );
            Exporter exporter = commander.getExporter();
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
            releaseAnyLockOn( segment, commander );
            return new Change( Change.Type.Removed, segment );

        } catch ( IOException e ) {
            throw new CommandException( "Failed to remove segment.", e );
        }
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
    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        String xml = (String) get( "xml" );
        if ( xml != null ) {
            MultiCommand multi = new MultiCommand( "restore segment" );
            RestoreSegment restoreSegment = new RestoreSegment();
            restoreSegment.set( "xml", xml );
            Long defaultSegmentId = (Long) get( "defaultSegment" );
            if ( defaultSegmentId != null ) {
                restoreSegment.set( "defaultSegment", defaultSegmentId );
            }
            multi.addCommand( restoreSegment );
            return multi;
        } else {
            throw new CommandException( "Can not restore plan segment." );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSegmentSpecific() {
        return false;
    }
}
