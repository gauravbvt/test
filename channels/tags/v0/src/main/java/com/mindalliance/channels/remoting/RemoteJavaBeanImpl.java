// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.remoting;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class RemoteJavaBeanImpl
        extends AbstractRemotableBean
        implements RemoteJavaBean, InvocationHandler, Serializable {

    private static final long serialVersionUID = 1347003690482290191L;
    private Importer importer ;
    private Exporter exporter ;
    private Class remoteInterface ;
    private CopiedJavaBean copy ;

    private RemoteListener remoteListener ;

    //----------------
    RemoteJavaBeanImpl(
            Importer importer, GUID guid, Class remoteInterface ) {

        super( guid );
        this.importer = importer ;
        this.remoteInterface = remoteInterface ;
    }

    //----------------
    public void setExporter( Exporter exporter ) {
        this.exporter = exporter ;
    }

    public final Exporter getExporter() {
        return this.exporter;
    }

    public final Importer getImporter() {
        return this.importer;
    }

    public final Class getRemoteInterface() {
        return this.remoteInterface;
    }

    //----------------
    private synchronized boolean isRemoteListening() {
        return this.remoteListener != null ;
    }

    private synchronized void startRemoteListening() {
        checkExportedState();

        if ( !isRemoteListening() ) {
            this.remoteListener = new RemoteListenerImpl();
            getExporter().addListener( getGUID(), this.remoteListener );
            getImporter().addListener(
                    getGUID(), getExporter().getClient() );
        }
    }

    private synchronized void stopRemoteListening() {
        checkExportedState();

        if ( isRemoteListening() && !hasListeners() ) {
            getImporter().removeListener(
                    getGUID(), getExporter().getClient() );
            getExporter().removeListener( getGUID(), this.remoteListener );
            this.remoteListener = null ;
        }
    }

    private void checkExportedState() {
        if ( getExporter() == null )
            throw new IllegalStateException( "Remote object not exported" );
    }

    //----------------
    @SuppressWarnings( "unchecked" )
    public Object invoke( Object proxy, Method method, Object[] args )
        throws Throwable {

        Class<?> declaringClass = method.getDeclaringClass();
        try {
            return RemoteJavaBean.class.isAssignableFrom( declaringClass ) ?
                        method.invoke( this, args )
                 : getRemoteInterface().isAssignableFrom( declaringClass ) ?
                        getImporter().invoke( method, args )
                 : method.invoke( this, args ) ;

        } catch ( IOException e ) {
            throw new RuntimeException( e );

        } catch ( InvocationTargetException e ) {
            throw e.getCause() ;
        }
    }

    //----------------
    /* (non-Javadoc)
     * @see com.mindalliance.channels.remoting.RemoteJavaBean#copy()
     */
    public synchronized CopiedJavaBean takeCopy() {
        // TODO local copy mechanism
//        if ( copy == null )
//            copy = ... ;
        return getCopy();
    }

    public final CopiedJavaBean getCopy() {
        return this.copy ;
    }

    public final boolean isCopied() {
        return getCopy() != null ;
    }

    //----------------
    @Override
    public void addPropertyChangeListener(
            PropertyChangeListener listener ) {

        super.addPropertyChangeListener( listener );
        startRemoteListening();
    }

    @Override
    public void addPropertyChangeListener(
            String propertyName, PropertyChangeListener listener ) {

        super.addPropertyChangeListener( propertyName, listener );
        startRemoteListening();
    }

    @Override
    public void addVetoableChangeListener(
            String propertyName, VetoableChangeListener listener ) {

        super.addVetoableChangeListener( propertyName, listener );
        startRemoteListening();
    }

    @Override
    public void addVetoableChangeListener(
            VetoableChangeListener listener ) {

        super.addVetoableChangeListener( listener );
        startRemoteListening();
    }

    @Override
    public void removePropertyChangeListener(
            PropertyChangeListener listener ) {

        super.removePropertyChangeListener( listener );
        stopRemoteListening();
    }

    @Override
    public void removePropertyChangeListener(
            String propertyName, PropertyChangeListener listener ) {

        super.removePropertyChangeListener( propertyName, listener );
        stopRemoteListening();
    }

    @Override
    public void removeVetoableChangeListener(
            String propertyName, VetoableChangeListener listener ) {

        super.removeVetoableChangeListener( propertyName, listener );
        stopRemoteListening();
    }

    @Override
    public void removeVetoableChangeListener(
            VetoableChangeListener listener ) {

        super.removeVetoableChangeListener( listener );
        stopRemoteListening();
    }

    //===================================================
    private class RemoteListenerImpl implements RemoteListener {

        /* (non-Javadoc)
         * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
         */
        public void propertyChange( PropertyChangeEvent evt ) {
        }

        /* (non-Javadoc)
         * @see VetoableChangeListener#vetoableChange(PropertyChangeEvent)
         */
        public void vetoableChange( PropertyChangeEvent evt )
            throws PropertyVetoException {
        }
    }
}
