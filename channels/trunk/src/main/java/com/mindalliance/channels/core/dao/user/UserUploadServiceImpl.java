package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.engine.imaging.ImagingService;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( UserUploadServiceImpl.class );

    private static final String CACHE = "cache";

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
    public boolean uploadUserPhoto( ChannelsUser user, FileUpload upload ) {
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
            imagingService.squarify(
                    outputFile.getAbsolutePath(),
                    new File( getSquaredDirectory(), fileName + "_squared.png" ) );
            LOG.info( "Uploaded file into ", outputFile );
            return true;
        } catch ( IOException e ) {
            LOG.warn( "Failed to upload user photo " + upload.getClientFileName(), e );
            return false;
        }
    }

    @Override
    public File findSquaredUserPhoto( String fileName ) {
        if ( fileName != null ) {
            return new File( getSquaredDirectory(), fileName + "_squared.png" );
        } else {
            return null;
        }
    }

/*    @Override
    public boolean removeUserPhoto( ChannelsUser user ) {
        boolean exists = user.getPhoto() != null;
        user.setPhoto( null );
*//*
        if ( exists ) {
            try {
                File squared = findSquaredUserPhoto( user.getPhoto() );
                if ( squared != null && squared.exists() ) {
                    if ( squared.delete() ) {
                        LOG.info( squared.getAbsolutePath() + " deleted" );
                    }
                }
                String filePath = getUserPhotoUploadPath( user );
                if ( filePath != null ) {
                    File photo = new File( filePath );
                    if ( photo.exists() ) {
                        if ( photo.delete() ) {
                            LOG.info( photo.getAbsolutePath() + " deleted" );
                        }
                    }
                }
            } catch ( Exception e ) {
                LOG.warn( "Failed to delete photo of user " + user.getUsername(), e );
            } finally {
                user.setPhoto( null );
            }
        }
*//*
        return exists;
    }*/

    private String getUserPhotoUploadPath( ChannelsUser user ) {
        String fileName = user.getPhoto(  );
        return fileName != null ? getUserPhotoUploadDirPath() + File.separator + fileName + ".png" : null;
    }

    private String getUserPhotoUploadDirPath() {
        String dirPath = getPublicDir().getAbsolutePath()
                + File.separator + getUserPhotoUpload();
        File dir = new File( dirPath );
        if ( dir.mkdirs() ) {
            LOG.info( "Created " + dirPath );
        }
        return dir.getAbsolutePath();
    }

    @Override
    public String getSquareUserIconURL( ChannelsUser user ) {
        File squareIconFile = getSquaredUserFile( user );
        if ( squareIconFile != null ) {
            if ( squareIconFile.exists() ) {
                return getUserPhotoUpload()
                        + "/" + user.getPhoto()
                        + "/" + Long.toString( System.currentTimeMillis() );     // seed to prevent caching
            } else {
                return null;
            }
        }
        return null;
    }

    private File getSquaredUserFile( ChannelsUser user ) {
        String fileName = user.getPhoto( );
        if ( fileName != null )
            return new File( getSquaredDirectory(), fileName + "_squared.png" );
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
