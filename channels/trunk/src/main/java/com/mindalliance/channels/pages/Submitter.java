package com.mindalliance.channels.pages;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 9:12:43 PM
 */
public interface Submitter {
    /**
     * Register a submitable component
     * @param submitable a submitable
     */
    void register(Submitable submitable);
}
