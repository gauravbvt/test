package com.mindalliance.channels.attachments;

import java.io.File;

/**
 * An actual file.
 */
public class FileAttachment implements Attachment {

    /**
     * The file on the server side.
     */
    private File file;

    /**
     * The type of this attachment
     */
    private Type type;

    /**
     * The external link to this file. Set by the manager.
     */
    private String url;
    /**
     * The SHA digest value for the file.
     */
    private String digest;

    public FileAttachment() {
    }

    public FileAttachment( Type type, File file, String url, String digest ) {
        this();
        setType( type );
        setFile( file );
        setUrl( url );
        setDigest( digest );
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

    /**
     * {@inheritDoc}
     */
    public boolean isUrl() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFile() {
        return true;
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

    public void setDigest( String digest ) {
        this.digest = digest;
    }

    public String getDigest() {
        return digest;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj ) {
        if ( obj instanceof FileAttachment ) {
            FileAttachment other = (FileAttachment) obj;
            return type == other.getType()
                    && url.equals( other.getUrl() )
                    && digest.equals( other.getDigest() );
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
        hash = hash * 31 + digest.hashCode();
        return hash;
    }
}
