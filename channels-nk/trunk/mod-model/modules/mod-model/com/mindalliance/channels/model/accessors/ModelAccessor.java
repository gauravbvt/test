// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model.accessors;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;
import org.ten60.netkernel.layer1.representation.BooleanAspect;

import com.mindalliance.channels.nk.ContextSupport;


public class ModelAccessor  extends NKFAccessorImpl {
        
    public ModelAccessor() {
        super( SAFE_FOR_CONCURRENT_USE, ContextSupport.SOURCE );
    }
    
    //  active:channels_database+operator@data:<open|delete>...
    @Override
    public void processRequest( INKFConvenienceHelper context ) throws Exception {
        ContextSupport ctx = new ContextSupport(context);
        String model = ctx.sourceString( "this:param:name");
        if (ctx.requestType() == ContextSupport.SOURCE) {
            source(model, ctx);
        }
        else {
            throw new Exception("Invalid request type");
        }
    }

    private void source( String model, ContextSupport ctx ) throws Exception {
        String operator = ctx.sourceString( "this:param:operator");
        if (operator == null) throw new Exception("Invalid request: missing operation");
        if (operator.equals( "open" )) {
            openModel(model, ctx);
        }
        else if (operator.equals( "delete" )) {
            deleteModel(model, ctx);
        }
        else if (operator.equals( "get" )) {
            getModel(model, ctx);
        }
        else if (operator.equals( "query" )) {
            queryModel(model, ctx);
        }
        else if (operator.equals( "validate" )) {
            validateElement(model, ctx);
        }
        else if (operator.equals( "xform" )) {
            makeXForm(model, ctx);
        }
        else {
            throw new Exception("Invalid model operation " + operator);
        }
    }

    private void makeXForm( String model, ContextSupport ctx ) {
        // TODO
    }

    private void validateElement( String model, ContextSupport ctx ) throws Exception {
        String xml = ctx.sourceString( "this:param:doc" );
        // Wrap it -> db with single element
        String db = "<db><project id=\"a\"><name>p</name><organization id=\"b\"><name>O</name></organization></project>" +
                        xml +
                    "</db>";
        // Validate wrapped element
        ctx.xdaHelper.validateRNG( ctx.xdaHelper.makeXDAAspect( db ), "schema:ifm.rng" );
        ctx.respond( new BooleanAspect(true) );
    }

    private void queryModel( String model, ContextSupport ctx ) throws Exception {
        ctx.log( "Querying model", "info" );
        ctx.subRequest("active:crud_database").
                forwardArg("operator").
                forwardArg("database", "name").
                forwardArg("xquery").
                setMimeType("text/xml");
    }

    private void getModel( String model, ContextSupport ctx ) {
        // TODO: Runs query that retuns entire contents according to schema
    }

    private void deleteModel( String model, ContextSupport ctx ) throws Exception {
        ctx.log( "Deleting model", "info" );
        ctx.subRequest( "active:crud_database" ).
                withArg("operator", "delete").
                forwardArg("database", "name").
                respond(new BooleanAspect(true)). // request is first issued
                setExpired();
    }

    private void openModel( String model, ContextSupport ctx ) throws Exception {
        ctx.log( "Opening model", "info" );
        ctx.subRequest( "active:crud_database" ).
            withString("operator", "open").
            forwardArg("database", "name");
        // Initialize from file if requested
        if (ctx.argumentExists("init")) {
          ctx.log("Initializing database from " + ctx.context.getThisRequest().getArgument("init"), "info");
          ctx.subRequest( "active:database" ).
              withString("operator", "load").
              withArg("init", ctx.getArgument( "init" )).
              issue();
        }
        ctx.respond(new BooleanAspect(true)).setExpired();
    }

}
