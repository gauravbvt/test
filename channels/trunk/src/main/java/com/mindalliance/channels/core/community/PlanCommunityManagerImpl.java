package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.community.participation.ParticipationManager;
import com.mindalliance.channels.core.dao.Exporter;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.Journal;
import com.mindalliance.channels.core.dao.JournalCommand;
import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.PlanServiceFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plan community manager.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/12
 * Time: 3:03 PM
 */
public class PlanCommunityManagerImpl implements PlanCommunityManager, ApplicationContextAware {


    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanCommunityManagerImpl.class );

    /**
     * All the plans, indexed by version uri (uri:version).
     */
    private final Map<CommunityDefinition, CommunityDao> daoIndex =
            Collections.synchronizedMap(
                    new HashMap<CommunityDefinition, CommunityDao>() );

    /**
     * Name of the default support community.
     */
    private String defaultSupportCommunity;
    /**
     * URI of the default community calendar host.
     */
    private String defaultCommunityCalendarHost;
    /**
     * Name of the default community calendar.
     */
    private String defaultCommunityCalendar;
    /**
     * Name of the default community calendar private ticket.
     */
    private String defaultCommunityCalendarPrivateTicket;

    private ApplicationContext applicationContext;
    private Map<Plan,PlanCommunity> domainPlanCommunities = new HashMap<Plan,PlanCommunity>();
    private String serverUrl;
    private ImportExportFactory importExportFactory;
    /** Manager for community manager event listeners. */
    private final Listeners listeners = new Listeners();



    @Autowired
    private PlanManager planManager;

    @Autowired
    private PlanServiceFactory planServiceFactory;

    @Autowired
    private ParticipationManager participationManager;

    @Autowired
    private CommunityDefinitionManager communityDefinitionManager;

    public PlanCommunityManagerImpl( ) {
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl( String serverUrl ) {
        this.serverUrl = serverUrl;
    }

    public ImportExportFactory getImportExportFactory() {
        return importExportFactory;
    }

    public void setImportExportFactory( ImportExportFactory importExportFactory ) {
        this.importExportFactory = importExportFactory;
    }

    public String getDefaultCommunityCalendar() {
        return defaultCommunityCalendar;
    }

    public void setDefaultCommunityCalendar( String defaultCommunityCalendar ) {
        this.defaultCommunityCalendar = defaultCommunityCalendar;
    }

    public String getDefaultCommunityCalendarHost() {
        return defaultCommunityCalendarHost;
    }

    public void setDefaultCommunityCalendarHost( String defaultCommunityCalendarHost ) {
        this.defaultCommunityCalendarHost = defaultCommunityCalendarHost;
    }

    public String getDefaultCommunityCalendarPrivateTicket() {
        return defaultCommunityCalendarPrivateTicket;
    }

    public void setDefaultCommunityCalendarPrivateTicket( String defaultCommunityCalendarPrivateTicket ) {
        this.defaultCommunityCalendarPrivateTicket = defaultCommunityCalendarPrivateTicket;
    }

    public String getDefaultSupportCommunity() {
        return defaultSupportCommunity;
    }

    public void setDefaultSupportCommunity( String defaultSupportCommunity ) {
        this.defaultSupportCommunity = defaultSupportCommunity;
    }

    @Override
    public CommunityDao getDao( PlanCommunity planCommunity ) {
        return getDao( planCommunity.getUri() );
    }

    @Override
    public CommunityDao getDao( String uri ) {
        synchronized ( daoIndex ) {
            CommunityDefinition communityDefinition = communityDefinitionManager.get( uri );
            if ( communityDefinition == null )
                return null;

            CommunityDao result = daoIndex.get( communityDefinition );
            if ( result == null ) {
                result = createDao( communityDefinition );
                daoIndex.put( communityDefinition, result );
            }

            return result;
        }
    }

    private CommunityDao createDao( CommunityDefinition communityDefinition ) {
        try {
            CommunityDao dao = new CommunityDao( communityDefinition );
            dao.setIdGenerator( communityDefinitionManager.getIdGenerator() );
            // Get plan dao, loading it if needed
            Plan plan = planManager.getPlan( communityDefinition.getPlanUri(), communityDefinition.getPlanVersion() );
            PlanDao planDao = planManager.getDao( plan );
            dao.setSubDao( planDao );
            // then load community
            dao.resetCommunity();
            dao.defineImmutableEntities();
            if ( importExportFactory != null )
                dao.load( importExportFactory.createImporter( "daemon", dao ) );
            else
                dao.validate();
            listeners.fireLoaded( dao );
            return dao;

        } catch ( IOException e ) {
            LOG.error( "Unable to load community " + communityDefinition.getUri(), e );
            return null;
        }
    }

    public List<PlanCommunity> getPlanCommunities() {
        List<PlanCommunity> result = new ArrayList<PlanCommunity>();
        synchronized( this ) {
             for ( CommunityDefinition definition : communityDefinitionManager ) {
                 String uri = definition.getUri();
                 CommunityDao dao = getDao( uri );
                 if ( dao != null )
                     result.add( dao.getPlanCommunity() );
             }
            for ( Plan plan : planManager.getPlans() ) {
                result.add( getDomainPlanCommunity( plan ) );
            }
        }
        Collections.sort( result );
        return Collections.unmodifiableList( result );
    }

    @Override
    public synchronized void setApplicationContext( ApplicationContext applicationContext ) {
        this.applicationContext = applicationContext;
    }

    @Override
    public PlanCommunity getPlanCommunity( final String planCommunityUri ) {
        return (PlanCommunity)CollectionUtils.find(
                getPlanCommunities(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        PlanCommunity planCommunity = (PlanCommunity)object;
                        return planCommunity.getUri().equals( planCommunityUri )
                                && ( !planCommunity.isDomainCommunity() || planCommunity.isDevelopment() );
                    }
                });
    }

    @Override
    public PlanCommunity getDomainPlanCommunity( Plan plan ) {
        PlanCommunity planCommunity = domainPlanCommunities.get( plan );
        if ( planCommunity == null ) {
            CommunityService communityService = (CommunityService)applicationContext.getBean( "communityService" ); // prototype bean
            communityService.setPlanService( getPlanService( plan ) );
            planCommunity = new PlanCommunity( plan );
            communityService.setPlanCommunity( planCommunity );
            domainPlanCommunities.put( plan, planCommunity );
        }
        return planCommunity;
    }

    @Override
    public PlanCommunity findPlanCommunity( final String planCommunityUri, final int planVersion ) {
        return (PlanCommunity)CollectionUtils.find(
                getPlanCommunities(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        PlanCommunity planCommunity = (PlanCommunity)object;
                        return planCommunity.getUri().equals( planCommunityUri )
                                && ( planCommunity.getPlanVersion() == planVersion );
                    }
                });
    }

    private PlanService getPlanService( Plan plan ) {
        return planServiceFactory.getService( plan );
    }

    ///// CommandListener


    @Override
    public void commandDone( Commander commander, Command command, Change change ) {
        if ( !commander.isReplaying() && command.isTop() && !change.isNone() )
            onAfterCommand( commander.getPlanCommunity(), command );
    }

    @Override
    public void commandUndone( Commander commander, Command command, Change change ) {
        commandDone( commander, command, change );
    }

    @Override
    public void commandRedone( Commander commander, Command command, Change change ) {
        commandDone( commander, command, change );
    }

    @Override
    public void started( Commander commander ) {
    }

    /**
     * Callback after a command was executed.
     *
     * @param planCommunity         the plan community
     * @param command      the command
     */
    private void onAfterCommand( PlanCommunity planCommunity, JournalCommand command ) {
        if ( command != null && command.isMemorable() )
            try {
                CommunityDao dao = getDao( planCommunity );
                Exporter exporter = importExportFactory.createExporter( "daemon", dao );
                synchronized ( dao ) {
                    Journal journal = dao.getJournal();
                    if ( command.forcesSnapshot()
                            || journal.size() >= communityDefinitionManager.getSnapshotThreshold() )
                        dao.save( exporter );
                    else {
                        journal.addCommand( command );
                        dao.saveJournal( exporter );
                    }
                }

            } catch ( IOException e ) {
                throw new RuntimeException( "Failed to save journal", e );
            }
    }

    public void clearCache() {
        // clearing done via aspect
    }


    /**
     * Listener event management.
     */
    private static final class Listeners {

        /** Whoever cares about plan manager events. */
        private final List<CommunityListener> communityListeners =
                Collections.synchronizedList( new ArrayList<CommunityListener>() );

        private Listeners() {
        }

        public void addListener( CommunityListener communityListener ) {
            communityListeners.add( communityListener );
        }

        public void removeListener( CommunityListener communityListener ) {
            communityListeners.remove( communityListener );
        }


        public void fireAboutToUnload( CommunityDao communityDao ) {
            synchronized ( communityListeners ) {
                for ( CommunityListener communityListener : communityListeners )
                    communityListener.aboutToUnload( communityDao );
            }
        }

        public void fireCreated( PlanCommunity planCommunity ) {
            synchronized ( communityListeners ) {
                for ( CommunityListener communityListener : communityListeners )
                    communityListener.created( planCommunity );
            }
        }

        public void fireLoaded( CommunityDao communityDao ) {
            synchronized ( communityListeners ) {
                for ( CommunityListener communityListener : communityListeners )
                    communityListener.loaded( communityDao );
            }
        }
    }


}
