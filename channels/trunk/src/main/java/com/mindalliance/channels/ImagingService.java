package com.mindalliance.channels;

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
}
