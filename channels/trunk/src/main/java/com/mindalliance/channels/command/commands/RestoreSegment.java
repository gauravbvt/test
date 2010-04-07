package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.Commander;
import com.mindalliance.channels.dao.Importer;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.command.AbstractCommand;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.dao.ImportExportFactory;
import com.mindalliance.channels.model.Segment;

import java.util.List;

/**
 * Restore a delete segment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 9, 2009
 * Time: 11:22:35 AM
 */
public class RestoreSegment extends AbstractCommand {

    public RestoreSegment() {
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "restore plan segment";
    }

    /**
     * {@inheritDoc}
     */
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        ImportExportFactory importExportFactory = queryService.getPlanManager().getImportExportFactory();
        Importer importer = importExportFactory.createImporter( commander.getPlanDao() );
        String xml = (String) get( "xml" );
        if ( xml != null ) {
            Long defaultSegmentId = (Long) get( "defaultSegment" );
            Segment defaultSegment = null;
            if ( defaultSegmentId != null ) {
                // a default segment was added before removing the one to be restored.
                List<Segment> segments = queryService.list( Segment.class );
                assert segments.size() == 1;
                defaultSegment = segments.get( 0 );
            }
            Segment segment = importer.restoreSegment( xml );
            set( "segment", segment.getId() );
            if ( defaultSegment != null ) queryService.remove( defaultSegment );
            return new Change( Change.Type.Added, segment );
        } else {
            throw new CommandException( "Can't restore plan segment." );
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
        Segment segment = commander.resolve(
                Segment.class,
                (Long) get( "segment" ) );
        return new RemoveSegment( segment );
    }


}
