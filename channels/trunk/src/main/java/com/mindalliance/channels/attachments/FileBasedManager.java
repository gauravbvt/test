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
     * List of documents, indexed by tickets. Reverse index of fileMap.
     */
    private Map<String, Document> documentMap = Collections.synchronizedMap(
            new HashMap<String, Document>() );

    /**
     * List of deleted documents, indexed by attchment tickets.
     */
    private Map<String, Document> deletedMap = Collections.synchronizedMap(
            new HashMap<String, Document>() );

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
    public Document getDocument( String ticket ) {
        return documentMap.get( ticket );
    }

    /**
     * {@inheritDoc}
     */
    public List<Document> getDocuments( List<String> tickets ) {
        List<Document> documents = new ArrayList<Document>();
        for ( String ticket : tickets ) {
            Document document = getDocument( ticket );
            if ( document != null )
                if ( !documents.contains( document ) ) documents.add( document );
        }
        return documents;
    }

    private Document getDeletedDocument( String ticket ) {
        return deletedMap.get( ticket );
    }

    private void index( String ticket, Document document ) {
        documentMap.put( ticket, document );
        fileMap.setProperty( ticket,
                MessageFormat.format(
                        "{0},{1},{2}",                    // NON-NLS
                        document.getKey(),
                        document.getType().name(),
                        document.getDigest() ) );
    }

    private void deindex( String ticket ) {
        Document document = documentMap.get( ticket );
        if ( document != null ) {
            documentMap.remove( ticket );
            deletedMap.put( ticket, document );
            fileMap.remove( ticket );
        } else {
            log.warn( "Failed to deindex: ticket " + ticket + " not found" );
        }
    }

    /**
     * {@inheritDoc}
     */
    public String attach( Document.Type type, FileUpload fileUpload, List<String> tickets ) {
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
            FileDocument fileDocument = new FileDocument(
                    type,
                    file,
                    path + file.getName(),
                    digest );
            synchronized ( this ) {
                Document actual = resolve( fileDocument );
                if ( !getDocuments( tickets ).contains( actual ) ) {
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

    // Return pre-existing document if one exists for the same file.
    private Document resolve( final FileDocument document ) {
        FileDocument toSameFile = (FileDocument) CollectionUtils.find( documentMap.values(), new Predicate() {
            public boolean evaluate( Object obj ) {
                Document prior = (Document) obj;
                return prior.isFile()
                        && document.getFile().getName().
                        // > 0 -> must not be the exact same file, but a duplicate
                                indexOf( ( (FileDocument) prior ).getFile().getName() ) > 0
                        && document.getDigest().equals( prior.getDigest() );
            }
        } );
        if ( toSameFile != null ) {
            document.getFile().delete();
            document.setFile( toSameFile.getFile() );
        }
        return document;
    }

    private String makeTicket() {
        return UUID.randomUUID().toString();
    }

    /**
     * {@inheritDoc}
     */
    public String attach( Document.Type type, String url, String digest, List<String> tickets ) {
        File uploaded = findUploaded( url, digest );
        String ticket = null;
        if ( uploaded != null ) {
            FileDocument document = new FileDocument(
                    type,
                    uploaded,
                    path + uploaded.getName(),
                    digest
            );
            if ( !getDocuments( tickets ).contains( document ) ) {
                ticket = makeTicket();
                index( ticket, document );
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
        FileDocument document = (FileDocument)CollectionUtils.find(
            documentMap.values(),
            new Predicate() {
                public boolean evaluate( Object obj ) {
                    Document doc = (Document)obj;
                    return doc.isFile()
                            && doc.getUrl().equals( url )
                            && doc.getDigest().equals( digest );
                }
            });
        if ( document != null ) {
            return document.getFile();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String attach( Document.Type type, URL url, List<String> tickets ) {
        String ticket = makeTicket();
        synchronized ( this ) {
            UrlDocument urlDocument = new UrlDocument( type, url.toString() );
            if ( !getDocuments( tickets ).contains( urlDocument ) ) {
                index( ticket, urlDocument );
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
    public Document reattach( String ticket ) {
        Document document = getDeletedDocument( ticket );
        if ( document != null ) {
            deletedMap.remove( ticket );
            synchronized ( this ) {
                index( ticket, document );
                save();
            }
        } else {
            log.warn( "Failed to re-attach ticket " + ticket );
        }
        return document;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void reattachAll( List<String> tickets ) {
        for ( String ticket : tickets ) {
            Document document = getDeletedDocument( ticket );
            if ( document != null ) {
                deletedMap.remove( ticket );
                index( ticket, document );
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
        for ( Document document : deletedMap.values() ) {
            document.delete();
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
            log.error( "Unable to save document map " + mapFileName + ".", e );
        } finally {
            if ( out != null )
                try {
                    out.close();
                } catch ( IOException e ) {
                    log.error( "Unable to close document map" + mapFileName + ".", e );
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
        log.info( "Starting file attachment manager" );
        load();
        isRunning = true;
    }

    /**
     * Save map file to upload directory.
     */
    public void stop() {
        log.info( "Stopping file attachment manager" );
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
     * Reconstruct the document map.
     *
     * @param index the stored document index
     */
    private void setFileMap( Properties index ) {
        this.fileMap = index;
        documentMap = new HashMap<String, Document>();
        for ( String ticket : index.stringPropertyNames() ) {
            String value = index.getProperty( ticket );
            String[] elements = value.split( "," );

            String uriString = elements[0];
            Document.Type type = Document.Type.valueOf( elements[1] );
            try {
                URI uri = new URI( unescape( uriString ) );
                Document document = uri.getScheme() == null ?
                        new FileDocument( type,
                                new File( directory, uriString ),
                                path + uriString,
                                elements[2] )
                        : new UrlDocument( type, uri.toURL().toString() );

                documentMap.put( ticket, document );

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
