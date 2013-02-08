/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.query;

import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Plan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Creator and cache for plan services.
 */
public class PlanServiceFactoryImpl implements PlanServiceFactory, ApplicationContextAware {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanServiceFactoryImpl.class );

    private final Map<Plan,PlanService> services = new HashMap<Plan, PlanService>();

    private ApplicationContext applicationContext;
    private PlanManager planManager;

    /**
     * Notify all services of impending doom...
     */
    public synchronized void destroy() {
        for ( PlanService planService : services.values() )
            planService.onDestroy();
        services.clear();
    }

    @Override
    public synchronized void setApplicationContext( ApplicationContext applicationContext ) {
        this.applicationContext = applicationContext;
    }

    @Override
    public synchronized PlanService getService( Plan plan ) {
        PlanService planService = services.get( plan );
        if ( planService != null )
            return planService;

        PlanService newPlanService = (PlanService) applicationContext.getBean( "planService" );  // a prototype, not a singleton
        newPlanService.setPlan( plan );
        services.put( plan, newPlanService );
        return newPlanService;
    }

    @Override
    public PlanService getService( String planUri, int planVersion ) {
        Plan plan = planManager.getPlan( planUri, planVersion );
        if ( plan == null ) {
            LOG.error( "Plan not found " + planUri + " v." + planVersion );
            throw new RuntimeException( "Plan not found " + planUri + " v." + planVersion );
        }
        return getService( plan );
    }

    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }
}
