/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.query;

import com.mindalliance.channels.core.model.Plan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Creator and cache for plan services.
 */
public class PlanServiceFactoryImpl implements PlanServiceFactory, ApplicationContextAware {

    private final Map<Plan,PlanService> services = new HashMap<Plan, PlanService>();

    private ApplicationContext applicationContext;

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

        PlanService result = (PlanService) applicationContext.getBean( "planService" );
        result.setPlan( plan );
        services.put( plan, result );
        return result;
    }
}
