// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels;

/**
 * Public interface to agency-based functionality.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface Agency {

    /**
     * Return the local model.
     */
    Model getLocalModel();

    /**
     * Return the integrated model.
     */
    Model getIntegratedModel();

    /**
     * Return the official long name of the agency.
     */
    String getName();

    /**
     * Return the short name (acronym) of the agency.
     */
    String getShortName();

    /**
     * Return collaborating agencies.
     */
    Agencies getCollaboratingAgencies();
}
