// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

/*
 * Accessor that waits on or signals a semaphore.
 * The "operand" argument can be: "wait", "signal" or "reset"
 */
package com.mindalliance.channels.crud.accessors;

import java.util.HashMap;
import java.util.Map;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.NKFException;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;

import com.mindalliance.channels.nk.ContextSupport;
import com.ten60.netkernel.urii.aspect.BooleanAspect;

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
        super( SAFE_FOR_CONCURRENT_USE, ContextSupport.SOURCE ); // Must allow concurrent requests
    }

    @Override
    public void processRequest( INKFConvenienceHelper context ) throws Exception {
        ContextSupport ctx = new ContextSupport(context);
        if (ctx.requestType() == ContextSupport.SOURCE) {
            source(ctx);
        }
        else {
                throw(new Exception("Unsupported request type"));
        }       
    }

    private void source( ContextSupport ctx ) throws Exception {
        String op = ctx.sourceString( "this:param:operator" );
        if (op.equals( "beginRead" )) {
            beginRead(ctx);
        }
        else if (op.equals( "endRead" )) {
            endRead(ctx);
        }
        else if (op.equals( "beginWrite" )) {
            beginWrite(ctx);
        }
        else if (op.equals( "endWrite" )) {
            endWrite(ctx);
        }
        else if (op.equals( "reset" )) {
            reset();
        }
        else throw new Exception("Invalid operator value " + op);
        ctx.respond( new BooleanAspect(true) );
    }

    private void beginRead(ContextSupport ctx ) throws NKFException {
        try {
            enterCritical(ctx);
            incrementCount("activeReaders");
            if (getCount("activeWriters") == 0 ) {
                incrementCount("readingReaders");
                signal("readerSem");
            }
        }
        finally {
            leaveCritical(ctx);
        }
    }
    
    private void endRead( ContextSupport ctx ) throws NKFException {
        try {
            enterCritical(ctx);
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
            leaveCritical(ctx);
        }
    }
    
    private void beginWrite( ContextSupport ctx ) throws NKFException {
        try {
            enterCritical(ctx);
            incrementCount("activeWriters");
            if (getCount("readingReaders") == 0) {
                incrementCount("writingWriters");
                signal("writerSem");
            }
        }
        finally {
            leaveCritical(ctx);
        }
        waitOn("writerSem", ctx);
        grabLock("write", ctx);
    }
    
    private void endWrite( ContextSupport ctx ) throws NKFException {
        releaseLock("write", ctx);
        try {
            enterCritical(ctx);
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
            leaveCritical(ctx);
        }
    }
    
    private void reset() {
        Counts = new HashMap<String,Integer>();
    }

    private void enterCritical( ContextSupport ctx ) throws NKFException {
        ctx.subRequest("active:lock").
            withArg("operand","lock:" + CRITICAL_LOCK).
            issue();   
    }
    
    private void leaveCritical( ContextSupport ctx ) throws NKFException {
        ctx.subRequest("active:unlock").
            withArg("operand","lock:" + CRITICAL_LOCK).
            issue();
    }
    
    private void signal( String sem ) throws NKFException {
        incrementCount(sem);
    }

    private void waitOn( String sem, ContextSupport ctx ) throws NKFException {
        int sleeps = 0;
        while (getCount(sem)<= 0) {
            if (sleeps++ > MAX_SLEEPS) {
                throw new NKFException("Exceeded maximum wait");
            }
            sleep(SLEEP_TIME, ctx); // Assumes that other requests will be able to execute this thread unsafe accessor while this one sleeps.
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

    private void grabLock( String lock, ContextSupport ctx ) throws NKFException {
        ctx.subRequest("active:lock").
            withArg("operand","lock:" + lock).
            issue();   
    }

    private void releaseLock( String lock, ContextSupport ctx ) throws NKFException {
        ctx.subRequest("active:unlock").
            withArg("operand","lock:" + lock).
            issue();   
    }

    private void sleep(int msecs, ContextSupport ctx) throws NKFException {
        ctx.subRequest("active:sleep").
            withString("operator", "<time>" +  msecs + "</time>").
            issue();
    }
    
}
