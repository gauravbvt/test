package com.mindalliance.channels.attachments;

import java.io.File;

/**
 * An actual file.
 */
public class FileAttachment implements Attachment {

    /** The file on the server side. */
    private File file;

    public FileAttachment() {
    }

    public File getFile() {
        return file;
    }

    public void setFile( File file ) {
        this.file = file;
    }

    public String getIcon() {
        return "document.png";      // NON-NLS
    }

    public String getLabel() {
        return file.getName();
    }

    public String getLink() {
        return file.getName();
    }
}
