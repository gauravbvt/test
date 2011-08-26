package com.mindalliance.channels.core.attachments;

import java.io.File;
import java.io.Serializable;

/**
 * A file with url and digest.
 */
final class FileDocument implements Serializable {

    /**
     * The SHA digest value for the file.
     */
    private final String digest;

    /**
     * The file on the server side.
     */
    private final File file;

    /**
     * The external link to this file. Set by the manager.
     */
    private final String url;

    //-------------------------------
    FileDocument( File file, String url, String digest ) {
        if ( file == null || url == null || digest == null )
            throw new IllegalArgumentException();

        this.file = file;
        this.url = url;
        this.digest = digest;
    }

    //-------------------------------
    /**
     * Delete the underlying file.
     */
    public void delete() {
        file.delete();
    }

    /**
     * Test if this file has the same content as a prior file.
     * @param prior the prior file.
     * @return true if contents are the same (presumably)
     */
    boolean isDuplicate( FileDocument prior ) {
        return file.getName().indexOf( prior.getFile().getName() ) > 0
            && digest.equals( prior.getDigest() );
    }

    public String getDigest() {
        return digest;
    }

    public File getFile() {
        return file;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null || getClass() != obj.getClass() )
            return false;

        FileDocument that = (FileDocument) obj;
        return file.equals( that.getFile() ) && url.equals( that.getUrl() );
    }

    @Override
    public int hashCode() {
        int result = file.hashCode();
        result = 31 * result + url.hashCode();
        return result;
    }
}
