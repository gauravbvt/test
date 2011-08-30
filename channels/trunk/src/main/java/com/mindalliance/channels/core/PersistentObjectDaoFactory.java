/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core;

/**
 * Access to plan-specific persistent object dao.
 */
public interface PersistentObjectDaoFactory {

    /**
     * Check to see if database can be opened.
     *
     * @param uri the plan uri
     * @return true if the database exists and was opened
     */
    boolean check( String uri );

    /**
     * Get the persistent object dao for a given plan uri.
     * @param planUri the plan uri
     * @return the dao
     */
    PersistentObjectDao getDao( String planUri );
}
