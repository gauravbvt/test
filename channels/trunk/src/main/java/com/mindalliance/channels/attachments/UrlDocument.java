package com.mindalliance.channels.attachments;

/**
 * An URL attachment...
 */
public class UrlDocument implements Document {

    /**
     * The actual URL.
     */
    private String url;

    /**
     * The type.
     */
    private Type type;

    public UrlDocument() {
    }

    public UrlDocument( Type type, String url ) {
        this.type = type;
        this.url = url;
    }

    /**
     * The text of the link to this attachment.
     *
     * @return a label
     */
    public String getLabel() {
        return url;
    }

    /**
     * {@inheritDoc}
     */
    public String getKey() {
        return FileBasedManager.escape( url );
    }

    /**
     * {@inheritDoc}
     */
    public String getUrl() {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }

    public Type getType() {
        return type;
    }

    public String getDigest() {
        return "";
    }

    public void setType( Type type ) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPolicyViolation() {
        return getType() == Type.PolicyCant;
    }

    /**
     * {@inheritDoc}
     */
    public void delete() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUrl() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFile() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj ) {
        if ( obj instanceof UrlDocument ) {
            UrlDocument other = (UrlDocument) obj;
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
}
