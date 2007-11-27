// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.nk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Properties;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.INKFRequest;
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly;
import org.ten60.netkernel.layer1.nkf.INKFResponse;
import org.ten60.netkernel.layer1.nkf.NKFException;
import org.ten60.netkernel.layer1.representation.IAspectNVP;
import org.ten60.netkernel.xml.representation.IAspectXDA;
import org.ten60.netkernel.xml.xda.IXDA;
import org.ten60.netkernel.xml.xda.IXDAReadOnly;

import com.ten60.netkernel.urii.IURAspect;
import com.ten60.netkernel.urii.IURRepresentation;
import com.ten60.netkernel.urii.aspect.IAspectString;
import com.ten60.netkernel.urii.aspect.StringAspect;


public class ContextSupport {
    
    public class Request {
        
        INKFRequest nkRequest = null;
        IURRepresentation representation = null;
        INKFResponse response = null;

        public Request() {}
        
        public Request( INKFRequest request ) {
            nkRequest = request;
        }

        public Request ofType(int type) {
            nkRequest.setRequestType( type );
            return this;
        }
        
        public Request withArg(String name, IURAspect aspect) {
            nkRequest.addArgument( name, aspect );
            return this;
        }
        
        public Request withArg(String name, String uri) throws NKFException {
            nkRequest.addArgument( name, uri );
            return this;
        }

        private Request withArg( String name, IURRepresentation rep ) {
            nkRequest.addArgument(name, rep);
            return this;
        }

        public Request withString( String name, String value ) {
            withArg(name, new StringAspect(value));
            return this;
        }

        public Request withData( String name, String value ) throws Exception {
            withArg(name, asData(value));
            return this;
        }

        public Request withXDA( String name, IXDAReadOnly document ) {
            withArg(name, xdaHelper.makeXDAAspect(document));
            return this;
        }

        public Request forwardArg( String name ) throws NKFException {
            withArg( name, context.source( "this:param:" + name ) );
            return this;
        }

        public Request forwardArg( String fromName, String toName) throws NKFException {
            withArg( toName, context.source( "this:param:" + fromName ) );
            return this;
        }

        public Request issue() throws NKFException {
            representation = context.issueSubRequest( nkRequest );
            return this;
        }

        public Request respond() throws NKFException {
            if (representation == null) issue();
            response = context.createResponseFrom( representation );
            return this;
        }
        
        public Request respond(IURAspect aspect) throws NKFException {
            if (representation == null && nkRequest != null) issue(); // allow creation of a response without issuing a request
            response = context.createResponseFrom( aspect );
            return this;
        }

        public Request setMimeType(String mimeType) throws NKFException {
            if (response == null) respond();
            response.setMimeType( mimeType );
            return this;
        }

        public Request setExpired() throws NKFException {
            if (response == null) respond();
            response.setExpired();
            return this;
        }

        public Request setCacheable() throws NKFException {
            if (response == null) respond();
            response.setCacheable();
            return this;
        }

        public IURAspect transreptTo( Class<? extends IURAspect> aspectClass ) throws NKFException {
            if (representation == null) issue();
            return context.transrept( representation, aspectClass );
        }

        public Request withSystemArg( IURAspect aspect ) {
            nkRequest.addSystemArgument( aspect );
            return this;
        }

        public IURAspect issueForAspect( Class<? extends IURAspect> aspectClass ) throws NKFException {
            nkRequest.setAspectClass( aspectClass );
            return context.issueSubRequestForAspect( nkRequest );
        }

}
    
    public static final String LOG_URL = "ffcpl:/etc/LogConfig.xml";

    public static final int NEW = INKFRequestReadOnly.RQT_NEW;
    public static final int SOURCE = INKFRequestReadOnly.RQT_SOURCE;
    public static final int SINK = INKFRequestReadOnly.RQT_SINK;
    public static final int DELETE = INKFRequestReadOnly.RQT_DELETE;
    public static final int EXISTS = INKFRequestReadOnly.RQT_EXISTS;

    public static final String URI_SYSTEM = INKFRequestReadOnly.URI_SYSTEM;
    
    public INKFConvenienceHelper context;
    public XDAHelper xdaHelper;
    
    public ContextSupport(INKFConvenienceHelper context) {
        this.context =context;
        xdaHelper = new XDAHelper(context);
    }
    
    public Request subRequest( String uri ) throws NKFException {
        Request req = new Request(context.createSubRequest( uri ));
        return req;
    }
    
    public Request respond(IURAspect aspect) throws NKFException {
        Request req = new Request();
        req.respond(aspect);
        return req;
    }

    public IAspectXDA asXDAAspect( IXDAReadOnly document ) {
        return xdaHelper.makeXDAAspect( document );
    }
    
    public IAspectXDA asXDAAspect( String xml ) throws NKFException {
        return xdaHelper.makeXDAAspect( xml );
    }
    
    public void log(String content, String level) throws NKFException {
        subRequest("active:application-log").
            withString("operand", content ).
            withArg("configuration", LOG_URL ). // Defaults to ffcpl:/etc/LogConfig.xml
            withString("operator", "<log>" + "<" + level + "/>" + "</log>").
            issue();
      }

    public String getProperty(String name, String uri) throws IOException, Exception {
        String content = ((IAspectString)context.sourceAspect( uri, IAspectString.class )).getString();
        Properties props = new Properties();
        props.load( new ByteArrayInputStream(content.getBytes()) );
        String value = props.getProperty( name );
        return value; 
    }
    
    public void expire(String uri) throws NKFException {
        subRequest("active:expire").
            withArg("operand", uri).
            issue();
    }
    
    public IURAspect attachGoldenThread(IURAspect resource, String uri) throws NKFException {
        log("Attaching GT " + uri, "info");
        return subRequest("active:attachGoldenThread").
                withArg("operand", resource).
                withArg("param", uri).
                transreptTo(resource.getClass());
    }
    
    public void cutGoldenThread(String uri) throws NKFException {
        subRequest("active:cutGoldenThread").
            withArg("param", uri).
            issue();
        log("Cut GT " + uri, "info");
    }

    public void sleep(int msecs) throws NKFException {
        log("Sleeping for " + msecs, "info");
        subRequest("active:sleep").
            withString("operator", "<time>" + msecs + "</time>").
            issue();
    }
    
    public String getGUID() throws NKFException, Exception {
        IAspectXDA xml = ((IAspectXDA)context.sourceAspect("active:guid",IAspectXDA.class));
        return xml.getXDA().getText( ".", true );
    }
    
    public String asData(Object value) throws Exception {
        return "data:text/plain," + URLEncoder.encode(value.toString(), "UTF-8");
    }


    public boolean argumentExists( String name ) throws NKFException {
        return context.getThisRequest().argumentExists( name );
    }


    public boolean exists( String uri ) throws NKFException {
        return context.exists( uri );
    }


    public int requestType() throws NKFException {
        return context.getThisRequest().getRequestType();
    }


    public String sourceString( String uri ) throws NKFException {
        return ((IAspectString)context.sourceAspect(uri, IAspectString.class)).getString();
    }

    public IXDA sourceXDA( String uri ) throws NKFException {
        return sourceXDAAspect(uri).getClonedXDA();
    }

    public IAspectXDA sourceXDAAspect( String uri ) throws NKFException {
        return (IAspectXDA)context.sourceAspect(uri, IAspectXDA.class);
    }

   public IXDAReadOnly sourceXDAReadOnly(String uri) throws NKFException {
        String xml = ((IAspectString)context.sourceAspect(uri, IAspectString.class)).getString(); // get string aspect first otherwise db.* also gathers whitespace children
        IXDAReadOnly db = xdaHelper.makeXDA( xml ); 
        return db;
    }


public IAspectNVP sourceNVP( String uri ) throws NKFException {
    return (IAspectNVP)context.sourceAspect( uri, IAspectNVP.class );
}


public boolean isArgumentSet( String name ) throws NKFException {
    return argumentExists(name) && (context.getThisRequest().getArgument(name) != null);
}


public String getArgument( String name ) throws NKFException {
    return context.getThisRequest().getArgument(name);
}


public Iterator getArguments() throws NKFException {
    return context.getThisRequest().getArguments();
}



}
