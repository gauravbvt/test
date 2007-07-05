// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.util;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A simple creator of globally unique IDs.
 *
 * GUIDs are created by appending the server ID, the current time and a
 * unique sequence number since the creation of this factory.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:46 $
 *
 * @opt attributes
 * @depend - &lt;generates&gt; - GuidImpl
 */
public class GUIDFactoryImpl implements GUIDFactory, Serializable {

    private AtomicLong sequence = new AtomicLong( 0 );
    private String serverId;

    /**
     * Create a new factory.
     */
    public GUIDFactoryImpl() {
    }

    /**
     * Create a new factory using a given server ID.
     * @param serverId the given server ID
     */
    public GUIDFactoryImpl( String serverId ) {
        setServerId( serverId );
    }

    /**
     * Return a new GUID.
     * Will throw IllegalStateExeption if called before setting a server ID.
     */
    public GUID newGuid() {

        if ( getServerId() == null )
            throw new IllegalStateException();

        return new GuidImpl(
                getServerId()
                + "/" + this.sequence.getAndIncrement()
                + "-" + System.currentTimeMillis() );
    }

    /**
     * Return the ID of this server, hopefully unique across communicating
     * servers...
     */
    public final String getServerId() {
        return this.serverId;
    }

    /**
     * Set the ID of this server.
     * @param serverId a unique ID across communicating servers.
     */
    public void setServerId( final String serverId ) {
        this.serverId = serverId;
    }

    /**
     * Return the value of sequence.
     */
    public long getSequence() {
        return this.sequence.get();
    }

    /**
     * Set the value of sequence.
     * @param sequence The new value of sequence
     */
    public void setSequence( long sequence ) {
        this.sequence.set( sequence );
    }

    //========================================
    /**
     * A GUID implementation.
     */
    public static final class GuidImpl implements GUID {

        private static final long serialVersionUID = 4736280854640093169L;
        private String stringForm;

        /**
         * Create a new GUID.
         * @param stringForm a string representation
         */
        protected GuidImpl( String stringForm ) {
            this.stringForm = stringForm;
        }

        /**
         * Return a string representation of this GUID.
         */
        @Override
        public String toString() {
            return this.stringForm;
        }

        /**
         * Compare this GUID to another GUID.
         * @param guid the other
         * @return true if GUIDs are equivalent.
         */
        public boolean equals( GuidImpl guid ) {
            return this.stringForm.equals(  guid.toString() );
        }

        /**
         * Compare this GUID to another object.
         * @param guid the other
         * @return true is the object is an equivalent GUID.
         */
        @Override
        public boolean equals( Object guid ) {
            return guid == this
                || guid instanceof GuidImpl
                        && this.equals(  (GuidImpl) guid );
        }

        /**
         * Return a hashcode value for this GUID.
         */
        @Override
        public int hashCode() {
            return this.stringForm.hashCode();
        }
    }
}
