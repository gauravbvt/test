package com.mindalliance.channels.model;


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
         * A photo or icon.
         */
        Image( "Picture" ),
        /**
         * A policy document that mandates whatever the document is attached to.
         */
        PolicyMust( "Mandating policy" ),

        /**
         * A policy document that prohibits whatever the document is attached to.
         */
        PolicyCant( "Prohibiting policy" ),
        /**
         * A memorandum of understanding document.
         */
        MOU( "Memorandum of Understanding" ),
        /**
         * List of end-line or comma-separated tags to pre-load into a plan.
         */
        TAGS( "Tags" ),
        /**
         * Info standards.
         */
        InfoStandards( "Info standards");


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

    private String name = "";

    public Attachment( Attachment attachment ) {
        this( attachment.getUrl(), attachment.getType(), attachment.getName() );
    }

    public Attachment( String url, Type type ) {
        this( url, type, "" );
    }

    public Attachment( String url, Type type, String name ) {
        this.url = url;
        this.type = type;
        this.name = name;
    }

    /**
     * Add prefix to url if relative.
     *
     * @param url    a string
     * @param prefix a string
     * @return a string
     */
    public static String addPrefixIfRelative( String url, String prefix ) {
        String result = url;
        if ( !url.toLowerCase().startsWith( "http" ) ) {
            result = prefix + url;
        }
        return result;
    }

    public String getUrl() {
        return url;
    }

    public Type getType() {
        return type;
    }

    private void setType( Type type ) {
        this.type = type;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Get a label.
     *
     * @return a string
     */
    public String getLabel() {
        return getName().isEmpty() ? getUrl() : getName();
    }

    /**
     * Represents a reference document.
     *
     * @return a boolean
     */
    public boolean isReference() {
        return type == Type.Reference;
    }

    /**
     * Represents an image.
     *
     * @return a boolean
     */
    public boolean isPicture() {
        return type == Type.Image;
    }

    /**
     * Is MOU.
     *
     * @return a boolean
     */
    public boolean isMOU() {
        return type == Type.MOU;
    }

    /**
     * Is tags.
     *
     * @return a boolean
     */
    public boolean isTags() {
        return type == Type.TAGS;
    }

    public boolean isInfoStandards() {
        return type == Type.InfoStandards;
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
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     * @throws ClassCastException if the specified object's type prevents it
     *                            from being compared to this object.
     */
    public int compareTo( Attachment o ) {
        return url.compareTo( o.getUrl() );
    }

    /**
     * Merge two attachments into one.
     *
     * @param attachment an attachment
     * @param other      an attachment
     * @return an attachment
     */
    public static Attachment merge( Attachment attachment, Attachment other ) {
        Attachment merged = new Attachment( attachment );
        if ( other.getType().ordinal() > attachment.getType().ordinal() ) {
            merged.setType( other.getType() );
            merged.setName( other.getName() );
        }
        return merged;
    }

    /**
     * Is a prohibiting policy.
     *
     * @return a boolean
     */
    public boolean isProhibition() {
        return getType() == Attachment.Type.PolicyCant;
    }

    /**
     * Is a mandating policy.
     *
     * @return a boolean
     */
    public boolean isMandate() {
        return getType() == Attachment.Type.PolicyMust;
    }

}
