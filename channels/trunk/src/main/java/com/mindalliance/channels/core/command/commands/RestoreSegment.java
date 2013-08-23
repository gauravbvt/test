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
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.Importer;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;

import java.util.List;

/**
 * Restore a deleted segment.
 */
public class RestoreSegment extends AbstractCommand {

    public RestoreSegment() {
        super( "daemon" );
    }

    public RestoreSegment( String userName ) {
        super( userName );
    }

    @Override
    public String getName() {
        return "restore segment";
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        QueryService queryService = commander.getQueryService();
        ImportExportFactory importExportFactory = commander.getImportExportFactory();
        Importer importer = importExportFactory.createImporter( getUserName(), commander.getDao() );
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
            describeTarget( segment );
            set( "segment", segment.getId() );
            if ( defaultSegment != null )
                queryService.remove( defaultSegment );
            return new Change( Change.Type.Added, segment );
        } else {
            throw new CommandException( "Can't restore segment." );
        }
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
        return new RemoveSegment( getUserName(), segment );
    }
}
