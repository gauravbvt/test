// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.crud;

import org.ten60.netkernel.layer1.representation.StringAspect;
import org.ten60.netkernel.xml.representation.IAspectXDA;

import com.mindalliance.channels.nk.ContextSupport;
import com.ten60.netkernel.urii.aspect.IAspectBoolean;

public class DBXMLContainer implements IXMLContainer {
    
    private String name;
    private ContextSupport ctx;
    
    public DBXMLContainer(String name, ContextSupport contextSupport) {
        this.name = name;
        ctx = contextSupport;
    }
    
    private IAspectXDA getContainerDescriptor() throws Exception {
        String result = "<dbxml><name>" + name + "</name></dbxml>";
        return ctx.asXDAAspect(result);
    }
    
    private IAspectXDA getDocumentDescriptor( String id ) throws Exception {
        String xml = "<dbxml>" + "<name>" + id + "</name>" + "<container>"
                + getContainerName() + "</container>" + "</dbxml>";
        IAspectXDA descriptor = ctx.asXDAAspect( xml );
        return descriptor;
    }

    public boolean containerExists() throws Exception {
      // Check if already exists
      boolean exists = ((IAspectBoolean)ctx.subRequest( "active:dbxmlExistsContainer" ).
                              withArg("operator", getContainerDescriptor()).
                              transreptTo(IAspectBoolean.class)).isTrue();
      return exists;
    }

    public void createContainer() throws Exception {
      ctx.subRequest( "active:dbxmlCreateContainer" ).
          withArg("operator", getContainerDescriptor() ).
          issue();
    }

    public void deleteContainer() throws Exception {
        ctx.subRequest("active:dbxmlDeleteContainer").
            withArg("operator", getContainerDescriptor()).
            issue();
    }

    public String getContainerName() throws Exception {
        IAspectXDA descriptor = getContainerDescriptor();
        String name = descriptor.getXDA().getText( "name", true );
        return name;
    }

    public IAspectXDA getDocument( String id ) throws Exception {
        IAspectXDA doc = (IAspectXDA)ctx.subRequest("active:dbxmlGetDocument").
                                            withArg("operator", getDocumentDescriptor(id)).
                                            transreptTo(IAspectXDA.class );
        return doc;
    }

    public void putDocument( IAspectXDA doc ) throws Exception {
        String id = ctx.xdaHelper.textAtXPath( doc, "id[1]" ); // document *must* have id
        ctx.subRequest( "active:dbxmlPutDocument" ).
            withArg( "operand", doc ).
            withArg( "operator", getDocumentDescriptor( id ) ).
            issue();
    }

    public IAspectXDA deleteDocument( String id ) throws Exception {
        IAspectXDA deleted = getDocument( id );
        IAspectXDA op = getDocumentDescriptor( id );
        ctx.subRequest( "active:dbxmlDeleteDocument" ).
            withArg( "operator", op ).
            issue();
        return deleted;
    }

    public IAspectXDA queryContainer( String query ) throws Exception {
        String op = "<dbxml>\n" + " <container>" + getContainerName()
                + "</container>\n" + " <xquery>\n" + "  <![CDATA[\n   " + query
                + "\n  ]]>\n" + " </xquery>\n" + "</dbxml>";
        IAspectXDA res = (IAspectXDA)ctx.subRequest( "active:dbxmlQuery" ).
                                            withArg( "operator", new StringAspect( op ) ).
                                            transreptTo(IAspectXDA.class);
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
        IAspectBoolean res = (IAspectBoolean)ctx.subRequest( "active:dbxmlBooleanQuery" ).
            withString( "operator", op ).
            transreptTo(IAspectBoolean.class );
        return res;
    }

}
