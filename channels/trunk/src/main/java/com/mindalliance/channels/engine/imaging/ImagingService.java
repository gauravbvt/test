package com.mindalliance.channels.engine.imaging;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.query.Assignments;

import java.io.File;
import java.io.IOException;

/**
 * An image transformation service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 18, 2009
 * Time: 1:25:37 PM
 */
public interface ImagingService {

    /**
     * Last file name part before extension denoting negation if present.
     */
    String NEGATED = "_negated";
    /**
     * URL of negated icon.
     */
    String NEGATED_ICON_URL = "/conceptual.png";

    public static final String SEPARATOR = "---";


    /**
     * Find the size of an image given its url.
     *
     *
     * @param communityService community service
     * @param url a string
     * @return an int array
     */
    int[] getImageSize( CommunityService communityService,  String url );

    /**
     * Whether the url points to a valid image.
     * @param url a url string
     * @param communityService a community service
     * @return a boolean
     */
    boolean isPicture( String url, CommunityService communityService );

    /**
     * Create/update PNG icons for model object from image at given url.
     *
     *
     * @param communityService community service
     * @param url         a string
     * @param modelObject a model object
     * @return true if successful
     */
    boolean iconize( CommunityService communityService,  String url, ModelObject modelObject );

    /**
     * Remove icons for model object.
     *
     * @param modelObject a model object
     */
    void deiconize( CommunityService communityService, ModelObject modelObject );

    /**
     * Return url of squared icon..
     *
     *
     * @param communityService community service
     * @param modelObject a model object
     * @return a string
     */
    String getSquareIconUrl( CommunityService communityService,  ModelObject modelObject );


    /**
     * Find icon name for given model object.
     *
     *
     *
     * @param communityService community service
     * @param modelObject   a model object
     * @return a string
     */
    String findIconName( CommunityService communityService,  ModelObject modelObject );

    /**
     * Find icon name for given model object.
     *
     *
     *
     * @param communityService community service
     * @param assignment   an assignment
     * @return a string
     */
    String findIconName( CommunityService communityService,  Assignment assignment );

    /**
     * Find icon name for given part.
     *
     *
     *
     *
     * @param communityService community service
     * @param part a specable
     * @param assignments a list of assignments
     * @param simplified whether simplified
     * @return a string
     */
    String findIconName( CommunityService communityService,  Specable part, Assignments assignments, boolean simplified );

    /**
     * Get icon directory.
     * @return  a directory
     * @throws java.io.IOException an IO exception
     */
    File getIconDirectoryFile() throws IOException;

    /**
     * Find icon file given relative filename.
     *
     * @param communityService community service
     * @param encodedPath a string
     * @return  a file
     * @throws java.io.IOException if fails
     */
    File findIcon( CommunityService communityService,  String encodedPath ) throws IOException;

    /**
     * Return path to "too_complex" image.
     * @return a string
     */
    String tooComplexImagePath();

    /**
     * Make square a file given its path and store into given file.
     * Make numbered versions with empty space under for labels.
     * @param filePath path to image to make square
     * @param iconFile file where to store squared image
     * @param iconPrefix pre-extension string indicating an icon (to make icons from the squarified image)
     * @return boolean whether successful
     */
    boolean squarifyAndIconize( String filePath, File iconFile, String iconPrefix );

    /**
     * Get image dir path.
     * @return a string
     */
    String getImageDirPath();

    /**
     * Return path to thumbnail of an image, making the thumbnail if needed.
     * @param imagePath the path of the full-sized image relative to the image directory
     * @param imageName the image file name
     * @return the path to the thumbnail
     */
    String getThumbnailPath( String imagePath, String imageName );

 }
