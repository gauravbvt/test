package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Attachment;

/**
 * A record of a document attachment.
 */
public class AttachmentImpl implements Attachment {

    /**
     * A document.
     */
    private String url;
    /**
     * A type.
     */
    private Type type;

    private String name = "";

    public AttachmentImpl( Attachment attachment ) {
        this( attachment.getUrl(), attachment.getType(), attachment.getName() );
    }

    public AttachmentImpl( String url, Type type ) {
        this( url, type, "" );
    }

    public AttachmentImpl( String url, Type type, String name ) {
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

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Type getType() {
        return type;
    }

    private void setType( Type type ) {
        this.type = type;
    }

    @Override
    public String getName() {
        return name == null ? "" : name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public String getLabel() {
        return getName().isEmpty() ? getUrl() : getName();
    }

    @Override
    public boolean isReference() {
        return type == Type.Reference;
    }

    @Override
    public boolean isPicture() {
        return type == Type.Image;
    }

    @Override
    public boolean isMOU() {
        return type == Type.MOU;
    }

    @Override
    public boolean isTags() {
        return type == Type.TAGS;
    }

    @Override
    public boolean isInfoStandards() {
        return type == Type.InfoStandards;
    }

    @Override
    public boolean isHelp() {
        return type == Type.Help;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof AttachmentImpl ) {
            Attachment other = (Attachment) obj;
            return type == other.getType()
                    && url.equals( other.getUrl() );
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + type.hashCode();
        hash = hash * 31 + url.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "AttachmentImpl{" + "url='" + url + '\'' + ", type=" + type + ", name='" + name + '\'' + '}';
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
    @Override
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
        AttachmentImpl merged = new AttachmentImpl( attachment );
        if ( other.getType().ordinal() > attachment.getType().ordinal() ) {
            merged.setType( other.getType() );
            merged.setName( other.getName() );
        }
        return merged;
    }

    @Override
    public boolean isProhibition() {
        return getType() == AttachmentImpl.Type.PolicyCant;
    }

    @Override
    public boolean isMandate() {
        return getType() == AttachmentImpl.Type.PolicyMust;
    }



}
