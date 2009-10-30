package com.mindalliance.channels.attachments;


import java.io.Serializable;
import java.text.MessageFormat;

/**
 * A record of a document attachment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 29, 2009
 * Time: 2:42:35 PM
 */
public class Attachment implements Serializable, Comparable<Attachment> {

    /**
     * The specific kind of document.
     */
    public enum Type {

        /**
         * A miscellaneous document.
         */
        Reference( "Reference" ),
        /**
         * A related policy that does not specifically mandate nor prohibit.
         */
        Policy( "Policy" ),
        /**
         * An image.
         */
        Image( "Image" ),
        /**
         * A policy document that mandates whatever the document is attached to.
         */
        PolicyMust( "Mandating policy" ),

        /**
         * A policy document that prohibits whatever the document is attached to.
         */
        PolicyCant( "Prohibiting policy" );

        //--------------------------------
        /**
         * A description of the type that will hopefully make sense to the user.
         */
        private String label;

        Type( String label ) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public String getStyle() {
            return MessageFormat.format( "doc_{0}", toString() );                         // NON-NLS
        }

    }

    /**
     * A document.
     */
    private String url;
    /**
     * A type.
     */
    private Type type;

    public Attachment( String url, Type type ) {
        this.url = url;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public Type getType() {
        return type;
    }

    /**
     * Represents policy violation.
     *
     * @return a boolean
     */
    public boolean isPolicyViolation() {
        return type == Type.PolicyCant;
    }

    /**
     * Represents an image.
     *
     * @return a boolean
     */
    public boolean isImage() {
        return type == Type.Image;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj ) {
        if ( obj instanceof Attachment ) {
            Attachment other = (Attachment) obj;
            return type == other.getType()
                    && url.equals( other.getUrl() );
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + type.hashCode();
        hash = hash * 31 + url.hashCode();
        return hash;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param   o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this object.
     */
    public int compareTo( Attachment o ) {
        return url.compareTo( o.getUrl() );
    }

}
