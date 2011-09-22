/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.PlanServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Creator/cache for plan-specific commanders.
 */
public class CommanderFactoryImpl implements CommanderFactory, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger( CommanderFactoryImpl.class );

    private final Map<Plan, Commander> commanders = new ConcurrentHashMap<Plan, Commander> ();

    private List<CommandListener> commonListeners = new ArrayList<CommandListener>();

    private ImportExportFactory importExportFactory;

    private PlanManager planManager;

    private PlanServiceFactory planServiceFactory;

    private List<PresenceListener> presenceListeners;

    private int timeout;

    @Override
    public void afterPropertiesSet() {
        planManager.assignPlans();
    }

    @Override
    public Commander getCommander( Plan plan ) {
        Commander commander = commanders.get( plan );
        if ( commander != null )
            return commander;

        synchronized ( this ) {
            // Check if someone else beat us at initialization
            Commander cmd = commanders.get( plan );
            if ( cmd != null )
                return cmd;

            DefaultCommander newCommander = new DefaultCommander();
            commanders.put( plan, newCommander );
            newCommander.setPlanManager( planManager );
            newCommander.setPlanDao( planManager.getDao( plan ) );

            PlanService planService = planServiceFactory.getService( plan );
            newCommander.setQueryService( planService );
            newCommander.setLockManager( new DefaultLockManager( planService ) );

            newCommander.setCommandListeners( commonListeners );
            newCommander.setImportExportFactory( importExportFactory );
            newCommander.setTimeout( timeout );
            newCommander.setPresenceListeners( presenceListeners );
            newCommander.initialize();

            return newCommander;
        }
    }

    /**
     * Listeners on all commanders.
     *
     * @param commonListeners command listeners
     */
    public void setCommonListeners( List<CommandListener> commonListeners ) {
        this.commonListeners = Collections.unmodifiableList( commonListeners );
    }

    /**
     * Presence listeners on all commanders.
     *
     * @param presenceListeners the listeners
     */
    public void setPresenceListeners( List<PresenceListener> presenceListeners ) {
        this.presenceListeners = Collections.unmodifiableList( presenceListeners );
    }

    //-------------------------------

    /**
     * Specify the common import/export factory for all commanders.
     *
     * @param importExportFactory the factory
     */
    public void setImportExportFactory( ImportExportFactory importExportFactory ) {
        this.importExportFactory = importExportFactory;
    }

    /**
     * Specify the common plan manager for all commanders.
     *
     * @param planManager a plan manager
     */
    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }

    /**
     * Set the plan service factory for initializing the new commanders.
     *
     * @param planServiceFactory the factory
     */
    public void setPlanServiceFactory( PlanServiceFactory planServiceFactory ) {
        this.planServiceFactory = planServiceFactory;
    }

    /**
     * Set the command timeout for all commanders.
     *
     * @param timeout number of seconds
     */
    public void setTimeout( int timeout ) {
        this.timeout = timeout;
    }
}
