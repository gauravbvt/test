package com.mindalliance.channels.store

import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 15, 2008
* Time: 10:40:23 AM
* To change this template use File | Settings | File Templates.
*/
interface IXMLContainer {

    public static final String DOCUMENT_GOLDEN_THREAD = "gt:channels/document/"

    String getName()
    boolean containerExists()
    void createContainer()
    void deleteContainer()
    void dump(Writer writer)
    String queryContainer(String xquery)
    String getContainerName()
    void initializeContainer(String uri)
    String getDocument(String id)
    void putDocument(String doc, String id)
    void deleteDocument(String id)
    boolean documentExists(String id)

}