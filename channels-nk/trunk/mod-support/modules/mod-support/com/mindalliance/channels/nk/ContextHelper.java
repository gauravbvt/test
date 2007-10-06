// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.nk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.INKFRequest;
import org.ten60.netkernel.layer1.nkf.NKFException;
import org.ten60.netkernel.xml.representation.IAspectXDA;

import com.ten60.netkernel.urii.IURAspect;
import com.ten60.netkernel.urii.IURRepresentation;
import com.ten60.netkernel.urii.aspect.IAspectString;
import com.ten60.netkernel.urii.aspect.StringAspect;


public class ContextHelper {

    public static final String LOG_URL = "ffcpl:/etc/LogConfig.xml";

    public INKFConvenienceHelper context;
    
    public ContextHelper(INKFConvenienceHelper context) {
        this.context = context;
    }

    public void log(String content, String level) throws NKFException {
        INKFRequest req=context.createSubRequest("active:application-log");
        req.addArgument("operand", new StringAspect(content) );
        req.addArgument("configuration", LOG_URL ); // Defaults to ffcpl:/etc/LogConfig.xml
        String levelXml = "<log>" + "<" + level + "/>" + "</log>";
        req.addArgument("operator", new StringAspect(levelXml) );
        // context.issueAsyncSubRequest(req);
        context.issueSubRequest(req);
      }

    public String getProperty(String name, String uri) throws IOException, Exception {
        String content = ((IAspectString)context.sourceAspect( uri, IAspectString.class )).getString();
        Properties props = new Properties();
        props.load( new ByteArrayInputStream(content.getBytes()) );
        String value = props.getProperty( name );
        return value; 
    }
    
    public void expire(String uri) throws NKFException {
        INKFRequest req = context.createSubRequest("active:expire");
        req.addArgument("operand", uri);
        context.issueSubRequest(req);
    }
    
    public IURAspect attachGoldenThread(IURAspect resource, String uri) throws NKFException {
        INKFRequest req=context.createSubRequest("active:attachGoldenThread");
        req.addArgument("operand", resource);
        req.addArgument("param", uri);
        IURRepresentation result=context.issueSubRequest(req);
        log("Attached GT " + uri, "info");
        return context.transrept( result, resource.getClass() );
    }
    
    public void cutGoldenThread(String uri) throws NKFException {
        INKFRequest req=context.createSubRequest("active:cutGoldenThread");
        req.addArgument("param", uri);
        context.issueSubRequest(req);
        log("Cut GT " + uri, "info");
    }

    public void sleep(int msecs) throws NKFException {
        log("Sleeping for " + msecs, "info");
        INKFRequest req = context.createSubRequest("active:sleep");
        String time = "<time>" + msecs + "</time>";
        req.addArgument("operator", new StringAspect(time));
        context.issueSubRequest(req);
    }
    
    public String getGUID() throws NKFException, Exception {
        IAspectXDA xml = ((IAspectXDA)context.sourceAspect("active:guid",IAspectXDA.class));
        return xml.getXDA().getText( ".", true );
    }
    
    public String asData(Object value) throws Exception {
        return "data:text/plain," + URLEncoder.encode(value.toString(), "UTF-8");
    }

}
