package com.mindalliance.channels.store

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.NetKernelCategory
import org.ten60.netkernel.xml.representation.IAspectXDA
import com.ten60.netkernel.urii.aspect.IAspectString


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

    String getContainerName() {
         return name
     }

    boolean containerExists() {
        boolean exists
        use(NetKernelCategory) {
            exists = ctx.isTrue("active:dbxmlExistsContainer", [operator: string(getContainerDescriptor())])
        }
        return exists
    }

    void createContainer() {
        use(NetKernelCategory) {
            ctx.subrequest("active:dbxmlCreateContainer", [operator: string(getContainerDescriptor())])
        }
    }

    void deleteContainer() {
        use(NetKernelCategory) {
            ctx.subrequest("active:dbxmlDeleteContainer", [operator: string(getContainerDescriptor())])
        }
    }

    String queryContainer(String xquery) {
        String xml
        String op = "<dbxml><container>${getContainerName()}</container><xquery><![CDATA[$xquery]]></xquery></dbxml>"
          use(NetKernelCategory) {
            xml = ctx.sourceString("active:dbxmlQuery", [operator: string(op)])
            /*IAspectXDA res = (IAspectXDA)ctx.transrept("active:dbxmlQuery", IAspectXDA.class, [operator: string(op)])
            xml = ((IAspectString)ctx.transrept(res, IAspectString.class)).getString()*/
        }
        return xml;
    }

    void dump(Writer writer) {
        String xquery = "<dump name=\'${getContainerName()}\'>{ collection(\'${getContainerName()}\')/* }</dump>"
        String xml = queryContainer(xquery)
        writer.write(xml)
    }


    String getDocument(String id) {
        String xml
        use(NetKernelCategory) {
            xml = ctx.sourceString("active:dbxmlGetDocument",
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
           String text = ctx.sourceString(uri)
           def xml = new XmlParser().parseText(text)
           xml.children().each { doc ->
                String id = doc.@id
                doc.@db = getContainerName()
                StringWriter writer = new StringWriter()
                XmlNodePrinter xmlPrinter = new XmlNodePrinter(new PrintWriter(writer))
                xmlPrinter.print(doc)
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