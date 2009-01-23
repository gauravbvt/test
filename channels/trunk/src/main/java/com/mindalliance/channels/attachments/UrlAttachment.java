package com.mindalliance.channels.attachments;

import java.net.URL;

/**
 * An URL attachment...
 */
public class UrlAttachment implements Attachment {

    /** The actual URL. */
    private URL url;

    /** The type. */
    private Type type;

    public UrlAttachment() {
    }

    public UrlAttachment( Type type, URL url ) {
        this.type = type;
        this.url = url;
    }

    /**
     * The text of the link to this attachment.
     * @return a label
     */
    public String getLabel() {
        return url.toString();
    }

    /**
     * The actual url of the attachment.
     * @return a url, local or not
     */
    public String getLink() {
        return url.toString();
    }

    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl( URL url ) {
        this.url = url;
    }
}
