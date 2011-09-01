package com.mindalliance.channels.core.model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 21, 2010
 * Time: 10:31:33 AM
 */
public interface Conceptualizable {
    /**
     * Whether conceptual.
     *
     * @return a boolean
     */
    boolean isConceptual();

    /**
     * Whether implicitly or explicitly de facto conceptual.
     *
     * @return a boolean
     */
    boolean isDeFactoConceptual();

    /**
     * Reason given.
     *
     * @return a string
     */
    String getConceptualReason();
}
