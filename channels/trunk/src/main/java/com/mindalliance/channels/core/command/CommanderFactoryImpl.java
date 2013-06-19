/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.db.services.activities.ExecutedCommandService;
import com.mindalliance.channels.db.services.activities.PresenceRecordService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.social.PresenceListener;
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

    private final Map<PlanCommunity, Commander> commanders = new ConcurrentHashMap<PlanCommunity, Commander>();

    private ImportExportFactory importExportFactory;

    private PlanManager planManager;

    private int timeout;

    @Autowired
    private PresenceRecordService presenceRecordService;

    @Autowired
    private ExecutedCommandService executedCommandService;

    @Autowired
    private Analyst analyst;

    @Autowired
    private PlanCommunityManager planCommunityManager;

    @Override
    public void afterPropertiesSet() {
        planManager.assignPlans(); // todo - COMMUNITY - do we want this? It should be ok for a user to have no plan and no planCommunityUri set (when user is in the Home Page, about to enter a community)
    }

    @Override
    public Commander getCommander( CommunityService communityService ) {
        PlanCommunity planCommunity = communityService.getPlanCommunity();
        Commander commander = commanders.get( planCommunity );
        if ( commander != null )
            return commander;

        synchronized ( this ) {
            // Check if someone else beat us at initialization
            Commander cmd = commanders.get( planCommunity );
            if ( cmd != null )
                return cmd;
            communityService.clearCache();
            DefaultCommander newCommander = new DefaultCommander();
            commanders.put( planCommunity, newCommander );
            newCommander.setCommunityService( communityService );
            newCommander.setPlanManager( planManager );
            newCommander.setLockManager( new DefaultLockManager( ) );

            newCommander.setCommandListeners( getCommonListeners( communityService ) );
            newCommander.setImportExportFactory( importExportFactory );
            newCommander.setTimeout( timeout );
            newCommander.setPresenceListeners( getPresenceListeners( communityService ) );
            newCommander.initialize();

            return newCommander;
        }
    }

    public void reset( PlanCommunity planCommunity ) {
        commanders.remove( planCommunity );
    }

    /**
     * Listeners on all commanders.
     */
    public List<CommandListener> getCommonListeners( CommunityService communityService ) {
        List<CommandListener> listeners = new ArrayList<CommandListener>();
        boolean isDomain = communityService.getPlanCommunity().isDomainCommunity();
        listeners.add( executedCommandService );
        if ( isDomain ) {
            listeners.add( analyst );
            listeners.add( planManager );
        } else {
            listeners.add( planCommunityManager );
        }
        return Collections.unmodifiableList( listeners );
    }

    /**
     * Presence listeners on all commanders.
     */
    public List<PresenceListener> getPresenceListeners( CommunityService communityService ) {
        List<PresenceListener> listeners = new ArrayList<PresenceListener>();
        listeners.add( presenceRecordService );
        return Collections.unmodifiableList( listeners );
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
     * Set the command timeout for all commanders.
     *
     * @param timeout number of seconds
     */
    public void setTimeout( int timeout ) {
        this.timeout = timeout;
    }
}
