package com.mindalliance.channels.attachments;

import com.mindalliance.channels.ModelObject;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * An attachment manager that keeps uploaded files in a directory.
 */
public class FileBasedManager implements AttachmentManager {

    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger( FileBasedManager.class );

    /** Default maximum file name length (128). */
    private static final int MAX_LENGTH = 128;

    /** The directory to keep files in. */
    private File directory;

    /** The webapp-relative path to file URLs. */
    private String path;

    /** The maximum file name length. Anything above that will get truncated. */
    private int maxLength = MAX_LENGTH;

    public FileBasedManager() {
    }

    private File getSavedFile( ModelObject object, Attachment.Type type, String name ) {
        final String truncatedName = name.substring( 0, Math.min( name.length(), getMaxLength() ) );
        final String idealName = FileAttachment.createFileName( object, type, truncatedName );

        File result = new File( getDirectory(), idealName );
        int i = 0;
        while ( result.exists() ) {
            final String actual = idealName + ++i;
            result = new File( getDirectory(), actual );
        }

        return result;
    }

    /** {@inheritDoc} */
    @SuppressWarnings( { "unchecked" } )
    public Iterator<Attachment> attachments( final ModelObject object ) {
        final String prefix = "f" + Long.toString( object.getId() ) + "_" ;
        final File[] files = getDirectory().listFiles(
                new FilenameFilter() {
                    public boolean accept( File dir, String name ) {
                        return name.startsWith( prefix );
                    }
                } );
        final Iterator<Attachment> result;
        if ( files == null ) {
            final List<Attachment> attachmentList = Collections.emptyList();
            result = attachmentList.iterator();
        } else {
            result = (Iterator<Attachment>) new TransformIterator(
                    Arrays.asList( files ).iterator(),
                    new Transformer() {
                    public Object transform( Object o ) {
                        final File file = (File) o;
                        final FileAttachment fa = new FileAttachment( object, file );
                        fa.setLink(
                                MessageFormat.format( "{0}/{1}",                          // NON-NLS
                                        getPath(), file.getName() ) );
                        return fa;
                    }
                } );
        }
        return result;
    }

    /** {@inheritDoc} */
    public void attach( ModelObject object, Attachment.Type type, FileUpload fileUpload ) {
        final File savedFile = getSavedFile( object, type, fileUpload.getClientFileName() );

        try {
            fileUpload.writeTo( savedFile );

        } catch ( IOException e ) {
            LOG.error( MessageFormat.format( "Error while uploading file: {0}", savedFile ), e );
        }
    }

    /** {@inheritDoc} */
    public void attach( ModelObject object, Attachment.Type type, URL url ) {
        // TODO
    }

    /** {@inheritDoc} */
    public void detach( ModelObject object, Attachment attachment ) {
        if ( attachment instanceof FileAttachment ) {
            final FileAttachment fa = (FileAttachment) attachment;
            fa.getFile().delete();
//        } else {
            // UrlAttachment ua = (UrlAttachment) attachment;
            // TODO
        }

    }

    /** {@inheritDoc} */
    public void detachAll( ModelObject object ) {
        final String prefix = MessageFormat.format( "f{0}_",                              // NON-NLS
                                                    Long.toString( object.getId() ) );
        final File[] files = getDirectory().listFiles(
                new FilenameFilter() {
                    public boolean accept( File dir, String name ) {
                        return name.startsWith( prefix );
                    }
                } );
        for ( File f : files )
            f.delete();
    }

    public File getDirectory() {
        return directory;
    }

    /**
     * Set the directory where files will be stored.
     * Files in the directory can be removed independently.
     * @param directory a directory
     */
    public void setDirectory( File directory ) {
        LOG.info( MessageFormat.format( "Upload directory: {0}", directory.getAbsolutePath() ) );
        this.directory = directory;
    }

    public String getPath() {
        return path;
    }

    public void setPath( String path ) {
        this.path = path;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength( int maxLength ) {
        this.maxLength = maxLength;
    }
}
