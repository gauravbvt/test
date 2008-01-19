package com.mindalliance.channels.store

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.NetKernelCategory

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 15, 2008
 * Time: 10:51:16 AM
 * To change this template use File | Settings | File Templates.
 */
class XMLStore {

    public static final String DATABASE_GOLDEN_THREAD = "gt:channels/database"

    private IXMLContainer container
    private Context ctx

    XMLStore(String name, Context context) {
        ctx = context
        container = new DBXMLContainer(name, context)  // change this to use XML store other than DBXML
    }

    boolean containerExists() {
        return container.containerExists()
    }

    String getContainerName() {
        return container.getContainerName()
    }

    void deleteContainer() {
        container.deleteContainer()
        use (NetKernelCategory) {
            ctx.cutGoldenThread( DATABASE_GOLDEN_THREAD )
        }
    }

    void createContainer() {
        container.createContainer();
    }

    void dumpContainer(Writer writer) {
        container.dump(writer)
    }

    void initializeContainer(String uri) {
        container.initializeContainer(uri)
    }

    public String getDocument(String id) {
        container.getDocument(id)
    }

   public void updateDocument(String doc, String id) {
        // Delete older version (must exist else exception)
        container.deleteDocument(id);
        // Then replace with new version
        container.putDocument(doc, id)
        // Cut the GoldenThread associated with the contents of the entire store
        ctx.cutGoldenThread(DATABASE_GOLDEN_THREAD);
    }

    public void createDocument(String doc, String id) {
        use(NetKernelCategory) {
            container.putDocument(doc, id)
            // Cut the GoldenThread associated with the contents of the entire store
            ctx.cutGoldenThread(DATABASE_GOLDEN_THREAD);
        }
    }

    public void deleteDocument(String id) {
        use(NetKernelCategory) {
            container.deleteDocument(id)
            // Cut the GoldenThread associated with the contents of the entire store
            ctx.cutGoldenThread(DATABASE_GOLDEN_THREAD);
        }
    }

    public boolean documentExists(String id) {
        return container.documentExists(id)
    }
}