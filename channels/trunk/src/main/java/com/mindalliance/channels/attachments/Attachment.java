package com.mindalliance.channels.attachments;

/**
 * An external attachment.
 */
public interface Attachment {

    /**
     * The icon to use for this kind of attachment.
     * @return a url, local or not.
     */
    String getIcon();

    /**
     * The text of the link to this attachment.
     * @return a label
     */
    String getLabel();

    /**
     * The actual url of the attachment.
     * @return a url, local or not
     */
    String getLink();

}
