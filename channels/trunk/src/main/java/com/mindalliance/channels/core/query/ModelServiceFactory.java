/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.query;

import com.mindalliance.channels.core.model.CollaborationModel;

/**
 * Creator and cache for plan services.
 */
public interface ModelServiceFactory {

    /**
     * Get the dedicated service for a given plan.
     * @param collaborationModel the plan
     * @return the service
     */
    ModelService getService( CollaborationModel collaborationModel );

    ModelService getService( String planUri, int planVersion );
}
