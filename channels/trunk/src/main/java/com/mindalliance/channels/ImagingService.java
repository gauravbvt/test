package com.mindalliance.channels;

import com.mindalliance.channels.model.ModelObject;

/**
 * An image transformation service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 18, 2009
 * Time: 1:25:37 PM
 */
public interface ImagingService extends Service {

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
}
