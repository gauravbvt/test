package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.EOIsHolder;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;

/**
 * Toggle the time sensitivity of an EOI for an EOI holder.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/23/14
 * Time: 1:02 PM
 */
public class ToggleTimeSensitivity extends AbstractCommand {

    public ToggleTimeSensitivity() {
    }

    public ToggleTimeSensitivity( String userName ) {
        super( userName );
    }

    public ToggleTimeSensitivity( String username, EOIsHolder eoisHolder, String content ) {
        super( username );
        needLockOn( eoisHolder );
        if ( eoisHolder instanceof SegmentObject ) {
            set( "segment", ( (SegmentObject) eoisHolder ).getSegment().getId() );
        }
        set( "type", eoisHolder.getTypeName() );
        set( "eoisHolder", eoisHolder.getId() );
        set( "eoiContent", content );
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        EOIsHolder eoisHolder = getEoisHolder( commander );
        describeTarget( (ModelObject) eoisHolder );
        String eoiContent = (String) get( "eoiContent" );
        boolean timeSensitive = eoisHolder.isTimeSensitive( eoiContent );
        eoisHolder.setTimeSensitive( eoiContent, !timeSensitive );
        return new Change( Change.Type.Updated, eoisHolder, "updated" );
    }

    private EOIsHolder getEoisHolder( Commander commander ) throws CommandException {
        try {
            if ( get( "segment" ) != null ) {
                Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
                return segment.findFlow( (Long) get( "eoisHolder" ) );

            } else {
                return commander.getCommunityService().find( InfoProduct.class, (Long) get( "eoisHolder" ) );
            }
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh", e );
        }
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        Command toggleTimeSensitivity = new ToggleTimeSensitivity( getUserName() );
        if ( get( "segment" ) != null )
            toggleTimeSensitivity.set( "segment", get( "segment" ) );
        toggleTimeSensitivity.set( "eoisHolder", get( "eoisHolder" ) );
        toggleTimeSensitivity.set( "eoiContent", get( "eoiContent" ) );
        return toggleTimeSensitivity;
    }

    @Override
    public String getName() {
        return "Toggle EOI time sensitivity";
    }
}
