// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.impl;

import java.beans.PropertyDescriptor;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;

/**
 * Provide property and vetoable change support for subclasses
 * of AbstractJavaBean.
 * <p>To use, just define your regular getters/setters to the subclass.
 * Setters throwing PropertyVetoException will be advised accordingly.</p> 
 * 
 * @see AbstractJavaBean
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */

public aspect JavaBeans {

    pointcut setters( AbstractJavaBean b, Object v ): 
        execution( public void set*(*) )
            && target( b )
            && args( v );

    pointcut vetoableSetters( AbstractJavaBean b, Object v ): 
        execution( public void set*(*) throws PropertyVetoException )
            && target( b )
            && args( v );
            
    void around( AbstractJavaBean b, Object v ) 
    throws PropertyVetoException: vetoableSetters( b, v ) {

        try {
            String setterName = thisJoinPointStaticPart.getSignature().getName();
            PropertyDescriptor pd = b.getPropertyDescriptor( setterName );
            Object oldValue = pd.getReadMethod().invoke( b );
        
            b.fireVetoableChange( pd.getName(), oldValue, v );
        
            proceed( b, v );
            
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        }
    }

    void around( AbstractJavaBean b, Object v ): setters( b, v ) {
        try {
            String setterName = thisJoinPointStaticPart.getSignature().getName();
            PropertyDescriptor pd = b.getPropertyDescriptor( setterName );
            Object oldValue = pd.getReadMethod().invoke( b );
    
            proceed( b, v );
            b.firePropertyChange( pd.getName(), oldValue, v );
            
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        }
    }

}
