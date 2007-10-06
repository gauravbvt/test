// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.accessors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly;
import org.ten60.netkernel.layer1.nkf.INKFResponse;
import org.ten60.netkernel.layer1.nkf.NKFException;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;
import org.ten60.netkernel.xml.representation.IAspectXDA;

import com.mindalliance.channels.data.Model;
import com.mindalliance.channels.data.Mutex;
import com.mindalliance.channels.nk.ContextHelper;
import com.mindalliance.channels.nk.XDAHelper;
import com.ten60.netkernel.urii.aspect.IAspectString;


public class QueryAccessor extends NKFAccessorImpl {
    
    public QueryAccessor() {
        super( SAFE_FOR_CONCURRENT_USE, INKFRequestReadOnly.RQT_SOURCE );
    }
    
    private class Support {
        
        INKFConvenienceHelper context;
        ContextHelper contextHelper;
        XDAHelper xdaHelper;
        Mutex mutex;
        Model model;
        
        public Support(String config, INKFConvenienceHelper context) {
            this.context = context;
            contextHelper = new ContextHelper(context);
            xdaHelper = new XDAHelper(context);
            mutex = new Mutex(context);
            model = new Model(config, context);
        }
        
        public void log(String message, String level) throws NKFException {
            contextHelper.log( message, level );
        }
    }

    //  active:channels_model+operator@data:<open|delete>...
    @Override
    public void processRequest( INKFConvenienceHelper context ) throws Exception {
        String config = context.getThisRequest().getArgument("config"); // optional, can be null
        Support support = new Support(config, context);
        if (context.getThisRequest().getRequestType() == INKFRequestReadOnly.RQT_SOURCE) {
            source(support);
        }
        else {
            throw new Exception("Invalid request type");
        }
    }

    // Make queries cacheable (all arguments are pass-by-reference)
    // active:channels_model+operator@data:<open|delete|query>+xquery@data:<queryNameNoExtension>[+<varName>@data:<value>]*
    private void source( Support support ) throws Exception {
        support.log( ">> START QUERY", "info" );
        String query = ( (IAspectString) support.context.sourceAspect( "this:param:xquery", IAspectString.class ) ).getString();
        support.log( "Query: " + query, "info" );
        Map<String,String> variables = extractQueryVariables( support.context );
        support.log( "with variables " + variables, "info" );
        // Prepare the query
        if ( !variables.isEmpty() ) {
            // Add variable declaration as prologue
            query = declareVariables( query, variables ); 
        }
        query = support.model.filter( query ); // Substitute place holders
        IAspectXDA result;
        try {
            support.mutex.beginRead( "QUERY" );
            result = support.model.queryModel( query );
        }
        finally {
            support.mutex.endRead( "QUERY" );
        }
        support.log( "<< END QUERY", "info" );
        // Return Response
        INKFResponse resp = support.context.createResponseFrom( result );
        resp.setCacheable();
        resp.setMimeType( "text/xml" );
        support.context.setResponse( resp );
   }

    private String declareVariables( String query, Map<String,String> variables ) {
        String prologuedQuery = query;
        for (String propName : variables.keySet()) {
          String decl = "declare variable $" +  propName + " := '" + variables.get(propName) + "';\n";
          prologuedQuery = decl + prologuedQuery;
        }
        return prologuedQuery;
    }

    private Map<String,String> extractQueryVariables( INKFConvenienceHelper context ) throws NKFException {
        Map<String,String>variables = new HashMap<String,String>();
        Iterator iter = context.getThisRequest().getArguments();
        while (iter.hasNext()) {
            String name = (String)iter.next();
            if (!(name.equals( "xquery" ) || name.equals( "operator" ))) {
            // Variable values passed in as data:text/plain,<uri-encoded value>
            String value = ((IAspectString)context.sourceAspect( "this:param:" + name, IAspectString.class )).getString();
            variables.put( name, value );
            }
        }
        return variables;
    }


}
