package com.mindalliance.channels.pages.components;

import org.apache.wicket.Component;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/5/14
 * Time: 3:54 PM
 */
public interface TabIndexer {

    void giveTabIndexTo( Component component );

    int getTabIndexOf( Component component );
}
