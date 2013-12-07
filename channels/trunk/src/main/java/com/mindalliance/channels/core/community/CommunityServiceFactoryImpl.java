package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.query.PlanServiceFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Creator and cache for plan community services.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/5/13
 * Time: 8:38 PM
 */
public class CommunityServiceFactoryImpl implements CommunityServiceFactory, ApplicationContextAware {

    private final Map<PlanCommunity, CommunityService> services = new HashMap<PlanCommunity, CommunityService>();

    private ApplicationContext applicationContext;
    private PlanServiceFactory planServiceFactory;

    /**
     * Notify all services of impending doom...
     */
    public synchronized void destroy() {
        for ( CommunityService communityService : services.values() )
            communityService.onDestroy();
        services.clear();
    }

    @Override
    public synchronized void setApplicationContext( ApplicationContext applicationContext ) {
        this.applicationContext = applicationContext;
    }

    @Override
    public synchronized CommunityService getService( PlanCommunity planCommunity ) {
        CommunityService communityService = services.get( planCommunity );
        if ( communityService != null )
            return communityService;
        CommunityService result = (CommunityService) applicationContext.getBean( "communityService" );  // a prototype, not a singleton
        result.setPlanCommunity( planCommunity );
        result.setPlanService( planServiceFactory.getService(
                planCommunity.getPlanUri(),
                planCommunity.getPlanVersion() ) );
        result.cleanUp( );
        services.put( planCommunity, result );
        return result;
    }

    @Override
    public synchronized void removeService( PlanCommunity planCommunity ) {
        services.remove( planCommunity );
    }

    public void setPlanServiceFactory( PlanServiceFactory planServiceFactory ) {
        this.planServiceFactory = planServiceFactory;
    }
}
