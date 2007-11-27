// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.nk; 

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.INKFRequest;
import org.ten60.netkernel.layer1.nkf.NKFException;
import org.ten60.netkernel.xml.representation.DOMXDAAspect;
import org.ten60.netkernel.xml.representation.IAspectXDA;
import org.ten60.netkernel.xml.representation.IXAspect;
import org.ten60.netkernel.xml.xda.DOMXDA;
import org.ten60.netkernel.xml.xda.DOMXPathResult;
import org.ten60.netkernel.xml.xda.IXDA;
import org.ten60.netkernel.xml.xda.IXDAReadOnly;
import org.ten60.netkernel.xml.xda.IXPathResult;
import org.ten60.netkernel.xml.xda.XPathLocationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ten60.netkernel.urii.IURRepresentation;
import com.ten60.netkernel.urii.aspect.IAspectBoolean;
import com.ten60.netkernel.urii.aspect.IAspectString;
import com.ten60.netkernel.urii.aspect.StringAspect;
import com.ten60.netkernel.util.XMLUtils;


public class XDAHelper {
    
    private INKFConvenienceHelper context;
    
    public XDAHelper(INKFConvenienceHelper context) {
        this.context = context;
    }
    
    public IAspectXDA makeXDAAspect (String xml) throws NKFException {
        return (IAspectXDA)context.transrept( new StringAspect(xml), IAspectXDA.class );
    }
    
    public IXDA makeXDA (String xml) throws NKFException {
        return makeXDAAspect(xml).getClonedXDA();
    }
    
    public void validateRNG(IAspectXDA doc) throws Exception { 
        String schemaURL = doc.getXDA().getText( "@schema", true );  // doc.@schema;
        if (schemaURL == null) throw new Exception ("Missing schema attribute");
        INKFRequest req = context.createSubRequest("active:validateRNG");
        req.addArgument("operand", doc);
        req.addArgument("operator", schemaURL);
        IURRepresentation result = context.issueSubRequest(req);
        boolean valid = ((IAspectBoolean)context.transrept(result, IAspectBoolean.class)).isTrue();
        if (!valid) {
            String problem = ((IAspectString)context.transrept(result, IAspectString.class)).getString();
            throw new Exception("Document is invalid: " + problem);
        }
      }
    public IXDAReadOnly sourceXDA(String uri) throws NKFException {
        IXDAReadOnly xda = ((IAspectXDA)context.sourceAspect(uri, IAspectXDA.class)).getXDA();
        return xda;
    }

    public IAspectXDA makeXDAAspect( IXDAReadOnly element ) {
        return(IAspectXDA) new DOMXDAAspect((DOMXDA)element);
    }

    private String asString( IXPathResult pathResult ) throws NKFException {
        String s;
        DOMXPathResult domResult = (DOMXPathResult)pathResult;
        NodeList nodeList = domResult.getNodeList();
        switch (nodeList.getLength()) {
        case 0 :
            s = "";
            break;
        case 1 :
            s = asString(nodeList.item( 0 ));
            break;
        default: // many nodes
            StringBuffer buf = new StringBuffer();
            buf.append("<root>\n");
            for (int i=0; i < nodeList.getLength(); i++) {
                buf.append(asString(nodeList.item( i )));
                buf.append( '\n' );
            }
            buf.append("</root>");
            s = buf.toString();
        }
        return s;
    }
    
    public String asString(Node node) {
       String s = XMLUtils.getInstance().toXML(node, true, true);
       return s; 
    }

    public String getName( IXDAReadOnly xda ) {
       DOMXDA domXDA = (DOMXDA)xda;
       Node node = domXDA.getRoot();
       return node.getLocalName();
    }

    public boolean existsXPath( IXDAReadOnly doc, String xpath ) throws XPathLocationException {
        DOMXPathResult result = (DOMXPathResult)doc.eval( xpath );
        NodeList nodeList = result.getNodeList();
        return nodeList.getLength() > 0;
    }
    
    public boolean existsXPath( IAspectXDA doc, String xpath ) throws XPathLocationException {
        return existsXPath(doc.getXDA(), xpath);
    }

    public String evalXPath( IXDAReadOnly xda, String xpath ) throws Exception {
        return asString(xda.eval( xpath ));
    }

    public String evalXPath( IAspectXDA doc, String xpath ) throws Exception {
        return evalXPath(doc.getXDA(), xpath);
    }

    public String textAtXPath( IXDAReadOnly doc, String xpath ) throws XPathLocationException {
        return doc.getText( xpath, true );
    }

    public String textAtXPath( IAspectXDA doc, String xpath ) throws XPathLocationException {
        return textAtXPath(doc.getXDA(), xpath);
    }

    public String asXML( IXDA doc ) throws NKFException {
        return ((IAspectString)context.transrept( new DOMXDAAspect((DOMXDA)doc), IAspectString.class )).getString();
    }
    
    public String asXML( IAspectXDA doc ) throws NKFException {
        return ((IAspectString)context.transrept(doc, IAspectString.class )).getString();
    }
    public String getCookie(String cookieName) throws Exception {
        String operator = "<cookie><get>" + cookieName + "</get></cookie>";
        IAspectString ias = new StringAspect(operator);
        INKFRequest req = context.createSubRequest("active:HTTPCookie");
        req.addArgument( "operand", "this:param:cookie" );
        req.addArgument( "operator", ias );
        IURRepresentation rep = context.issueSubRequest( req );
        IAspectXDA xda = (IAspectXDA)context.transrept( rep, IXAspect.class );
        String xml = asXML( xda );
        return xml;
    }

    
}
