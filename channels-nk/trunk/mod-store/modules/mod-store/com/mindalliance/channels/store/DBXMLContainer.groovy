package com.mindalliance.channels.store

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.NetKernelCategory


/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 15, 2008
* Time: 10:47:28 AM
* To change this template use File | Settings | File Templates.
*/
class DBXMLContainer implements IXMLContainer {

    private Context ctx
    private String name

    DBXMLContainer(String name, Context context) {
        this.name = name
        ctx = context
    }

    String getName() {
        return name;
    }

    boolean containerExists() {
        boolean exists
        use(NetKernelCateogry) {
            exists = ctx.isTrue("active:dbxmlExistsContainer", [operator: string(getContainerDescriptor())])
        }
        return exists
    }

    void createContainer() {
        use(NetKernelCateogry) {
            ctx.subrequest("active:dbxmlCreateContainer", [operator: string(getContainerDescriptor())])
        }
    }

    void deleteContainer() {
        use(NetKernelCategory) {
            ctx.subrequest("active:dbxmlDeleteContainer", [operator: string(getContainerDescriptor())])
        }
    }

    String getContainerName() {
        return name
    }

    String getDocument(String id) {
        use(NetKernelCategory) {
            String xml = ctx.sourceString("active:dbxmlGetDocument",
                                            [operator: string(getDocumentDescriptor(id)),
                                             goldenThread: DOCUMENT_GOLDEN_THREAD + id])
        }
        return xml
    }

    void putDocument(String doc, String id) {
        use(NetKernelCategory) {
            ctx.subrequest("active:dbxmlPutDocument",
                                [operand: string(doc),
                                 operator: string(getDocumentDescriptor(id))])
        }

    }

    void deleteDocument(String id) {
        use(NetKernelCategory) {
            ctx.subrequest("active:dbxmlDeleteDocument",
                            [operator: string(getDocumentDescriptor(id))])
                    // Cut the GoldenThread associated with this resource
            ctx.cutGoldenThread(DOCUMENT_GOLDEN_THREAD + id);
        }

    }

    boolean documentExists(String id) {
        boolean exists = true
        try {
            getDocument(id)
        }
        catch (Exception e) {
            exists = false
        }
        return exists
    }

    void initializeContainer(String uri) {
        use(NetKernelCategory) {
           String xml = ctx.sourceString(uri)
           xml.childNodes().each { doc ->
                String id = doc.@id
                StringWriter writer = new StringWriter()
                XmlNodePrinter xmlPrinter = new XmlNodePrinter(new PrintWriter(writer))
                xmlPrinter.print(node)
                putDocument(writer.toString(), id)
           }
        }
    }

    private String getContainerDescriptor() {
        "<dbxml><name>$name</name></dbxml>"
    }

    private String getDocumentDescriptor( String id ) {
        "<dbxml><name>$id</name><container>${getContainerName()}</container></dbxml>"
    }


}