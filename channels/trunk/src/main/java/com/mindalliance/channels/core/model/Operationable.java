package com.mindalliance.channels.core.model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 21, 2010
 * Time: 10:31:33 AM
 */
public interface Operationable {
    /**
     * Whether operational.
     *
     * @return a boolean
     */
    boolean isOperational();

    /**
     * Whether effectively operational.
     *
     * @return a boolean
     */
    boolean isEffectivelyOperational();
}
