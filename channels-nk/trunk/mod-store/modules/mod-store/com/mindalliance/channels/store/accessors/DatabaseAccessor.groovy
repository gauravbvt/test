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

    private static final MAX_DELETE_RETRIES = 60
    private static final int SLEEP_BETWEEN_RETRIES = 500; // msecs
    //
    // Does a container exist with the given name?
    // name : name of container
    void exists(Context ctx) {
        use(NetKernelCategory) {
            String dbName = ctx.sourceString("this:param:name")
            XMLStore store = new XMLStore(dbName, ctx)
            boolean exists = store.containerExists()
            ctx.log("$dbName exists is $exists", 'info')
            ctx.respond(bool(exists))
        }

    }
    // Create a container with given name if does not already exist
    // Responds whether a container needed to be created
    // name : name of container
    void create(Context ctx) {
        use(NetKernelCategory) {
            String dbName = ctx.sourceString("this:param:name")
            XMLStore store = new XMLStore(dbName, ctx)
            boolean exists = store.containerExists()
            if (!exists) {
                store.createContainer()
                ctx.log("Created XML container $dbName", 'info')
            }
            ctx.respond(bool(exists))
        }
    }
    // Responds with an XML dump for the entire content of the container
    // name : name of container
    void source(Context ctx) {
        use(NetKernelCategory) {
            String dbName = ctx.sourceString("this:param:name")
            XMLStore store = new XMLStore(dbName, ctx)
            StringWriter writer = new StringWriter()
            store.dumpContainer(writer)
            String dump = writer.toString()
            ctx.log("Dump:\n$dump", 'info')
            ctx.respond(string(dump))
        }
    }
    // Load xml from a dump
    // name : name of container
    // load:  xml containing a list of documents to load
    void sink(Context ctx) {
        use(NetKernelCategory) {
            String dbName = ctx.sourceString("this:param:name")
            XMLStore store = new XMLStore(dbName, ctx)
            String loadUri = ctx.load
            store.initializeContainer(loadUri)
            ctx.log("XML containe $dbName initialized from $loadUri", 'info')
        }
    }

    // name : name of container
    void delete(Context ctx) {
        use(NetKernelCategory) {
            String dbName = ctx.sourceString("this:param:name")
            XMLStore store = new XMLStore(dbName, ctx)
            int retries = MAX_DELETE_RETRIES
            while (retries > 0 && store.containerExists() )
                try {
                    store.deleteContainer()
                } catch (Exception e) {
                    ctx.log("Failed to delete container $dbName", 'warning')
                    retries--
                    sleep(SLEEP_BETWEEN_RETRIES)
                }
            if (retries == 0 && store.containerExists()) {
                throw new Exception("Failed to delete container ${store.getContainerName()}")
            }
            ctx.log("XML container $dbName deleted", 'info')
        }
    }

}