// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.xml.representation.IAspectXDA;
import org.ten60.netkernel.xml.xda.IXDA;
import org.ten60.netkernel.xml.xda.IXDAReadOnly;
import org.ten60.netkernel.xml.xda.IXDAReadOnlyIterator;

import com.mindalliance.channels.nk.ContextHelper;
import com.mindalliance.channels.nk.XDAHelper;
import com.ten60.netkernel.urii.aspect.IAspectBoolean;

public class Model {
    
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
    
    public static final String MODEL_GOLDEN_THREAD = "gt:channels/model";
    public static final String ELEMENT_GOLDEN_THREAD = "gt:channels/element/";
    private IXMLContainer container = null;
    private ContextHelper contextHelper;
    private XDAHelper xdaHelper;
    
    public Model(String containerConfig, INKFConvenienceHelper context) {
        container = new DBXMLContainer(containerConfig, context);
        contextHelper = new ContextHelper(context);
        xdaHelper = new XDAHelper(context);
    }
    
    public boolean modelExists() throws Exception {
        return container.containerExists();
    }
    
    public String getModelName() throws Exception {
        return container.getContainerName();
    }
    
    public void deleteModel() throws Exception {
        container.deleteContainer();
        contextHelper.cutGoldenThread( MODEL_GOLDEN_THREAD );
    }
    
    public void createModel() throws Exception {
        container.createContainer();
    }
    
    public IAspectXDA getElement(String id) throws Exception {
        IAspectXDA doc = container.getDocument(id);
        // Attach golden thread to element
        doc = (IAspectXDA) contextHelper.attachGoldenThread(doc, ELEMENT_GOLDEN_THREAD + id);
        contextHelper.log("Got document " + doc.getXDA(), "info");
        // return goldenthreaded element
        return doc;
    }
    
    public void updateElement(IAspectXDA doc) throws Exception {
        String id = xdaHelper.textAtXPath( doc, "id" ); // document *must* have id
        contextHelper.log("Updating element with id " + id, "info");
        // Delete older version (must exist else exception)
        container.deleteDocument(id);
        // Then replace with new version
        container.putDocument(doc);
        contextHelper.log("Element " + id + " put in container " + getModelName(), "info");    
        // Cut the GoldenThread associated with this resource
        contextHelper.cutGoldenThread(ELEMENT_GOLDEN_THREAD + id);
        // Cut the GoldenThread associated with all existing queries
        contextHelper.cutGoldenThread(MODEL_GOLDEN_THREAD);
    }
    
    public IAspectXDA queryModel(String query) throws Exception {
        contextHelper.log("Processing query: " + query, "info");
        IAspectXDA res = container.queryContainer(query);
        res = (IAspectXDA)contextHelper.attachGoldenThread(res, MODEL_GOLDEN_THREAD);
        contextHelper.log("Got results to query:\n" + res.getXDA(), "info");
        return res;
    }

//  Adds id element if not there, sets schema attribute, validates and stores. Returns doc as stored.
    public IAspectXDA createElement(IAspectXDA xml) throws Exception {
        IXDA doc = xml.getClonedXDA();
        String kind =  xdaHelper.getName(doc); // xml element's local name
        contextHelper.log("Creating " + kind + ":\n " + doc, "info");
        // Generate and add GUID as root attribute id if needed 
        if (!xdaHelper.existsXPath(doc, "id")) {
            String guid = contextHelper.getGUID();
            doc.appendPath( ".", "id", guid );
        }
        else {
            contextHelper.log("ID preset", "warning");
        }
        // (Re)set schema attribute
        String schemaPrefix = contextHelper.getProperty( "schema_url", "ffcpl:/etc/data.properties" );
        doc.appendPath(".", "@schema", schemaPrefix + kind + ".rng");
        IAspectXDA res = xdaHelper.makeXDAAspect(doc);
        // Validate
        try {
            xdaHelper.validateRNG(res); // throws an exception if not valid
        }
        catch (Exception e) {
            contextHelper.log("Document of kind " + kind + " is invalid:\n" + doc, "severe");
            throw (e);
        }
        // Store new document (exception if conflict on id) 
        container.putDocument(xdaHelper.makeXDAAspect( doc ));   
        // Cut the GoldenThread associated with all existing queries
        contextHelper.cutGoldenThread(MODEL_GOLDEN_THREAD);
        contextHelper.log("Created document " + doc + " in database " + getModelName(), "info");
        return res;
    }
    
//  Return an array containing an array of deleted element IDs and an array of updated element IDs
    public DeletionRecord deleteElement(String id) throws Exception {
        contextHelper.log("Deleting element " + id, "info");
        DeletionRecord deletionRecord = new DeletionRecord();
        IXDAReadOnly refTable = xdaHelper.sourceXDA("ffcpl:/resources/schemas/referenceTable.xml");
        deleteElementExcept(id, deletionRecord, refTable);
        // Cut the GoldenThread associated with this resource
        contextHelper.cutGoldenThread(ELEMENT_GOLDEN_THREAD + id);
        // Cut the GoldenThread associated with all existing queries
        contextHelper.cutGoldenThread(MODEL_GOLDEN_THREAD);
        return deletionRecord;
    }

    private void deleteElementExcept( String id, DeletionRecord deletionRecord, IXDAReadOnly refTable ) throws Exception {
        contextHelper.log("Deleting " + id + " except if in " + deletionRecord.getDeleted(), "info");
        // delete if not already deleted
        if (!deletionRecord.alreadyDeleted( id )) {
            IAspectXDA element = container.deleteDocument(id);
            contextHelper.log("Deleted document " + id + " from container " + getModelName(), "info");
            deletionRecord.addDeleted(id);
            // Delete referrers to deleted document that are not already deleted
            deleteReferencesTo(element, deletionRecord, refTable);
        }
    }

    private void deleteReferencesTo( IAspectXDA element,
            DeletionRecord deletionRecord, IXDAReadOnly refTable )
            throws Exception {
        IXDAReadOnly elXDA = element.getXDA();
        String elName = xdaHelper.getName( elXDA );
        String elId = xdaHelper.textAtXPath( elXDA, "id" );
        contextHelper.log( "Deleting references to\n" + elName + ":" + elId
                + " except for " + deletionRecord.getDeleted(), "info" );
        IXDAReadOnlyIterator from = refTable.readOnlyIterator( "./*[@to = \"" + elName + "\"]/from" );
        while ( from.next() ) {
            String[] refNames = xdaHelper.textAtXPath( from, "@element" ).split("\\|" );
            String refPath = xdaHelper.textAtXPath( from, "." );
            String cascade;
            if ( from.isTrue( "@cascade" ) ) {
                cascade = xdaHelper.textAtXPath( from, "@cascade" );
            }
            else {
                cascade = refPath;
            }
            for ( String kind : refNames ) {
                List<String> ids = findReferrers( elId, kind, refPath );
                for ( String id : ids ) {
                    if ( cascade.equals( "." ) ) {
                        // Delete element and cascade
                        deleteElementExcept( id, deletionRecord, refTable ); 
                    }
                    else {
                        IAspectXDA referrer = getElement( id );
                        // log("Deleting in " + referrer + " reference
                        // " + cascade, "info");
                        deleteReference( referrer.getClonedXDA(), refPath,
                                cascade, elId, deletionRecord ); // remove reference to element from referrer
                    }
                }
            }
        }
    }

    private void deleteReference( IXDA referrer, String refPath, String cascade, String id, DeletionRecord deletionRecord ) throws Exception {
        contextHelper.log("Delete reference to " + id + " by " + referrer + " via " + refPath + " deleting " + cascade, "info");
        if (refPath.indexOf(cascade) != 0){     // Invalid cascade path
            contextHelper.log("Error in reference table: " + cascade + " in " + refPath, "severe");
            throw new Exception("Invalid reference table");
        }
        String testPath = refPath + "[. = \"" + id + "\"]";
        while (referrer.isTrue(testPath)) {
            if (refPath.equals(cascade)) { // just delete the reference
                referrer.delete( testPath );
                contextHelper.log("Deleted immediate reference " + id + " in " + referrer + " at " + testPath, "info");
            }
            else { // delete ancestor of element
                // get the ancestor to delete
                String parentPath = makeParentXPath(refPath, cascade, id); 
                referrer.delete(parentPath);
                contextHelper.log("Deleted reference holder " + parentPath + " in " + referrer , "info");
            }
            // update element
            updateElement(xdaHelper.makeXDAAspect( referrer ));
            deletionRecord.addUpdated(xdaHelper.textAtXPath( referrer, "id" ));
        }
    }

    // cascade[refPathAfterCascade = "id"]
    private String makeParentXPath( String refPath, String cascade, String id ) {
        String refPathAfterCascade = refPath.substring(cascade.length() + 1);
        String xpath = cascade + "[" + refPathAfterCascade + " = \"" + id + "\"]";
        return xpath;
    }

    private List<String> findReferrers( String id, String kind, String path ) throws Exception {
        contextHelper.log("Finding " + kind + " referrers of " + id + " by " + path, "info");
        String query = "<list>\n" +
                                    "{\n" +
                                        "for $e in collection('__MODEL__')/" + kind + "\n" +
                                        "where $e/" + path.substring(2,path.length()) + " = '" + id + "'\n" +
                                        "return\n" +
                                        "  <id>{$e/id/text()}</id>\n" +
                                    "}\n" +
                    "</list>";
        IAspectXDA result = queryModel(filter(query));
        IXDAReadOnly list = result.getXDA();
        contextHelper.log("Referrers are: \n" + list, "info");
        List<String> ids = new ArrayList<String>();
        IXDAReadOnlyIterator refId = result.getXDA().readOnlyIterator( "./id" );
        while(refId.next()) {
            ids.add(xdaHelper.textAtXPath( refId, "." ));
        }
        return ids;
    }
    
    public String filter(String s) throws Exception {
        String fs = s.replaceAll("__MODEL__", getModelName()); 
        // more filters here
        return fs;
      }

    public IAspectBoolean elementExists( String kind, String id ) throws Exception {
        IAspectBoolean exists = container.documentExists(kind, id);
        // Attach golden thread to element
        exists = (IAspectBoolean)contextHelper.attachGoldenThread(exists, ELEMENT_GOLDEN_THREAD + id);
        contextHelper.log("Document " + id + " exists: " + exists.isTrue(), "info");
        // return goldenthreaded element
        return exists;
    }

    public IAspectXDA getInformation( String[] ids ) throws Exception {
        IXDA template = xdaHelper.makeXDA( "<information/>");
        try {
            // 1- collect all distinct category IDs, explicit and implied
            Set<String> idSet = getCategoryIdsOf(ids);
            // 2- Collect the topics (names only) and their EOIs (names and descriptions) for each information in each category
            // 3- Aggregate the EOIs across categories into each named topic 
            //          - no duplicate topics by name, no duplicate EOIs by name per topic
            //          - When collapsing EOIs, accumulate privacy and minimize confidence (TODO)
            // 4- Construct and return an aggregated information element as xml.
            // log("Building information template from " + idSet, "info");
            for (String id : idSet) {
                IXDAReadOnly category = getElement(id).getXDA();
                IXDAReadOnlyIterator topic = category.readOnlyIterator( "information/topic" );
                // for all topics
                while (topic.next()) {
                    String topicName = xdaHelper.textAtXPath( topic, "name" );
                    String topicDescription;
                    IXDA templateTopic;
                    if (xdaHelper.existsXPath( topic, "description" ))
                        topicDescription = xdaHelper.textAtXPath( topic, "description" );
                    else
                        topicDescription = "";
                    String namedTopicXPath = "topic[name = \"" + topicName + "\"]";
                    if (!xdaHelper.existsXPath( template, namedTopicXPath )) {
                        // var list = template.topic.(name == topicName); // is topic already in template?
                        templateTopic =  xdaHelper.makeXDA("<topic>\n" + 
                                                            " <name>" + topicName + "</name>\n" +
                                                            " <description>" + topicDescription + "</description>\n" +
                                                            "</topic>");
                        template.append( templateTopic, ".", "." );
                        // contextHelper.log("Added topic " + templateTopic + " to template = " + template, "info");
                    }
                    IXDAReadOnlyIterator eoi = category.readOnlyIterator("information/" + namedTopicXPath + "/eoi" );
                    while (eoi.next()) {
                        String eoiName = xdaHelper.textAtXPath( eoi, "name" );
                        String eoiDescription;
                        if (xdaHelper.existsXPath( eoi, "description" ))
                            eoiDescription = xdaHelper.textAtXPath( eoi, "description" );
                        else
                            eoiDescription = "";
                        String namedEOIXpath = namedTopicXPath + "/eoi[name = \"" + eoiName + "\"]";
                        if (!xdaHelper.existsXPath( template, namedEOIXpath )) {
                            // var list = template.topic.(name == topicName); // is topic already in template?
                            IXDA templateTopicEOI =  xdaHelper.makeXDA("<eoi>\n" + 
                                                                " <name>" + eoiName + "</name>\n" +
                                                                " <description>" + eoiDescription + "</description>\n" +
                                                                "</eoi>");
                            template.append( templateTopicEOI, ".", namedTopicXPath );
                            // contextHelper.log("Added eoi: " + templateTopicEOI + " to topic " + topicName + " in template = " + template, "info");
                        }
                        else {
                            contextHelper.log("EOI collision with " + eoiName + " in topic " + topicName + " from category " + id, "warning");
                        }
                    }
                }
            }
        }
    catch(Exception e) {
        contextHelper.log("Getting information template failed: " + e, "severe");
        throw e;
    }
    contextHelper.log("Information template for " + ids + " =\n" + template, "info");
    return xdaHelper.makeXDAAspect( template );
    }

    private Set<String> getCategoryIdsOf( String[] ids ) throws Exception {
        Set<String> idSet = new HashSet<String>();
        for (String id : ids) {
            IXDAReadOnly element = getElement(id).getXDA();
            if (xdaHelper.getName(element).equals("category")) {
                idSet.add(id);
                idSet.addAll(getImpliedCategoryIds(id));
            }
            else {
                idSet.addAll(getCategoryIdsOfCategorized(id));
            }
        }
        return idSet;
    }

    private Set<String> getCategoryIdsOfCategorized( String elementId ) throws Exception {
        // contextHelper.log("Getting all categories of " + elementId, "info");
        Set<String> idSet = new HashSet<String>();
        IXDAReadOnly doc = getElement(elementId).getXDA();
        IXDAReadOnlyIterator categoryId = doc.readOnlyIterator( "categories/categoryId" );
        while (categoryId.next()) {
            String id = xdaHelper.textAtXPath( categoryId, "." );
            idSet.add(id);
            idSet.addAll( getImpliedCategoryIds(id) );
        }
        return idSet;
    }
    
    private Set<String>getImpliedCategoryIds(String categoryId) throws Exception {
        // contextHelper.log("Getting implied categories by " + categoryId, "info");
        Set<String> idSet = new HashSet<String>();
        findImpliedCategoryIds(categoryId, idSet);
        contextHelper.log("Categories implied by " + categoryId + " = " + idSet, "info");
        return idSet;
    }

    private void findImpliedCategoryIds( String categoryId, Set<String> idSet ) throws Exception {
        IXDAReadOnly category = getElement(categoryId).getXDA();
        IXDAReadOnlyIterator impliedId = category.readOnlyIterator( "implies/categoryId" );
        while (impliedId.next()) {
            String id = xdaHelper.textAtXPath( impliedId, "." );
            if (idSet.add(id)) {
              findImpliedCategoryIds(id, idSet);
            }            
        }
    }

}
