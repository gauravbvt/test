// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

/*
 * Accessor that gets, increments, decrements or resets a counter.
 * The "operand" argument can be: "get", "increment", "decrement" or "reset"
 */
package com.mindalliance.channels.data.accessors;

import org.ten60.netkernel.layer1.nkf.*;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;

import com.ten60.netkernel.urii.IURRepresentation;
import com.ten60.netkernel.urii.aspect.IAspectString;
import com.ten60.netkernel.urii.aspect.StringAspect;

import java.util.*;

public class Counter extends NKFAccessorImpl {
    
    static Map<String,Long> Counts = new HashMap<String,Long>();

    public Counter( ) {
        super( NOT_SAFE_FOR_CONCURRENT_USE, INKFRequestReadOnly.RQT_SOURCE );
    }

    @Override
    public void processRequest( INKFConvenienceHelper context ) throws Exception {
        if (context.getThisRequest().getRequestType() == INKFRequestReadOnly.RQT_SOURCE) {
            source(context);
        }
        else {
                throw(new Exception("Invalid request type"));
        }       
    }
    
    private void source(INKFConvenienceHelper context) throws Exception {
        String path = context.getThisRequest().getURIWithoutFragment();
        IURRepresentation rep = context.getThisRequest().getArgumentValue( "var:operand" );
        IAspectString asp = (IAspectString)context.transrept(rep, IAspectString.class);
        String arg = asp.getString();
        Long value;
        if (arg.equals( "get" )) {
            value = getCounter(path);
        }
        else if (arg.equals( "increment" )) {
            value = incrementCounter(path);
        }
        else if (arg.equals( "decrement" )) {
            value = decrementCounter(path);
        }
        else if (arg.equals( "reset" )) {
            value = resetCounter(path);
        }
        else throw(new Exception("Invalid operand value " + arg));
        INKFResponse resp = context.createResponseFrom( new StringAspect(value.toString()) );
        context.setResponse(resp);
    }

    private Long decrementCounter( String path ) {
        Long value = getCounter(path);
        value = new Long(value.longValue() - 1);
        Counts.put( path, value );
        return value;
    }

    private Long incrementCounter( String path ) {
        Long value = getCounter(path);
        value = new Long(value.longValue() + 1);
        Counts.put( path, value );
        return value;
    }

    private Long getCounter( String path ) {
        Long value = Counts.get( path );
        if (value == null) {
            return resetCounter(path);
        }
        else {
            return value;
        }
    }
    
    private Long resetCounter( String path ) {
        Long value = new Long(0);
        Counts.put( path, value );
        return value;
    }



}
