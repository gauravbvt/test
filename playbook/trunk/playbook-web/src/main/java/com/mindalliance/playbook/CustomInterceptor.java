package com.mindalliance.playbook;

import com.mindalliance.playbook.model.Timestamped;
import org.apache.wicket.proxy.ILazyInitProxy;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;

/**
 * Custom hibernate interceptor to allow saving of spring-proxied persistent entities.
 */
public class CustomInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = -7236657795094635851L;

    private static final Logger LOG = LoggerFactory.getLogger( CustomInterceptor.class );

    @Override
    public String getEntityName( Object object ) {
        Object target = object instanceof ILazyInitProxy ?
                        ( (ILazyInitProxy) object ).getObjectLocator().locateProxyTarget() : object;
        return target.getClass().getName();
    }

    @Override
    public boolean onSave( Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types ) {

        if ( Timestamped.class.isAssignableFrom( entity.getClass() ) ) {
            Timestamped timestamped = (Timestamped) entity;
            timestamped.setLastModified( new Date() );
        }
        return super.onSave( entity, id, state, propertyNames, types );
    }

    @Override
    public boolean onFlushDirty( Object entity, Serializable id, Object[] currentState, Object[] previousState,
                                 String[] propertyNames, Type[] types ) {

        if ( Timestamped.class.isAssignableFrom( entity.getClass() ) ) {
            Timestamped timestamped = (Timestamped) entity;
            timestamped.setLastModified( new Date() );
        }
        return super.onFlushDirty( entity, id, currentState, previousState, propertyNames, types );
    }
}
