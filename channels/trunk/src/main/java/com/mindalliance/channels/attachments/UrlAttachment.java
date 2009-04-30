package com.mindalliance.channels.attachments;

import com.mindalliance.channels.model.ModelObject;

/**
 * An URL attachment...
 */
public class UrlAttachment implements Attachment {

    /** The actual URL. */
    private String url;

    /** The type. */
    private Type type;

    /** The object. */
    private ModelObject object;

    public UrlAttachment() {
    }

    public UrlAttachment( ModelObject object, Type type, String url ) {
        this.type = type;
        this.url = url;
        this.object = object;
    }

    /**
     * The text of the link to this attachment.
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

    public ModelObject getObject() {
        return object;
    }
}
