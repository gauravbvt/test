package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.dao.PlanListener;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserInfo;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.query.QueryService;
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
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 7, 2009
 * Time: 9:33:25 AM
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

    /**
     * Query service.
     */
    private QueryService queryService;

    //-------------------------------
    public IssueScanner() {
    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
        analyst.setIssueScanner( this );
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
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
        LOG.debug( "Rescanning issue in " + plan.getName() );
        terminate( plan.getUri() );
        scan( plan );
    }

    @Override
    public void scan( Plan plan ) {
        Daemon daemon = new Daemon( plan );
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

    //===============================================================
    /**
     * Background analysis daemon.
     */
    public class Daemon extends Thread {

        /**
         * Thread local variable holding the plan.
         * Looked up by PlanManager when resolving current plan.
         */
        private ThreadLocal<Plan> planHolder;

        /**
         * Whether scan is active (else terminates).
         */
        private boolean active;

        public Daemon( final Plan plan ) {
            setDaemon( true );
            setPriority( Thread.NORM_PRIORITY - PRIORITY_REDUCTION );
            planHolder = new ThreadLocal<Plan>() {
                @Override
                protected synchronized Plan initialValue() {
                    return plan;
                }
            };
        }

        /**
         * Get thread local plan.
         *
         * @return a plan
         */
        public Plan getPlan() {
            return planHolder.get();
        }

        /**
         * Activate scan.
         */
        public synchronized void activate() {
            LOG.info( "Activating issue sweep on plan " + getPlan() );
            active = true;
            super.start();
        }

        /**
         * Abort scan.
         */
        public synchronized void terminate() {
            LOG.info( "Terminating issue sweep on plan " + getPlan() );
            active = false;
        }

        /**
         * Run thread, aborting as soon as possible if scan terminated.
         */
        @Override
        public void run() {
            setupUser();
            try {
                LOG.debug( "Current user = {}, plan = {}", User.current(), User.plan() );
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
                analyst.findAllUnwaivedIssues();
                if ( !active ) return;
                analyst.isValid( getPlan() );
                if ( !active ) return;
                analyst.isComplete( getPlan() );
                if ( !active ) return;
                analyst.isRobust( getPlan() );
                if ( !active ) return;
                analyst.countTestFailures( getPlan(), Issue.VALIDITY );
                if ( !active ) return;
                analyst.countTestFailures( getPlan(), Issue.COMPLETENESS );
                if ( !active ) return;
                analyst.countTestFailures( getPlan(), Issue.ROBUSTNESS );

                long endTime = System.currentTimeMillis();
                LOG.info( "Issue sweep completed on " + getPlan() + " in "
                          + ( endTime - startTime ) + " msecs" );

            } catch ( Throwable e ) {
                LOG.error( "Deamon failed", e );
                terminate();
            }
        }

        private void setupUser() {
            User principal = new User( new UserInfo( "daemon", "*,Issue Scanner,*,ROLE_ADMIN" ) );
            principal.setPlan( getPlan() );
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.add( new GrantedAuthorityImpl( "ROLE_ADMIN" ) );
            SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken( "daemon", principal, authorities ) );
        }

        private void scanIssues( ModelObject mo ) {
            if ( !active ) return;
            analyst.listIssues( mo, true );
        }
    }
}
