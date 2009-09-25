package com.mindalliance.channels.attachments;

import com.mindalliance.channels.AttachmentManager;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.dao.PlanManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * An attachment manager that keeps uploaded files in a directory.
 * An index file is kept to maintain the association between model objects and files.
 */
public class FileBasedManager implements AttachmentManager {


    /**
     * Character used to escape funny characters.
     */
    private static final char ESCAPE = '%';

    /**
     * Characters to be escaped in file name.
     */
    private static final String CHARS = "%_/\\:?&=";

    /**
     * Default maximum file name length (128).
     */
    private static final int MAX_LENGTH = 128;

    /**
     * The logger.
     */
    private final Logger log = LoggerFactory.getLogger( FileBasedManager.class );

    /**
     * Plan manager.
     */
    private PlanManager planManager;

    /**
     * Name of the file to store map into, relative to directory.
     */
    private String digestsMapFile = "digests.properties";

    /**
     * The webapp-relative path to file URLs.
     */
    private String uploadPath = "";

    /**
     * The maximum file name length. Anything above that will get truncated.
     */
    private int maxLength = MAX_LENGTH;

    /**
     * List of documents, indexed by url.
     */
    private Map<String, FileDocument> documentMap;


    public FileBasedManager() {
    }

    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }

    private Map<String, FileDocument> getDocumentMap() {
        if ( documentMap == null ) {
            documentMap = Collections.synchronizedMap(
                    new HashMap<String, FileDocument>() );
            load( documentMap );
        }
        return documentMap;
    }

    private File createFile( String name ) {
        String truncatedName = StringUtils.reverse(
                StringUtils.reverse( name ).substring( 0, Math.min( name.length(), getMaxLength() ) ) );
        // String idealName = escape( truncatedName );
        File result = new File( getUploadDirectory(), truncatedName );
        int i = 0;
        while ( result.exists() ) {
            String actual = ++i + "_" + truncatedName;
            result = new File( getUploadDirectory(), actual );
        }

        return result;
    }


    // Return pre-existing document if one exists for the same file.
    private FileDocument resolve( final FileDocument document ) {
        FileDocument toSameFile = (FileDocument) CollectionUtils.find(
                getDocumentMap().values(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        FileDocument prior = (FileDocument) object;
                        return prior.isFile()
                                && document.getFile().getName().
                                // > 0 -> must not be the exact same file, but a duplicate
                                        indexOf( prior.getFile().getName() ) > 0
                                && document.getDigest().equals( prior.getDigest() );
                    }
                } );
        if ( toSameFile != null ) {
            document.getFile().delete();
            document.setFile( toSameFile.getFile() );
            document.setUrl( toSameFile.getUrl() );
        }
        return document;
    }

    public synchronized String getUploadPath() {
        return uploadPath;
    }

    public synchronized void setUploadPath( String uploadPath ) {
        this.uploadPath = uploadPath;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength( int maxLength ) {
        this.maxLength = maxLength;
    }


    public String getDigestsMapFile() {
        return digestsMapFile;
    }

    public void setDigestsMapFile( String digestsMapFile ) {
        this.digestsMapFile = digestsMapFile;
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


    private void save() {
        Writer out = null;
        try {
            File file = new File( getUploadDirectory(), digestsMapFile );
            out = new FileWriter( file );
            Properties digests = new Properties();
            for ( String url : getDocumentMap().keySet() ) {
                digests.setProperty( url, getDocumentMap().get( url ).getDigest() );
            }
            digests.store( out, " File digests. Do not edit." );
        } catch ( IOException e ) {
            log.error( "Unable to save document digests " + digestsMapFile + ".", e );
        } finally {
            if ( out != null )
                try {
                    out.close();
                } catch ( IOException e ) {
                    log.error( "Unable to document digest map" + digestsMapFile + ".", e );
                }
        }
    }


    private synchronized void load( Map<String, FileDocument> docMap ) {
        Properties digests = new Properties();
        Reader in = null;
        try {
            in = new FileReader( new File( getUploadDirectory(), digestsMapFile ) );
            digests.load( in );

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
        for ( String url : digests.stringPropertyNames() ) {
            String digest = digests.getProperty( url );
            // String unescapedUrl = unescape( url );
            FileDocument document = new FileDocument(
                    new File( getUploadDirectory(), url ),
                    /*path + */url, digest );
            if ( exists( url ) ) {
                docMap.put( url, document );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean exists( String url ) {
        return isValidUrl( url ) && ( !isFileDocument( url ) || isUploaded( url ) );
    }

    private boolean isValidUrl( String url ) {
        if ( url.startsWith( uploadPath ) ) return true;
        try {
            new URL( url );
        } catch ( MalformedURLException e ) {
            return false;
        }
        return true;
    }

    private boolean isUploaded( final String url ) {
        return getUploadDirectory().listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return url.substring( url.lastIndexOf( '/' ) + 1 ).equals( name );
            }
        } ).length == 1;
    }

    private boolean isFileDocument( String url ) {
        return url.startsWith( uploadPath );
    }


    /**
     * {@inheritDoc}
     */
    public Attachment upload( Attachment.Type type, FileUpload fileUpload ) {
        String fileName = fileUpload.getClientFileName();
        //String escaped = escape( fileName );
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        Attachment attachment = null;
        try {
            File file = createFile( fileName );
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
                    "UTF-8" );
            FileDocument fileDocument = new FileDocument(
                    file,
                    uploadPath + file.getName(),
                    digest );
            synchronized ( this ) {
                FileDocument actual = resolve( fileDocument );
                getDocumentMap().put( actual.getUrl(), actual );
                attachment = new Attachment( actual.getUrl(), type );
                save();
            }
        } catch ( IOException e ) {
            log.error( MessageFormat.format( "Error while uploading file: {0}", fileName ), e );
        } catch ( NoSuchAlgorithmException e ) {
            log.error( MessageFormat.format( "Error while uploading file: {0}", fileName ), e );
        } finally {
            try {
                if ( in != null ) in.close();
                if ( out != null ) out.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        return attachment;
    }

    /**
     * {@inheritDoc }
     */
    public String getLabel( Attachment attachment ) {
        FileDocument fileDocument = getDocumentMap().get( attachment.getUrl() );
        return fileDocument == null ? attachment.getUrl() : fileDocument.getFile().getName();
    }

    /**
     * {@inheritDoc }
     *
     * @param service
     */
    public synchronized void removeUnattached( QueryService service ) {
        List<String> attachedUrls = service.findAllAttached();
        File[] files = getUploadDirectory().listFiles();
        if ( files != null ) {
            List<File> uploadedFiles = Arrays.asList( files );
            for ( File file : uploadedFiles ) {
                String name = file.getName();
                if ( !( name.equals( "readme.txt" )
                        || name.equals( digestsMapFile ) ) ) {
                    String url = uploadPath + name;
                    if ( !attachedUrls.contains( url ) ) {
                        log.warn( "Removing unattached " + url );
                        file.delete();
                        getDocumentMap().remove( url );
                    }
                }
            }
            save();
        }
    }

    /**
     * Get the location of the uploaded files.
     *
     * @return a directory
     */
    public File getUploadDirectory() {
        File uploadsDir = new File( planManager.getPlanVersionDirectory() + File.separator + uploadPath );
        if ( !uploadsDir.exists() ) {
            uploadsDir.mkdir();
            log.info( "Created upload directory: {}", uploadsDir.getAbsolutePath() );
        }
        return uploadsDir;
    }
}
