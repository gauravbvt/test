/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.query;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.UserDao;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.engine.nlp.SemanticMatcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Creator and cache for plan services.
 */
public class PlanServiceFactoryImpl implements PlanServiceFactory {

    private final Map<Plan,PlanService> services = new HashMap<Plan, PlanService>();

    private PlanManager planManager;

    private SemanticMatcher semanticMatcher;

    private UserDao userDao;

    private AttachmentManager attachmentManager;

    /**
     * Notify all services of impending doom...
     */
    public synchronized void destroy() {
        for ( PlanService planService : services.values() )
            planService.onDestroy();
        services.clear();
    }

    @Override
    public synchronized PlanService getService( Plan plan ) {
        PlanService planService = services.get( plan );
        if ( planService != null )
            return planService;

        PlanService result = new PlanService( planManager, semanticMatcher, userDao, plan, attachmentManager );
        services.put( plan, result );
        return result;
    }

    /**
     * Set the plan manager for all plan services.
     * @param planManager the plan manager
     */
    public synchronized void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }

    /**
     * Set the semantic matcher used by all services.
     * @param semanticMatcher the semantic matcher
     */
    public synchronized void setSemanticMatcher( SemanticMatcher semanticMatcher ) {
        this.semanticMatcher = semanticMatcher;
    }

    /**
     * Set the user dao used by all services.
     * @param userDao the user manager
     */
    public synchronized void setUserDao( UserDao userDao ) {
        this.userDao = userDao;
    }

    /**
     * Set the attachment manager used by all services.
     * @param attachmentManager the attachment manager
     */
    public synchronized void setAttachmentManager( AttachmentManager attachmentManager ) {
        this.attachmentManager = attachmentManager;
    }
}
