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

/* Implements http://www.eonclash.com/Tutorials/Multithreading/MartinHarvey1.1/Ch11.html

    * Read operations can execute concurrently.
    * Write operations cannot execute at the same time as read operations.
    * Write operations cannot execute at the same time as write operations.
    * 
    * There is an asymmetry in the synchronization scheme: threads potentially wanting to read 
    * will block before reading if there are any active writers, whilst threads wanting to write 
    * block before writing if there are any reading readers. This gives priority to writing threads; 
    * a sensible approach, given that writes are less frequent than reads.
 */
public class MREWSynchronizer extends NKFAccessorImpl {
    
    private static final String CRITICAL_LOCK = "MREWSynchronizer_critical";
    private final static int SLEEP_TIME = 100; // 0.1 sec
    private final static int MAX_SLEEPS = 300; // 30 secs (300 * 0.1)
    
    private static Map<String,Integer> Counts = new HashMap<String,Integer>();

    
    public MREWSynchronizer( ) {
        super( SAFE_FOR_CONCURRENT_USE, INKFRequestReadOnly.RQT_SOURCE ); // Must allow concurrent requests
    }

    @Override
    public void processRequest( INKFConvenienceHelper context ) throws Exception {
        if (context.getThisRequest().getRequestType() == INKFRequestReadOnly.RQT_SOURCE) {
            source(context);
        }
        else {
                throw(new Exception("Unsupported request type"));
        }       
    }

    private void source( INKFConvenienceHelper context ) throws Exception {
        IURRepresentation rep = context.getThisRequest().getArgumentValue( "var:operator" );
        IAspectString asp = (IAspectString)context.transrept(rep, IAspectString.class);
        String op = asp.getString();
        if (op.equals( "beginRead" )) {
            beginRead(context);
        }
        else if (op.equals( "endRead" )) {
            endRead(context);
        }
        else if (op.equals( "beginWrite" )) {
            beginWrite(context);
        }
        else if (op.equals( "endWrite" )) {
            endWrite(context);
        }
        else if (op.equals( "reset" )) {
            reset();
        }
        else throw new Exception("Invalid operator value " + op);
        INKFResponse resp = context.createResponseFrom( new BooleanAspect(true) );
        context.setResponse(resp);
    }

    private void beginRead(INKFConvenienceHelper context ) throws NKFException {
        try {
            enterCritical(context);
            incrementCount("activeReaders");
            if (getCount("activeWriters") == 0 ) {
                incrementCount("readingReaders");
                signal("readerSem");
            }
        }
        finally {
            leaveCritical(context);
        }
    }
    
    private void endRead( INKFConvenienceHelper context ) throws NKFException {
        try {
            enterCritical(context);
            decrementCount("readingReaders");
            decrementCount("activeReaders");
            if (getCount("readingReaders") == 0) {
                while (getCount("writingWriters") < getCount("activeWriters")) {
                    incrementCount("writingWriters");
                    signal("writerSem");
                }
            }
        }
        finally {
            leaveCritical(context);
        }
    }
    
    private void beginWrite( INKFConvenienceHelper context ) throws NKFException {
        try {
            enterCritical(context);
            incrementCount("activeWriters");
            if (getCount("readingReaders") == 0) {
                incrementCount("writingWriters");
                signal("writerSem");
            }
        }
        finally {
            leaveCritical(context);
        }
        waitOn("writerSem", context);
        grabLock("write", context);
    }
    
    private void endWrite( INKFConvenienceHelper context ) throws NKFException {
        releaseLock("write", context);
        try {
            enterCritical(context);
            decrementCount("writingWriters");
            decrementCount("activeWriters");
            if (getCount("activeWriters") == 0) {
                while (getCount("readingReaders") < getCount("activeReaders")) {
                    incrementCount("readingReaders");
                    signal("readerSem");
                }
            }
        }
        finally {
            leaveCritical(context);
        }
    }
    
    private void reset() {
        Counts = new HashMap<String,Integer>();
    }

    private void enterCritical( INKFConvenienceHelper context ) throws NKFException {
        INKFRequest req=context.createSubRequest("active:lock");
        req.addArgument("operand","lock:" + CRITICAL_LOCK);
        context.issueSubRequest(req);   
    }
    
    private void leaveCritical( INKFConvenienceHelper context ) throws NKFException {
        INKFRequest req=context.createSubRequest("active:unlock");
        req.addArgument("operand","lock:" + CRITICAL_LOCK);
        context.issueSubRequest(req);   
    }
    
    private void signal( String sem ) throws NKFException {
        incrementCount(sem);
    }

    private void waitOn( String sem, INKFConvenienceHelper context ) throws NKFException {
        int sleeps = 0;
        while (getCount(sem)<= 0) {
            if (sleeps++ > MAX_SLEEPS) {
                throw new NKFException("Exceeded maximum wait");
            }
            sleep(SLEEP_TIME, context); // Assumes that other requests will be able to execute this thread unsafe accessor while this one sleeps.
        }
        decrementCount(sem);
    }

    private int getCount( String name ) {
        Integer value = Counts.get( name );
        if (value == null) {
            return resetCounter(name);
        }
        else {
            return value.intValue();
        }
    }

    private int resetCounter( String name ) {
        Integer value = new Integer(0);
        Counts.put( name, value );
        return value;
    }

    private int incrementCount( String name ) {
        Integer value = getCount(name);
        value = new Integer(value.intValue() + 1);
        Counts.put( name, value );
        return value.intValue();
    }

    private int decrementCount( String name ) {
        Integer value = getCount(name);
        value = new Integer(value.intValue() - 1);
        Counts.put( name, value );
        return value.intValue();
    }

    private void grabLock( String lock, INKFConvenienceHelper context ) throws NKFException {
        INKFRequest req=context.createSubRequest("active:lock");
        req.addArgument("operand","lock:" + lock);
        context.issueSubRequest(req);   
    }

    private void releaseLock( String lock, INKFConvenienceHelper context ) throws NKFException {
        INKFRequest req=context.createSubRequest("active:unlock");
        req.addArgument("operand","lock:" + lock);
        context.issueSubRequest(req);   
    }

    private void sleep(int msecs, INKFConvenienceHelper context) throws NKFException {
        INKFRequest req = context.createSubRequest("active:sleep");
        String time = "<time>" +  msecs + "</time>";
        req.addArgument("operator", new StringAspect(time));
        context.issueSubRequest(req);
    }
    
}
