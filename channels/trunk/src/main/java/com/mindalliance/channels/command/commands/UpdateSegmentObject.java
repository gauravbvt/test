package com.mindalliance.channels.command.commands;

import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.SegmentObject;

/**
 * Command to update a model object contained in a segment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 12:41:28 PM
 */
public class UpdateSegmentObject extends UpdateObject {

    public UpdateSegmentObject() {
    }

    public UpdateSegmentObject(
            final Identifiable identifiable,
            final String property,
            final Object value ) {
        this( identifiable, property, value, Action.Set );
    }

    public UpdateSegmentObject(
            final Identifiable identifiable,
            final String property,
            final Object value,
            final Action action ) {
        super( identifiable, property, value, action );
        set( "segment", ( (SegmentObject) identifiable ).getSegment().getId() );
        set( "isNode", identifiable instanceof Node );
    }

    protected String getObjectTypeName( String type ) {
        if ( type.contains( "flow" ) )
            return "flow";
        else
            return "part";
    }

    /**
     * {@inheritDoc}
     */
    protected Identifiable getIdentifiable( Commander commander ) throws CommandException {
        try {
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            boolean isNode = (Boolean) get( "isNode" );
            SegmentObject segmentObject;
            if ( isNode ) {
                segmentObject = segment.getNode( (Long) get( "object" ) );
            } else {
                segmentObject = segment.findFlow( (Long) get( "object" ) );
            }
            return segmentObject;
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e );
        }
    }

    /**
     * {@inheritDoc}
     */
    protected UpdateObject createUndoCommand(
            Identifiable identifiable,
            String property,
            Object value,
            Action action ) {
        return new UpdateSegmentObject( identifiable, property, value, action );
    }


}
