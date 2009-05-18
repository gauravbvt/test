package com.mindalliance.channels.attachments;

import com.mindalliance.channels.AttachmentManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
                if ( !attachments.contains( attachment ) ) attachments.add( attachment );
        }
        return attachments;
    }

    private Attachment getDeletedAttachment( String ticket ) {
        return deletedMap.get( ticket );
    }

    private void index( String ticket, Attachment attachment ) {
        attachmentMap.put( ticket, attachment );
        fileMap.setProperty( ticket,
                MessageFormat.format(
                        "{0},{1},{2}",                    // NON-NLS
                        attachment.getKey(),
                        attachment.getType().name(),
                        attachment.getDigest() ) );
    }

    private void deindex( String ticket ) {
        Attachment attachment = attachmentMap.get( ticket );
        if ( attachment != null ) {
            attachmentMap.remove( ticket );
            deletedMap.put( ticket, attachment );
            fileMap.remove( ticket );
        } else {
            log.warn( "Failed to deindex: ticket " + ticket + " not found" );
        }
    }

    /**
     * {@inheritDoc}
     */
    public String attach( Attachment.Type type, FileUpload fileUpload, List<String> tickets ) {
        String ticket = makeTicket();
        String fileName = fileUpload.getClientFileName();
        String escaped = escape( fileName );
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            File file = createFile( escaped );
            MessageDigest messageDigest = MessageDigest.getInstance( "SHA" );
            in = new BufferedInputStream(
                    new DigestInputStream(
                            fileUpload.getInputStream(),
                            messageDigest ) );
            out = new BufferedOutputStream( new FileOutputStream( file ) );
            int c;
            do {
                c = in.read();
                if ( c >= 0 ) out.write( c );
            } while ( c >= 0 );
            // fileUpload.writeTo( file );
            String digest = URLEncoder.encode(
                    new String( messageDigest.digest() ).replaceAll( ",", "\\u002c" ),
                    "UTF-8");
            FileAttachment fileAttachment = new FileAttachment(
                    type,
                    file,
                    path + file.getName(),
                    digest );
            synchronized ( this ) {
                Attachment actual = resolve( fileAttachment );
                if ( !getAttachments( tickets ).contains( actual ) ) {
                    index( ticket, actual );
                    save();
                } else {
                    ticket = null;
                }
            }
        } catch ( IOException e ) {
            log.error( MessageFormat.format( "Error while uploading file: {0}", fileName ), e );
            ticket = null;
        } catch ( NoSuchAlgorithmException e ) {
            log.error( MessageFormat.format( "Error while uploading file: {0}", fileName ), e );
            ticket = null;
        } finally {
            try {
                if ( in != null ) in.close();
                if ( out != null ) out.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        return ticket;
    }

    // Return pre-existing attachment if one exists for the same file.
    private Attachment resolve( final FileAttachment attachment ) {
        FileAttachment toSameFile = (FileAttachment) CollectionUtils.find( attachmentMap.values(), new Predicate() {
            public boolean evaluate( Object obj ) {
                Attachment prior = (Attachment) obj;
                return prior.isFile()
                        && attachment.getFile().getName().
                        // > 0 -> must not be the exact same file, but a duplicate
                                indexOf( ( (FileAttachment) prior ).getFile().getName() ) > 0
                        && attachment.getDigest().equals( prior.getDigest() );
            }
        } );
        if ( toSameFile != null ) {
            attachment.getFile().delete();
            attachment.setFile( toSameFile.getFile() );
        }
        return attachment;
    }

    private String makeTicket() {
        return UUID.randomUUID().toString();
    }

    /**
     * {@inheritDoc}
     */
    public String attach( Attachment.Type type, String url, String digest, List<String> tickets ) {
        File uploaded = findUploaded( url, digest );
        String ticket = null;
        if ( uploaded != null ) {
            FileAttachment attachment = new FileAttachment(
                    type,
                    uploaded,
                    path + uploaded.getName(),
                    digest
            );
            if ( !getAttachments( tickets ).contains( attachment ) ) {
                ticket = makeTicket();
                index( ticket, attachment );
                save();
            }
        } else {
            log.warn( "Could not find uploaded file " + url );
        }
        return ticket;
    }

    /**
     * {@inheritDoc}
     */
    public File findUploaded( final String url, final String digest ) {
        FileAttachment attachment = (FileAttachment)CollectionUtils.find(
            attachmentMap.values(),
            new Predicate() {
                public boolean evaluate( Object obj ) {
                    Attachment attachment = (Attachment)obj;
                    return attachment.isFile()
                            && attachment.getUrl().equals( url )
                            && attachment.getDigest().equals( digest );
                }
            });
        if ( attachment != null ) {
            return attachment.getFile();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String attach( Attachment.Type type, URL url, List<String> tickets ) {
        String ticket = makeTicket();
        synchronized ( this ) {
            UrlAttachment urlAttachment = new UrlAttachment( type, url.toString() );
            if ( !getAttachments( tickets ).contains( urlAttachment ) ) {
                index( ticket, urlAttachment );
                save();
            } else {
                ticket = null;
            }
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
        for ( String ticket : index.stringPropertyNames() ) {
            String value = index.getProperty( ticket );
            String[] elements = value.split( "," );

            String uriString = elements[0];
            Attachment.Type type = Attachment.Type.valueOf( elements[1] );
            try {
                URI uri = new URI( unescape( uriString ) );
                Attachment attachment = uri.getScheme() == null ?
                        new FileAttachment( type,
                                new File( directory, uriString ),
                                path + uriString,
                                elements[2] )
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
