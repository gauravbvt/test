/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.attachments;

import com.mindalliance.channels.core.Attachable;
import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.Attachment.Type;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.Upload;
import com.mindalliance.channels.core.community.CommunityDao;
import com.mindalliance.channels.core.community.CommunityListener;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.ModelDao;
import com.mindalliance.channels.core.dao.ModelListener;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.model.AttachmentImpl;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Tag;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private ModelManager modelManager;

    /**
     * Plan community manager.
     */
    private PlanCommunityManager planCommunityManager;

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
     *
     * @param modelManager the plan manager
     */
    public void setModelManager( ModelManager modelManager ) {
        if ( modelManager == null )
            throw new IllegalArgumentException();

        this.modelManager = modelManager;
        modelManager.addListener( new AModelListener() );
    }

    public void setPlanCommunityManager( PlanCommunityManager planCommunityManager ) {
        if ( planCommunityManager == null )
            throw new IllegalArgumentException();
        this.planCommunityManager = planCommunityManager;
        planCommunityManager.addListener( new ACommunityListener() );
    }

    private void reloadTags( CollaborationModel collaborationModel ) {
        collaborationModel.setTags( new ArrayList<Tag>() );
        for ( Attachment attachment : collaborationModel.getAttachments() ) {
            String url = attachment.getUrl();
            if ( attachment.isTags() )
                reloadTagsFromUrl( collaborationModel, url, new TagLoader( collaborationModel ) );
        }
    }

    /**
     * Remove a mapped url for a given plan.
     *
     * @param communityService a community service
     * @param url              the url
     */
    @Override
    public void remove( CommunityService communityService, String url ) {
        remove( url );
    }

    private void remove( CollaborationModel collaborationModel, String url ) {
        remove(  url  );
    }

    private void remove( String url ) {
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

    private File createFile( File uploadDirectory, String name ) {
        String truncatedName = StringUtils
                .reverse( StringUtils.reverse( name ).substring( 0, Math.min( name.length(), maxLength ) ) );
        // String idealName = escape( truncatedName );
        File result = new File( uploadDirectory, truncatedName );
        int i = 0;
        while ( result.exists() ) {
            String actual = ++i + "_" + truncatedName;
            result = new File( uploadDirectory, actual );
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
                if ( document.isDuplicate( prior ) ) {
                    document.delete();
                    return prior;
                }

            return document;
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

    private void save( CommunityService communityService ) throws IOException  {
        if ( communityService.isForDomain() )
            save( communityService.getPlan() );
        else
            save( communityService.getPlanCommunity() );
    }

    private void save( PlanCommunity planCommunity ) throws IOException {
        saveFor( new File( getUploadDirectory( planCommunity ), digestsMapFile ) );
    }

    private void save( CollaborationModel collaborationModel ) throws IOException {
        saveFor( new File( getUploadDirectory( collaborationModel ), digestsMapFile ) );
    }

    private void saveFor( File file ) throws IOException {
        Properties digests = new Properties();
        synchronized ( documentMap ) {
            for ( Entry<String, FileDocument> entry : documentMap.entrySet() )
                digests.setProperty( entry.getKey(), entry.getValue().getDigest() );
        }
        LOG.debug( "Saving attachment digests into " + file.getAbsolutePath() );
        Writer out = new FileWriter( file );
        try {
            digests.store( out, " File digests. Do not edit." );

        } finally {
            out.close();
        }
    }

    @Override
    public boolean exists( CommunityService communityService, String url ) {
        return communityService.isForDomain()
                ? exists( communityService.getPlan(), url )
                : exists( communityService.getPlanCommunity(), url );
    }

    @Override
    public boolean exists( PlanCommunity planCommunity, String url ) {
        return isValidUrl( url ) && ( !isFileDocument( url ) || isUploaded( planCommunity, url ) );
    }


    @Override
    public boolean exists( CollaborationModel collaborationModel, String url ) {
        return isValidUrl( url ) && ( !isFileDocument( url ) || isUploaded( collaborationModel, url ) );
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

    private boolean isUploaded( PlanCommunity planCommunity, final String url ) {
        return isUploaded( getUploadDirectory( planCommunity ), url );
    }

    private boolean isUploaded( CollaborationModel collaborationModel, final String url ) {
        return isUploaded( getUploadDirectory( collaborationModel ), url );
    }

    private boolean isUploaded( File directory, final String url ) {
        return directory.listFiles( new FilenameFilter() {
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
    public Attachment upload( CommunityService communityService, Upload upload ) {

        try {
            return doUpload( communityService, upload );

        } catch ( NoSuchAlgorithmException e ) {
            LOG.error( "System does not support SHA digests", e );
        } catch ( IOException e ) {
            LOG.warn( "Unable to download attachment", e );
        }

        return null;
    }

    private Attachment doUpload( CommunityService communityService, Upload upload ) throws NoSuchAlgorithmException, IOException {
        AttachmentImpl attachment = null;

        MessageDigest messageDigest = MessageDigest.getInstance( "SHA" );
        InputStream in = new BufferedInputStream( new DigestInputStream( upload.getInputStream(), messageDigest ) );
        try {
            FileDocument fileDocument = upload( in, messageDigest, createFile( getUploadDirectory( communityService ),
                    upload.getFileName() ) );

            synchronized ( documentMap ) {
                FileDocument actual = resolve( fileDocument );
                documentMap.put( actual.getUrl(), actual );
                attachment = new AttachmentImpl( actual.getUrl(), upload.getSelectedType() );
                attachment.setName( upload.getName() );
                save( communityService );
            }

        } finally {
            in.close();
        }

        return attachment;
    }

    private FileDocument upload( InputStream in, MessageDigest messageDigest, File outputFile ) throws IOException {

        BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( outputFile ) );
        try {
            int count;
            do {
                count = in.read();
                if ( count >= 0 )
                    out.write( count );
            } while ( count >= 0 );
            LOG.info( "Uploaded file into ", outputFile );

            return new FileDocument( outputFile,
                    uploadPath + outputFile.getName(),
                    URLEncoder.encode( new String( messageDigest.digest() )
                            .replaceAll( ",", "\\u002c" ), "UTF-8" ) );

        } finally {
            out.close();
        }
    }

    @Override
    public String getLabel( CommunityService communityService, Attachment attachment ) {
        FileDocument fileDocument = documentMap.get( attachment.getUrl() );
        return attachment.getName().isEmpty() ? fileDocument == null ? attachment.getUrl()
                : fileDocument.getFile().getName()
                : attachment.getName();
    }

    @Override
    public File getUploadDirectory( CommunityService communityService ) {
        return communityService.isForDomain()
                ? getUploadDirectory( communityService.getPlan() )
                : getUploadDirectory( communityService.getPlanCommunity() );
    }

    @Override
    public File getUploadDirectory( CollaborationModel collaborationModel ) {
        return getUploadDirectory( modelManager.getVersionDirectory( collaborationModel ) );
    }

    @Override
    public File getUploadDirectory( String planCommunityUri ) {
        return getUploadDirectory( planCommunityManager.getPlanCommunity( planCommunityUri ) );
    }

    private File getUploadDirectory( PlanCommunity planCommunity ) {
        return getUploadDirectory(  planCommunityManager.getCommunityDirectory( planCommunity ) );
    }

    private File getUploadDirectory( File contextDirectory ) {
        File uploadsDir = new File( contextDirectory, uploadPath );
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
    public File getUploadedFile( CommunityService communityService, String relativePath ) {
        return communityService.isForDomain()
                ? getUploadedFile( communityService.getPlan(), relativePath )
                : getUploadedFile( communityService.getPlanCommunity(), relativePath );
    }

    private File getUploadedFile( PlanCommunity planCommunity, String relativePath ) {
        return new File( getUploadDirectory( planCommunity ), relativePath.replaceFirst( uploadPath, "" ) );
    }

    @Override
    public File getUploadedFile( CollaborationModel collaborationModel, String planRelativePath ) {
        return new File( getUploadDirectory( collaborationModel ), planRelativePath.replaceFirst( uploadPath, "" ) );
    }

    private void reloadTagsFromUrl( CollaborationModel collaborationModel, String url, Loader loader ) {
        BufferedReader in = null;
        try {

            in = new BufferedReader( new InputStreamReader(
                    isUploadedFileDocument( url ) ? new FileInputStream( getUploadedFile( collaborationModel, url ) )
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

        if ( attachable instanceof CollaborationModel && ( attachment.isTags() ) )
            reloadTags( (CollaborationModel) attachable );
        LOG.debug( "Removed " + attachment + " from " + attachable );
    }

    @Override
    public void addAttachment( Attachment attachment, Attachable attachable ) {
        attachable.addAttachment( attachment );
        if ( attachable instanceof CollaborationModel && ( attachment.isTags() ) )
            reloadTags( (CollaborationModel) attachable );
        LOG.debug( "Added " + attachment + " from " + attachable );
    }

    @Override
    public void afterPropertiesSet() {
        if ( modelManager == null )
            throw new IllegalArgumentException( "modelManager must be set" );
    }

    /**
     * Plan manager hook to react on plan changes.
     */
    private class AModelListener implements ModelListener {

        private File[] getAttachedFiles( CollaborationModel collaborationModel ) {
            return getUploadDirectory( collaborationModel ).listFiles();
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

        private void removeUnattached( CollaborationModel collaborationModel, List<String> validUrls ) throws IOException {
            File[] files = getAttachedFiles( collaborationModel );
            if ( files != null ) {
                for ( File file : files ) {
                    String fileName = file.getName();
                    if ( isReserved( fileName ) ) {
                        String url = uploadPath + fileName;
                        if ( !validUrls.contains( url ) ) {
                            if ( file.delete() )
                                LOG.warn( "Removing unattached {}", url );
                            remove( collaborationModel, url );
                        }
                    }
                }
                save( collaborationModel );
            }
        }

        private void load( CollaborationModel collaborationModel ) throws FileNotFoundException {
            Properties digests = new Properties();
            Reader in = new FileReader( new File( getUploadDirectory( collaborationModel ), digestsMapFile ) );
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
                    if ( exists( collaborationModel, url ) )
                        documentMap.put( url,
                                new FileDocument( new File( getUploadDirectory( collaborationModel ), url ),
                                        url,
                                        digests.getProperty( url ) ) );
            }
        }

        @Override
        public void aboutToProductize( CollaborationModel devCollaborationModel ) {
            try {
                ModelDao modelDao = modelManager.getDao( devCollaborationModel );
                removeUnattached( devCollaborationModel, modelDao.findAllAttached() );
            } catch ( IOException e ) {
                LOG.error( "Unable to clean up attachments for model " + devCollaborationModel.getUri(), e );
            }
        }

        @Override
        public void productized( CollaborationModel collaborationModel ) {
        }

        @Override
        public void created( CollaborationModel devCollaborationModel ) {
        }

        @Override
        public void loaded( ModelDao modelDao ) {
            CollaborationModel collaborationModel = modelDao.getCollaborationModel();
            try {
                load( collaborationModel );
                removeUnattached( collaborationModel, modelDao.findAllAttached() );
                reloadTags( collaborationModel );

            } catch ( FileNotFoundException ignored ) {
                LOG.debug( "No file digests found for model " + collaborationModel.getUri() );
            } catch ( IOException e ) {
                LOG.warn( "Unable to load attachments for model " + collaborationModel.getUri(), e );
            }
        }

        @Override
        public void aboutToUnload( ModelDao modelDao ) {
            try {
                save( modelDao.getCollaborationModel() );
            } catch ( IOException e ) {
                LOG.error( "Unable to save attachments for model " + modelDao.getCollaborationModel().getUri(), e );
            }
        }

        @Override
        public void created( PlanCommunity planCommunity ) {
            // Do nothing
        }

        @Override
        public void loaded( CommunityDao communityDao ) {
            // Do nothing
        }
    }

    /**
     * Plan community manager hook to react on plan community changes.
     */
    private class ACommunityListener implements CommunityListener {

        @Override
        public void aboutToUnload( CommunityDao communityDao ) {
            try {
                save( communityDao.getPlanCommunity() );
            } catch ( IOException e ) {
                LOG.error( "Unable to save attachments for model " + communityDao.getPlanCommunity().getUri(), e );
            }
        }

        @Override
        public void created( PlanCommunity planCommunity ) {
            // do nothing
        }

        @Override
        public void loaded( CommunityDao communityDao ) {
            // do nothing - todo - remove unattached
        }
    }
}
