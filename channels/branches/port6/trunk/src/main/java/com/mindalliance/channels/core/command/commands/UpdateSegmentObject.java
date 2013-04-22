/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;

/**
 * Command to update a model object contained in a segment.
 */
public class UpdateSegmentObject extends UpdateObject {

    public UpdateSegmentObject() {
    }

    public UpdateSegmentObject( String userName, Identifiable identifiable, String property, Object value ) {
        this( userName, identifiable, property, value, Action.Set );
    }

    public UpdateSegmentObject( String userName, Identifiable identifiable, String property, Object value,
                                Action action ) {
        super( userName, identifiable, property, value, action );
        set( "segment", ( (SegmentObject) identifiable ).getSegment().getId() );
        set( "isNode", identifiable instanceof Node );
    }

    @Override
    protected String getObjectTypeName( String type ) {
        return type.contains( "flow" ) ? "flow" : super.getObjectTypeName( type );
    }

    @Override
    protected Identifiable getIdentifiable( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            boolean isNode = (Boolean) get( "isNode" );
            return isNode ? segment.getNode( (Long) get( "object" ) ) : segment.findFlow( (Long) get( "object" ) );
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e );
        }
    }

    @Override
    protected UpdateObject createUndoCommand( Identifiable identifiable, String property, Object value,
                                              Action action ) {
        return new UpdateSegmentObject( getUserName(), identifiable, property, value, action );
    }
}
