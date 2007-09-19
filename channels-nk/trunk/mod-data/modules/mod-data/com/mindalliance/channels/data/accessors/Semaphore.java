// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

/*
 * Accessor that waits on or signals a semaphore.
 * The "operand" argument can be: "wait", "signal" or "reset"
 */
package com.mindalliance.channels.data.accessors;

import org.ten60.netkernel.layer1.nkf.*;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;
import com.ten60.netkernel.urii.IURRepresentation;
import com.ten60.netkernel.urii.aspect.BooleanAspect;
import com.ten60.netkernel.urii.aspect.IAspectString;
import com.ten60.netkernel.urii.aspect.StringAspect;

import java.util.*;

public class Semaphore extends NKFAccessorImpl {
    
    final static int SLEEP_TIME = 100; // 0.1 sec
    final static int MAX_SLEEPS = 300; // 30 secs (300 * 0.1)
    
    static Map<String,Integer> signals = new HashMap<String,Integer>();

    public Semaphore( ) {
        super( SAFE_FOR_CONCURRENT_USE, INKFRequestReadOnly.RQT_SOURCE );
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
        if (arg.equals( "wait" )) {
            waitOn(path, context);
        }
        else if (arg.equals( "signal" )) {
            signal(path, context);
        }
        else if (arg.equals( "reset" )) {
            reset(path, context);
        }
        else throw new Exception("Invalid operand value " + arg);
        INKFResponse resp = context.createResponseFrom( new BooleanAspect(true) );
        context.setResponse(resp);
    }

    private void reset( String path, INKFConvenienceHelper context ) {
        signals = new HashMap<String,Integer>();
    }

    // Polling wait (can't mess with pausing and resuming threads)
    private void waitOn(String path, INKFConvenienceHelper context) throws NKFException {
        int sleeps = 0;
        while (getCount(path)<= 0) {
            if (sleeps++ > MAX_SLEEPS) {
                throw new NKFException("Exceeding maximum wait");
            }
            sleep(context); // Assumes that other requests will be able to execute this thread unsafe accessor while this one sleeps.
        }
        decrementCount(path);
    }
    
    private void signal(String path, INKFConvenienceHelper context) {
        incrementCount(path);
    }

    private void incrementCount( String path ) {
        synchronized (signals) {
            int count = getCount(path);
            signals.put( path, new Integer(count + 1) );
        }
    }

    private void decrementCount( String path ) {
        synchronized (signals) {
            int count = getCount(path);
            signals.put( path, new Integer(count - 1) );
        }
    }

    private int getCount( String path ) {
        synchronized (signals) {
            Integer count = signals.get( path );
            if (count == null) {
                return 0;
            }
            else {
                return count.intValue();
            }
        }
    }

    private void sleep(INKFConvenienceHelper context) throws NKFException {
        INKFRequest req = context.createSubRequest("active:sleep");
        req.addArgument("operator", new StringAspect("<time>100</time>"));
        context.issueSubRequest(req);        
    }

}
