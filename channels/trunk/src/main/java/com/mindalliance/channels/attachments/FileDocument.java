package com.mindalliance.channels.attachments;

import java.io.File;
import java.io.Serializable;

/**
 * A file with url and digest.
 */
public class FileDocument implements Serializable {

    /**
     * The file on the server side.
     */
    private File file;


    /**
     * The external link to this file. Set by the manager.
     */
    private String url;
    /**
     * The SHA digest value for the file.
     */
    private String digest;

    public FileDocument() {
    }

    public FileDocument( File file, String url, String digest ) {
        this();
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

    /**
     * {@inheritDoc}
     */
    public void delete() {
        file.delete();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFile() {
        return true;
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
        if ( obj instanceof FileDocument ) {
            FileDocument other = (FileDocument) obj;
            return url.equals( other.getUrl() )
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
        hash = hash * 31 + url.hashCode();
        hash = hash * 31 + digest.hashCode();
        return hash;
    }
}
