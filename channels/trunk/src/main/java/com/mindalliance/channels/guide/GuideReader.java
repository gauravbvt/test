package com.mindalliance.channels.guide;

/**
 * Guide reader.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/25/12
 * Time: 11:10 AM
 */
public interface GuideReader {

    /**
     * Get the guide data.
     *
     * @return a guide
     */
    Guide getGuide();

    /**
     * Get server's url.
     *
     * @return a string
     */
    String getServerUrl();
}
