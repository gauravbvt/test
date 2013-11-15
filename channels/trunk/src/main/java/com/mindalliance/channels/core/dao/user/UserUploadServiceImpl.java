package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.engine.imaging.ImagingService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User upload service implementation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/18/12
 * Time: 9:09 AM
 */
public class UserUploadServiceImpl implements UserUploadService {

    @Autowired
    private ImagingService imagingService;

    @Autowired
    private UserRecordService userInfoService;

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( UserUploadServiceImpl.class );

    private static final String CACHE = "cache";

    private static final String[] EXT = {"png"};

    private Resource publicDirectory;
    private String userPhotoUpload;
    private String userPhotoURL;

    public UserUploadServiceImpl() {
    }

    public Resource getPublicDirectory() {
        return publicDirectory;
    }

    public void setPublicDirectory( Resource publicDirectory ) {
        this.publicDirectory = publicDirectory;
    }

    public String getUserPhotoUpload() {
        return userPhotoUpload;
    }

    public void setUserPhotoUpload( String userPhotoUpload ) {
        this.userPhotoUpload = userPhotoUpload;
    }

    public String getUserPhotoURL() {
        return userPhotoURL;
    }

    public void setUserPhotoURL( String userPhotoURL ) {
        this.userPhotoURL = userPhotoURL;
    }

    private File getPublicDir() {
        try {
            File publicDir = getPublicDirectory().getFile();
            if ( publicDir.mkdirs() ) {
                LOG.info( publicDir.getAbsolutePath() + " created" );
            }
            return publicDir;
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public boolean uploadUserPhoto( ChannelsUser user, FileUpload upload, CommunityService communityService ) {
        if ( user.getPhoto() != null ) {
            LOG.warn( "Photo already uploaded for " + user.getUsername() );
            return false;
        }
        try {
            BufferedImage image = ImageIO.read( upload.getInputStream() );
            String fileName = user.getUsername() + "_" + Long.toString( System.currentTimeMillis() );
            user.setPhoto( fileName );
            File outputFile = new File( getUserPhotoUploadDirPath() + File.separator + fileName + ".png" );
            ImageIO.write( image, "png", outputFile );
            imagingService.squarifyAndIconize(
                    outputFile.getAbsolutePath(),
                    new File( getSquaredDirectory(), fileName + SQUARED + ".png" ),
                    ICON );
            LOG.info( "Uploaded file into ", outputFile );
            return true;
        } catch ( IOException e ) {
            LOG.warn( "Failed to upload user photo " + upload.getClientFileName(), e );
            return false;
        }
    }

    @Override
    public File findSquaredUserPhoto( String photoName ) {
        if ( photoName != null ) {
            return new File( getSquaredDirectory(), photoName + SQUARED + ".png" );
        } else {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void cleanUpPhotos() {
        LOG.info( "Cleaning up uploaded photos" );
        int count = 0;
        Set<String> allPhotos = new HashSet<String>();
        for ( ChannelsUser user : userInfoService.getAllEnabledUsers() ) {
            String photo = user.getPhoto();
            if ( photo != null )
                allPhotos.add( photo );
        }
        for ( File photoFile : (Collection<File>) FileUtils.listFiles( getUserPhotoUploadDir(), EXT, false ) ) {
            String fileName = StringUtils.removeEnd( photoFile.getName(), ".png" );
            if ( !allPhotos.contains( fileName ) ) {
                if ( photoFile.delete() ) count++;
            }
        }
        for ( File photoFile : (Collection<File>) FileUtils.listFiles( getSquaredDirectory(), EXT, false ) ) {
            String photoFileName = photoFile.getName();
            String fileName = photoFileName.substring( 0, photoFileName.lastIndexOf( SQUARED ) );
            if ( !allPhotos.contains( fileName ) ) {
                if ( photoFile.delete() ) count++;
            }
        }
        LOG.info( "Deleted " + count + " unused photos" );
    }

    private String getUserPhotoUploadPath( ChannelsUser user ) {
        String fileName = user.getPhoto();
        return fileName != null ? getUserPhotoUploadDirPath() + File.separator + fileName + ".png" : null;
    }

    private String getUserPhotoUploadDirPath() {
        return getUserPhotoUploadDir().getAbsolutePath();
    }

    private File getUserPhotoUploadDir() {
        String dirPath = getPublicDir().getAbsolutePath()
                + File.separator + getUserPhotoUpload();
        File dir = new File( dirPath );
        if ( dir.mkdirs() ) {
            LOG.info( "Created " + dirPath );
        }
        return dir;
    }


    @Override
    public String getSquareUserIconURL( ChannelsUser user ) {
        String photo = user.getPhoto();
        if ( photo != null ) {
            return getUserPhotoUpload() + File.separator + photo;
        } else {
            return null;
        }
    }

    private File getSquaredIconUserFile( ChannelsUser user ) {
        String fileName = user.getPhoto();
        if ( fileName != null )
            return new File( getSquaredDirectory(), fileName + SQUARED + ICON + ".png" );
        else
            return null;
    }

    private File getSquaredDirectory() {
        File squaredDir = new File( getUserPhotoUploadDirPath() + File.separator + CACHE );
        if ( squaredDir.mkdirs() ) {
            LOG.info( squaredDir.getAbsolutePath() + " created" );
        }
        return squaredDir;
    }


}
