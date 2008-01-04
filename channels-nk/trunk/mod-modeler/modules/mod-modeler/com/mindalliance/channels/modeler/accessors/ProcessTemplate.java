// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.modeler.accessors;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;
import org.ten60.netkernel.layer1.representation.IAspectNVP;

import com.mindalliance.channels.nk.ContextSupport;
import com.ten60.netkernel.urii.aspect.IAspectString;
import com.ten60.netkernel.urii.aspect.StringAspect;


public class ProcessTemplate  extends NKFAccessorImpl {

    public ProcessTemplate() {
        super(4,false,ContextSupport.SOURCE);
    }
    
    @Override
    public void processRequest( INKFConvenienceHelper context ) throws Exception {
        ContextSupport ctx = new ContextSupport(context);
        switch (ctx.requestType()) {
        case ContextSupport.SOURCE:
            source(ctx);
            break;
        default: throw new Exception("Invalid request type");
        }
    }
        
    private void source( ContextSupport ctx ) throws Exception {
        String template = null;
        String uri = ctx.getArgument( "template" );
        IAspectNVP nvp = ctx.sourceNVP( "this:param:param");
        if (uri.startsWith( "xrl:" )) { 
            // Obtain template via XRL, passing in links and param
            String links = ctx.getArgument( "operator" );
            template = ((IAspectString)ctx.subRequest( "active:source" ).
                                            withArg( "uri", uri ).
                                            withArg("operator", links). // Exception if null
                                            withArg( "param", nvp ).
                                            transreptTo(IAspectString.class)
                        ).getString();
        }
        else {
            template = ctx.sourceString( uri );
        }
        // Substitute in parameters
        for (Object obj : nvp.getNames()) {
            String name = (String)obj;
            String value = nvp.getValue( name );
            template = template.replaceAll( "~~"+name+"~~", value );
        }
        // Make response
        ctx.respond(new StringAspect(template)).
            setMimeType( "text/xml" ).
            setExpired();
    }

}
