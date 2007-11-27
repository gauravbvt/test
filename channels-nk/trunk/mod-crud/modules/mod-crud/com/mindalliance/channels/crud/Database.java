// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.crud;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ten60.netkernel.xml.representation.IAspectXDA;
import org.ten60.netkernel.xml.xda.IXDA;
import org.ten60.netkernel.xml.xda.IXDAReadOnly;
import org.ten60.netkernel.xml.xda.IXDAReadOnlyIterator;

import com.mindalliance.channels.nk.ContextSupport;
import com.ten60.netkernel.urii.aspect.IAspectBoolean;


public class Database {
    
    public class DeletionRecord {
        private Set<String> deleted = new HashSet<String>();
        private Set<String> updated = new HashSet<String>();
        
        public boolean alreadyDeleted( String id ) {
            return deleted.contains(id);
        }

        public Set<String> getDeleted() {
            return deleted;
        }

        public void addDeleted( String id ) {
            deleted.add(id);
        }

        public Set<String> getUpdated() {
            return updated;
        } 
        
        public void addUpdated(String id) {
            updated.add( id );
        }
    }
    
    public static final String DATABASE_GOLDEN_THREAD = "gt:channels/database";
    public static final String DOCUMENT_GOLDEN_THREAD = "gt:channels/document/";
    public static final String REFERENCE_TABLE = "ffcpl:/resources/meta/referenceTable.xml";
    private IXMLContainer container = null;
    private ContextSupport ctx;
    
    public Database(String name, ContextSupport contextSupport) {
        container = new DBXMLContainer(name, contextSupport);
        ctx = contextSupport;
    }
    
    public boolean exists() throws Exception {
        return container.containerExists();
    }
    
    public String getName() throws Exception {
        return container.getContainerName();
    }
    
    public void delete() throws Exception {
        container.deleteContainer();
        ctx.cutGoldenThread( DATABASE_GOLDEN_THREAD );
    }
    
    public void create() throws Exception {
        container.createContainer();
    }
    
    public IAspectXDA getDocument(String id) throws Exception {
        IAspectXDA doc = container.getDocument(id);
        // Attach golden thread to document
        doc = (IAspectXDA) ctx.attachGoldenThread(doc, DOCUMENT_GOLDEN_THREAD + id);
        ctx.log("Got document " + doc.getXDA(), "info");
        // return goldenthreaded document
        return doc;
    }
    
    public void updateDocument(IAspectXDA doc) throws Exception {
        String id = ctx.xdaHelper.textAtXPath( doc, "id" ); // document *must* have id
        ctx.log("Updating document with id " + id, "info");
        // Delete older version (must exist else exception)
        container.deleteDocument(id);
        // Then replace with new version
        container.putDocument(doc);
        ctx.log("Document " + id + " put in container " + getName(), "info");    
        // Cut the GoldenThread associated with this resource
        ctx.cutGoldenThread(DOCUMENT_GOLDEN_THREAD + id);
        // Cut the GoldenThread associated with all existing queries
        ctx.cutGoldenThread(DATABASE_GOLDEN_THREAD);
    }
    
    public IAspectXDA queryDatabase(String query) throws Exception {
        ctx.log("Processing query: " + query, "info");
        IAspectXDA res = container.queryContainer(query);
        res = (IAspectXDA)ctx.attachGoldenThread(res, DATABASE_GOLDEN_THREAD);
        ctx.log("Got results to query:\n" + res.getXDA(), "info");
        return res;
    }

//  Adds id document if not there, sets schema attribute, validates and stores. Returns doc as stored.
    public IAspectXDA createDocument(IXDA doc) throws Exception {
        String kind =  ctx.xdaHelper.getName(doc); // xml document's local name
        ctx.log("Creating " + kind + ":\n " + doc, "info");
        // Generate and add GUID as root attribute id if needed 
        if (!ctx.xdaHelper.existsXPath(doc, "id")) {
            String guid = ctx.getGUID();
            doc.appendPath( ".", "id", guid );
        }
        else {
            ctx.log("ID preset", "warning");
        }
        IAspectXDA res = ctx.xdaHelper.makeXDAAspect(doc);
        // Store new document (exception if conflict on id) 
        container.putDocument(ctx.xdaHelper.makeXDAAspect( doc ));   
        // Cut the GoldenThread associated with all existing queries
        ctx.cutGoldenThread(DATABASE_GOLDEN_THREAD);
        ctx.log("Created document " + doc + " in database " + getName(), "info");
        return res;
    }
    
//  Return an array containing an array of deleted document IDs and an array of updated document IDs
    public DeletionRecord deleteDocument(String id, IXDAReadOnly refTable) throws Exception {
        ctx.log("Deleting document " + id, "info");
        DeletionRecord deletionRecord = new DeletionRecord();
        deleteDocumentExcept(id, deletionRecord, refTable);
        // Cut the GoldenThread associated with this resource
        ctx.cutGoldenThread(DOCUMENT_GOLDEN_THREAD + id);
        // Cut the GoldenThread associated with all existing queries
        ctx.cutGoldenThread(DATABASE_GOLDEN_THREAD);
        return deletionRecord;
    }

    private void deleteDocumentExcept( String id, DeletionRecord deletionRecord, IXDAReadOnly refTable ) throws Exception {
        ctx.log("Deleting " + id + " except if in " + deletionRecord.getDeleted(), "info");
        // delete if not already deleted
        if (!deletionRecord.alreadyDeleted( id )) {
            IAspectXDA document = container.deleteDocument(id);
            ctx.log("Deleted document " + id + " from container " + getName(), "info");
            deletionRecord.addDeleted(id);
            // Delete referrers to deleted document that are not already deleted
            deleteReferencesTo(document, deletionRecord, refTable);
        }
    }

    private void deleteReferencesTo( IAspectXDA document,
            DeletionRecord deletionRecord, IXDAReadOnly refTable )
            throws Exception {
        IXDAReadOnly elXDA = document.getXDA();
        String elName = ctx.xdaHelper.getName( elXDA );
        String elId = ctx.xdaHelper.textAtXPath( elXDA, "id" );
        ctx.log( "Deleting references to\n" + elName + ":" + elId
                + " except for " + deletionRecord.getDeleted(), "info" );
        IXDAReadOnlyIterator from = refTable.readOnlyIterator( "./*[@to = \"" + elName + "\"]/from" );
        while ( from.next() ) {
            String[] refNames = ctx.xdaHelper.textAtXPath( from, "@document" ).split("\\|" );
            String refPath = ctx.xdaHelper.textAtXPath( from, "." );
            String cascade;
            if ( from.isTrue( "@cascade" ) ) {
                cascade = ctx.xdaHelper.textAtXPath( from, "@cascade" );
            }
            else {
                cascade = refPath;
            }
            for ( String kind : refNames ) {
                List<String> ids = findReferrers( elId, kind, refPath );
                for ( String id : ids ) {
                    if ( cascade.equals( "." ) ) {
                        // Delete document and cascade
                        deleteDocumentExcept( id, deletionRecord, refTable ); 
                    }
                    else {
                        IAspectXDA referrer = getDocument( id );
                        // log("Deleting in " + referrer + " reference
                        // " + cascade, "info");
                        deleteReference( referrer.getClonedXDA(), refPath,
                                cascade, elId, deletionRecord ); // remove reference to document from referrer
                    }
                }
            }
        }
    }

    private void deleteReference( IXDA referrer, String refPath, String cascade, String id, DeletionRecord deletionRecord ) throws Exception {
        ctx.log("Delete reference to " + id + " by " + referrer + " via " + refPath + " deleting " + cascade, "info");
        if (refPath.indexOf(cascade) != 0){     // Invalid cascade path
            ctx.log("Error in reference table: " + cascade + " in " + refPath, "severe");
            throw new Exception("Invalid reference table");
        }
        String testPath = refPath + "[. = \"" + id + "\"]";
        while (referrer.isTrue(testPath)) {
            if (refPath.equals(cascade)) { // just delete the reference
                referrer.delete( testPath );
                ctx.log("Deleted immediate reference " + id + " in " + referrer + " at " + testPath, "info");
            }
            else { // delete ancestor of document
                // get the ancestor to delete
                String parentPath = makeParentXPath(refPath, cascade, id); 
                referrer.delete(parentPath);
                ctx.log("Deleted reference holder " + parentPath + " in " + referrer , "info");
            }
            // update document
            updateDocument(ctx.xdaHelper.makeXDAAspect( referrer ));
            deletionRecord.addUpdated(ctx.xdaHelper.textAtXPath( referrer, "id" ));
        }
    }

    // cascade[refPathAfterCascade = "id"]
    private String makeParentXPath( String refPath, String cascade, String id ) {
        String refPathAfterCascade = refPath.substring(cascade.length() + 1);
        String xpath = cascade + "[" + refPathAfterCascade + " = \"" + id + "\"]";
        return xpath;
    }

    private List<String> findReferrers( String id, String kind, String path ) throws Exception {
        ctx.log("Finding " + kind + " referrers of " + id + " by " + path, "info");
        String query = "<list>\n" +
                                    "{\n" +
                                        "for $e in collection('__DB__')/" + kind + "\n" +
                                        "where $e/" + path.substring(2,path.length()) + " = '" + id + "'\n" +
                                        "return\n" +
                                        "  <id>{$e/id/text()}</id>\n" +
                                    "}\n" +
                    "</list>";
        IAspectXDA result = queryDatabase(filter(query));
        IXDAReadOnly list = result.getXDA();
        ctx.log("Referrers are: \n" + list, "info");
        List<String> ids = new ArrayList<String>();
        IXDAReadOnlyIterator refId = result.getXDA().readOnlyIterator( "./id" );
        while(refId.next()) {
            ids.add(ctx.xdaHelper.textAtXPath( refId, "." ));
        }
        return ids;
    }
    
    public String filter(String s) throws Exception {
        String fs = s.replaceAll("__DB__", getName()); 
        // more filters here
        return fs;
      }

    public IAspectBoolean documentExists( String kind, String id ) throws Exception {
        IAspectBoolean exists = container.documentExists(kind, id);
        // Attach golden thread to document
        exists = (IAspectBoolean)ctx.attachGoldenThread(exists, DOCUMENT_GOLDEN_THREAD + id);
        ctx.log("Document " + id + " exists: " + exists.isTrue(), "info");
        // return goldenthreaded document
        return exists;
    }

}

