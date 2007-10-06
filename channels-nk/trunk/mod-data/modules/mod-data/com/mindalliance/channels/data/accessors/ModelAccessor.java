// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.accessors;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly;
import org.ten60.netkernel.layer1.nkf.INKFResponse;
import org.ten60.netkernel.layer1.nkf.NKFException;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;
import org.ten60.netkernel.layer1.representation.BooleanAspect;
import org.ten60.netkernel.xml.representation.IAspectXDA;
import org.ten60.netkernel.xml.xda.IXDAReadOnly;
import org.ten60.netkernel.xml.xda.IXDAReadOnlyIterator;

import com.mindalliance.channels.data.Model;
import com.mindalliance.channels.data.Mutex;
import com.mindalliance.channels.nk.ContextHelper;
import com.mindalliance.channels.nk.XDAHelper;
import com.ten60.netkernel.urii.aspect.IAspectString;


public class ModelAccessor extends NKFAccessorImpl {
    
    private static final int MAX_DELETE_RETRIES = 50;
    private static final int SLEEP_BETWEEN_RETRIES = 200; // msecs
    
    public ModelAccessor() {
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

    private void source( Support support ) throws Exception {
        String operator = ((IAspectString)support.context.sourceAspect("this:param:operator", IAspectString.class)).getString();
        if (operator == null) throw new Exception("Invalid request: missing operation");
        if (operator.equals( "open" )) {
            openModel(support);
        }
        else if (operator.equals( "delete" )) {
            deleteModel(support);
        }
        else if (operator.equals( "get" )) {
            getModel(support);
        }
        else {
            throw new Exception("Invalid model operation " + operator);
        }
    }

    private void getModel( Support support ) throws Exception {
        support.log( "Getting model", "info" );
        String name = support.model.getModelName();
        IAspectXDA xda = (IAspectXDA) support.context.sourceAspect( "db:" + name, IAspectXDA.class );
        // Return Response
        INKFResponse resp=support.context.createResponseFrom(xda);
        resp.setExpired(); // don't cache
        support.context.setResponse(resp);
    }

    private void deleteModel(Support support) throws Exception {
        support.log( "Deleting model", "info" );
        boolean exists = support.model.modelExists();
        int retries = MAX_DELETE_RETRIES;
        String modelName = support.model.getModelName();
        while (support.model.modelExists() && (retries > 0)) {
            retries--;
            try {
                exists = support.model.modelExists();
                if (exists) {
                    support.model.deleteModel();
                    support.log("Deleted model: " + modelName, "info");
                }
                else {
                    support.log("Database not deleted (does not exist): " + modelName, "warning");
                }
            }
            catch(Exception e) { // Deleting fails temporarily and spurriously
                support.log("Delete model failed: " + e, "warning");
                support.contextHelper.sleep(SLEEP_BETWEEN_RETRIES);
            }
        }
        // Return Response
        INKFResponse resp=support.context.createResponseFrom(new BooleanAspect(exists));
        resp.setExpired(); // don't cache
        support.context.setResponse(resp);
    }

    private void openModel( Support support ) throws Exception {
        support.log( "Opening model", "info" );
        boolean exists;
        try {
            support.log("Opening model: " + support.model.getModelName(), "info");
            exists = support.model.modelExists();
            if (!exists) {
              // Create database
              support.model.createModel();
              support.log("Created model: " + support.model.getModelName(), "info");
              // Initialize from file if requested
              if (support.context.getThisRequest().argumentExists("init")) {
                support.log("Initializing database from " + support.context.getThisRequest().getArgument("init"), "info");
                initializeFrom(support, "this:param:init");
              }
              else { // look for an initialization file at db:<containerName> and use it if there
                String uri = "db:" + support.model.getModelName();
                if (support.context.exists(uri)) {
                    initializeFrom(support, uri);
                }
              }
            }
        }
        catch(Exception e) {
            support.log("Open model failed: " + e, "severe");
            throw e ;
        }
        // Return Response
        INKFResponse resp=support.context.createResponseFrom(new BooleanAspect(!exists));
        resp.setExpired(); // don't cache
        support.context.setResponse(resp);
    }

    private void initializeFrom( Support support, String uri ) throws Exception {
        String xml = ((IAspectString)support.context.sourceAspect(uri, IAspectString.class)).getString(); // get string aspect first otherwise db.* also gathers whitespace children
        IXDAReadOnly db = support.xdaHelper.makeXDA( xml ); 
        support.log("Initializing db from " + uri, "info");
        IXDAReadOnlyIterator element = db.readOnlyIterator( "./*" );
        while (element.next()) {
            support.model.createElement(support.xdaHelper.makeXDAAspect( element ));
        }
        support.log("Database initialized", "info");
    }

}
