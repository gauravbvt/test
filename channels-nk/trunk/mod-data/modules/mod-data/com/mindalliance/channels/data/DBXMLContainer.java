// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.INKFRequest;
import org.ten60.netkernel.layer1.representation.StringAspect;
import org.ten60.netkernel.xml.representation.IAspectXDA;
import org.ten60.netkernel.xml.xda.IXDAReadOnly;

import com.mindalliance.channels.nk.ContextHelper;
import com.mindalliance.channels.nk.XDAHelper;
import com.ten60.netkernel.urii.IURRepresentation;
import com.ten60.netkernel.urii.aspect.IAspectBoolean;

public class DBXMLContainer implements IXMLContainer {
    
    static private final String DBXML_CONFIG_URI = "ffcpl:/etc/dbxml_config.xml";
    
    private String configURI;
    private ContextHelper contextHelper;
    private XDAHelper xdaHelper;
    
    public DBXMLContainer(String configURI, INKFConvenienceHelper context) {
        this.configURI = (configURI == null)?DBXML_CONFIG_URI:configURI;
        xdaHelper = new XDAHelper(context);
        contextHelper = new ContextHelper(context);
    }
    
    private IAspectXDA getContainerDescriptor() throws Exception {
        IXDAReadOnly configs = ((IAspectXDA)contextHelper.context.sourceAspect(configURI, IAspectXDA.class)).getXDA();
        String env = configs.getText( "@env", true );
        String xpath = "config[@name = \"" + env + "\"]/dbxml";
        String result = xdaHelper.evalXPath(configs, xpath);
        return xdaHelper.makeXDAAspect(result);
    }
    
    private IAspectXDA getDocumentDescriptor( String id ) throws Exception {
        String xml = "<dbxml>" + "<name>" + id + "</name>" + "<container>"
                + getContainerName() + "</container>" + "</dbxml>";
        IAspectXDA descriptor = xdaHelper.makeXDAAspect( xml );
        return descriptor;
    }

    public boolean containerExists() throws Exception {
      IAspectXDA descriptor = getContainerDescriptor();
      // Check if already exists
      INKFRequest req=contextHelper.context.createSubRequest("active:dbxmlExistsContainer");
      req.addArgument("operator", descriptor);
      IURRepresentation res = contextHelper.context.issueSubRequest(req);
      boolean exists = ((IAspectBoolean)contextHelper.context.transrept(res, IAspectBoolean.class)).isTrue();
      return exists;
    }

    public void createContainer() throws Exception {
      IAspectXDA descriptor = getContainerDescriptor();
      INKFRequest req=contextHelper.context.createSubRequest("active:dbxmlCreateContainer");
      req.addArgument("operator", descriptor );
      contextHelper.context.issueSubRequest(req);
    }

    public void deleteContainer() throws Exception {
        IAspectXDA descriptor = getContainerDescriptor();
        INKFRequest req=contextHelper.context.createSubRequest("active:dbxmlDeleteContainer");
        req.addArgument("operator", descriptor);
        contextHelper.context.issueSubRequest(req);
    }

    public String getContainerName() throws Exception {
        IAspectXDA descriptor = getContainerDescriptor();
        String name = descriptor.getXDA().getText( "name", true );
        return name;
    }

    public IAspectXDA getDocument( String id ) throws Exception {
        IAspectXDA op =  getDocumentDescriptor(id);
        INKFRequest req = contextHelper.context.createSubRequest("active:dbxmlGetDocument");
        req.addArgument("operator", op);
        IURRepresentation rep = contextHelper.context.issueSubRequest(req);
        IAspectXDA doc = (IAspectXDA)contextHelper.context.transrept( rep, IAspectXDA.class );
        return doc;
    }

    public void putDocument( IAspectXDA doc ) throws Exception {
        String id = xdaHelper.textAtXPath( doc, "id[1]" ); // document *must* have id
        IAspectXDA op = getDocumentDescriptor( id );
        INKFRequest req = contextHelper.context.createSubRequest( "active:dbxmlPutDocument" );
        req.addArgument( "operand", doc );
        req.addArgument( "operator", op );
        contextHelper.context.issueSubRequest( req );
    }

    public IAspectXDA deleteDocument( String id ) throws Exception {
        IAspectXDA deleted = getDocument( id );
        IAspectXDA op = getDocumentDescriptor( id );
        INKFRequest req = contextHelper.context.createSubRequest( "active:dbxmlDeleteDocument" );
        req.addArgument( "operator", op );
        contextHelper.context.issueSubRequest( req );
        return deleted;
    }

    public IAspectXDA queryContainer( String query ) throws Exception {
        String op = "<dbxml>\n" + " <container>" + getContainerName()
                + "</container>\n" + " <xquery>\n" + "  <![CDATA[\n   " + query
                + "\n  ]]>\n" + " </xquery>\n" + "</dbxml>";
        INKFRequest req = contextHelper.context.createSubRequest( "active:dbxmlQuery" );
        req.addArgument( "operator", new StringAspect( op ) );
        IURRepresentation rep = contextHelper.context.issueSubRequest( req );
        IAspectXDA res = (IAspectXDA) contextHelper.context.transrept( rep, IAspectXDA.class );
        return res;
    }

    public IAspectBoolean documentExists( String kind, String id ) throws Exception {
        String query = "<root>\n" +
                       "  { collection('" + getContainerName() + "')/" + kind + "[id = '" + id + "']}\n" +
                       "</root>";
        String op = "<dbxml>\n" + 
                        " <container>" + getContainerName() + "</container>\n" + 
                        " <xquery>\n" + 
                            "  <![CDATA[\n   " + query  + "\n  ]]>\n" +
                        " </xquery>\n" + 
                     "</dbxml>";
        INKFRequest req = contextHelper.context.createSubRequest( "active:dbxmlBooleanQuery" );
        req.addArgument( "operator", new StringAspect( op ) );
        IURRepresentation rep = contextHelper.context.issueSubRequest( req );
        IAspectBoolean res = (IAspectBoolean) contextHelper.context.transrept( rep, IAspectBoolean.class );
        return res;
    }

}
