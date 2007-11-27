// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.crud.accessors;

import java.util.HashSet;
import java.util.Set;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;
import org.ten60.netkernel.xml.representation.IAspectXDA;
import org.ten60.netkernel.xml.xda.IXDA;
import org.ten60.netkernel.xml.xda.IXDAIterator;
import org.ten60.netkernel.xml.xda.IXDAReadOnly;
import org.ten60.netkernel.xml.xda.IXDAReadOnlyIterator;

import com.mindalliance.channels.crud.Database;
import com.mindalliance.channels.crud.Mutex;
import com.mindalliance.channels.crud.Database.DeletionRecord;
import com.mindalliance.channels.nk.ContextSupport;
import com.ten60.netkernel.urii.aspect.BooleanAspect;
import com.ten60.netkernel.urii.aspect.IAspectBoolean;


public class DocumentAccessor extends NKFAccessorImpl  {
         
    public DocumentAccessor() {
        super( SAFE_FOR_CONCURRENT_USE, ContextSupport.SOURCE | // getDocument
                                        ContextSupport.SINK |   // updateDocument
                                        ContextSupport.NEW |    // createDocument
                                        ContextSupport.DELETE | // deleteDocument
                                        ContextSupport.EXISTS); // documentExists
    }
    // active:channels_document...
    @Override
    public void processRequest( INKFConvenienceHelper context ) throws Exception {
        ContextSupport ctx = new ContextSupport(context);
        String dbName = ctx.sourceString("this:param:database");
        Database database = new Database(dbName, ctx);
        Mutex mutex = new Mutex(ctx);
        switch (ctx.requestType())
            {   case ContextSupport.SOURCE:
                    getDocument(database, mutex,  ctx);
                    break;
                case ContextSupport.SINK:
                    updateDocument(database, mutex, ctx);
                    break;
                case ContextSupport.DELETE:
                    deleteDocument(database, mutex,  ctx);
                    break;
                case ContextSupport.NEW: 
                    createDocument(database, mutex,  ctx);
                    break;
                case ContextSupport.EXISTS: 
                    documentExists(database, mutex,  ctx);
                    break;
                default: throw new Exception("Unsupported request type");
            }       
        }

    private void documentExists( Database database, Mutex mutex, ContextSupport ctx ) throws Exception {
        IAspectBoolean exists;
        ctx.log( ">> START EXISTS", "info" );
        // Access parameter
        String id = ctx.sourceString( "this:param:id");
        String kind = ctx.sourceString( "this:param:kind");
        ctx.log( "Checking if a " + kind + " document exists with id " + id + " in database "
                + database.getName(), "info" );
        // Get document
        try {
            mutex.beginRead( "EXISTS" );
            exists = database.documentExists(kind, id);
        }
        finally {
            mutex.endRead( "EXISTS" );
        }
        ctx.log( "<< END EXISTS", "info" );

        // Return Response
        ctx.respond( exists ).setMimeType( "text/xml" ).setCacheable();
    }

    private void createDocument( Database database, Mutex mutex, ContextSupport ctx ) throws Exception {
        IAspectXDA document = null;
        ctx.log(">> START CREATE", "info");
        try {
            mutex.beginWrite("CREATE");
            IXDA arg = ctx.sourceXDA("this:param:doc");
            document = database.createDocument(arg);
        }
        catch(Exception e) {
            ctx.log("Creating document failed: " + e, "severe");
            throw e;
        }
        finally {
            mutex.endWrite("CREATE");
        }
            
        ctx.log("<< END CREATE", "info");

        ctx.respond( document ).setExpired().setMimeType("text/xml");
    }

    private void deleteDocument( Database database, Mutex mutex, ContextSupport ctx ) throws Exception {
        ctx.log(">> START DELETE", "info");
        String id = ctx.sourceString( "this:param:id");
        IXDAReadOnly refTable = ctx.xdaHelper.sourceXDA( "this:param:refTable" );
        IXDA list =  ctx.xdaHelper.makeXDAAspect("<ids><deleted/><updated/></ids>").getClonedXDA();
        try {
            mutex.beginWrite("DELETE");
            DeletionRecord deletionRecord = database.deleteDocument(id, refTable); // delete document and cascade delete on references
            for (String deleted : deletionRecord.getDeleted()) {
                IXDAReadOnly idElem = ctx.xdaHelper.makeXDA( "<id>" + deleted + "</id>");
                list.append( idElem, ".", "./deleted" );
            }
            for (String updated : deletionRecord.getUpdated()) {
                IXDAReadOnly idElem = ctx.xdaHelper.makeXDA( "<id>" + updated + "</id>");
                list.append( idElem, ".", "./updated" );
            }
            ctx.log("Deleted document and cascaded:\n" + list, "info");
        }
        finally {
            mutex.endWrite("DELETE");
        }
        ctx.log("<< END DELETE", "info");


//        Return Response
        ctx.respond( ctx.asXDAAspect(list) ).
            setExpired(). // don't cache
            setMimeType("text/xml");

    }

    private void updateDocument( Database database, Mutex mutex, ContextSupport ctx ) throws Exception {
        ctx.log(">> START UPDATE", "info");
        IAspectXDA doc = ctx.sourceXDAAspect( ContextSupport.URI_SYSTEM );
        ctx.log("Updating with: " + ctx.xdaHelper.asXML( doc ), "info");
        if (ctx.xdaHelper.existsXPath( doc, "id" )) {
          try {
            mutex.beginWrite("UPDATE");
            database.updateDocument(doc);
          }
          finally {
            mutex.endWrite("UPDATE");
          }
        }
        else {
            ctx.log( "Can't update document with no id", "severe" );
            throw new Exception("Can't update document with no id");
        }
        ctx.log("<< END UPDATE", "info");
//        Return Response
        ctx.respond(new BooleanAspect(true)).setExpired(); // don't cache -- redundant because of pass-by-value argument "doc"
    }

    private void getDocument( Database database, Mutex mutex, ContextSupport ctx ) throws Exception {
        ctx.log( ">> START GET", "info" );

        // Access parameter
        String id = ctx.sourceString( "this:param:id");
        IXDA doc;
        ctx.log( "Getting document " + id + " from container "
                + database.getName(), "info" );
        // Get document
        try {
            mutex.beginRead( "GET" );
            doc = database.getDocument( id ).getClonedXDA();
            if ( ctx.argumentExists( "nameReferenced" ) ) {
                addNamesToReferences( doc, database, ctx);
            }
        }
        finally {
            mutex.endRead( "GET" );
        }
        ctx.log( "<< END GET", "info" );

        // Return Response
        ctx.respond( ctx.xdaHelper.makeXDAAspect( doc ) ).
            setMimeType( "text/xml" ).
            setCacheable();
    }
    
//  Adds names of ids in listed references
    private void addNamesToReferences( IXDA elem, Database database, ContextSupport ctx) throws Exception {
        ctx.log("Adding names to IDs in " + elem, "info");
        Set<String> names = findReferenceNames(elem, ctx);
        for (String name : names) {
                IXDAIterator reference = elem.iterator( ".//" + name );
                while (reference.next()) {
                    String nameOfReferenced = getNameFromId(ctx.xdaHelper.textAtXPath( reference, "." ), database, ctx);
                    reference.appendPath( ".", "@name", nameOfReferenced );
                    ctx.log("Added name to: " + reference, "info");
                }
            }
    }

    private Set<String> findReferenceNames( IXDA elem, ContextSupport ctx ) throws Exception {
        Set<String> list = new HashSet<String>();
        IXDAReadOnlyIterator child = elem.readOnlyIterator( ".//*" );
        while (child.next()) {
            String elName = ctx.xdaHelper.getName( child );
            ctx.log("Checking name " + elName, "info");
            if ((elName != null) && elName.matches(".*Id")) { // better enforce this naming pattern throughout...
                ctx.log("Found listed ID at " + elName, "info");
                list.add(elName);
            }
        }
        return list;
    }

    private String getNameFromId( String id, Database database, ContextSupport ctx) throws Exception {
        ctx.log("Getting name from id :" + id, "info");
        IAspectXDA document = database.getDocument(id);
        return ctx.xdaHelper.textAtXPath( document, "name" );
    }


}
