// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.crud;

import org.ten60.netkernel.xml.representation.IAspectXDA;

import com.ten60.netkernel.urii.aspect.IAspectBoolean;


public interface IXMLContainer {
    
    public boolean containerExists() throws Exception;
    public void createContainer() throws Exception;
    public void deleteContainer() throws Exception;
    public String getContainerName() throws Exception;
    public IAspectXDA getDocument(String id) throws Exception; 
    public void putDocument(IAspectXDA doc) throws Exception;
    public IAspectXDA deleteDocument(String id) throws Exception;
    public IAspectXDA queryContainer(String query) throws Exception;
    public IAspectBoolean documentExists( String kind, String id ) throws Exception;

}
