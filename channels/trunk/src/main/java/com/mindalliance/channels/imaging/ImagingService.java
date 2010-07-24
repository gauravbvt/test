package com.mindalliance.channels.imaging;

import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.query.QueryService;

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
     * Find the size of an image given its url.
     *
     * @param url a string
     * @return an int array
     */
    int[] getImageSize( String url );

    /**
     * Create/update PNG icons for model object from image at given url.
     *
     * @param url         a string
     * @param modelObject a model object
     * @return true if successful
     */
    boolean iconize( String url, ModelObject modelObject );

    /**
     * Remove icons for model object.
     *
     * @param modelObject a model object
     */
    void deiconize( ModelObject modelObject );

    /**
     * Return path of icon up to and excluding ".png" extension.
     *
     * @param modelObject a model object
     * @return a string
     */
    String getIconPath( ModelObject modelObject );

    /**
     * Return url of squared icon..
     *
     * @param modelObject a model object
     * @return a string
     */
    String getSquareIconUrl( ModelObject modelObject );

    /**
     * Find icon name for given model object.
     *
     * @param modelObject   a model object
     * @param imagesDirName the name of the directory with the default icons
     * @return a string
     */
    String findIconName( ModelObject modelObject, String imagesDirName );

    /**
     * Find icon name for given part.
     *
     * @param part a part
     * @param imagesDirName the name of the directory with the default icons
     * @param queryService the query service
     * @return a string
     */
    String findIconName( Part part, String imagesDirName, QueryService queryService );

    /**
     * Get icon directory.
     * @return  a directory
     */
    File getIconDirectoryFile() throws IOException;
}
