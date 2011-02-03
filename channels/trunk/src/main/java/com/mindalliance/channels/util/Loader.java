package com.mindalliance.channels.util;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/11
 * Time: 11:06 AM
 */
public interface Loader {
    /**
     * Load definition from reader.
     *
     * @param reader a buffered reader
     * @throws IOException an io exception
     */
    void load( BufferedReader reader ) throws IOException;
}
