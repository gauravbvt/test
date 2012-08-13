/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.attachments;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * A loader...
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
