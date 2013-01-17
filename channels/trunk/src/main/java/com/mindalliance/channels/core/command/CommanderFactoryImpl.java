/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.query.PlanServiceFactory;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.social.PresenceListener;
import com.mindalliance.channels.social.services.ExecutedCommandService;
import com.mindalliance.channels.social.services.PresenceRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

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

    private final Map<String, Commander> commanders = new ConcurrentHashMap<String, Commander>();

    private List<CommandListener> commonListeners = new ArrayList<CommandListener>();

    private ImportExportFactory importExportFactory;

    private PlanManager planManager;

    private PlanServiceFactory planServiceFactory;

    private List<PresenceListener> presenceListeners = new ArrayList<PresenceListener>();

    private int timeout;

    @Autowired
    private PresenceRecordService presenceRecordService;

    @Autowired
    private ExecutedCommandService executedCommandService;

    @Autowired
    private Analyst analyst;


    @Override
    public void afterPropertiesSet() {
        initPresenceListeners();
        initCommonListeners();
        planManager.assignPlans();
    }

    @Override
    public Commander getCommander( PlanCommunity planCommunity ) {
        String planCommunityUri = planCommunity.getUri();
        Commander commander = commanders.get( planCommunityUri );
        if ( commander != null )
            return commander;

        synchronized ( this ) {
            // Check if someone else beat us at initialization
            Commander cmd = commanders.get( planCommunityUri );
            if ( cmd != null )
                return cmd;
            planCommunity.clearCache();
            DefaultCommander newCommander = new DefaultCommander();
            commanders.put( planCommunityUri, newCommander );
            newCommander.setPlanCommunity( planCommunity );
            newCommander.setPlanManager( planManager );
            newCommander.setLockManager( new DefaultLockManager( ) );

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
     */
    public void initCommonListeners() {
        List<CommandListener> listeners = new ArrayList<CommandListener>();
        listeners.add( analyst );
        listeners.add( planManager );
        listeners.add( executedCommandService );
        commonListeners = Collections.unmodifiableList( listeners );
    }

    /**
     * Presence listeners on all commanders.
     */
    public void initPresenceListeners() {
        List<PresenceListener> listeners = new ArrayList<PresenceListener>();
        listeners.add( presenceRecordService );
        presenceListeners = Collections.unmodifiableList( listeners );
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

    public Analyst getAnalyst() {
        return analyst;
    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
    }

    public PresenceRecordService getPresenceRecordService() {
        return presenceRecordService;
    }

    public void setPresenceRecordService( PresenceRecordService presenceRecordService ) {
        this.presenceRecordService = presenceRecordService;
    }

    public ExecutedCommandService getExecutedCommandService() {
        return executedCommandService;
    }

    public void setExecutedCommandService( ExecutedCommandService executedCommandService ) {
        this.executedCommandService = executedCommandService;
    }

    /**
     * Specify the common plan manager for all commanders.
     *
     * @param planManager a plan manager
     */
    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }

    public PlanManager getPlanManager() {
        return planManager;
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
