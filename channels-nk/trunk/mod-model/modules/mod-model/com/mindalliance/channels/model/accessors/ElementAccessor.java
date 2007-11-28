// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model.accessors;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;

import com.mindalliance.channels.nk.ContextSupport;

/*
 * CRUD for model elements.
 */
public class ElementAccessor extends NKFAccessorImpl  {
         
    public ElementAccessor() {
        super( SAFE_FOR_CONCURRENT_USE, ContextSupport.SOURCE | // getDocument
                                        ContextSupport.SINK |   // updateDocument
                                        ContextSupport.NEW |    // createDocument
                                        ContextSupport.DELETE | // deleteDocument
                                        ContextSupport.EXISTS); // documentExists
    }
    // active:channels_document...
    @Override
    public void processRequest( INKFConvenienceHelper context ) throws Exception {
        ContextSupport ctx = new ContextSupport(context);
        String dbName = ctx.sourceString("this:param:database");

        switch (ctx.requestType())
            {   case ContextSupport.SOURCE:
                    getElement(ctx);
                    break;
                case ContextSupport.SINK:
                    updateElement(ctx);
                    break;
                case ContextSupport.DELETE:
                    deleteElement(ctx);
                    break;
                case ContextSupport.NEW: 
                    createElement(ctx);
                    break;
                case ContextSupport.EXISTS: 
                    elementExists(ctx);
                    break;
                default: throw new Exception("Unsupported request type");
            }       
        }
    private void elementExists( ContextSupport ctx ) {
    }
    private void createElement( ContextSupport ctx ) {
    }
    private void deleteElement( ContextSupport ctx ) {
    }
    private void updateElement( ContextSupport ctx ) {
    }
    private void getElement( ContextSupport ctx ) {
    }
}
