/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.dao.PlanListener;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.PlanServiceFactory;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.db.data.users.UserAccess;
import com.mindalliance.channels.db.data.users.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Scans all plans for issues in low priority threads to warm up the cache.
 */
public class IssueScanner implements Scanner, PlanListener {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( IssueScanner.class );

    /**
     * Daemon threads reduction in priority compared to normal priority.
     */
    private static final int PRIORITY_REDUCTION = 2;

    /**
     * Daemon threads that each scan a plan for issues.
     */
    private final Map<String, Daemon> daemons =
            Collections.synchronizedMap( new HashMap<String, Daemon>() );

    /**
     * Analyst.
     */
    private Analyst analyst;

    private PlanServiceFactory planServiceFactory;

    //-------------------------------
    public IssueScanner() {
    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
        analyst.setIssueScanner( this );
    }

 /*   public void scan() {
        LOG.debug( "Activating issue scans" );
        synchronized ( daemons  ) {
            for ( Daemon daemon : daemons.values() )
                daemon.activate();
        }
    }*/

    @Override
    public void terminate() {
        LOG.debug( "Terminating issue scans" );
        synchronized ( daemons  ) {
            for ( String uri : daemons.keySet() )
                terminate( uri );
        }
    }

    @Override
    public void rescan( Plan plan ) {
        LOG.debug( "Rescanning issue in {}", plan.getName() );
        terminate( plan.getUri() );
        scan( plan );
    }

    @Override
    public void scan( Plan plan ) {
        Daemon daemon = new Daemon( planServiceFactory.getService( plan ) );
        daemons.put( plan.getUri(), daemon );
        daemon.activate();
    }

    private void terminate( String uri ) {
        synchronized ( daemons ) {
            Daemon daemon = daemons.get( uri );
            if ( daemon != null ) {
                String daemonName = daemon.getName();
                LOG.debug( "{} isAlive: {}", daemonName, daemon.isAlive() );
                daemon.terminate();
                try {
                    daemon.join();
                    LOG.debug( "Joined with {}", daemonName );
                } catch ( InterruptedException e ) {
                    LOG.warn( "Interrupted while joining with thread " + daemonName, e );
                }
                daemons.remove( uri );
            }
        }
    }

    @Override
    public void aboutToProductize( Plan devPlan ) {
        terminate( devPlan.getUri() );
    }

    @Override
    public void aboutToUnload( PlanDao planDao ) {
        aboutToProductize( planDao.getPlan() );
    }

    @Override
    public void created( Plan devPlan ) {
        scan( devPlan );
    }

    @Override
    public void loaded( PlanDao planDao ) {
        created( planDao.getPlan() );
    }

    @Override
    public void productized( Plan plan ) {
    }

    public void setPlanServiceFactory( PlanServiceFactory planServiceFactory ) {
        this.planServiceFactory = planServiceFactory;
    }

    //===============================================================
    /**
     * Background analysis daemon.
     */
    public class Daemon extends Thread {

        /**
         * The plan being analyzed.
         */
        private final Plan plan;

        private final QueryService queryService;

        /**
         * Whether scan is active (else terminates).
         */
        private boolean active;

        public Daemon( QueryService queryService ) {
            this.queryService = queryService;
            this.plan = queryService.getPlan();

            setDaemon( true );
            setPriority( Thread.NORM_PRIORITY - PRIORITY_REDUCTION );
        }

        /**
         * Get the plan.
         *
         * @return a plan
         */
        public Plan getPlan() {
            return plan;
        }

        /**
         * Activate scan.
         */
        public synchronized void activate() {
            LOG.info( "Activating issue sweep on plan {}", plan );
            active = true;
            super.start();
        }

        /**
         * Abort scan.
         */
        public synchronized void terminate() {
            LOG.info( "Terminating issue sweep on plan {}", plan );
            active = false;
        }

        /**
         * Run thread, aborting as soon as possible if scan terminated.
         */
        @Override
        public void run() {
            ChannelsUser user = setupUser();
            try {
                LOG.debug( "Current user = {}, plan = {}", user, plan );
                long startTime = System.currentTimeMillis();
                if ( !active ) return;
                for ( ModelObject mo : queryService.list( ModelObject.class ) ) {
                    if ( !active ) return;
                    // Garbage-collect unreferenced and undefined entities.
                    queryService.cleanup( mo.getClass(), mo.getName() );
                    if ( !active ) return;
                    scanIssues( mo );
                }
                if ( !active ) return;
                for ( Segment segment : queryService.list( Segment.class ) ) {
                    if ( !active ) return;
                    Iterator<Part> parts = segment.parts();
                    while ( parts.hasNext() ) {
                        if ( !active ) return;
                        Part part = parts.next();
                        scanIssues( part );
                    }
                    if ( !active ) return;
                    Iterator<Flow> flows = segment.flows();
                    while ( flows.hasNext() ) {
                        Flow flow = flows.next();
                        scanIssues( flow );
                    }
                }
//                if ( !active ) return;
//                analyst.findAllIssues();
                if ( !active ) return;

                analyst.findAllUnwaivedIssues( queryService );
                if ( !active ) return;
                analyst.isValid( queryService, plan );
                if ( !active ) return;
                analyst.isComplete( queryService, plan );
                if ( !active ) return;
                analyst.isRobust( queryService, plan );
                if ( !active ) return;
                analyst.countTestFailures( queryService, plan, Issue.VALIDITY );
                if ( !active ) return;
                analyst.countTestFailures( queryService, plan, Issue.COMPLETENESS );
                if ( !active ) return;
                analyst.countTestFailures( queryService, plan, Issue.ROBUSTNESS );
                if ( !active ) return;
                // Find all commitments
                // queryService.findAllCommitments();
                long endTime = System.currentTimeMillis();
                LOG.info( "Issue sweep completed on " + plan + " in "
                          + ( endTime - startTime ) + " msecs" );

            } catch ( Throwable e ) {
                LOG.error( "Deamon failed", e );
                terminate();
            }
        }

        private ChannelsUser setupUser() {
            ChannelsUser principal = new ChannelsUser(
                    new UserRecord( "_channels_", "daemon", "Issue Scanner", "", UserAccess.UserRole.Admin ) );
            principal.setPlan( plan );
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.add( new GrantedAuthorityImpl( "ROLE_ADMIN" ) );
            SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken( "daemon", principal, authorities ) );
            return principal;
        }

        private void scanIssues( ModelObject mo ) {
            if ( !active ) return;

            // Model object can be null when deleted when scan was in progress
            if ( mo != null )
                analyst.listIssues( queryService, mo, true );
        }
    }
}
