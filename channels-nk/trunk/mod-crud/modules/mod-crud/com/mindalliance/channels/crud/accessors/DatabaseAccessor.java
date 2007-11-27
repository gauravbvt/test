// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.crud.accessors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.NKFException;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;
import org.ten60.netkernel.layer1.representation.BooleanAspect;
import org.ten60.netkernel.xml.representation.IAspectXDA;
import org.ten60.netkernel.xml.xda.IXDAReadOnly;
import org.ten60.netkernel.xml.xda.IXDAReadOnlyIterator;

import com.mindalliance.channels.crud.Database;
import com.mindalliance.channels.crud.Mutex;
import com.mindalliance.channels.nk.ContextSupport;


public class DatabaseAccessor extends NKFAccessorImpl {
    
    private static final int MAX_DELETE_RETRIES = 50;
    private static final int SLEEP_BETWEEN_RETRIES = 200; // msecs
    
    public DatabaseAccessor() {
        super( SAFE_FOR_CONCURRENT_USE, ContextSupport.SOURCE );
    }

    //  active:channels_database+operator@data:<open|delete>...
    @Override
    public void processRequest( INKFConvenienceHelper context ) throws Exception {
        ContextSupport ctx = new ContextSupport(context);
        String name = ctx.sourceString("this:param:name");
        Database database = new Database(name, ctx);
        Mutex mutex = new Mutex(ctx);
        if (ctx.requestType() == ContextSupport.SOURCE) {
            source(ctx, database, mutex);
        }
        else {
            throw new Exception("Invalid request type");
        }
    }

    private void source( ContextSupport ctx, Database database, Mutex mutex ) throws Exception {
        String operator = ctx.sourceString("this:param:operator");
        if (operator == null) throw new Exception("Invalid request: missing operation");
        if (operator.equals( "open" )) {
            openDatabase(ctx, database, mutex);
        }
        else if (operator.equals( "delete" )) {
            deleteDatabase(ctx, database, mutex);
        }
        else if (operator.equals( "query" )) {
            queryDatabase(ctx, database, mutex);
        }
        else if (operator.equals( "load" )) {
            loadDatabase(ctx, database, mutex);
        }
        else {
            throw new Exception("Invalid database operation " + operator);
        }
    }

    private void openDatabase( ContextSupport ctx, Database database, Mutex mutex ) throws Exception {
        boolean exists;
        try {
            ctx.log("Opening database: " + database.getName(), "info");
            exists = database.exists();
            if (!exists) {
              //  database
              database.create();
              ctx.log("Created database: " + database.getName(), "info");
            }
        }
        catch(Exception e) {
            ctx.log("Open database failed: " + e, "severe");
            throw e ;
        }
        // Return Response
        ctx.respond(new BooleanAspect(!exists)).
            setExpired(); // don't cache
    }

    private void deleteDatabase(ContextSupport ctx, Database database, Mutex mutex) throws Exception {
        ctx.log( "Deleting database", "info" );
        boolean exists = database.exists();
        int retries = MAX_DELETE_RETRIES;
        String databaseName = database.getName();
        while (database.exists() && (retries > 0)) {
            retries--;
            try {
                exists = database.exists();
                if (exists) {
                    database.delete();
                    ctx.log("Deleted database: " + databaseName, "info");
                }
                else {
                    ctx.log("Database not deleted (does not exist): " + databaseName, "warning");
                }
            }
            catch(Exception e) { // Deleting fails temporarily and spurriously
                ctx.log("Delete database failed: " + e, "warning");
                ctx.sleep(SLEEP_BETWEEN_RETRIES);
            }
        }
        // Return Response
        ctx.respond(new BooleanAspect(exists)).setExpired(); // don't cache
    }

    private void loadDatabase( ContextSupport ctx, Database database, Mutex mutex ) throws Exception {
        String dbName = database.getName();
        String uri = ctx.getArgument( "init" );
        ctx.log( "Loading database "+ dbName + " from " + uri, "info" );
        initializeFrom( uri, dbName, ctx );
        ctx.log("Database " + dbName + " initialized", "info");
    }

    private void queryDatabase( ContextSupport ctx, Database database, Mutex mutex ) throws Exception {
        ctx.log( ">> START QUERY", "info" );
        String query = ctx.sourceString( "this:param:xquery");
        ctx.log( "Query: " + query, "info" );
        Map<String,String> variables = extractQueryVariables( ctx );
        ctx.log( "with variables " + variables, "info" );
        // Prepare the query
        if ( !variables.isEmpty() ) {
            // Add variable declaration as prologue
            query = declareVariables( query, variables ); 
        }
        query = database.filter( query ); // Substitute place holders
        IAspectXDA result;
        try {
            mutex.beginRead( "QUERY" );
            result = database.queryDatabase( query );
        }
        finally {
            mutex.endRead( "QUERY" );
        }
        ctx.log( "<< END QUERY", "info" );
        // Return Response
        ctx.respond( result ).
            setCacheable().
            setMimeType( "text/xml" );
    }

    private String declareVariables( String query, Map<String,String> variables ) {
        String prologuedQuery = query;
        for (String propName : variables.keySet()) {
          String decl = "declare variable $" +  propName + " := '" + variables.get(propName) + "';\n";
          prologuedQuery = decl + prologuedQuery;
        }
        return prologuedQuery;
    }

    private Map<String,String> extractQueryVariables( ContextSupport ctx ) throws NKFException {
        Map<String,String>variables = new HashMap<String,String>();
        Iterator iter = ctx.getArguments();
        while (iter.hasNext()) {
            String name = (String)iter.next();
            if (!(name.equals( "xquery" ) || name.equals( "operator" ))) {
            // Variable values passed in as data:text/plain,<uri-encoded value>
            String value = ctx.sourceString( "this:param:" + name);
            variables.put( name, value );
            }
        }
        return variables;
    }

    private void initializeFrom( String uri, String name, ContextSupport ctx ) throws Exception {
        IXDAReadOnly db = ctx.sourceXDAReadOnly( uri ); 
        ctx.log("Initializing db from " + uri, "info");
        IXDAReadOnlyIterator document = db.readOnlyIterator( "./*" );
        while (document.next()) {
            ctx.subRequest("active:crud_document").
                        ofType(ContextSupport.NEW).
                        withData("database", name).
                        withXDA("doc", document).
                        issue();
        }
    }
}

