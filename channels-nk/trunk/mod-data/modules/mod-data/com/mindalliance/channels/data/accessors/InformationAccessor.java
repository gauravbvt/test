// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.accessors;

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


public class InformationAccessor extends NKFAccessorImpl {

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

    public InformationAccessor() {
        super( SAFE_FOR_CONCURRENT_USE, INKFRequestReadOnly.RQT_SOURCE );
    }
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
        IAspectXDA information;
        support.log( ">> START INFORMATION", "info" );
        String arg = support.context.getThisRequest().getArgument( "ids" ).substring( 4 ); // ids@ids:<id>[,<id>]*
        support.log( "Getting information templates for  " + arg + " in model "
                + support.model.getModelName(), "info" );
        try {
            support.mutex.beginRead( "INFORMATION" );
            String[] ids = arg.split("\\,");
            information = support.model.getInformation(ids);
        }
        finally {
            support.mutex.endRead( "INFORMATION" );
        }
        support.log( "<< END INFORMATION", "info" );

        // Return Response
        INKFResponse resp = support.context.createResponseFrom( information );
        resp.setMimeType( "text/xml" );
        resp.setCacheable();
        support.context.setResponse( resp );
    }

}
