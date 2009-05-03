package com.mindalliance.channels.attachments;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Properties;
import java.util.Observable;

/**
 * An attachment manager that keeps uploaded files in a directory.
 * An index file is kept to maintain the association between model objects and files.
 */
public class FileBasedManager implements AttachmentManager, Lifecycle, Observer {

    /** Separator used for value in map file. */
    private static final int COMMA = (int) ',';

    /** Character used to escape funny characters. */
    private static final char ESCAPE = '%';

    /** Characters to be escaped in file name. */
    private static final String CHARS = "%_/\\:?&=";                                      // NON-NLS

    /** Default maximum file name length (128). */
    private static final int MAX_LENGTH = 128;

    /** The logger. */
    private final Logger log = LoggerFactory.getLogger( FileBasedManager.class );

    /** Name of the file to store map into, relative to directory. */
    private String mapFileName = "index.properties";                                      // NON-NLS

    /** The directory to keep files in. */
    private File directory;

    /** The webapp-relative path to file URLs. */
    private String path = "";

    /** The maximum file name length. Anything above that will get truncated. */
    private int maxLength = MAX_LENGTH;

    /** "id,type", indexed by URI. */
    private Properties fileMap = new Properties();

    /**
     * List of attachments, indexed by object ids. Reverse index of fileMap.
     */
    private Map<Long,List<Attachment>> objectMap = new HashMap<Long,List<Attachment>>();

    /** True when the manager is running. */
    private boolean isRunning;

    public FileBasedManager() {
    }

    private File createFile( String name ) {
        String truncatedName = name.substring( 0, Math.min( name.length(), getMaxLength() ) );
        String idealName = escape( truncatedName );
        File result = new File( directory, idealName );
        int i = 0;
        while ( result.exists() ) {
            String actual = idealName + ++i;
            result = new File( directory, actual );
        }

        return result;
    }

    /** {@inheritDoc} */
    @SuppressWarnings( { "unchecked" } )
    public Iterator<Attachment> attachments( long id ) {
        return getAttachments( id ).iterator();
    }

    private synchronized List<Attachment> getAttachments( Long id ) {
        List<Attachment> attachments = objectMap.get( id );
        if ( attachments == null ) {
            attachments = Collections.synchronizedList( new ArrayList<Attachment>() );
            objectMap.put( id, attachments );
        }
        return attachments;
    }

    private synchronized void index( Long id, Attachment attachment ) {
        fileMap.setProperty( attachment.getKey(),
                             MessageFormat.format( "{0,number,0},{1}",                    // NON-NLS
                                                   id, attachment.getType().name() ) );
        getAttachments( id ).add( attachment );
    }

    private synchronized void deindex( long id, Attachment attachment ) {
        fileMap.remove( attachment.getKey() );
        getAttachments( id ).remove( attachment );
    }

    /** {@inheritDoc} */
    public void attach( long id, Attachment.Type type, FileUpload fileUpload ) {
        String fileName = fileUpload.getClientFileName();
        String escaped = escape( fileName );

        try {
            File file = createFile( escaped );
            FileAttachment fileAttachment = new FileAttachment( type, file, path + escaped );
            fileUpload.writeTo( file );
            index( id, fileAttachment );
            save();

        } catch ( IOException e ) {
            log.error( MessageFormat.format( "Error while uploading file: {0}", fileName ), e );
        }
    }

    /** {@inheritDoc} */
    public void attach( long id, Attachment.Type type, URL url ) {
        index( id, new UrlAttachment( type, url.toString() ) );
        save();
    }

    /** {@inheritDoc} */
    public void detach( long id, Attachment attachment ) {
        deindex( id, attachment );
        attachment.delete();
        save();
    }

    /** {@inheritDoc} */
    public void detachAll( long id ) {
        for ( Attachment a : new ArrayList<Attachment>( getAttachments( id ) ) ) {
            deindex( id, a );
            a.delete();
        }
        save();
    }

    public synchronized File getDirectory() {
        return directory;
    }

    /**
     * Set the directory where files will be stored.
     * Files in the directory can be removed independently.
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

    /**
     * Set the file map and compute reverse index.
     * @param fileMap the file map
     */
    private void setFileMap( Properties fileMap ) {
        this.fileMap = fileMap;

        objectMap = new HashMap<Long,List<Attachment>>();
        for ( String uriString : fileMap.stringPropertyNames() ) {
            String value = fileMap.getProperty( uriString );
            int comma = value.indexOf( COMMA );

            long id = Long.parseLong( value.substring( 0, comma ) );
            Attachment.Type type = Attachment.Type.valueOf( value.substring( comma + 1 ) );

            try {
                URI uri = new URI( unescape( uriString ) );
                Attachment attachment = uri.getScheme() == null ?
                            new FileAttachment( type,
                                                new File( directory, uriString ),
                                                path + uriString )
                          : new UrlAttachment( type, uri.toURL().toString() );

                getAttachments( id ).add( attachment );

            } catch ( URISyntaxException ignored ) {
                log.warn( "Malformed key in file map: {}. Ignored.", uriString );
            } catch ( MalformedURLException ignored ) {
                log.warn( "Malformed url in file map: {}. Ignored.", uriString );
            }
        }
    }

    public String getMapFileName() {
        return mapFileName;
    }

    public void setMapFileName( String mapFileName ) {
        this.mapFileName = mapFileName;
    }

    /** Load file map and compute reverse index. */
    public void start() {
        log.info( "Starting file attachments manager" );
        load();
        isRunning = true;
    }

    /** Save map file to upload directory. */
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

    private synchronized void save() {
        File file = new File( directory, mapFileName );
        Writer out = null;
        try {
            out = new FileWriter( file );
            fileMap.store( out, " Files-objects association file. Edit with care..." );
        } catch ( IOException e ) {
            log.error( "Unable to save file map.", e );
        } finally {
            if ( out != null )
                try {
                    out.close();
                } catch ( IOException e ) {
                    log.error( "Unable to close file map.", e );
                }
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Replace offending characters in a file name so it can be reconstituted by a call to unescape.
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

    /**
     * Reattach attachements to new model object ids.
     * @param idMap the conversion map
     */
    public synchronized void remap( Map<Long,Long> idMap ) {
        Properties original = fileMap;
        Properties remapped = new Properties();

        for ( String key : original.stringPropertyNames() ) {
            String value = original.getProperty( key );

            int comma = value.indexOf( COMMA );
            Long oldId = Long.parseLong( value.substring( 0, comma ) );
            Long newId = idMap.get( oldId );
            if ( newId == null )
                log.warn( "Missing remapping for old id {}", oldId );
            else {
                log.debug( "Remapping id {} --> {}", oldId, newId );
                remapped.setProperty( key, Long.toString( newId ) + value.substring( comma ) );
            }
        }

        setFileMap( remapped );
        save();
    }

    /**
     * Potentially called when ids were remapped.
     * @param o an Importer
     * @param arg an id map
     */
    public void update( Observable o, Object arg ) {
        remap( (Map<Long,Long>) arg );
    }
}
