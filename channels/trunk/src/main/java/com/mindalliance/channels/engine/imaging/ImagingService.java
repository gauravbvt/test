package com.mindalliance.channels.engine.imaging;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.engine.query.Assignments;

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
     *
     * @param plan
     * @param url a string
     * @return an int array
     */
    int[] getImageSize( Plan plan, String url );

    /**
     * Create/update PNG icons for model object from image at given url.
     *
     *
     * @param plan
     * @param url         a string
     * @param modelObject a model object
     * @return true if successful
     */
    boolean iconize( Plan plan, String url, ModelObject modelObject );

    /**
     * Remove icons for model object.
     *
     * @param modelObject a model object
     */
    void deiconize( ModelObject modelObject );

    /**
     * Return url of squared icon..
     *
     *
     * @param plan
     * @param modelObject a model object
     * @return a string
     */
    String getSquareIconUrl( Plan plan, ModelObject modelObject );

    /**
     * Find icon name for given model object.
     *
     *
     *
     * @param plan
     * @param modelObject   a model object
     * @return a string
     */
    String findIconName( Plan plan, ModelObject modelObject );

    /**
     * Find icon name for given model object.
     *
     *
     *
     * @param plan
     * @param assignment   an assignment
     * @return a string
     */
    String findIconName( Plan plan, Assignment assignment );

    /**
     * Find icon name for given part.
     *
     *
     *
     *
     * @param plan
     * @param part a specable
     * @param assignments a list of assignments
     * @return a string
     */
    String findIconName( Plan plan, Specable part, Assignments assignments );

    /**
     * Get icon directory.
     * @return  a directory
     * @throws java.io.IOException an IO exception
     */
    File getIconDirectoryFile() throws IOException;

    /**
     * Find icon file given relative filename.
     * @param encodedPath a string
     * @return  a file
     * @throws java.io.IOException if fails
     */
    File findIcon( String encodedPath ) throws IOException;
}
