package com.mindalliance.channels.attachments;

/**
 * An URL attachment...
 */
public class UrlAttachment implements Attachment {

    /**
     * The actual URL.
     */
    private String url;

    /**
     * The type.
     */
    private Type type;

    public UrlAttachment() {
    }

    public UrlAttachment( Type type, String url ) {
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
}
