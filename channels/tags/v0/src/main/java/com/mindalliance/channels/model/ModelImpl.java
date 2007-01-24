// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import com.mindalliance.channels.Model;
import com.mindalliance.channels.remoting.AbstractJavaBean;
import com.mindalliance.channels.remoting.GUIDFactory;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * A collection of model objects accessible using OGNL paths.
 *
 * @see <a href="http://www.ognl.org/">OGNL website</a>
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class ModelImpl extends AbstractJavaBean implements Model {

    private GUIDFactory guidFactory ;

    //--------------------------------
    /**
     * Default constructor.
     */
    public ModelImpl() {
        super();
    }

    //--------------------------------
    /* (non-Javadoc)
     * @see Model#get(java.lang.Object)
     */
    public Object get( Object path ) throws ModelException {

        try {
            return Ognl.getValue( path, this );

        } catch ( OgnlException ex ) {
            throw new ModelException( ex );
        }
    }

    /* (non-Javadoc)
     * @see Model#set(java.lang.Object, java.lang.Object)
     */
    public void set( Object path, Object value ) throws ModelException {

        try {
            Ognl.setValue( path, this, value );

        } catch ( OgnlException ex ) {
            throw new ModelException( ex );
        }
    }

    /**
     * Return the factory used to create new objects.
     */
    public GUIDFactory getGuidFactory() {
        return guidFactory;
    }

    /**
     * Set the factory for obtaining fresh GUID for new objects.
     * @param guidFactory the factory to use.
     */
    public void setGuidFactory(  GUIDFactory guidFactory ) {
        this.guidFactory = guidFactory;
    }
}
