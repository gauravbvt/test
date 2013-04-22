/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core;

import com.mindalliance.channels.core.Attachment.Type;

import java.io.IOException;
import java.io.InputStream;

/**
 * Specifications for incoming attachments.
 */
public interface Upload {

    /**
     * Specify the type of attachment.
     *
     * @return a type
     */
    Type getSelectedType();

    /**
     * Specify the name of the attachment, as appearing in lists.
     *
     * @return the name
     */
    String getName();

    /**
     * The name of the underlying file.
     *
     * @return a file name
     */
    String getFileName();

    /**
     * The actual data of the file.
     *
     * @return an input stream on the data
     * @throws IOException on errors
     */
    InputStream getInputStream() throws IOException;
}
