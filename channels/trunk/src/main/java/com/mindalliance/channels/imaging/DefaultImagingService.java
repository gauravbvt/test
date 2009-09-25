package com.mindalliance.channels.imaging;

import com.mindalliance.channels.AttachmentManager;
import com.mindalliance.channels.ImagingService;
import com.mindalliance.channels.model.ModelObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import sun.awt.image.BufferedImageGraphicsConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Default imaging service.
 * Tip of the hat to http://www.componenthouse.com/article-20 for resize algo
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 18, 2009
 * Time: 1:48:49 PM
 */
public class DefaultImagingService implements ImagingService {

    /**
     * Sizes of icons to generate.
     */
    private static final int[] ICON_HEIGHTS = {32, 56, 72, 84, 100};
    /**
     * Attachment manager.
     */
    private AttachmentManager attachmentManager;
    /**
     * The directory to keep files in.
     */
    //private Resource uploadDirectory;
    /**
     * The webapp-relative path to file URLs.
     */
    //private String uploadPath = "";
    /**
     * Directory for generated icons.
     */
    private Resource iconDirectory;
    /**
     * The webapp-relative path to generated icons.
     */
    private String iconPath;
    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultImagingService.class );

    public DefaultImagingService() {
    }


    public void setAttachmentManager( AttachmentManager attachmentManager ) {
        this.attachmentManager = attachmentManager;
    }


    public Resource getIconDirectory() {
        return iconDirectory;
    }


    public void setIconDirectory( Resource iconDirectory ) {
        this.iconDirectory = iconDirectory;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath( String iconPath ) {
        this.iconPath = iconPath;
    }

    /**
     * {@inheritDoc}
     */
    public int[] getImageSize( String url ) {
        int[] size = new int[2];
        BufferedImage image = getImage( url );
        if ( image != null ) {
            size[0] = image.getWidth();
            size[1] = image.getHeight();
        }
        return size;
    }

    private BufferedImage getImage( String url ) {
        try {
            BufferedImage image;
            if ( isFileDocument( url ) ) {
                image = ImageIO.read( getImageFile( url ) );
            } else {
                image = ImageIO.read( new URL( url ) );
            }
            return image;
        } catch ( IOException e ) {
            LOG.warn( "Failed to retrieve image at " + url );
            return null;
        }
    }

    private boolean isFileDocument( String url ) {
        return url.startsWith( attachmentManager.getUploadPath() );
    }


    private File getImageFile( String url ) {
        return new File(
                attachmentManager.getUploadDirectory(),
                url.replaceFirst( attachmentManager.getUploadPath(), "" ));
    }

    /**
     * Get the location of the uploaded files.
     *
     * @return a directory
     */
    public File uploadDirectory() {
        return attachmentManager.getUploadDirectory();
    }

    /**
     * {@inheritDoc}
     */
    public boolean iconize( String url, ModelObject modelObject ) {
        try {
            BufferedImage image = getImage( url );
            if ( image == null ) throw new Exception( "Image not found at " + url );
            int height = ICON_HEIGHTS[0];
            int width = height * image.getWidth() / image.getHeight();
            BufferedImage resized = resize( image, width, height );
            File iconFile = getIconFile( modelObject );
            ImageIO.write( resized, "png", iconFile );
            createNumberedIcons( resized, width, modelObject );
        } catch ( Exception e ) {
            LOG.warn( "Failed to iconize uploaded image at " + url + " (" + e.getMessage() + ")");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void deiconize( ModelObject modelObject ) {
        File iconFile = getIconFile( modelObject );
        iconFile.delete();
        for ( int i = 1; i < ICON_HEIGHTS.length; i++ ) {
            iconFile = getIconFile( modelObject, i );
            iconFile.delete();
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getIconPath( ModelObject modelObject ) {
        File iconFile = getIconFile( modelObject );
        if ( iconFile.exists() ) {
            String path = iconFile.getAbsolutePath();
            return path.substring(
                    0,
                    path.indexOf( ".png" )
            );
        } else {
            if ( modelObject.hasImage() ) {
                boolean success = iconize( modelObject.getImageUrl(), modelObject );
                if (success)
                    return getIconPath( modelObject );
                else
                    return null;
            } else {
                return null;
            }
        }
    }

    private String getIconsSubDirName( ModelObject modelObject ) {
        return modelObject.getClass().getSimpleName().toLowerCase();
    }

    private void createNumberedIcons( BufferedImage resized, int width, ModelObject modelObject ) throws IOException {
        for ( int i = 1; i < ICON_HEIGHTS.length; i++ ) {
            createNumberedIcon( resized, modelObject, width, ICON_HEIGHTS[i], i );
        }
    }

    private void createNumberedIcon(
            BufferedImage resized,
            ModelObject modelObject,
            int width,
            int height,
            int number ) throws IOException {
        BufferedImage icon = new BufferedImage( width, height, BufferedImage.TRANSLUCENT );
        icon.createGraphics();
        Graphics2D graphics = (Graphics2D) icon.getGraphics();
        // graphics.setColor( Color.WHITE );
        // graphics.fillRect( 0, 0, width, height );
        graphics.drawImage( resized, 0, 0, resized.getWidth(), resized.getHeight(), null );
        File iconFile = getIconFile( modelObject, number );
        ImageIO.write( icon, "png", iconFile );
        graphics.dispose();
    }

    private BufferedImage resize( BufferedImage image, int width, int height ) {
        BufferedImage modified = createCompatibleImage( image );
        modified = basicResize( modified, width * 4, height * 4 );
        modified = blurImage( modified );
        modified = basicResize( modified, width, height );
        return modified;
    }

    private BufferedImage basicResize( BufferedImage image, int width, int height ) {
        int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage resizedImage = new BufferedImage( width, height, type );
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setComposite( AlphaComposite.Src );
        graphics2D.setRenderingHint( RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR );
        graphics2D.setRenderingHint( RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY );
        graphics2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );
        graphics2D.drawImage( image, 0, 0, width, height, null );
        graphics2D.dispose();
        return resizedImage;
    }

    private BufferedImage createCompatibleImage( BufferedImage image ) {
        GraphicsConfiguration gc = BufferedImageGraphicsConfig.getConfig( image );
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage result = gc.createCompatibleImage( w, h, Transparency.TRANSLUCENT );
        Graphics2D g2 = result.createGraphics();
        g2.drawRenderedImage( image, null );
        g2.dispose();
        return result;
    }

    private BufferedImage blurImage( BufferedImage image ) {
        float ninth = 1.0f / 9.0f;
        float[] blurKernel = {
                ninth, ninth, ninth,
                ninth, ninth, ninth,
                ninth, ninth, ninth
        };
        Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
        map.put( RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR );
        map.put( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        map.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        RenderingHints hints = new RenderingHints( map );
        BufferedImageOp op = new ConvolveOp( new Kernel( 3, 3, blurKernel ), ConvolveOp.EDGE_NO_OP, hints );
        return op.filter( image, null );
    }

    private File getIconFile( ModelObject modelObject ) {
        String path = getIconDirectory( modelObject ).getAbsolutePath()
                + File.separator
                + sanitize( modelObject.getName() )
                + ".png";
        return new File( path );
    }

    private File getIconFile( ModelObject modelObject, int index ) {
        String path = getIconDirectory( modelObject ).getAbsolutePath()
                + File.separator
                + sanitize( modelObject.getName() )
                + index
                + ".png";
        return new File( path );
    }

    private String sanitize( String fileName ) {
        return fileName.replaceAll( File.separator, "" );
    }

    /**
     * Get the location of the generated icons for a given model object.
     *
     * @param modelObject a model object
     * @return a directory
     */
    private File getIconDirectory( ModelObject modelObject ) {
        try {
            File iconDir = iconDirectory.getFile();
            if ( !iconDir.exists() ) {
                iconDir.mkdir();
            }
            String subDirName = getIconsSubDirName( modelObject );
            File subDir = new File( iconDir.getAbsolutePath() + File.separator + subDirName );
            if ( !subDir.exists() ) {
                subDir.mkdir();
            }
            return subDir;
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }


}
