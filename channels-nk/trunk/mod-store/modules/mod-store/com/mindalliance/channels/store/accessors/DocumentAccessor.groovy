package com.mindalliance.channels.store.accessors

import com.mindalliance.channels.nk.accessors.AbstractDataAccessor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.store.XMLStore
import com.mindalliance.channels.nk.NetKernelCategory
import com.ten60.netkernel.urii.aspect.IAspectString

// TODO persist, golden thread 

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 15, 2008
* Time: 10:52:54 AM
*/
class DocumentAccessor extends AbstractDataAccessor {
    // Does a document exist at given id in a named container?
    // responds boolean
    // exception if container does not exist
    // db: a container name
    // id : a GUID
    void exists(Context ctx) {
        use(NetKernelCategory) {
             XMLStore store = new XMLStore(ctx.sourceString("this:param:db"), ctx)
             ctx.respond(bool(store.documentExists(ctx.sourceString("this:param:id"))))
        }
    }
    // Add a new document at a given id to a named container
    // responds boolean
    // exception if container does not exist
    // exception if id already in use
    // db: a container name
    // id : a GUID
    // doc: xml
    void create(Context ctx) {
        use(NetKernelCategory) {
            XMLStore store = new XMLStore(ctx.sourceString("this:param:db"), ctx)
            store.createDocument(ctx.sourceAspect("this:param:doc"), ctx.sourceString("this:param:id"))
            ctx.respond(bool(true))
        }
    }
    // Get a document at a given id from a named container
    // responds xml
    // exception if container does not exist
    // exception if document does not exist
    // db: a container name
    // id : a GUID
    void source(Context ctx) {
        use(NetKernelCategory) {
            XMLStore store = new XMLStore(ctx.sourceString("this:param:db"), ctx)
            ctx.respond(store.getDocument(ctx.sourceString("this:param:id"), IAspectString.class))
        }
    }
    // Update an existing document at a given id in a named container
    // responds boolean
    // exception if container does not exist
    // exception if the document does not exist
    // db: a container name
    // id : a GUID
    // doc: xml
    void sink(Context ctx) {
        use(NetKernelCategory) {
            XMLStore store = new XMLStore(ctx.sourceString("this:param:db"), ctx)
            store.updateDocument(ctx.sourceAspect("this:param:doc"), ctx.sourceString("this:param:id"))
            ctx.respond(bool(true))
        }
    }
    // Remove an existing document at a given id from a named container
    // responds boolean
    // exception if container does not exist
    // exception if the document does not exist
    // db: a container name
    // id : a GUID
    void delete(Context ctx) {
        use(NetKernelCategory) {
            XMLStore store = new XMLStore(ctx.sourceString("this:param:db"), ctx)
            store.deleteDocument(ctx.sourceString("this:param:id"))
            ctx.respond(bool(true))
        }
    }

}