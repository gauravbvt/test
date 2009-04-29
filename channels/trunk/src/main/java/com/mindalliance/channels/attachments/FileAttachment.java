package com.mindalliance.channels.attachments;

import com.mindalliance.channels.ModelObject;

import java.io.File;

/**
 * An actual file.
 */
public class FileAttachment implements Attachment {

    /** The file on the server side. */
    private File file;

    /** The type of this attachment */
    private Type type;

    /** The external link to this file. Set by the manager. */
    private String url;

    /** The model object of the attachment. */
    private ModelObject object;

    public FileAttachment() {
    }

    public FileAttachment( ModelObject object, Type type, File file, String url ) {
        this();
        this.object = object;
        setType( type );
        setFile( file );
        setUrl( url );
    }

    public final File getFile() {
        return file;
    }

    public final void setFile( File file ) {
        this.file = file;
    }

    public String getKey() {
        return file.getName();
    }

    public String getLabel() {
        return FileBasedManager.unescape( file.getName() );
    }

    public final Type getType() {
        return type;
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
        file.delete();
    }

    public final void setType( Type type ) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public final void setUrl( String url ) {
        this.url = url;
    }

    public final ModelObject getObject() {
        return object;
    }
}
