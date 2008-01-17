package com.mindalliance.channels.store.accessors

import com.mindalliance.channels.nk.accessors.AbstractDataAccessor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.store.XMLStore
import com.mindalliance.channels.nk.NetKernelCategory

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 15, 2008
* Time: 10:52:02 AM
* To change this template use File | Settings | File Templates.
*/

class DatabaseAccessor extends AbstractDataAccessor {
    // Does a container exist with the given name?
    // name : name of container
    void exists(Context ctx) {
        use(NetKernelCategory) {
            XMLStore store = new XMLStore(ctx.name, ctx)
            boolean exists = store.containerExists()
            ctx.respond(bool(exists))
        }

    }
    // Create a container with given name if does not already exist
    // Responds whether a container needed to be created
    // name : name of container
    void create(Context ctx) {
        use(NetKernelCategory) {
            XMLStore store = new XMLStore(ctx.name, ctx)
            boolean exists = store.containerExists()
            if (!exists) store.createContainer()
            ctx.respond(bool(exists))
        }
    }
    // Responds with an XML dump for the entire content of the container
    // name : name of container
    void source(Context ctx) {
        use(NetKernelCategory) {
            XMLStore store = new XMLStore(ctx.name, ctx)
            StringWriter writer = new StringWriter()
            store.dumpContainer(writer)
            ctx.respond(string(writer.toString()))
        }
    }
    // Load xml from a dump
    // name : name of container
    // load:  xml containing a list of documents to load
    void sink(Context ctx) {
        use(NetKernelCategory) {
            XMLStore store = new XMLStore(ctx.name, ctx)
            store.initializeContainer(ctx.load)
        }
    }

    // name : name of container
    void delete(Context ctx) {
        use(NetKernelCategory) {
            XMLStore store = new XMLStore(ctx.name, ctx)
            store.deleteContainer()
        }
    }

}