package com.mindalliance.channels.analysis;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.Scanner;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Scans all plans for issues in low priority threads to warm up the cache.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 7, 2009
 * Time: 9:33:25 AM
 */
public class IssueScanner implements Scanner {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( IssueScanner.class );
    /**
     * Daemon threads that each scan a plan for issues.
     */
    private Map<Plan, Daemon> daemons = new HashMap<Plan, Daemon>();
    /**
     * Analyst.
     */
    private Analyst analyst;
    /**
     * Query service.
     */
    private QueryService queryService;
    /**
     * Plan manager.
     */
    private PlanManager planManager;
    /**
     * Daemon threads reduction in priority compared to normal priority.
     */
    private static final int PRIORITY_REDUCTION = 2;

    private void initialize(  ) {
        for ( Plan plan : planManager.getPlans() ) {
            daemons.put( plan, new Daemon( plan ) );
        }
    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
        analyst.setIssueScanner( this );
    }

    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }

    public void setQueryService( QueryService queryService ) {
        this.queryService = queryService;
    }

    /**
     * {@inheritDoc}
     */
    public void scan() {
        initialize();
        LOG.debug( "Activating issue scans" );
        for ( Daemon daemon : daemons.values() ) {
            daemon.activate();
        }
    }

    /**
     * {@inheritDoc}
     */
     public void terminate() {
        LOG.debug( "Terminating issue scans" );
        for ( Daemon daemon : daemons.values() ) {
            daemon.terminate();
        }
    }

    /**
     * {@inheritDoc}
     */
      public void rescan( Plan plan ) {
        LOG.debug( "Rescanning issue in " + plan.getName() );
        daemons.get( plan ).terminate();
        Daemon daemon = new Daemon( plan );
        daemons.put( plan, daemon );
        daemon.activate();
    }

    /**
     * Background analysis daemon.
     */
    public class Daemon extends Thread {
        /**
         * Thread local variable holding the plan. Looked up by PlanManager when resolving current plan.
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
            LOG.debug( "Activating issue sweep on plan " + getPlan().getId() );
            active = true;
            super.start();
        }

        /**
         * Abort scan.
         */
        public synchronized void terminate() {
            LOG.debug( "Terminating issue sweep on plan " + getPlan().getId() );
            active = false;
        }

        /**
         * Run thread, aborting as soon as possible if scan terminated.
         */
        public void run() {
            try {
                if ( !active ) return;
                for ( ModelObject mo : queryService.list( ModelObject.class ) ) {
                    if ( !active ) return;
                    scanIssues( mo );
                }
                if ( !active ) return;
                for ( Scenario scenario : queryService.list( Scenario.class ) ) {
                    if ( !active ) return;
                    Iterator<Part> parts = scenario.parts();
                    while ( parts.hasNext() ) {
                        if ( !active ) return;
                        Part part = parts.next();
                        scanIssues( part );
                    }
                    if ( !active ) return;
                    Iterator<Flow> flows = scenario.flows();
                    while ( flows.hasNext() ) {
                        Flow flow = flows.next();
                        scanIssues( flow );
                    }
                }
                if ( !active ) return;
                queryService.findAllIssues( analyst );
                if ( !active ) return;
                queryService.findAllUnwaivedIssues( analyst );
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
            } catch ( Throwable e ) {
                e.printStackTrace();
                LOG.debug( "Deamon failed", e );
            }
        }

        private void scanIssues( ModelObject mo ) {
            if ( !active ) return;
            analyst.listIssues( mo, true );
            if ( !active ) return;
            analyst.listIssues( mo, false );
            if ( !active ) return;
            analyst.listUnwaivedIssues( mo, true );
            if ( !active ) return;
            analyst.hasIssues( mo, true );
            if ( !active ) return;
            analyst.getIssuesSummary( mo, true );
        }
    }
}
