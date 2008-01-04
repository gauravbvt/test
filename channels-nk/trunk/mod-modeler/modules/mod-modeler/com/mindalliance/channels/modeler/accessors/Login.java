// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.modeler.accessors;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;
import org.ten60.netkernel.layer1.representation.IAspectNVP;
import org.ten60.netkernel.xml.representation.IAspectXDA;

import com.mindalliance.channels.nk.Session;
import com.mindalliance.channels.nk.ContextSupport;


public class Login  extends NKFAccessorImpl {
    
    public static final String SESSION_COOKIE_NAME = "NETKERNELSESSION";
    public static final String AUTHETICATE_QUERY_URI = "ffcpl:/resources/xqueries/authenticate.xq";
    public static final String PROJECTS_CONFIG_URI = "ffcpl:/etc/projects.xml";
    public static final String VIEWLINKS_URI = "ffcpl:/etc/viewLinks.xml";
    public static final String INVALID_LOGIN_URI = "ffcpl:/modeler/view/invalidLogin";
   
    public Login() {
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
        IAspectNVP nvp = ctx.sourceNVP("this:param:param");
        String userid = nvp.getValue("userid");
        String project = nvp.getValue("project");
        String password = nvp.getValue("password");
        if (userid != null && password != null && project != null 
                && authenticate(userid, password, project, ctx )) {  
            if (ctx.isArgumentSet("cookie")) {
                String cookie = ctx.xdaHelper.getCookie(SESSION_COOKIE_NAME);
                System.out.println(cookie);
            }
            // Store session credentials
            Session session = new Session(ctx);
            session.storeToken("credentials", userid);
            // Store session project
            session.storeToken( "project", project);
            // Issue HTTP Redirect
            String url = nvp.getValue( "url" );
            ctx.subRequest("active:HTTPRedirect").
                withString("operator", "<url>"+url+"</url>").
                issue().
                setMimeType("text/xml").setExpired();
        }
        else {
            ctx.subRequest( "active:source" ).
                withArg( "uri", INVALID_LOGIN_URI ).
                withArg( "param", nvp ).
                withArg( "links", VIEWLINKS_URI).
                withString( "url", nvp.getValue( "url" ) ).
                issue().
                setMimeType("text/xml").setExpired();
       }
    }

    private boolean authenticate( String userid, String password, String project, ContextSupport ctx ) throws Exception {
        IAspectXDA iax = (IAspectXDA)ctx.subRequest( "active:xquery" ).
                                        withArg( "operator", AUTHETICATE_QUERY_URI ).
                                        withArg( "input", PROJECTS_CONFIG_URI ).
                                        withString( "userid", "<string>"+userid+"</string>" ).
                                        withString( "password", "<string>"+password+"</string>" ).
                                        withString( "project", "<string>"+project+"</string>").
                                        transreptTo(IAspectXDA.class);
        String tf = ctx.xdaHelper.textAtXPath( iax, ".//b" );
        return tf.equals( "t" );
    }
   
}
