package com.mindalliance.channels.pages.components;

/**
 * Tab indexable for sequential tabbing.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/14
 * Time: 11:16 PM
 */
public interface TabIndexable {

    /**
     * Provide tabIndexer with which to assign indices.
     *
     * @param tabIndexer what keep count of tab indices
     */
    void initTabIndexing( TabIndexer tabIndexer );

}
