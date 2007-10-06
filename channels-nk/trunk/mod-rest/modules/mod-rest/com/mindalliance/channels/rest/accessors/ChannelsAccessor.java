// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.rest.accessors;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.INKFRequest;
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly;
import org.ten60.netkernel.layer1.nkf.INKFResponse;
import org.ten60.netkernel.layer1.nkf.NKFException;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;
import org.ten60.netkernel.layer1.representation.IAspectNVP;
import org.ten60.netkernel.layer1.representation.StringAspect;

import com.mindalliance.channels.nk.ContextHelper;
import com.ten60.netkernel.urii.aspect.IAspectString;


public class ChannelsAccessor  extends NKFAccessorImpl {

    public ChannelsAccessor() {
        super( SAFE_FOR_CONCURRENT_USE, INKFRequestReadOnly.RQT_SOURCE );
    }
    
    @Override
    public void processRequest( INKFConvenienceHelper context ) throws Exception {
        ContextHelper contextHelper = new ContextHelper(context);
        if (contextHelper.context.getThisRequest().getRequestType() == INKFRequestReadOnly.RQT_SOURCE) {
            source(contextHelper);
        }
        else {
            contextHelper.log( "Invalid request type (not SOURCE)", "severe" );
            throw new Exception("Invalid request type");
        }
    }

    /*
     * ffcpl:/channels/element.*
     * ffcpl:/channels/information.*
     * ffcpl:/channels/query.*
    */
    private void source( ContextHelper contextHelper ) throws Exception {
        contextHelper.log("Sourcing: " + contextHelper.context.getThisRequest().getURI(), "info");
        String path = contextHelper.context.getThisRequest().getURIWithoutFragment();
        String operator = extractOperator(path);
        if (operator == null) throw new Exception("Invalid request: missing operation");
        if (operator.equals( "element" )) {
            sourceElement(contextHelper);
        }
        else if (operator.equals( "information" )) {
            sourceInformation(contextHelper);
        }
        else if (operator.equals( "query" )) {
            sourceQuery(contextHelper);
        }
        else {
            contextHelper.log( "Invalid operator: "+ operator, "severe" );
            throw new Exception("Invalid operator: "+ operator);
        }   
    }
    
    private void sourceQuery( ContextHelper contextHelper ) throws Exception {
        contextHelper.log( "Sourcing query", "info" );
        INKFResponse resp = null;
        if(contextHelper.context.exists("this:param:param")) {
            Map<String,String> params = getParams(contextHelper);
            String query = params.get("query");
            // query
            if(query != null) {
                INKFRequest req = contextHelper.context.createSubRequest("active:channels_query");
                req.addArgument("xquery", "xquery:" + query);
                addQueryVariables(params, req);
                try {
                    resp = contextHelper.context.createResponseFrom(contextHelper.context.issueSubRequest(req));
                } catch(Exception e) {
                    contextHelper.log( "Unable to process query: " + query, "severe" );
                    IAspectString error = new StringAspect("<error>Unable to process query: " + query 
                        + "." + e.getMessage() + "</error>");
                    resp = contextHelper.context.createResponseFrom(error);
                    e.printStackTrace();
                }
            }
        } else {
            IAspectString error = new StringAspect("<error>Expecting parameters but didn't get any in model.bsh</error>");
            resp = contextHelper.context.createResponseFrom(error);
        }
        resp.setMimeType("text/xml");
        contextHelper.context.setResponse(resp);
    }

    private void sourceElement( ContextHelper contextHelper ) throws Exception {
        contextHelper.log( "Sourcing element", "info" );
        INKFResponse response = null;
        String type=contextHelper.context.getThisRequest().getArgument("type");
        // Create
        if (type != null) {
            IAspectString doc = (IAspectString)contextHelper.context.sourceAspect("this:param:param", IAspectString.class); // POSTed
            response = createElement(type, doc, contextHelper);
        }
        else {
            String method = (((IAspectString)contextHelper.context.sourceAspect("literal:method", IAspectString.class)).getString()); // HTTP method
            Map<String, String> params = getParams(contextHelper);
            String id=params.get("id");
            // Get
            if (method.equals( "GET" )) {
                boolean nameReferenced = params.containsKey("nameReferenced");
                response = getElement(id, nameReferenced, contextHelper);
            }
            else if (method.equals("POST")) {
                String action = params.get("method");
                // Delete
                if (action.equals( "DELETE" )) {
                    response = deleteElement(id, contextHelper);
                }
                else if (action.equals( "PUT" )) {
                    // Update
                    IAspectString doc = (IAspectString)contextHelper.context.sourceAspect("this:param:param", IAspectString.class);
                    response = updateElement(doc, contextHelper);
                }
                else {
                    contextHelper.log("Invalid POST action: " + action, "severe" );
                    throw new Exception("Invalid POST action: " + action); 
                }     
            }
            else {
                contextHelper.log("Invalid HTTP method: " + method, "severe" );
                throw new Exception("Invalid HTTP method: " + method);
            }
        }
        response.setMimeType( "text/xml" );
        contextHelper.context.setResponse(response);
    }

    private INKFResponse createElement(String type, IAspectString doc, ContextHelper contextHelper) throws NKFException {
        try {
            INKFRequest req=contextHelper.context.createSubRequest("active:channels_element");
            req.setRequestType( INKFRequestReadOnly.RQT_NEW );
            req.addArgument("doc", doc);             
            return contextHelper.context.createResponseFrom(contextHelper.context.issueSubRequest(req));
        } catch(Throwable exc) {
            contextHelper.log("Error creating element: " + exc.getMessage(), "severe" );
            IAspectString error = new StringAspect("<error> Error creating element: " + exc.getMessage() + "</error>");
            return contextHelper.context.createResponseFrom(error);              
        }      
    }

    private INKFResponse getElement( String id, boolean nameReferenced, ContextHelper contextHelper ) throws NKFException {
        contextHelper.log("Getting element at id " + id + " naming references = " + nameReferenced, "info");
        try {
            INKFRequest req=contextHelper.context.createSubRequest("active:channels_element");
            req.setRequestType( INKFRequestReadOnly.RQT_SOURCE );
            req.addArgument("id", "id:" + id);
            if (nameReferenced) { 
                req.addArgument("nameReferenced", new StringAspect("true")); // value does not matter
            }
            return contextHelper.context.createResponseFrom(contextHelper.context.issueSubRequest(req));
        }
        catch (Throwable exc) {
            contextHelper.log("Error getting " + id + ": " + exc.getMessage(), "severe" );
            IAspectString error = new StringAspect("<error> Error getting " + id + ": " + exc.getMessage() + "</error>");
            return contextHelper.context.createResponseFrom(error);            
        }
    }

    private INKFResponse updateElement(IAspectString doc, ContextHelper contextHelper ) throws NKFException {
        try {
            INKFRequest req=contextHelper.context.createSubRequest("active:channels_element");
            req.setRequestType( INKFRequestReadOnly.RQT_SINK );
            req.addSystemArgument(doc);             
            return contextHelper.context.createResponseFrom(contextHelper.context.issueSubRequest(req));
        }
        catch (Throwable exc) {
            contextHelper.log("Error updating element: " + exc.getMessage(), "severe" );
            IAspectString error = new StringAspect("<error> Error updating element: " + exc.getMessage() + "</error>");
            return contextHelper.context.createResponseFrom(error);             
            
        }
    }

    private INKFResponse deleteElement( String id, ContextHelper contextHelper ) throws NKFException {
        try {
            INKFRequest req=contextHelper.context.createSubRequest("active:channels_element");
            req.setRequestType( INKFRequestReadOnly.RQT_DELETE );
            req.addArgument("id", "id:" + id);
            return contextHelper.context.createResponseFrom(contextHelper.context.issueSubRequest(req));
        }
        catch (Throwable exc) {
            contextHelper.log("Error deleting element: " + exc.getMessage(), "severe" );
            IAspectString error = new StringAspect("<error> Error deleting " + id + ": " + exc.getMessage() + "</error>");
            return contextHelper.context.createResponseFrom(error);              
        }
    }

    private void sourceInformation( ContextHelper contextHelper ) throws Exception {
        contextHelper.log( "Sourcing information", "info" );
        INKFResponse response = null;
        if(contextHelper.context.exists("this:param:param")) {
            Map<String,String> params = getParams(contextHelper);
            String ids=params.get("ids");
            if(ids != null) {
                try {
                    INKFRequest req=contextHelper.context.createSubRequest("active:channels_information");
                    req.addArgument("ids", "ids:" + ids);
                    response = contextHelper.context.createResponseFrom(contextHelper.context.issueSubRequest(req));
               } catch(Exception exc) {
                   contextHelper.log("Unable to get information template for: " + ids 
                           + ": " + exc.getMessage(), "severe" );
                   IAspectString error = new StringAspect("<error>Unable to get information template for: " + ids 
                           + ": " + exc.getMessage() + "</error>");
                    response = contextHelper.context.createResponseFrom(error);
                }
            }
            else {
                contextHelper.log("Expecting ids parameter but didn't get it", "severe" );
                throw new Exception("Expecting ids parameter but didn't get it");
            }

        } else {
            contextHelper.log("Expecting parameters but didn't get any", "severe" );
            throw new Exception("Expecting parameters but didn't get any");
        }
        response.setMimeType("text/xml");
        contextHelper.context.setResponse(response);
    }

    // The word after the '/' and before '+', if any
    private String extractOperator( String path ) {
        String op = null;
        int index = path.indexOf( '/' );
        if ( index != -1 ) {
            op = path.substring( index + 1 );
            index = op.indexOf( '+' );
            if ( index != -1 ) {
                op = op.substring( 0, index );
            }
        }
        return op;
    }
    
    private Map<String,String> getParams( ContextHelper contextHelper ) throws NKFException {
        Map<String,String> params = null;
        if(contextHelper.context.exists("this:param:param2")) {
            IAspectNVP nvp = (IAspectNVP) contextHelper.context.sourceAspect("this:param:param2", IAspectNVP.class);
            params = nvpToMap(nvp);
        } else  if(contextHelper.context.exists("this:param:param")) { 
            IAspectNVP nvp = (IAspectNVP) contextHelper.context.sourceAspect("this:param:param", IAspectNVP.class);
            params = nvpToMap(nvp);
 }
        else {
            contextHelper.log( "Found no param (or param2)", "warning" );
            params = new HashMap<String,String>();
        }
        return params;
    }

    private Map<String, String> nvpToMap( IAspectNVP nvp ) {
        Map<String,String> map = new HashMap<String,String>();
        Iterator allNames = nvp.getNames().iterator();
        while (allNames.hasNext()) {
            String name = (String)allNames.next();
            map.put( name, nvp.getValue( name ) );
        }
        return map;
    }

    private void addQueryVariables( Map<String, String> params, INKFRequest req ) throws Exception {
        Iterator paramNames = params.keySet().iterator();
        while (paramNames.hasNext()) {
          String paramName = (String)paramNames.next();
          if (!paramName.equals("query")) {
            String value = params.get(paramName);
            String encoded = "data:text/plain," + URLEncoder.encode(value, "UTF-8");
            req.addArgument(paramName, encoded);
          }
        }
    }    

}
