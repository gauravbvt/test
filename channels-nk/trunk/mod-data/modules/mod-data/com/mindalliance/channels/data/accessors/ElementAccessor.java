// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.accessors;

import java.util.HashSet;
import java.util.Set;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly;
import org.ten60.netkernel.layer1.nkf.INKFResponse;
import org.ten60.netkernel.layer1.nkf.NKFException;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;
import org.ten60.netkernel.xml.representation.IAspectXDA;
import org.ten60.netkernel.xml.xda.IXDA;
import org.ten60.netkernel.xml.xda.IXDAIterator;
import org.ten60.netkernel.xml.xda.IXDAReadOnly;
import org.ten60.netkernel.xml.xda.IXDAReadOnlyIterator;

import com.mindalliance.channels.data.Model;
import com.mindalliance.channels.data.Mutex;
import com.mindalliance.channels.data.Model.DeletionRecord;
import com.mindalliance.channels.nk.ContextHelper;
import com.mindalliance.channels.nk.XDAHelper;
import com.ten60.netkernel.urii.aspect.BooleanAspect;
import com.ten60.netkernel.urii.aspect.IAspectBoolean;


public class ElementAccessor  extends NKFAccessorImpl  {
    
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
     
    public ElementAccessor() {
        super( SAFE_FOR_CONCURRENT_USE, INKFRequestReadOnly.RQT_SOURCE | // getElement
                                        INKFRequestReadOnly.RQT_SINK |   // updateElement
                                        INKFRequestReadOnly.RQT_NEW |    // createElement
                                        INKFRequestReadOnly.RQT_DELETE | // deleteElement
                                        INKFRequestReadOnly.RQT_EXISTS); // elementExists
    }
    // active:channels_element...
    @Override
    public void processRequest( INKFConvenienceHelper context ) throws Exception {
        String config = context.getThisRequest().getArgument("config"); // optional, can be null
        Support support = new Support(config, context);
        switch (support.context.getThisRequest().getRequestType())
            {   case INKFRequestReadOnly.RQT_SOURCE:
                    getElement(support);
                    break;
                case INKFRequestReadOnly.RQT_SINK:
                    updateElement(support);
                    break;
                case INKFRequestReadOnly.RQT_DELETE:
                    deleteElement(support);
                    break;
                case INKFRequestReadOnly.RQT_NEW: 
                    createElement(support);
                    break;
                case INKFRequestReadOnly.RQT_EXISTS: 
                    elementExists(support);
                    break;
                default: throw new Exception("Unsupported request type");
            }       
        }

    private void elementExists( Support support ) throws Exception {
        IAspectBoolean exists;
        support.log( ">> START EXISTS", "info" );
        // Access parameter
        String id = support.context.getThisRequest().getArgument( "id" ).substring( 3 ); // id@id:<id>
        String kind = support.context.getThisRequest().getArgument( "kind" ).substring( 5 ); // kind@kind:<kind>
        support.log( "Checking if element exists with id " + id + " in model "
                + support.model.getModelName(), "info" );
        // Get element
        try {
            support.mutex.beginRead( "EXISTS" );
            exists = support.model.elementExists(kind, id);
        }
        finally {
            support.mutex.endRead( "EXISTS" );
        }
        support.log( "<< END EXISTS", "info" );

        // Return Response
        INKFResponse resp = support.context.createResponseFrom( exists );
        resp.setMimeType( "text/xml" );
        resp.setCacheable();
        support.context.setResponse( resp );
    }

    private void createElement( Support support ) throws Exception {
        IAspectXDA element = null;
        support.log(">> START CREATE", "info");
        try {
            support.mutex.beginWrite("CREATE");
            IAspectXDA arg = (IAspectXDA) support.context.sourceAspect("this:param:doc", IAspectXDA.class);
            element = support.model.createElement(arg);
        }
        catch(Exception e) {
            support.log("Creating element failed: " + e, "severe");
            throw e;
        }
        finally {
            support.mutex.endWrite("CREATE");
        }
            
        support.log("<< END CREATE", "info");

        INKFResponse resp = support.context.createResponseFrom( element );
        resp.setExpired(); // don't cache
        resp.setMimeType("text/xml");
        support.context.setResponse(resp);
    }

    private void deleteElement( Support support ) throws Exception {
        support.log(">> START DELETE", "info");
        String id = support.context.getThisRequest().getArgument( "id" ).substring( 3 );
        IXDA list =  support.xdaHelper.makeXDAAspect("<ids><deleted/><updated/></ids>").getClonedXDA();
        try {
            support.mutex.beginWrite("DELETE");
            DeletionRecord deletionRecord = support.model.deleteElement(id); // delete document and cascade delete on references
            for (String deleted : deletionRecord.getDeleted()) {
                IXDAReadOnly idElem = support.xdaHelper.makeXDA( "<id>" + deleted + "</id>");
                list.append( idElem, ".", "./deleted" );
            }
            for (String updated : deletionRecord.getUpdated()) {
                IXDAReadOnly idElem = support.xdaHelper.makeXDA( "<id>" + updated + "</id>");
                list.append( idElem, ".", "./updated" );
            }
            support.log("Deleted element and cascaded:\n" + list, "info");
        }
        finally {
            support.mutex.endWrite("DELETE");
        }
        support.log("<< END DELETE", "info");


//        Return Response
        INKFResponse resp=support.context.createResponseFrom(support.xdaHelper.makeXDAAspect(list));
        resp.setExpired(); // don't cache
        resp.setMimeType("text/xml");
        support.context.setResponse(resp);

    }

    private void updateElement( Support support ) throws Exception {
        support.log(">> START UPDATE", "info");
        IAspectXDA doc = (IAspectXDA) support.context.sourceAspect(INKFRequestReadOnly.URI_SYSTEM, IAspectXDA.class);
        doc = makeSchemaURLAbsolute(doc, support);
        try {
            support.xdaHelper.validateRNG(doc); // Make sure it is valid
        }
        catch (Exception e) {
            support.log("Update invalid: \n" + support.xdaHelper.asXML(doc) + "\n " + e, "severe");
            throw e;
        }
        support.log("Updating with: " + support.xdaHelper.asXML( doc ), "info");
        if (support.xdaHelper.existsXPath( doc, "id" )) {
          try {
            support.mutex.beginWrite("UPDATE");
            support.model.updateElement(doc);
          }
          finally {
            support.mutex.endWrite("UPDATE");
          }
        }
        else {
            support.log( "Can't update element with no id", "severe" );
            throw new Exception("Can't update element with no id");
        }
        support.log("<< END UPDATE", "info");
//        Return Response
        INKFResponse resp=support.context.createResponseFrom(new BooleanAspect(true));
        resp.setExpired(); // don't cache -- redundant because of pass-by-value argument "doc"
        support.context.setResponse(resp);
    }

    // http://localhost:8080/channels/schema/
    // /channels/schema/repository.rng
    private IAspectXDA makeSchemaURLAbsolute( IAspectXDA doc, Support support ) throws Exception {
        String schemaURL = doc.getXDA().getText( "@schema", true );  // doc.@schema;
        String prefix = support.contextHelper.getProperty( "schema_url", "ffcpl:/etc/data.properties" );
        if (!schemaURL.startsWith( prefix )) {
            support.log( "Making relative schema URL absolute: " + schemaURL, "warning" );
            int index = schemaURL.lastIndexOf( '/' );
            String absoluteURL = prefix + schemaURL.substring( index+1 );
            IXDA changedDoc = doc.getClonedXDA();
            changedDoc.setText( "@schema", absoluteURL );
            return support.xdaHelper.makeXDAAspect( changedDoc );
        }
        return doc;
    }
    
    private void getElement( Support support ) throws Exception {
        support.log( ">> START GET", "info" );

        // Access parameter
        String id = support.context.getThisRequest().getArgument( "id" ).substring( 3 );
        IXDA doc;
        support.log( "Getting element " + id + " from container "
                + support.model.getModelName(), "info" );
        // Get element
        try {
            support.mutex.beginRead( "GET" );
            doc = support.model.getElement( id ).getClonedXDA();
            if ( support.context.getThisRequest().argumentExists( "nameReferenced" ) ) {
                addNamesToReferences( support, doc );
            }
        }
        finally {
            support.mutex.endRead( "GET" );
        }
        support.log( "<< END GET", "info" );

        // Return Response
        INKFResponse resp = support.context.createResponseFrom( support.xdaHelper.makeXDAAspect( doc ) );
        resp.setMimeType( "text/xml" );
        resp.setCacheable();
        support.context.setResponse( resp );
    }
    
//  Adds names of ids in listed references
    private void addNamesToReferences( Support support, IXDA elem) throws Exception {
        support.log("Adding names to IDs in " + elem, "info");
        Set<String> names = findReferenceNames(support, elem);
        for (String name : names) {
                IXDAIterator reference = elem.iterator( ".//" + name );
                while (reference.next()) {
                    String nameOfReferenced = getNameFromId(support, support.xdaHelper.textAtXPath( reference, "." ));
                    reference.appendPath( ".", "@name", nameOfReferenced );
                    support.log("Added name to: " + reference, "info");
                }
            }
    }

    private Set<String> findReferenceNames( Support support, IXDA elem) throws Exception {
        Set<String> list = new HashSet<String>();
        IXDAReadOnlyIterator child = elem.readOnlyIterator( ".//*" );
        while (child.next()) {
            String elName = support.xdaHelper.getName( child );
            support.log("Checking name " + elName, "info");
            if ((elName != null) && elName.matches(".*Id")) { // better enforce this naming pattern throughout...
                support.log("Found listed ID at " + elName, "info");
                list.add(elName);
            }
        }
        return list;
    }

    private String getNameFromId( Support support, String id) throws Exception {
        support.log("Getting name from id :" + id, "info");
        IAspectXDA element = support.model.getElement(id);
        return support.xdaHelper.textAtXPath( element, "name" );
    }


}
