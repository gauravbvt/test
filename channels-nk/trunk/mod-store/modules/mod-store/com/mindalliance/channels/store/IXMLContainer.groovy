package com.mindalliance.channels.store

import com.ten60.netkernel.urii.IURAspect as Aspect

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
    Aspect queryContainer(String xquery, Class aspectClass)
    String getContainerName()
    void initializeContainer(String uri)
    Aspect getDocument(String id, Class aspectClass)
    void putDocument(Aspect doc, String id)
    void deleteDocument(String id)
    boolean documentExists(String id)

}