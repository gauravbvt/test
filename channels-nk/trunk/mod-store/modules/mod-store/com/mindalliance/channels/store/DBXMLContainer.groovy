package com.mindalliance.channels.store

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.NetKernelCategory
import com.ten60.netkernel.urii.IURAspect as Aspect
import com.ten60.netkernel.urii.aspect.IAspectString
import org.ten60.netkernel.layer1.representation.StringAspect
import groovy.util.slurpersupport.GPathResult

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
        assert name
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

    Aspect queryContainer(String xquery, Class aspectClass) {
        assert xquery
        assert aspectClass
        Aspect aspect
        use(NetKernelCategory) {
            String op = "<dbxml><container>${getContainerName()}</container><xquery><![CDATA[$xquery]]></xquery></dbxml>"
            aspect = ctx.sourceAspect("active:dbxmlQuery", [operator: string(op)], aspectClass)
            /*IAspectXDA res = (IAspectXDA)ctx.transrept("active:dbxmlQuery", IAspectXDA.class, [operator: string(op)])
            xml = ((IAspectString)ctx.transrept(res, IAspectString.class)).getString()*/
        }
        return aspect;
    }

    void dump(Writer writer) {
        String xquery = "<dump name=\'${getContainerName()}\'>{ collection(\'${getContainerName()}\')/* }</dump>"
        String xml = ((IAspectString) queryContainer(xquery, IAspectString.class)).getString()
        writer.write(xml)
    }

    void emptyContainer() {
        String xquery = """
            <docs name=\'${getContainerName()}\'>
                {
                    for \$doc in collection(\'${getContainerName()}\')/*
                    return
		 		        <doc>{\$doc/@id}</doc>
                }
            </docs>
            """
        String xml = ((IAspectString) queryContainer(xquery, IAspectString.class)).getString()
        GPathResult gpr = new XmlSlurper().parseText(xml)
        gpr.children().each {
            String id = it.@id
            deleteDocument(id)
        }
    }
    
    Aspect getDocument(String id, Class aspectClass) {
        assert id
        assert aspectClass
        Aspect aspect
        use(NetKernelCategory) {
            aspect = ctx.sourceAspect("active:dbxmlGetDocument",
                    [operator: string(getDocumentDescriptor(id)),
                            goldenThread: DOCUMENT_GOLDEN_THREAD + id], aspectClass)
        }
        return aspect
    }

    void putDocument(Aspect doc, String id) {
        assert doc
        assert id
        use(NetKernelCategory) {
            ctx.subrequest("active:dbxmlPutDocument",
                    [operand: doc,
                            operator: string(getDocumentDescriptor(id))])
        }

    }

    void deleteDocument(String id) {
        assert id
        use(NetKernelCategory) {
            ctx.subrequest("active:dbxmlDeleteDocument",
                    [operator: string(getDocumentDescriptor(id))])
            // Cut the GoldenThread associated with this resource
            ctx.cutGoldenThread(DOCUMENT_GOLDEN_THREAD + id);
        }

    }

    boolean documentExists(String id) {
        assert id
        boolean exists = true
        try {
            getDocument(id, Aspect.class)
        }
        catch (Exception e) {
            exists = false
        }
        return exists
    }

    // ASSUMES: All documents doc to be stored have their IDs at doc@id
    void initializeContainer(String uri) {
        String text = ctx.sourceString(uri)
        def xml = new XmlParser().parseText(text)
        xml.children().each {doc ->
            String id = doc.@id
            doc.@db = getContainerName()
            StringWriter writer = new StringWriter()
            XmlNodePrinter xmlPrinter = new XmlNodePrinter(new PrintWriter(writer))
            xmlPrinter.print(doc)
            putDocument(new StringAspect(writer.toString()), id)
        }
    }

    private String getContainerDescriptor() {
        "<dbxml><name>$name</name></dbxml>"
    }

    private String getDocumentDescriptor(String id) {
        "<dbxml><name>$id</name><container>${getContainerName()}</container></dbxml>"
    }

}