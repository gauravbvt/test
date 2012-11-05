package com.mindalliance.channels.core.command.commands;

import com.mindalliance.channels.core.command.AbstractCommand;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;

import java.util.List;

/**
 * Command to standardize the information in a flow as an information product.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/31/12
 * Time: 10:29 AM
 */
public class StandardizeInformation extends AbstractCommand {

    public StandardizeInformation() {
    }

    public StandardizeInformation( String userName ) {
        super( userName );
    }

    public StandardizeInformation( String username, Flow flow, boolean val ) {
        this( username );
        needLockOn( flow );
        set( "segment", flow.getSegment().getId() );
        set( "flow", flow.getId() );
        set( "standardize", val );
    }

    @Override
    public Change execute( Commander commander ) throws CommandException {
        try {
            QueryService queryService = commander.getQueryService();
            Segment segment = commander.resolve( Segment.class, (Long) get( "segment" ) );
            assert get( "flow" ) != null;
            Flow flow = segment.findFlow( (Long) get( "flow" ) );
            describeTarget( flow );
            boolean standardize = (Boolean) get( "standardize" );
            boolean wasStandardized = flow.isStandardized();
            String name = flow.getName();
            flow.setStandardized( standardize );
            if ( !wasStandardized
                    && standardize
                    && !name.isEmpty() ) {
                boolean existed = queryService.entityExists( InfoProduct.class, name, ModelEntity.Kind.Type );
                InfoProduct infoProduct = queryService.findOrCreateType( InfoProduct.class, name );
                flow.setInfoProduct( infoProduct );
                if ( !existed ) {
                    for ( ElementOfInformation eoi : flow.getEffectiveEois() ) {
                        infoProduct.addLocalEoi( new ElementOfInformation( eoi ) );
                    }
                }
            }
            if ( !standardize ) {
                // Take the current name of the info product
                InfoProduct infoProduct = flow.getInfoProduct();
                if ( infoProduct != null )
                    flow.setName( infoProduct.getName() );
                // Make local all current effective eois
                List<ElementOfInformation> eois = ElementOfInformation.copy( flow.getEffectiveEois() );
                flow.setInfoProduct( null );
                flow.setLocalEois( eois );
            }
            Change change = new Change( Change.Type.Updated, flow, "standardized" );
            if ( !name.isEmpty() && wasStandardized && !standardize )
                commander.cleanup( InfoProduct.class, name );
            return change;
        } catch ( NotFoundException e ) {
            throw new CommandException( "You need to refresh.", e );
        }
    }

    @Override
    protected Command makeUndoCommand( Commander commander ) throws CommandException {
        Command resetStandardizeInfo = new StandardizeInformation( getUserName() );
        resetStandardizeInfo.set( "segment", get( "segment" ) );
        resetStandardizeInfo.set( "flow", get( "flow" ) );
        resetStandardizeInfo.set( "standardize", !( (Boolean) get( "standardize" ) ) );
        return resetStandardizeInfo;
    }

    @Override
    public String getName() {
        return "Standardize info";
    }

    @Override
    public boolean isUndoable() {
        return true;
    }
}
