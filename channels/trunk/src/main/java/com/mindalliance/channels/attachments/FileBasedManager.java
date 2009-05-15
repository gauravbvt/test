package com.mindalliance.channels.attachments;

import com.mindalliance.channels.AttachmentManager;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * An attachment manager that keeps uploaded files in a directory.
 * An index file is kept to maintain the association between model objects and files.
 */
public class FileBasedManager implements AttachmentManager, Lifecycle {

    /**
     * Separator used for value in map file.
     */
    private static final int COMMA = (int) ',';

    /**
     * Character used to escape funny characters.
     */
    private static final char ESCAPE = '%';

    /**
     * Characters to be escaped in file name.
     */
    private static final String CHARS = "%_/\\:?&=";                                      // NON-NLS

    /**
     * Default maximum file name length (128).
     */
    private static final int MAX_LENGTH = 128;

    /**
     * The logger.
     */
    private final Logger log = LoggerFactory.getLogger( FileBasedManager.class );

    /**
     * True when the manager is running.
     */
    private boolean isRunning;

    /**
     * Name of the file to store map into, relative to directory.
     */
    private String mapFileName = "index.properties";                                      // NON-NLS

    /**
     * The directory to keep files in.
     */
    private File directory;

    /**
     * The webapp-relative path to file URLs.
     */
    private String path = "";

    /**
     * The maximum file name length. Anything above that will get truncated.
     */
    private int maxLength = MAX_LENGTH;

    /**
     * "string,type", indexed by URI.
     */
    private Properties fileMap = new Properties();

    /**
     * List of attachments, indexed by tickets. Reverse index of fileMap.
     */
    private Map<String, Attachment> attachmentMap = Collections.synchronizedMap(
            new HashMap<String, Attachment>() );

    /**
     * List of deleted attachments, indexed by attchment tickets.
     */
    private Map<String, Attachment> deletedMap = Collections.synchronizedMap(
            new HashMap<String, Attachment>() );

    public FileBasedManager() {
    }

    private File createFile( String name ) {
        String truncatedName = name.substring( 0, Math.min( name.length(), getMaxLength() ) );
        String idealName = escape( truncatedName );
        File result = new File( directory, idealName );
        int i = 0;
        while ( result.exists() ) {
            String actual = ++i + "_" + idealName;
            result = new File( directory, actual );
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Attachment getAttachment( String ticket ) {
        return attachmentMap.get( ticket );
    }

    /**
     * {@inheritDoc}
     */
    public List<Attachment> getAttachments( List<String> tickets ) {
        List<Attachment> attachments = new ArrayList<Attachment>();
        for ( String ticket : tickets ) {
            Attachment attachment = getAttachment( ticket );
            if ( attachment != null )
                attachments.add( attachment );
        }
        return attachments;
    }

    private Attachment getDeletedAttachment( String ticket ) {
        return deletedMap.get( ticket );
    }

    private void index( String ticket, Attachment attachment ) {
        attachmentMap.put( ticket, attachment );
        fileMap.setProperty( attachment.getKey(),
                MessageFormat.format(
                        "{0},{1}",                    // NON-NLS
                        ticket,
                        attachment.getType().name() ) );
    }

    private void deindex( String ticket ) {
        Attachment attachment = attachmentMap.get( ticket );
        if ( attachment != null ) {
            attachmentMap.remove( ticket );
            deletedMap.put( ticket, attachment );
            fileMap.remove( attachment.getKey() );
        } else {
            log.warn( "Failed to deindex: ticket " + ticket + " not found" );
        }
    }

    /**
     * {@inheritDoc}
     */
    public String attach( Attachment.Type type, FileUpload fileUpload ) {
        String ticket = makeTicket();
        String fileName = fileUpload.getClientFileName();
        String escaped = escape( fileName );

        try {
            File file = createFile( escaped );
            FileAttachment fileAttachment = new FileAttachment( type, file, path + file.getName() );
            fileUpload.writeTo( file );
            synchronized ( this ) {
                index( ticket, fileAttachment );
                save();
            }

        } catch ( IOException e ) {
            log.error( MessageFormat.format( "Error while uploading file: {0}", fileName ), e );
            ticket = null;
        }
        return ticket;
    }

    private String makeTicket() {
        return UUID.randomUUID().toString();
    }

    /**
     * {@inheritDoc}
     */
    public String attach( Attachment.Type type, URL url ) {
        String ticket = makeTicket();
        synchronized ( this ) {
            index( ticket, new UrlAttachment( type, url.toString() ) );
            save();
        }
        return ticket;
    }

    /**
     * {@inheritDoc}
     */
    public void detach( String ticket ) {
        synchronized ( this ) {
            deindex( ticket );
            save();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Attachment reattach( String ticket ) {
        Attachment attachment = getDeletedAttachment( ticket );
        if ( attachment != null ) {
            deletedMap.remove( ticket );
            synchronized ( this ) {
                index( ticket, attachment );
                save();
            }
        } else {
            log.warn( "Failed to re-attach ticket " + ticket );
        }
        return attachment;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void reattachAll( List<String> tickets ) {
        for ( String ticket : tickets ) {
            Attachment attachment = getDeletedAttachment( ticket );
            if ( attachment != null ) {
                deletedMap.remove( ticket );
                index( ticket, attachment );
            } else {
                log.warn( "Failed to re-attach ticket " + ticket );
            }
        }
        save();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void detachAll( List<String> tickets ) {
        for ( String ticket : tickets ) {
            deindex( ticket );
        }
        save();
    }

    /**
     * {@inheritDoc}
     */
    public void emptyTrash() {
        for ( Attachment attachment : deletedMap.values() ) {
            attachment.delete();
        }
    }

    public synchronized File getDirectory() {
        return directory;
    }

    /**
     * Set the directory where files will be stored.
     * Files in the directory can be removed independently.
     *
     * @param directory a directory
     */
    public synchronized void setDirectory( File directory ) {
        log.info( "Upload directory: {}", directory.getAbsolutePath() );
        this.directory = directory;
    }


    public synchronized String getPath() {
        return path;
    }

    public synchronized void setPath( String path ) {
        this.path = path;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength( int maxLength ) {
        this.maxLength = maxLength;
    }


    public String getMapFileName() {
        return mapFileName;
    }

    public void setMapFileName( String mapFileName ) {
        this.mapFileName = mapFileName;
    }

    private void save() {
        File file = new File( directory, mapFileName );
        Writer out = null;
        try {
            out = new FileWriter( file );
            fileMap.store( out, " Files-objects association file. Edit with care..." );
        } catch ( IOException e ) {
            log.error( "Unable to save attachment map " + mapFileName + ".", e );
        } finally {
            if ( out != null )
                try {
                    out.close();
                } catch ( IOException e ) {
                    log.error( "Unable to close attachment map" + mapFileName + ".", e );
                }
        }
    }

    /**
     * Replace offending characters in a file name so it can be reconstituted by a call to unescape.
     *
     * @param name the file name
     * @return an escaped version of the file name
     */
    static String escape( String name ) {
        StringBuilder buf = new StringBuilder( name.length() << 1 );
        for ( int i = 0; i < name.length(); i++ ) {
            char c = name.charAt( i );
            if ( CHARS.indexOf( (int) c ) >= 0 ) {
                buf.append( ESCAPE );
                buf.append( Integer.toHexString( (int) c ) );
                buf.append( ESCAPE );
            } else {
                buf.append( c );
            }
        }
        return buf.toString();
    }

    /**
     * Undo the escape() functionality.
     *
     * @param name an escaped string
     * @return the string with escaped values converted back.
     */
    static String unescape( String name ) {
        StringBuilder buf = new StringBuilder( name.length() << 1 );
        int i = 0;
        while ( i < name.length() ) {
            char c = name.charAt( i );
            if ( c == ESCAPE ) {
                int pos = name.indexOf( (int) ESCAPE, i + 1 );
                buf.append( (char) Integer.parseInt( name.substring( i + 1, pos ), 16 ) );
                i = pos;
            } else {
                buf.append( c );
            }
            i++;
        }
        return buf.toString();
    }

    // Lifecycle

    /**
     * Load file map.
     */
    public void start() {
        log.info( "Starting file attachments manager" );
        load();
        isRunning = true;
    }

    /**
     * Save map file to upload directory.
     */
    public void stop() {
        log.info( "Stopping file attachments manager" );
        isRunning = false;
    }

    private synchronized void load() {
        Properties result = new Properties();
        Reader in = null;
        try {
            in = new FileReader( new File( directory, mapFileName ) );
            result.load( in );

        } catch ( FileNotFoundException ignored ) {
            log.info( "Creating new file map." );
        } catch ( IOException e ) {
            log.error( "Error while reading file map. Some attachments may be lost", e );
        } finally {
            if ( in != null )
                try {
                    in.close();
                } catch ( IOException e ) {
                    log.error( "Unable to close file map.", e );
                }
        }
        setFileMap( result );
    }

    /**
     * Reconstruct the attachment map.
     *
     * @param index the stored attachment index
     */
    private void setFileMap( Properties index ) {
        this.fileMap = index;

        attachmentMap = new HashMap<String, Attachment>();
        for ( String uriString : index.stringPropertyNames() ) {
            String value = index.getProperty( uriString );
            int comma = value.indexOf( COMMA );

            String ticket = value.substring( 0, comma );
            Attachment.Type type = Attachment.Type.valueOf( value.substring( comma + 1 ) );

            try {
                URI uri = new URI( unescape( uriString ) );
                Attachment attachment = uri.getScheme() == null ?
                        new FileAttachment( type,
                                new File( directory, uriString ),
                                path + uriString )
                        : new UrlAttachment( type, uri.toURL().toString() );

                attachmentMap.put( ticket, attachment );

            } catch ( URISyntaxException ignored ) {
                log.warn( "Malformed key in file map: {}. Ignored.", uriString );
            } catch ( MalformedURLException ignored ) {
                log.warn( "Malformed url in file map: {}. Ignored.", uriString );
            }
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

}
