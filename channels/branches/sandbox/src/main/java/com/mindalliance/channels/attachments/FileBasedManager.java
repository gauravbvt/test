package com.mindalliance.channels.attachments;

import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
    private static final Logger LOG = LoggerFactory.getLogger( FileBasedManager.class );

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
    /**
     * Comma-separated video file extensions.
     */
    private String videoExtensions = "";
    /**
     * Comma-separated image file extensions.
     */
    private String imageExtensions = "";
    /**
     * List of video-hosting domains.
     */
    private List<String> videoDomains = new ArrayList<String>();
    /**
     * List of image-hosting domains.
     */
    private List<String> imageDomains = new ArrayList<String>();

    public FileBasedManager() {
    }

    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }

    public String getVideoExtensions() {
        return videoExtensions;
    }

    public void setVideoExtensions( String videoExtensions ) {
        this.videoExtensions = videoExtensions;
    }

    public String getImageExtensions() {
        return imageExtensions;
    }

    public void setImageExtensions( String imageExtensions ) {
        this.imageExtensions = imageExtensions;
    }

    public List<String> getVideoDomains() {
        return videoDomains;
    }

    public void setVideoDomains( List<String> videoDomains ) {
        this.videoDomains = videoDomains;
    }

    public List<String> getImageDomains() {
        return imageDomains;
    }

    public void setImageDomains( List<String> imageDomains ) {
        this.imageDomains = imageDomains;
    }

    private synchronized Map<String, FileDocument> getDocumentMap( Plan plan ) {
        if ( documentMap == null ) {
            documentMap = Collections.synchronizedMap( new HashMap<String, FileDocument>() );
            load( plan, documentMap );
        }
        return documentMap;
    }

    private File createFile( Plan plan, String name ) {
        String truncatedName = StringUtils.reverse(
                StringUtils.reverse( name ).substring(
                        0, Math.min( name.length(), getMaxLength() ) ) );
        // String idealName = escape( truncatedName );
        File result = new File( getUploadDirectory( plan ), truncatedName );
        int i = 0;
        while ( result.exists() ) {
            String actual = ++i + "_" + truncatedName;
            result = new File( getUploadDirectory( plan ), actual );
        }

        return result;
    }

    // Return pre-existing document if one exists for the same file.
    private FileDocument resolve( Plan plan, final FileDocument document ) {
        FileDocument toSameFile = (FileDocument) CollectionUtils.find(
                getDocumentMap( plan ).values(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        FileDocument prior = (FileDocument) object;
                        return prior.isFile()
                                // > 0 -> must not be the exact same file, but a duplicate
                                && document.getFile().getName().
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

    public String getUploadPath() {
        return uploadPath;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasVideoContent( String url ) {
        String lc_url = url.toLowerCase();
        return hasExtension( lc_url, getVideoExtensions() ) || hasDomain( lc_url, getVideoDomains() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasImageContent( String url ) {
        String lc_url = url.toLowerCase();
        return hasExtension( lc_url, getImageExtensions() ) || hasDomain( lc_url, getImageDomains() );
    }

    private boolean hasDomain( final String url, List<String> domains ) {
        return CollectionUtils.exists(
                domains,
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        try {
                            return new URL( url ).getHost().contains( ( (String) object ).toLowerCase().trim() );
                        } catch ( MalformedURLException e ) {
                            return false;
                        }
                    }
                }
        );
    }

    private boolean hasExtension( final String url, String extensions ) {
        return CollectionUtils.exists(
                Arrays.asList( StringUtils.split( extensions, ',' ) ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return url.endsWith( ( (String) object ).trim() );
                    }
                }
        );
    }

    public void setUploadPath( String uploadPath ) {
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


    private void save( Plan plan ) {
        Writer out = null;
        try {
            Properties digests = new Properties();
            for ( Map.Entry<String, FileDocument> entry : getDocumentMap( plan ).entrySet() )
                digests.setProperty( entry.getKey(), entry.getValue().getDigest() );

            out = new FileWriter( new File( getUploadDirectory( plan ), digestsMapFile ) );
            digests.store( out, " File digests. Do not edit." );

        } catch ( IOException e ) {
            LOG.error( "Unable to save document digests " + digestsMapFile + '.', e );

        } finally {
            if ( out != null )
                try {
                    out.close();
                } catch ( IOException e ) {
                    LOG.error( "Unable to document digest map" + digestsMapFile + '.', e );
                }
        }
    }

    private void load( Plan plan, Map<String, FileDocument> docMap ) {
        Properties digests = new Properties();
        Reader in = null;
        try {
            in = new FileReader( new File( getUploadDirectory( plan ), digestsMapFile ) );
            digests.load( in );

        } catch ( FileNotFoundException ignored ) {
            LOG.info( "Creating new file map." );
        } catch ( IOException e ) {
            LOG.error( "Error while reading file map. Some attachments may be lost", e );
        } finally {
            if ( in != null )
                try {
                    in.close();
                } catch ( IOException e ) {
                    LOG.error( "Unable to close file map.", e );
                }
        }
        for ( String url : digests.stringPropertyNames() ) {
            String digest = digests.getProperty( url );
            // String unescapedUrl = unescape( url );
            FileDocument document = new FileDocument(
                    new File( getUploadDirectory( plan ), url ),
                    /*path + */url, digest );
            if ( exists( plan, url ) ) {
                docMap.put( url, document );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean exists( Plan plan, String url ) {
        return isValidUrl( url ) && ( !isFileDocument( url ) || isUploaded( plan, url ) );
    }

    private boolean isValidUrl( String url ) {
        if ( !url.startsWith( uploadPath ) ) {
            try {
                new URL( url );
            } catch ( MalformedURLException ignored ) {
                return false;
            }
        }

        return true;
    }

    private boolean isUploaded( Plan plan, final String url ) {
        return getUploadDirectory( plan ).listFiles( new FilenameFilter() {
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
    public Attachment upload(
            Plan plan,
            Attachment.Type selectedType,
            String name,
            FileUpload upload ) {

        String fileName = upload.getClientFileName();
        //String escaped = escape( fileName );
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        Attachment attachment = null;
        try {
            File file = createFile( plan, fileName );
            MessageDigest messageDigest = MessageDigest.getInstance( "SHA" );
            in = new BufferedInputStream(
                    new DigestInputStream(
                            upload.getInputStream(),
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
                FileDocument actual = resolve( plan, fileDocument );
                getDocumentMap( plan ).put( actual.getUrl(), actual );
                attachment = new Attachment( actual.getUrl(), selectedType );
                attachment.setName( name );
                save( plan );
            }

        } catch ( IOException e ) {
            LOG.error( MessageFormat.format( "Error while uploading file: {0}", fileName ), e );
        } catch ( NoSuchAlgorithmException e ) {
            LOG.error( MessageFormat.format( "Error while uploading file: {0}", fileName ), e );
        } finally {
            try {
                if ( in != null ) in.close();
                if ( out != null ) out.close();
            } catch ( Exception e ) {
                LOG.warn( "Unable to close uploaded file", e );
            }
        }
        return attachment;
    }

    /**
     * {@inheritDoc }
     */
    public String getLabel( Plan plan, Attachment attachment ) {
        FileDocument fileDocument = getDocumentMap( plan ).get( attachment.getUrl() );
        return !attachment.getName().isEmpty()
                ? attachment.getName()
                :fileDocument == null
                ? attachment.getUrl()
                : fileDocument.getFile().getName();
    }

    /**
     * {@inheritDoc }
     *
     * @param planDao
     */
    public synchronized void removeUnattached( PlanDao planDao ) {
        List<String> attachedUrls = findAllAttached( planDao );
        Plan plan1 = planDao.getPlan();
        File[] files = getUploadDirectory( plan1 ).listFiles();
        if ( files != null ) {
            List<File> uploadedFiles = Arrays.asList( files );
            for ( File file : uploadedFiles ) {
                String fileName = file.getName();
                if ( !( fileName.equals( "readme.txt" )
                        || fileName.equals( digestsMapFile ) ) ) {
                    String url = uploadPath + fileName;
                    if ( !attachedUrls.contains( url ) ) {
                        LOG.warn( "Removing unattached " + url );
                        file.delete();
                        getDocumentMap( plan1 ).remove( url );
                    }
                }
            }
            save( plan1 );
        }
    }

    /**
     * Find urls of all attachments.
     *
     * @param planDao the plan's dao
     * @return a list of strings
     */
    private static List<String> findAllAttached( PlanDao planDao ) {

        List<ModelObject> allModelObjects = new ArrayList<ModelObject>();
        allModelObjects.addAll( planDao.list( ModelObject.class ) );
        for ( Segment segment : planDao.list( Segment.class ) ) {
            Iterator<Part> parts = segment.parts();
            while ( parts.hasNext() ) {
                allModelObjects.add( parts.next() );
            }
            Iterator<Flow> flows = segment.flows();
            while ( flows.hasNext() ) {
                allModelObjects.add( flows.next() );
            }
        }
        List<Attachment> allAttachments = new ArrayList<Attachment>();
        for ( ModelObject mo : allModelObjects )
            allAttachments.addAll( mo.getAttachments() );

        Set<String> allAttachedUrls = new HashSet<String>();
        for ( Attachment attachment : allAttachments )
            allAttachedUrls.add( attachment.getUrl() );

        return new ArrayList<String>( allAttachedUrls );
    }

    /**
     * Get the location of the uploaded files.
     *
     * @param plan the plan
     * @return a directory
     */
    public File getUploadDirectory( Plan plan ) {
        File uploadsDir = new File(
                planManager.getPlanVersionDirectory( plan ) + File.separator + uploadPath );
        if ( !uploadsDir.exists() ) {
            uploadsDir.mkdir();
            LOG.info( "Created upload directory: {}", uploadsDir.getAbsolutePath() );
        }
        return uploadsDir;
    }
}
