// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.community.CommunityDao;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.CollaborationModel;

/**
 * A listener to important plan management business.
 */
public interface ModelListener {

    /**
     * A plan is about to be put in production.
     *
     * @param devCollaborationModel the development plan
     */
    void aboutToProductize( CollaborationModel devCollaborationModel );

    /**
     * A new plan was put in production.
     *
     * @param collaborationModel the new plan
     */
    void productized( CollaborationModel collaborationModel );

    /**
     * A new development plan was created.
     *
     * @param devCollaborationModel the new plan.
     */
    void created( CollaborationModel devCollaborationModel );

    /**
     * A plan community was created.
     *
     * @param planCommunity the new plan community.
     */
    void created( PlanCommunity planCommunity );

    /**
     * A development plan has been loaded.
     *
     * @param modelDao the plan dao
     */
    void loaded( ModelDao modelDao );

    /**
     * A plan community has been loaded.
     *
     * @param communityDao the community dao
     */
    void loaded( CommunityDao communityDao );

    /**
     * A plan is about to be removed from memory.
     *
     * @param modelDao the plan dao
     */
    void aboutToUnload( ModelDao modelDao );
}
