package com.mindalliance.channels.attachments;

import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.dao.PlanListener;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Attachable;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Attachment.Type;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Tag;
import com.mindalliance.channels.util.InfoStandardsLoader;
import com.mindalliance.channels.util.Loader;
import com.mindalliance.channels.util.TagLoader;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * An attachment manager that keeps uploaded files in a directory. An index file is kept to maintain the association
 * between model objects and files.
 */
public class FileBasedManager implements AttachmentManager, InitializingBean {

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
    private final Map<String, FileDocument> documentMap =
            Collections.synchronizedMap( new HashMap<String, FileDocument>() );

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

    /**
     * Specify what plan manager to hook up to.
     * @param planManager the plan manager
     */
    public void setPlanManager( PlanManager planManager ) {
        if ( planManager == null )
            throw new IllegalArgumentException();

        this.planManager = planManager;
        planManager.addListener( new Listener() );
    }

    private void reloadTags( Plan plan ) {
        plan.setTags( new ArrayList<Tag>() );
        for ( Attachment attachment : plan.getAttachments() ) {
            String url = attachment.getUrl();
            if ( attachment.isTags() )
                reloadTagsFromUrl( plan, url, new TagLoader( plan ) );
            if ( attachment.isInfoStandards() )
                reloadTagsFromUrl( plan, url, new InfoStandardsLoader( plan ) );
        }
    }

    /**
     * Remove a mapped url for a given plan.
     *
     * @param plan the plan
     * @param url the url
     */
    @Override
    public void remove( Plan plan, String url ) {
        documentMap.remove( url );
    }

    public void setVideoExtensions( String videoExtensions ) {
        this.videoExtensions = videoExtensions;
    }

    public void setImageExtensions( String imageExtensions ) {
        this.imageExtensions = imageExtensions;
    }

    public void setVideoDomains( List<String> videoDomains ) {
        this.videoDomains = new ArrayList<String>( videoDomains );
    }

    public void setImageDomains( List<String> imageDomains ) {
        this.imageDomains = new ArrayList<String>( imageDomains );
    }

    private File createFile( Plan plan, String name ) {
        String truncatedName = StringUtils
                .reverse( StringUtils.reverse( name ).substring( 0, Math.min( name.length(), maxLength ) ) );
        // String idealName = escape( truncatedName );
        File result = new File( getUploadDirectory( plan ), truncatedName );
        int i = 0;
        while ( result.exists() ) {
            String actual = ++i + "_" + truncatedName;
            result = new File( getUploadDirectory( plan ), actual );
        }

        return result;
    }

    /**
     * Return pre-existing document if one exists for the same file.
     *
     * @param document the document of interest
     * @return the pre-existing document
     */
    private FileDocument resolve( FileDocument document ) {
        synchronized ( documentMap ) {
            for ( FileDocument prior : documentMap.values() )
                if ( document.isDuplicate( prior ) )
                    return prior;

            document.delete();
            return new FileDocument( document.getFile(), document.getUrl(), document.getDigest() );
        }
    }

    @Override
    public boolean hasVideoContent( String url ) {
        String lcUrl = url.toLowerCase();
        return hasExtension( lcUrl, videoExtensions ) || hasDomain( lcUrl, videoDomains );
    }

    @Override
    public boolean hasImageContent( String url ) {
        String lcUrl = url.toLowerCase();
        return hasExtension( lcUrl, imageExtensions ) || hasDomain( lcUrl, imageDomains );
    }

    private static boolean hasDomain( final String url, List<String> domains ) {
        return CollectionUtils.exists( domains, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                try {
                    return new URL( url ).getHost().contains( ( (String) object ).toLowerCase().trim() );
                } catch ( MalformedURLException ignored ) {
                    return false;
                }
            }
        } );
    }

    private static boolean hasExtension( final String url, String extensions ) {
        return CollectionUtils.exists( Arrays.asList( StringUtils.split( extensions, ',' ) ), new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return url.endsWith( ( (String) object ).trim() );
            }
        } );
    }

    public void setUploadPath( String uploadPath ) {
        this.uploadPath = uploadPath;
    }

    public void setMaxLength( int maxLength ) {
        this.maxLength = maxLength;
    }

    public void setDigestsMapFile( String digestsMapFile ) {
        this.digestsMapFile = digestsMapFile;
    }

    private void save( Plan plan ) throws IOException {
        Properties digests = new Properties();
        synchronized ( documentMap ) {
            for ( Entry<String, FileDocument> entry : documentMap.entrySet() )
                digests.setProperty( entry.getKey(), entry.getValue().getDigest() );
        }

        Writer out = new FileWriter( new File( getUploadDirectory( plan ), digestsMapFile ) );
        try {
            digests.store( out, " File digests. Do not edit." );

        } finally {
            out.close();
        }
    }

    @Override
    public boolean exists( Plan plan, String url ) {
        return isValidUrl( url ) && ( !isFileDocument( url ) || isUploaded( plan, url ) );
    }

    private boolean isValidUrl( String url ) {
        if ( !url.startsWith( uploadPath ) )
            try {
                new URL( url );
            } catch ( MalformedURLException ignored ) {
                return false;
            }

        return true;
    }

    private boolean isUploaded( Plan plan, final String url ) {
        return getUploadDirectory( plan ).listFiles( new FilenameFilter() {
            @Override
            public boolean accept( File dir, String name ) {
                return url.substring( url.lastIndexOf( '/' ) + 1 ).equals( name );
            }
        } ).length == 1;
    }

    private boolean isFileDocument( String url ) {
        return url.startsWith( uploadPath );
    }

    @Override
    public Attachment upload( Plan plan, Upload upload ) {

        //String escaped = escape( fileName );
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        Attachment attachment = null;
        try {
            File file = createFile( plan, upload.getFileName() );
            MessageDigest messageDigest = MessageDigest.getInstance( "SHA" );
            in = new BufferedInputStream( new DigestInputStream( upload.getInputStream(), messageDigest ) );
            out = new BufferedOutputStream( new FileOutputStream( file ) );
            int count;
            do {
                count = in.read();
                if ( count >= 0 )
                    out.write( count );
            } while ( count >= 0 );
            // fileUpload.writeTo( file );
            String digest = URLEncoder
                    .encode( new String( messageDigest.digest() ).replaceAll( ",", "\\u002c" ), "UTF-8" );
            FileDocument fileDocument = new FileDocument( file, uploadPath + file.getName(), digest );

            synchronized ( documentMap ) {
                FileDocument actual = resolve( fileDocument );
                documentMap.put( actual.getUrl(), actual );
                attachment = new Attachment( actual.getUrl(), upload.getSelectedType() );
                attachment.setName( upload.getName() );
                save( plan );
            }
        } catch ( IOException e ) {
            LOG.error( MessageFormat.format( "Error while uploading file: {0}", upload.getFileName() ), e );
        } catch ( NoSuchAlgorithmException e ) {
            LOG.error( MessageFormat.format( "Error while uploading file: {0}", upload.getFileName() ), e );
        } finally {
            try {
                if ( in != null )
                    in.close();
                if ( out != null )
                    out.close();
            } catch ( IOException e ) {
                LOG.warn( "Unable to close uploaded file", e );
            }
        }
        return attachment;
    }

    @Override
    public String getLabel( Plan plan, Attachment attachment ) {
        FileDocument fileDocument = documentMap.get( attachment.getUrl() );
        return attachment.getName().isEmpty() ? fileDocument == null ? attachment.getUrl()
                                                                     : fileDocument.getFile().getName()
                                              : attachment.getName();
    }

    @Override
    public File getUploadDirectory( Plan plan ) {
        File versionDirectory = planManager.getVersionDirectory( plan );
        File uploadsDir = new File( versionDirectory, uploadPath );
        if ( !uploadsDir.exists() && uploadsDir.mkdir() )
            LOG.info( "Created upload directory: {}", uploadsDir.getAbsolutePath() );

        return uploadsDir;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Attachment> getMediaReferences( Attachable object ) {
        return (List<Attachment>) CollectionUtils.select( object.getAttachments(), new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return isMediaReference( (Attachment) object );
            }
        } );
    }

    @Override
    public boolean isMediaReference( Attachment attachment ) {
        return isImageReference( attachment ) || isVideoReference( attachment );
    }

    @Override
    public boolean isVideoReference( Attachment attachment ) {
        return attachment.getType() == Type.Reference && hasVideoContent( attachment.getUrl() );
    }

    @Override
    public boolean isImageReference( Attachment attachment ) {
        return attachment.getType() == Type.Reference && hasImageContent( attachment.getUrl() );
    }

    @Override
    public boolean isUploadedFileDocument( String url ) {
        return url.startsWith( uploadPath );
    }

    @Override
    public File getUploadedFile( Plan plan, String planRelativePath ) {
        return new File( getUploadDirectory( plan ), planRelativePath.replaceFirst( uploadPath, "" ) );
    }

    private void reloadTagsFromUrl( Plan plan, String url, Loader loader ) {
        BufferedReader in = null;
        try {

            in = new BufferedReader( new InputStreamReader(
                    isUploadedFileDocument( url ) ? new FileInputStream( getUploadedFile( plan, url ) )
                                                  : new URL( url ).openStream() ) );

            loader.load( in );
        } catch ( IOException e ) {
            LOG.error( "Failed to load tags file " + url, e );
        } finally {
            if ( in != null )
                try {
                    in.close();
                } catch ( IOException e ) {
                    LOG.warn( "Failed to close tags file " + url, e );
                }
        }
    }

    @Override
    public void removeAttachment( Attachment attachment, Attachable attachable ) {
        attachable.removeAttachment( attachment );

        if ( attachable instanceof Plan && ( attachment.isTags() || attachment.isInfoStandards() ) )
            reloadTags( (Plan) attachable );
    }

    @Override
    public void addAttachment( Attachment attachment, Attachable attachable ) {
        attachable.addAttachment( attachment );
        if ( attachable instanceof Plan && ( attachment.isTags() || attachment.isInfoStandards() ) )
            reloadTags( (Plan) attachable );
    }

    @Override
    public void afterPropertiesSet() {
        if ( planManager == null )
            throw new IllegalArgumentException( "planManager must be set" );
    }

    /**
     * Plan manager hook to react on plan changes.
     */
    private class Listener implements PlanListener {

        private File[] getAttachedFiles( Plan plan ) {
            return getUploadDirectory( plan ).listFiles();
        }

        /**
         * Check is a filename is reserved for internal housekeeping.
         *
         * @param fileName the file name
         * @return true if file should not be messed with
         */
        private boolean isReserved( String fileName ) {
            return !"readme.txt".equals( fileName ) && !fileName.equals( digestsMapFile );
        }

        private void removeUnattached( Plan plan, List<String> validUrls ) throws IOException {
            File[] files = getAttachedFiles( plan );
            if ( files != null ) {
                for ( File file : files ) {
                    String fileName = file.getName();
                    if ( isReserved( fileName ) ) {
                        String url = uploadPath + fileName;
                        if ( !validUrls.contains( url ) ) {
                            if ( file.delete() )
                                LOG.warn( "Removing unattached {}", url );
                            remove( plan, url );
                        }
                    }
                }
                save( plan );
            }
        }

        private void load( Plan plan ) throws FileNotFoundException {
            Properties digests = new Properties();
            Reader in = new FileReader( new File( getUploadDirectory( plan ), digestsMapFile ) );
            try {
                digests.load( in );
            } catch ( IOException e ) {
                LOG.error( "Error while reading file map. Some attachments may be lost", e );
            } finally {
                try {
                    in.close();
                } catch ( IOException e ) {
                    LOG.error( "Unable to close file map.", e );
                }
            }

            synchronized ( documentMap ) {
                for ( String url : digests.stringPropertyNames() )
                    if ( exists( plan, url ) )
                        documentMap.put( url,
                                         new FileDocument( new File( getUploadDirectory( plan ), url ),
                                                           url,
                                                           digests.getProperty( url ) ) );
            }
        }

        @Override
        public void aboutToProductize( Plan devPlan ) {
            try {
                PlanDao planDao = planManager.getDao( devPlan );
                removeUnattached( devPlan, planDao.findAllAttached() );
            } catch ( IOException e ) {
                LOG.error( "Unable to clean up attachments for plan " + devPlan.getUri(), e );
            }
        }

        @Override
        public void productized( Plan plan ) {
        }

        @Override
        public void created( Plan devPlan ) {
        }

        @Override
        public void loaded( PlanDao planDao ) {
            Plan plan = planDao.getPlan();
            try {
                load( plan );
                removeUnattached( plan, planDao.findAllAttached() );
                reloadTags( plan );

            } catch ( FileNotFoundException ignored ) {
                LOG.debug( "No file digests found for plan " + plan.getUri() );
            } catch ( IOException e ) {
                LOG.warn( "Unable to load attachments for plan " + plan.getUri(), e );
            }
        }

        @Override
        public void aboutToUnload( PlanDao planDao ) {
            try {
                save( planDao.getPlan() );
            } catch ( IOException e ) {
                LOG.error( "Unable to save attachments for plan " + planDao.getPlan().getUri(), e );
            }
        }
    }
}
