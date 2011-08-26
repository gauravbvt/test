package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.model.Identifiable;

/**
 * That which can acquire and release locks, and other resources.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 23, 2010
 * Time: 10:13:57 PM
 */
public interface Releaseable {

    /**
     * Release any lock on an identifiable.
     * @param identifiable  an identifiable
     */
    void requestLockOn( Identifiable identifiable );

    /**
     * Release any lock on an identifiable.
     * @param identifiable  an identifiable
     */
    void releaseAnyLockOn( Identifiable identifiable );

    /**
     * Release locks acquired after initialization.
     */
    void release();
}
