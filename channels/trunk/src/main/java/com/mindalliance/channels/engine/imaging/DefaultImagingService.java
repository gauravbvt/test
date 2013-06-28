package com.mindalliance.channels.engine.imaging;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.Assignments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import sun.awt.image.BufferedImageGraphicsConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Default imaging service.
 * Tip of the hat to http://www.componenthouse.com/article-20 for resize algo
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 18, 2009
 * Time: 1:48:49 PM
 */
public class DefaultImagingService implements ImagingService, InitializingBean {

    /**
     * Sizes of icons to generate.
     */
    private static final int[] ICON_HEIGHTS = {32, 56, 72, 84, 100, 116};

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
     * Directory for default icons.
     */
    private Resource imageDirectory;

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

    public Resource getImageDirectory() {
        return imageDirectory;
    }

    public void setImageDirectory( Resource imageDirectory ) {
        this.imageDirectory = imageDirectory;
    }

    public String getImageDirPath() {
        return imageDirPath();
    }

    @Override
    public File getIconDirectoryFile() throws IOException {
        return iconDirectory.getFile();
    }

    @Override
    public int[] getImageSize( CommunityService communityService, String url ) {
        int[] size = new int[2];
        size[0] = 0;
        size[1] = 0;
        try {
            BufferedImage image = getImage( communityService, url );
            size[0] = image.getWidth();
            size[1] = image.getHeight();
        } catch ( IOException e ) {
            LOG.warn( "Unable to get image size for " + url, e );
        }
        return size;
    }

    private BufferedImage getImage( CommunityService communityService, String url ) throws IOException {
        BufferedImage image = null;
        File uploadedFile = attachmentManager.getUploadedFile( communityService, url );
        if ( uploadedFile.exists() ) {
            image = isUploadedFileDocument( url )
                    ? ImageIO.read( uploadedFile )
                    : isFileDocument( url ) ? ImageIO.read( new File( url ) ) : ImageIO.read( new URL( url ) );
        }
        if ( image == null ) {
            LOG.warn( "No image at " + url );
            throw new IOException( "No image at " + url );
        }
        return image;
    }

    private static boolean isFileDocument( String url ) {
        return url.startsWith( File.separator );
    }

    private boolean isUploadedFileDocument( String url ) {
        return attachmentManager.isUploadedFileDocument( url );
    }

    @Override
    public boolean iconize( CommunityService communityService, String url, ModelObject modelObject ) {
        try {
            BufferedImage image = getImage( communityService, url );
            int height = ICON_HEIGHTS[0];
            int width = height * image.getWidth() / image.getHeight();

            BufferedImage resized = resize( image, width, height );
            ImageIO.write( resized, "png", getIconFile( communityService, modelObject, ".png" ) );
            createNumberedIcons( communityService, resized, width, modelObject );

        } catch ( IOException e ) {
            LOG.warn( "Failed to iconize uploaded image at " + url + " (" + e.getMessage() + ')' );
            return false;
        }
        return true;
    }

    private boolean squarify( CommunityService communityService, String url, ModelObject modelObject ) {
        try {
            BufferedImage image = ImageIO.read( new File( url ) );
           // BufferedImage image = getImage( communityService, url );
            BufferedImage icon = doSquarify( image );
            ImageIO.write( icon, "png", getIconFile( communityService, modelObject, "_squared.png" ) );
            return true;

        } catch ( IOException e ) {
            LOG.warn( "Failed to squarify image at " + url + " (" + e.getMessage() + ')', e );
            return false;
        }
    }

    private BufferedImage doSquarify( BufferedImage image ) {
        int width = image.getWidth();
        int height = image.getHeight();
        int max = Math.max( width, height );

        BufferedImage squared = new BufferedImage( max, max, Transparency.TRANSLUCENT );
        squared.createGraphics();
        Graphics2D graphics = (Graphics2D) squared.getGraphics();
        graphics.drawImage( image,
                ( max - width ) / 2,
                ( max - height ) / 2,
                image.getWidth(),
                image.getHeight(),
                null );
        graphics.dispose();
        return squared;
    }

    @Override
    public boolean squarify( String filePath, File iconFile ) {
        try {
            BufferedImage image = ImageIO.read( new File( filePath ) );
            BufferedImage icon = doSquarify( image );
            ImageIO.write( icon, "png", iconFile );
            return true;
        } catch ( IOException e ) {
            LOG.warn( "Failed to squarify image at " + filePath + " (" + e.getMessage() + ')' );
            return false;
        }

    }

    @Override
    public void deiconize( CommunityService communityService, ModelObject modelObject ) {
        try {
            File iconFile = getIconFile( communityService, modelObject, ".png" );
            if ( iconFile.delete() )
                LOG.debug( "Deleted {}", iconFile );
            iconFile = getIconFile( communityService, modelObject, "_squared.png" );
            if ( iconFile.delete() )
                LOG.debug( "Deleted {}", iconFile );

            for ( int i = 1; i < ICON_HEIGHTS.length; i++ ) {
                File file = getIconFile( communityService, modelObject, i + ".png" );
                if ( file.delete() )
                    LOG.debug( "Deleted {}", file );
                file = getIconFile( communityService, modelObject, i + "_negated.png" );
                if ( file.delete() )
                    LOG.debug( "Deleted {}", file );
            }

        } catch ( IOException e ) {
            LOG.error( "Unable to deiconize object " + modelObject.getId(), e );
        }
    }

    private String getModelObjectIconsPath( CommunityService communityService, ModelObject modelObject ) {
        try {
            File iconFile = getIconFile( communityService, modelObject, ".png" );
            if ( iconFile.exists() ) {
                String path = iconFile.getAbsolutePath();
                return path.substring( 0, path.indexOf( ".png" ) );
            }

            if ( modelObject.hasImage() && iconize( communityService, modelObject.getImageUrl(), modelObject ) )
                return getModelObjectIconsPath( communityService, modelObject );

        } catch ( IOException e ) {
            LOG.trace( "Exception when getting " + modelObject.getId(), e );
        }

        return null;
    }

    @Override
    public String getSquareIconUrl( CommunityService communityService, ModelObject modelObject ) {
        try {
            File squareIconFile = getIconFile( communityService, modelObject, "_squared.png" );
            if ( squareIconFile.exists() ) {
                String prefix = getIconFilePrefix( communityService );

                String absolutePath = squareIconFile.getAbsolutePath();
                String relPath = absolutePath.substring( prefix.length() );
                String encodedPath = relPath.replaceAll(
                        File.separatorChar == '\\'
                                ? "\\\\" :
                                File.separator, SEPARATOR );

                return "icons" + File.separator + encodedPath;
            }

            String path = getModelObjectIconsPath( communityService, modelObject );
            if ( path != null && squarify( communityService, path + ".png", modelObject ) )
                return getSquareIconUrl( communityService, modelObject );

        } catch ( IOException e ) {
            LOG.warn( "Failed to get square icon url", e );
        }

        return null;
    }

    private String getIconFilePrefix( CommunityService communityService ) throws IOException {
        return iconDirectory.getFile().getAbsolutePath()
                + File.separator + getFlattenedContextUri( communityService ) + File.separator;
    }

    @Override
    public File findIcon( CommunityService communityService, String encodedPath ) throws IOException {
        String decodedPath = URLEncoder.encode( encodedPath, "UTF-8" );
        decodedPath = decodedPath.replaceAll( SEPARATOR, File.separator );
        return new File( getIconFilePrefix( communityService ) + decodedPath );
    }

    private void createNumberedIcons( CommunityService communityService, BufferedImage resized, int width, ModelObject modelObject )
            throws IOException {

        for ( int i = 1; i < ICON_HEIGHTS.length; i++ )
            createNumberedIcon( communityService, resized, modelObject, width, ICON_HEIGHTS[i], i );
    }

    private void createNumberedIcon( CommunityService communityService,
                                     BufferedImage resized,
                                     ModelObject modelObject,
                                     int width,
                                     int height,
                                     int number )
            throws IOException {

        BufferedImage icon = new BufferedImage( width, height, BufferedImage.TRANSLUCENT );
        icon.createGraphics();
        Graphics2D graphics = (Graphics2D) icon.getGraphics();
        graphics.drawImage( resized, 0, 0, resized.getWidth(), resized.getHeight(), null );
        ImageIO.write( icon, "png", getIconFile( communityService, modelObject, number + ".png" ) );
        String negatedIconUrl = getImageDirectory().getFile().getAbsolutePath() + NEGATED_ICON_URL;
        BufferedImage negatedIcon = ImageIO.read( new File( negatedIconUrl ) );
        graphics.drawImage( negatedIcon, 0, 0, null );
        ImageIO.write( icon, "png", getIconFile( communityService, modelObject, number + NEGATED + ".png" ) );
        graphics.dispose();
    }

    private static BufferedImage resize( BufferedImage image, int width, int height ) {
        return basicResize( createCompatibleImage( image ), width, height );
    }

    private static BufferedImage basicResize( BufferedImage image, int width, int height ) {
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

    private static BufferedImage createCompatibleImage( BufferedImage image ) {
        GraphicsConfiguration gc = BufferedImageGraphicsConfig.getConfig( image );
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage result = gc.createCompatibleImage( w, h, Transparency.TRANSLUCENT );
        Graphics2D g2 = result.createGraphics();
        g2.drawRenderedImage( image, null );
        g2.dispose();
        return result;
    }

    private File getIconFile( CommunityService communityService, ModelObject modelObject, String suffix ) throws IOException {
        return new File( getIconsDirectoryFor( communityService, modelObject ),
                sanitizeFileName( modelObject.getName() ) + suffix );
    }


    private static String sanitizeFileName( String fileName ) {

        String sanitized = fileName
                .replaceAll( File.separator.equals( "\\" ) ? "\\\\" : File.separator, "" )
                .replaceAll( " ", "_" );

        try {
            sanitized = URLEncoder.encode( sanitized, "UTF-8" );
        } catch ( UnsupportedEncodingException ignored ) {
            LOG.error( "UTF-8 encoding not supported!" ); // should never happen
        }

        return sanitized;
    }

    /**
     * Return a "directory-safe" equivalent name.
     *
     * @param name original name
     * @return safe version
     */
    public static String uriToDirName( String name ) {
        return name.replaceAll( "\\W", "_" );
    }

    /**
     * Get the location of the generated icons for a given model object.
     *
     * @param modelObject a model object
     * @return a directory
     * @throws IOException on errors
     */
    private File getIconsDirectoryFor( CommunityService communityService, ModelObject modelObject ) throws IOException {
        return new File( getIconsAbsolutePathFor( communityService, modelObject ) );
    }

    private File getBaseIconDirectory() throws IOException {
        File iconDir = iconDirectory.getFile();
        if ( iconDir.mkdirs() )
            LOG.info( "Created {}", iconDir );

        return iconDir;
    }

    private String getIconsAbsolutePathFor( CommunityService communityService, ModelObject modelObject ) throws IOException {
        File subDir = new File( getBaseIconDirectory(), getFlattenedContextUri( communityService ) );
        if ( subDir.mkdir() )
            LOG.info( "Created {}", subDir );

        File moSubDir = new File( subDir, modelObject.getClass().getSimpleName().toLowerCase() );
        if ( moSubDir.mkdir() )
            LOG.info( "Created {}", moSubDir );

        return moSubDir.getAbsolutePath();
    }

    private static String getFlattenedContextUri( CommunityService communityService ) {
        return uriToDirName( communityService.isForDomain()
                ? communityService.getPlan().getVersionUri()
                : communityService.getPlanCommunity().getUri() );
    }

    @Override
    public String findIconName( CommunityService communityService, ModelObject modelObject ) {
        String iconName = getModelObjectIconsPath( communityService, modelObject );
        if ( iconName != null )
            return iconName;

        return imageDirPath() + '/' +
                ( modelObject instanceof Actor
                        ? ( (Actor) modelObject ).isSystem() ? "system" : "person"
                        : modelObject instanceof Role ? "role"
                        : modelObject instanceof Organization ? "organization"
                        : modelObject instanceof Agency ? "organization"
                        : modelObject instanceof Plan ? "plan"
                        : modelObject instanceof com.mindalliance.channels.core.model.Event ? "event"
                        : modelObject instanceof Place ? "place"
                        : modelObject instanceof TransmissionMedium ? "medium"
                        : modelObject instanceof InfoFormat ? "info_format"
                        : modelObject instanceof InfoProduct ? "info_product"
                        : modelObject instanceof Function ? "function"
                        : "unknown" );
    }

    private String imageDirPath() {
        try {
            return imageDirectory.getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new IllegalStateException( e );
        }
    }

    @Override
    public String findIconName( CommunityService communityService, Assignment assignment ) {
        String iconName = null;
        Actor actor = assignment.getActor();
        if ( actor != null && !actor.isUnknown() ) {
            iconName = findSpecificIcon( communityService, actor, new ArrayList<Actor>() );
        }
        if ( iconName == null ) {
            Role role = assignment.getRole();
            if ( role != null && !role.isUnknown() ) {
                iconName = findSpecificIcon( communityService, role, new ArrayList<Role>() );
            }
        }
        if ( iconName == null ) {
            Organization org = assignment.getOrganization();
            if ( org != null && !org.isUnknown() ) {
                iconName = findSpecificIcon( communityService, org, new ArrayList<Organization>() );
            }
        }
        return iconName == null
                ? findGenericIconName( assignment.getPart() )
                : iconName;
    }

    @Override
    public String findIconName( CommunityService communityService, Specable part, Assignments assignments ) {

        Assignments partAssignments = assignments.withAll( part );
        String specific = findSpecificIcon( communityService, part, partAssignments );
        return specific == null ? findGenericIconName( part )
                : specific;
    }

    private String findSpecificIcon( CommunityService communityService, Specable part, Assignments assignments ) {

        String actorIcon = findSpecificIcon( communityService, part.getActor(), assignments.getActualActors() );
        if ( actorIcon != null )
            return actorIcon;

        String roleIcon = findSpecificIcon( communityService, part.getRole(), assignments.getRoles() );
        return roleIcon == null ? findSpecificOrgIcon( communityService, part.getOrganization(),
                assignments.getOrganizations() )
                : roleIcon;
    }

    private String findSpecificOrgIcon( CommunityService communityService, Organization spec, List<Organization> candidates ) {

        Organization organization = candidates.size() == 1 ? candidates.get( 0 ) : spec;
        if ( organization != null ) {
            String s = getModelObjectIconsPath( communityService, organization );
            if ( s != null )
                return s;

            List<Organization> ancestors = organization.ancestors();
            for ( Organization ancestor : ancestors ) {
                String path = getModelObjectIconsPath( communityService, ancestor );
                if ( path != null )
                    return path;
            }

            for ( ModelEntity type : organization.getAllTypes() ) {
                String path = getModelObjectIconsPath( communityService, type );
                if ( path != null )
                    return path;
            }
        }

        return null;
    }

    private <T extends ModelEntity> String findSpecificIcon( CommunityService communityService, T spec, List<T> candidates ) {
        T entity = candidates.size() == 1 ? candidates.get( 0 ) : spec;

        if ( entity != null ) {
            String s = getModelObjectIconsPath( communityService, entity );
            if ( s != null )
                return s;

            for ( ModelEntity type : entity.getAllTypes() ) {
                String path = getModelObjectIconsPath( communityService, type );
                if ( path != null )
                    return path;
            }
        }

        return null;
    }

    private String findGenericIconName( Specable specable ) {
        Actor actor = specable.getActor();
        return imageDirPath() + '/' + (
                actor != null ? actor.isSystem() ? "system" : "person"
                        : specable.getRole() != null ? "role"
                        : specable.getOrganization() == null ? "unknown"
                        : "organization" );
    }

    @Override
    public String tooComplexImagePath() {
        return imageDirPath() + '/' + "too_complex.png";
    }

    @Override
    public void afterPropertiesSet() {
        if ( iconDirectory == null || imageDirectory == null || attachmentManager == null )
            throw new IllegalStateException( "Need attachmentManager, icon and image directories" );
    }
}
